// package lottery.domains.content.jobs;
//
// import admin.domains.jobs.MailJob;
// import javautils.StringUtil;
// import javautils.date.Moment;
// import javautils.math.MathUtil;
// import lottery.domains.content.biz.SysConfigService;
// import lottery.domains.content.biz.UserBillService;
// import lottery.domains.content.biz.UserGameReportService;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.UserGameDividendBillDao;
// import lottery.domains.content.dao.UserGameReportDao;
// import lottery.domains.content.entity.SysConfig;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.UserGameDividendBill;
// import lottery.domains.content.global.Global;
// import lottery.domains.content.vo.bill.UserGameReportVO;
// import lottery.domains.content.vo.config.GameDividendConfigRule;
// import lottery.domains.pool.LotteryDataFactory;
// import org.apache.commons.collections.CollectionUtils;
// import org.hibernate.criterion.Criterion;
// import org.hibernate.criterion.Restrictions;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
//
// /**
//  * 老虎机真人体育分红结算任务
//  */
// @Component
// public class GameDividendJob {
//     private static final Logger log = LoggerFactory.getLogger(GameDividendJob.class);
//     @Autowired
//     private UserDao userDao;
//     @Autowired
//     private UserGameDividendBillDao uGameDividendBillDao;
//     @Autowired
//     private UserBillService uBillService;
//     @Autowired
//     private UserGameReportService uGameReportService;
//     @Autowired
//     private UserGameReportDao uGameReportDao;
//     @Autowired
//     private SysConfigService configService;
//
//     @Autowired
//     private MailJob mailJob;
//
//
//     @Autowired
//     private LotteryDataFactory dataFactory;
//
//     /**
//      * 调度任务
//      */
//     @Scheduled(cron = "0 30 2 1,16 * *")
//     // @Scheduled(cron = "0 16 5 1,16 * *")
//     // @PostConstruct
//     public void schedule() {
//         try {
//             if (!dataFactory.getGameDividendConfig().isEnabled()) {
//                 log.debug("老虎机真人体育分红没有开启，不发放");
//                 return;
//             }
//
//             // 获取结算周期
//             String startDate = getStartDate();
//             int startDay = Integer.valueOf(startDate.substring(8));
//             if (startDay != 1 && startDay != 16) {
//                 return;
//             }
//
//             String endDate = getEndDate();
//             String lastDate = getLastDate();
//             if (lastDate.equals(endDate)) {
//                 return;
//             }
//
//             log.debug("{}~{}老虎机真人体育分红发放开始", startDate, endDate);
//
//             // 开始分红
//             settleUp(startDate, endDate);
//
//             log.debug("{}-{}老虎机真人体育分红发放完成", startDate, endDate);
//         } catch (Exception e) {
//             log.error("老虎机真人体育分红发放出错", e);
//         }
//     }
//
//     /**
//      * 开始结算
//      */
//     public void settleUp(String sTime, String eTime) {
//         // 查找所有总代号
//         List<User> zongDais = findZongDais();
//         if (CollectionUtils.isEmpty(zongDais)) {
//             return;
//         }
//
//         // 结算所有总代号
//         List<UserGameDividendBill> bills = new ArrayList<>();
//         for (User zongDai : zongDais) {
//             UserGameDividendBill bill = settleUpWithUser(zongDai, sTime, eTime);
//             if (bill != null) {
//                 bills.add(bill);
//             }
//         }
//
//         // 保存结果
//         if (CollectionUtils.isNotEmpty(bills)) {
//             for (UserGameDividendBill bill : bills) {
//                 saveResult(bill);
//             }
//         }
//
//         sendMail(bills, sTime, eTime);
//     }
//
//     private UserGameDividendBill createBill(int userId, String sTime, String eTime) {
//         UserGameDividendBill dividendBill = new UserGameDividendBill();
//         dividendBill.setUserId(userId);
//         dividendBill.setIndicateStartDate(sTime);
//         dividendBill.setIndicateEndDate(eTime);
//         dividendBill.setScale(0);
//         dividendBill.setBillingOrder(0);
//         dividendBill.setUserAmount(0);
//         return dividendBill;
//     }
//
//     /**
//      * 结算某一个用户
//      */
//     public UserGameDividendBill settleUpWithUser(User user, String sTime, String eTime) {
//         UserGameDividendBill bill = createBill(user.getId(), sTime, eTime);
//
//         // 查找所有下级及他自己的报表
//         List<UserGameReportVO> reports = uGameReportService.report(user.getId(), sTime, eTime);
//         if (reports == null || reports.size() == 1) {
//             return null;
//         }
//
//         // 汇总
//         UserGameDividendBill summaryBill = summaryUpReports(reports);
//         bill.setBillingOrder(summaryBill.getBillingOrder()); // 销量
//         bill.setThisLoss(summaryBill.getThisLoss()); // 本次亏损
//         bill.setScale(summaryBill.getScale()); // 分红比例
//
//         if (summaryBill.getBillingOrder() <= 0) {
//             return null;
//         }
//
//         if (bill.getThisLoss() >= 0) {
//             bill.setStatus(Global.GAME_DIVIDEND_BILL_NOT_REACHED);
//             return bill;
//         }
//
//         // 分红金额计算
//         double thisLoss = bill.getThisLoss();
//         double lastLoss = calculateLastLoss(user.getId(), sTime, eTime); // 上半月亏损
//         double totalLoss = lastLoss > 0 ? thisLoss + lastLoss : thisLoss; // 累计亏损
//         bill.setLastLoss(lastLoss);
//         bill.setTotalLoss(totalLoss);
//         double userAmount = 0; // 分红金额
//         if (totalLoss < 0) {
//             userAmount = MathUtil.multiply(Math.abs(totalLoss), bill.getScale()); // 分红金额
//         }
//         bill.setUserAmount(userAmount);
//
//         if (bill.getUserAmount() <= 0) {
//             bill.setStatus(Global.GAME_DIVIDEND_BILL_NOT_REACHED);
//             return bill;
//         }
//
//         bill.setStatus(Global.GAME_DIVIDEND_BILL_UNAPPROVE);
//         return bill;
//     }
//
//     /**
//      * 计算亏损
//      */
//     private double calculateLoss(List<UserGameReportVO> reports) {
//         UserGameReportVO totalBean = reports.get(0); // 下半月亏损总计
//
//         double loss = totalBean.getPrize() + totalBean.getWaterReturn() + totalBean.getProxyReturn() + totalBean.getActivity() - totalBean.getBillingOrder();
//         return loss;
//     }
//
//     /**
//      * 下半月累计亏损
//      */
//     private double calculateLastLoss(int userId, String sTime, String eTime) {
//         String currDate = sTime;
//         if (StringUtil.isNotNull(currDate)) {
//
//             // 确定是否是下半月
//             int currDay = Integer.valueOf(currDate.substring(8));
//             if (currDay == 16) { // 今天16号，那今天就是下半月
//                 String lastStartDate = currDate.substring(0, 8) + "01";
//                 String lastEndDate = currDate;
//
//                 // // 上半月分红情况
//                 List<UserGameReportVO> lastReports = uGameReportService.report(userId, lastStartDate, lastEndDate);
//                 double lastLoss = calculateLoss(lastReports);
//                 return lastLoss;
//             }
//         }
//
//         return 0;
//     }
//
//     private UserGameDividendBill saveResult(UserGameDividendBill dividendBill) {
//         User user = userDao.getById(dividendBill.getUserId());
//
//         String time = new Moment().toSimpleTime();
//         // 如果用户无效，那他的账单也设置为无效，并且不加钱
//         if (user.getAStatus() != 0 && user.getAStatus() != -1) {
//             dividendBill.setStatus(Global.GAME_DIVIDEND_BILL_DENIED);
//             // dividendBill.setUserAmount(0);
//             dividendBill.setRemarks("用户状态为非正常，系统自动拒绝发放");
//         }
//
//         // 保存数据
//         dividendBill.setSettleTime(time);
//         uGameDividendBillDao.add(dividendBill);
//
//         return dividendBill;
//     }
//
//     private UserGameDividendBill summaryUpReports(List<UserGameReportVO> reports) {
//         double billingOrder = 0d;
//         double loss = 0d;
//         // 总销量
//         for (UserGameReportVO report : reports) {
//             if ("总计".equals(report.getName())) {
//                 billingOrder = report.getBillingOrder();
//                 loss = report.getPrize() + report.getWaterReturn() + report.getProxyReturn() + report.getActivity() - report.getBillingOrder();
//                 break;
//             }
//         }
//
//         UserGameDividendBill bill = new UserGameDividendBill();
//         bill.setBillingOrder(billingOrder);
//         bill.setThisLoss(loss);
//
//         // 分红规则
//         GameDividendConfigRule rule = dataFactory.determineGameDividendRule(Math.abs(loss));
//         if (rule != null) {
//             bill.setScale(rule.getScale());
//         }
//
//         return bill;
//     }
//
//     /**
//      * 查找所有主管号
//      */
//     private List<User> findZhuGuans() {
//         List<Criterion> criterions = new ArrayList<>();
//         criterions.add(Restrictions.gt("id", 0));
//         criterions.add(Restrictions.eq("code", dataFactory.getCodeConfig().getSysCode()));
//         criterions.add(Restrictions.eq("upid", Global.USER_TOP_ID));// 上级是mick
//         criterions.add(Restrictions.in("type", Arrays.asList(Global.USER_TYPE_PROXY, Global.USER_TYPE_PLAYER)));// 类型必须是用户或代理
//         criterions.add(Restrictions.not(Restrictions.in("AStatus", Arrays.asList(-2, -3))));// 非永久冻结及禁用状态
//
//         List<User> users = userDao.list(criterions, null);
//
//         return users;
//     }
//
//     /**
//      * 查找所有直属号
//      */
//     private List<User> findZhiShus() {
//
//         // 查找所有主管号
//         List<User> zhuGuans = findZhuGuans();
//         if (CollectionUtils.isEmpty(zhuGuans)) {
//             return null;
//         }
//
//         List<Integer> zhuGuanIds = new ArrayList<>();
//         for (User zhuGuan : zhuGuans) {
//             zhuGuanIds.add(zhuGuan.getId());
//         }
//
//         // 再查找所有直属号
//         List<Criterion> criterions = new ArrayList<>();
//
//         criterions.add(Restrictions.gt("id", 0));
//         criterions.add(Restrictions.eq("code", dataFactory.getCodeConfig().getSysCode()));
//         criterions.add(Restrictions.in("upid", zhuGuanIds));// 上级是主管
//         criterions.add(Restrictions.in("type", Arrays.asList(Global.USER_TYPE_PROXY, Global.USER_TYPE_PLAYER)));// 类型必须是用户或代理
//         criterions.add(Restrictions.not(Restrictions.in("AStatus", Arrays.asList(-2, -3))));// 非永久冻结及禁用状态
//
//         List<User> users = userDao.list(criterions, null);
//
//         return users;
//     }
//
//     /**
//      * 查找所有总代号
//      */
//     private List<User> findZongDais() {
//
//         // 查找所有直属号
//         List<User> zhiShus = findZhiShus();
//         if (CollectionUtils.isEmpty(zhiShus)) {
//             return null;
//         }
//
//         List<Integer> zhiShuIds = new ArrayList<>();
//         for (User zhiShu : zhiShus) {
//             zhiShuIds.add(zhiShu.getId());
//         }
//
//         // 再查找所有总代号
//         List<Criterion> criterions = new ArrayList<>();
//
//         criterions.add(Restrictions.gt("id", 0));
//         criterions.add(Restrictions.eq("code", dataFactory.getCodeConfig().getSysCode()));
//         criterions.add(Restrictions.in("upid", zhiShuIds));// 上级是直属
//         criterions.add(Restrictions.in("type", Arrays.asList(Integer.valueOf(Global.USER_TYPE_PROXY), Integer.valueOf(Global.USER_TYPE_PLAYER))));// 类型必须是用户或代理
//         criterions.add(Restrictions.not(Restrictions.in("AStatus", Arrays.asList(-2, -3))));// 非永久冻结及禁用状态
//
//         List<User> users = userDao.list(criterions, null);
//
//         return users;
//     }
//
//     private String getStartDate() {
//         Moment moment = new Moment().add(-1, "days");
//         int day = moment.day();
//
//         if (day <= 15) {
//             moment = moment.day(1);
//         }
//         else {
//             moment = moment.day(16);
//         }
//
//         return moment.toSimpleDate();
//     }
//
//     private String getEndDate() {
//         Moment moment = new Moment().add(-1, "days");
//         int day = moment.day();
//
//         if (day <= 15) {
//             moment = moment.day(16);
//         }
//         else {
//             moment = moment.add(1, "months");
//             moment = moment.day(1);
//         }
//
//         return moment.toSimpleDate();
//     }
//
//     private String getLastDate() {
//         SysConfig lastDividendDate = configService.get("DIVIDEND", "LAST_DATE");
//         if (lastDividendDate != null && StringUtil.isNotNull(lastDividendDate.getValue())) {
//             // 确定是否是上半月
//             return lastDividendDate.getValue();
//         }
//
//         return null;
//     }
//
//     private void sendMail(List<UserGameDividendBill> bills, String sTime, String eTime) {
//         double totalBillingOrder = 0;
//         double totalLoss = 0;
//         double totalAmount = 0;
//
//         if (CollectionUtils.isNotEmpty(bills)) {
//             for (UserGameDividendBill bill : bills) {
//                 if (bill.getUserAmount() > 0) {
//                     totalAmount = MathUtil.add(totalAmount, bill.getUserAmount());
//                 }
//             }
//         }
//
//         List<UserGameReportVO> reports = uGameReportService.report(sTime, eTime);
//         if (CollectionUtils.isNotEmpty(reports)) {
//             for (UserGameReportVO report : reports) {
//                 if ("总计".equals(report.getName())) {
//                     totalBillingOrder = report.getBillingOrder();
//                     totalLoss = calculateLoss(reports);
//                     break;
//                 }
//             }
//         }
//
//         mailJob.sendGameDividend(sTime, eTime, totalBillingOrder, totalLoss, totalAmount);
//     }
// }