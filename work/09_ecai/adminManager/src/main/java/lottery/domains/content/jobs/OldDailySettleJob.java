// package lottery.domains.content.jobs;
//
// import javautils.date.DateUtil;
// import javautils.math.MathUtil;
// import lottery.domains.content.biz.UserBillService;
// import lottery.domains.content.biz.UserLotteryReportService;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.UserLotteryReportDao;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.UserLotteryReport;
// import lottery.domains.content.global.Global;
// import lottery.domains.content.vo.bill.UserLotteryReportVO;
// import org.apache.commons.collections.CollectionUtils;
// import org.hibernate.criterion.Criterion;
// import org.hibernate.criterion.Restrictions;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
//
// /**
//  * 日结
//  *
//  * 1：1960 -> 1962 -> 1964 -> mick(72)
//  * 2：1960 -> 1962 -> mick(72)
//  * 3：1960 -> 1964 -> mick(72)
//  * 3：日结的金额追加到该账号的余额，同时追加该账号今天的报表优惠字段
//  * 4：日结的依据是其不分层级所有下级的消费统计(user_lottery_report)
//  * 5：每天凌晨1点半开始结算
//  *
//  * Created by Nick on 2016/9/27.
//  */
// @Component
// //@Transactional
// public class OldDailySettleJob {
//     private static final Logger log = LoggerFactory.getLogger(OldDailySettleJob.class);
//     @Autowired
//     private UserDao userDao;
//     @Autowired
//     private UserBillService uBillService;
//     @Autowired
//     private UserLotteryReportService uLotteryReportService;
//     @Autowired
//     private UserLotteryReportDao uLotteryReportDao;
//
//     /**
//      * 调度任务
//      */
//     @Scheduled(cron = "0 30 2 0/1 * *")
//     public void schedule() {
//         try {
//             log.debug("日结发放开始");
//
//             // 获取昨天yyyy-MM-dd
//             String yesterday = DateUtil.getYesterday();
//             String today = DateUtil.getCurrentDate();
//
//             // 开始日结
//             settleUp(yesterday, today);
//
//             log.debug("日结发放完成");
//         } catch (Exception e) {
//             log.error("日结发放出错", e);
//         }
//     }
//
//     /**
//      * 开始结算某一天的
//      */
//     public void settleUp(String sTime, String eTime) {
//         // 查找所有由1964或1962开出的1960号
//         List<User> users = findUsers(sTime, eTime);
//         if (CollectionUtils.isEmpty(users)) {
//             return;
//         }
//
//         // 结算某一个用户
//         for (User user : users) {
//             settleUpWithUser(user, sTime, eTime);
//         }
//     }
//
//     /**
//      * 结算某一个用户
//      */
//     public void settleUpWithUser(User user, String sTime, String eTime) {
//         // 查找所有下级的报表
//         List<UserLotteryReportVO> reports = uLotteryReportService.reportLowersAndSelf(user.getId(), sTime, eTime);
//         if (CollectionUtils.isEmpty(reports)) {
//             return;
//         }
//
//         // 总消费金额
//         double totalBilling = sumTotalBilling(reports);
//         if (totalBilling <= 0) {
//             return;
//         }
//
//         // 消费金额 * 1.2% = 日结金额
//         double totalAmount = MathUtil.multiply(totalBilling, 0.012);
//         if (totalAmount <= 0) {
//             return;
//         }
//
//         // 给1960加钱
//         userDao.updateLotteryMoney(user.getId(), totalAmount);
//
//         // 增加账单
//         String remarks = String.format("日结,销量：%s", new BigDecimal(totalBilling).setScale(4, RoundingMode.FLOOR).toString());
//         uBillService.addDailySettleBill(user, Global.BILL_ACCOUNT_LOTTERY, totalAmount, remarks);
//     }
//
//     private double sumTotalBilling(List<UserLotteryReportVO> reports) {
//         for (UserLotteryReportVO reportLowersAndSelf : reports) {
//             if ("总计".equals(reportLowersAndSelf.getName())) {
//                 return reportLowersAndSelf.getBillingOrder();
//             }
//
//         }
//         return 0;
//     }
//
//     /**
//      * 查找所有由1964或1962开出的1960号
//      */
//     private List<User> findUsers(String sTime, String eTime) {
//         List<Criterion> criterions = new ArrayList<>();
//         criterions.add(Restrictions.ge("code", 1960)); // 这个查询应该不会很快，有待优化
//
//         // 查找所有1960用户
//         List<User> users = userDao.list(criterions, null);
//         if (CollectionUtils.isEmpty(users)) {
//             return null;
//         }
//
//         // 放到map里等下方便查找
//         HashMap<Integer, User> usersMap = new HashMap<>();
//         for (User user : users) {
//             usersMap.put(user.getId(), user);
//         }
//
//         // 先筛选出符合条件的1960
//         //  * 1：1960 -> 1962 -> 1964 -> mick(72)
//         //  * 2：1960 -> 1962 -> mick(72)
//         //  * 3：1960 -> 1964 -> mick(72)
//         List<User> users1960 = new ArrayList<>();
//         for (User self : users) {
//             // 必须是1960号
//             if (self.getCode() != 1960 || self.getUpid() == 0) {
//                 continue;
//             }
//
//             User upLevel1User = usersMap.get(self.getUpid());
//             User upLevel2User = upLevel1User == null ? null : usersMap.get(upLevel1User.getUpid());
//
//             if (upLevel1User == null || upLevel2User == null) {
//                 continue;
//             }
//
//             //  * 1：1960 -> 1962 -> 1964 -> mick(72)
//             if (upLevel1User.getCode() == 1962 && upLevel2User.getCode() == 1964 && upLevel2User.getUpid() == 72) {
//                 users1960.add(self);
//             }
//             //  * 2：1960 -> 1962 -> mick(72)
//             else if (upLevel1User.getCode() == 1962 && upLevel1User.getUpid() == 72) {
//                 users1960.add(self);
//             }
//             //  * 3：1960 -> 1964 -> mick(72)
//             else if (upLevel1User.getCode() == 1964 && upLevel1User.getUpid() == 72) {
//                 users1960.add(self);
//             }
//         }
//
//         // 再筛选出满足3人1000以上消费的
//         List<User> consumedUsers = new ArrayList<>();
//         for (User user1960 : users1960) {
//             List<User> userLowers = userDao.getUserLower(user1960.getId());
//
//             if (CollectionUtils.isEmpty(userLowers)) {
//                 continue;
//             }
//
//             int consumedCount = 0;
//             for (User userLower : userLowers) {
//                 UserLotteryReport userLotteryReport = uLotteryReportDao.get(userLower.getId(), sTime);
//                 if (userLotteryReport == null) {
//                     continue;
//                 }
//
//                 if (userLotteryReport != null && userLotteryReport.getBillingOrder() >= 1000) {
//                     consumedCount++;
//                     if (consumedCount >= 3) {
//                         break;
//                     }
//                 }
//             }
//
//             if (consumedCount >= 3) {
//                 consumedUsers.add(user1960);
//             }
//         }
//
//         users.clear();
//         usersMap.clear();
//
//         return consumedUsers;
//     }
// }
