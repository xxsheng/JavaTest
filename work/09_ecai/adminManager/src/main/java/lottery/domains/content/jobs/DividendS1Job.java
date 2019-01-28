// package lottery.domains.content.jobs;
//
// import admin.domains.jobs.MailJob;
// import javautils.StringUtil;
// import javautils.date.Moment;
// import javautils.math.MathUtil;
// import lottery.domains.content.biz.*;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.UserGameReportDao;
// import lottery.domains.content.dao.UserLotteryReportDao;
// import lottery.domains.content.entity.*;
// import lottery.domains.content.global.Global;
// import lottery.domains.content.vo.bill.UserGameReportVO;
// import lottery.domains.content.vo.bill.UserLotteryReportVO;
// import lottery.domains.content.vo.config.DividendConfigRule;
// import lottery.domains.content.vo.user.UserDividendBillAdapter;
// import lottery.domains.content.vo.user.UserDividendReportVO;
// import lottery.domains.pool.LotteryDataFactory;
// import lottery.web.content.utils.UserCodePointUtil;
// import org.apache.commons.collections.CollectionUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
//
// /**
//  * 每月1号，16号凌晨发放半个周期分红，每月12号清零一次
//  *
//  * 契约分红结算任务
//  * * 1962主管
//  *      1：分红2%-5%（保底2%；亏损30W以上3%；亏损50W以上4%；亏损100W以上5%；）
//  *      2：无最低人数要求
//  *      3：按亏损取比例，累计一个月
//  *      4：平台发放
//  * * 1960招商
//  *      1：分红15%-35%（保底15%；销量150W以上20%；销量300W以上21%；销量450W以上22%；销量750W以上24%；销量1500W以上26%；销量3000W以上28%；销量4500W以上30%；销量7000W以上35%；）
//  *      2：5个最低人数要求
//  *      3：按销量取比例，计算按亏损，累计一个月
//  *      4：平台发放
//  * * 1960平级招商
//  *      1：1%-25%
//  *      2：无最低人数要求
//  *      3：1960招商发放
//  * * 1956直属（含1960招商直属和1960平级招商直属）
//  *      1：1%-15%
//  *      2：无最低人数要求
//  *      3：1960招商或1960平级招商发放
//  * * 直属以下
//  *      1：分红0%-10%
//  *      2：无最低人数要求
//  *      3：谁签的谁发
//  */
// @Component
// public class DividendS1Job {
//     private static final Logger log = LoggerFactory.getLogger(DividendS1Job.class);
//     @Autowired
//     private UserDao userDao;
//     @Autowired
//     private UserService uService;
//     @Autowired
//     private UserDividendService uDividendService;
//     @Autowired
//     private UserDividendBillService uDividendBillService;
//     @Autowired
//     private UserLotteryReportService uLotteryReportService;
//     @Autowired
//     private UserLotteryReportDao uLotteryReportDao;
//     @Autowired
//     private UserGameReportService uGameReportService;
//     @Autowired
//     private UserGameReportDao uGameReportDao;
//     @Autowired
//     private UserCodePointUtil uCodePointUtil;
//     @Autowired
//     private MailJob mailJob;
//
//     @Autowired
//     private LotteryDataFactory dataFactory;
//
//     /**
//      * 调度任务
//      */
//     @Scheduled(cron = "0 15 2 1,16 * *")
//     // @PostConstruct
//     public void schedule() {
//         try {
//             if (!dataFactory.getDividendConfig().isEnable()) {
//                 log.info("62体系分红没有开启，不发放");
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
//
//             log.info("发放62体系分红开始：{}~{}", startDate, endDate);
//
//             // 将未领取的分红置为已过期
//             expireUnCollect();
//
//             // 开始分红
//             settleUp(startDate, endDate);
//
//             log.info("发放62体系分红完成：{}-{}", startDate, endDate);
//         } catch (Exception e) {
//             log.error("分红62体系发放出错", e);
//         }
//     }
//
//     private void expireUnCollect() {
//         uDividendBillService.updateAllExpire();
//     }
//
//     /**
//      * 开始结算
//      */
//     private void settleUp(String sTime, String eTime) {
//         List<UserDividendBillAdapter> bills = new ArrayList<>();
//
//         List<User> zhuGuans = new ArrayList<>();
//         try {
//             // 查找所有主管号
//             zhuGuans = uService.findZhuGuansForS1();
//             if (CollectionUtils.isEmpty(zhuGuans)) {
//                 log.error("没有找到任何62体系主管账号，本次未产生任何分红数据");
//                 return;
//             }
//
//             log.info("发放62体系主管分红开始：{}~{}", sTime, eTime);
//             List<UserDividendBillAdapter> zhuGuansBills = settleZhuGuans(zhuGuans, sTime, eTime);
//             if (CollectionUtils.isNotEmpty(zhuGuansBills)) {
//                 bills.addAll(zhuGuansBills);
//             }
//             log.info("发放62体系主管分红完成：{}~{}", sTime, eTime);
//         } catch (Exception e) {
//             log.error("发放62体系主管分红出错", e);
//             return;
//         }
//
//         try {
//             log.info("发放62体系招商及以下分红开始：{}~{}", sTime, eTime);
//             List<UserDividendBillAdapter> zhaoShangBills = settleUpZhaoShangs(zhuGuans, sTime, eTime);
//             if (CollectionUtils.isNotEmpty(zhaoShangBills)) {
//                 bills.addAll(zhaoShangBills);
//             }
//             log.info("发放62体系招商及以下分红完成：{}~{}", sTime, eTime);
//         } catch (Exception e) {
//             log.error("发放62体系招商及以下分红出错", e);
//         }
//
//         sendMail(bills, sTime, eTime);
//     }
//
//     /**
//      * 发放主管分红，不往下结
//      */
//     private List<UserDividendBillAdapter> settleZhuGuans(List<User> zhuGuans, String sTime, String eTime) {
//         List<UserDividendBillAdapter> bills = new ArrayList<>();
//         for (User zhuGuan : zhuGuans) {
//             // 主管不往下结算,但只结算1960等级,即是平级招商
//             UserDividendBillAdapter billAdapter = settleUpWithUser(zhuGuan, sTime, eTime, false, Global.DIVIDEND_ISSUE_TYPE_PLATFORM);
//             if (billAdapter != null) {
//                 bills.add(billAdapter);
//             }
//         }
//
//         // 保存账单
//         if (CollectionUtils.isNotEmpty(bills)) {
//             for (UserDividendBillAdapter bill : bills) {
//                 processLineBill(bill);
//             }
//         }
//
//         return bills;
//     }
//
//     /**
//      * 发放招商分红，往下结
//      */
//     private List<UserDividendBillAdapter> settleUpZhaoShangs(List<User> zhuGuans, String sTime, String eTime) {
//         // 查找所有总代号
//         List<User> zhaoShangs = uService.findZhaoShangsForS1(zhuGuans);
//         if (CollectionUtils.isEmpty(zhaoShangs)) {
//             log.error("没有找到任何62体系招商号");
//             return null;
//         }
//
//         List<UserDividendBillAdapter> bills = new ArrayList<>();
//         for (User zhaoShang : zhaoShangs) {
//             // 招商需要逐级往下进行结算
//             UserDividendBillAdapter billAdapter = settleUpWithUser(zhaoShang, sTime, eTime, true, Global.DIVIDEND_ISSUE_TYPE_PLATFORM);
//             if (billAdapter != null) {
//                 bills.add(billAdapter);
//             }
//         }
//
//         // 保存账单
//         if (CollectionUtils.isNotEmpty(bills)) {
//             for (UserDividendBillAdapter bill : bills) {
//                 processLineBill(bill);
//             }
//         }
//
//         return bills;
//     }
//
//     private UserDividendBill createBill(int userId, String sTime, String eTime, UserDividend userDividend, int issueType) {
//         UserDividendBill dividendBill = new UserDividendBill();
//         dividendBill.setUserId(userId);
//         dividendBill.setIndicateStartDate(sTime);
//         dividendBill.setIndicateEndDate(eTime);
//         dividendBill.setMinValidUser(userDividend.getMinValidUser());
//         dividendBill.setValidUser(0);
//         dividendBill.setScale(userDividend.getScale());
//         dividendBill.setBillingOrder(0);
//         dividendBill.setUserAmount(0);
//         dividendBill.setIssueType(issueType);
//         dividendBill.setSeries(Global.DAIYU_SERIES_S1);
//         return dividendBill;
//     }
//
//     /**
//      * 检查是否合法
//      */
//     private boolean check(User user, UserDividend uDividend) {
//         if (user.getId() == Global.USER_TOP_ID) {
//             String error = String.format("契约分红错误提醒;用户%s为总账号，但查找到其拥有分红配置，本次不对其进行结算，不影响整体结算；体系：62体系；", user.getUsername());
//             log.error(error);
//             mailJob.addWarning(error);
//             return false;
//         }
//
//         int series = uCodePointUtil.getSeries(user);
//         if (series != Global.DAIYU_SERIES_S1) {
//             String error = String.format("契约分红错误提醒;用户%s不是62体系，本次不对其团队进行结算，不影响整体结算；体系：62体系；", user.getUsername());
//             log.error(error);
//             mailJob.addWarning(error);
//             return false;
//         }
//
//         if (uDividend.getSeries() != Global.DAIYU_SERIES_S1) {
//             String error = String.format("契约分红错误提醒;用户%s的契约分红配置[ID：%s]不是62体系，本次不对其团队进行结算，不影响整体结算；体系：62体系；", user.getUsername(), uDividend.getId());
//             log.error(error);
//             mailJob.addWarning(error);
//             return false;
//         }
//
//         // 是否是主管
//         boolean isZhuGuan = uCodePointUtil.isZhuGuanForS1(user);
//         if (isZhuGuan) {
//             if (uDividend.getFixed() != 0) {
//                 double min = dataFactory.getDividendConfig().getZhuGuanMinScaleForS1();
//                 double max = dataFactory.getDividendConfig().getZhuGuanMaxScaleForS1();
//                 String error = String.format("契约分红错误提醒;用户%s为主管账号，但分红比例%s不是系统配置比例%s~%s，本次不对其团队进行结算；体系：62体系；", user.getUsername(), uDividend.getScale(), min, max);
//                 log.error(error);
//                 mailJob.addWarning(error);
//                 return false;
//             }
//
//             return true;
//         }
//         else {
//             // 如果不是主管，那就必须要有先有上级的配置
//             UserDividend upUserDividend = uDividendService.getByUserId(user.getUpid());
//             if (upUserDividend == null) {
//                 String error = String.format("契约分红错误提醒;用户%s没有找到上级的分红配置，本次不对其团队进行结算；体系：62体系；", user.getUsername());
//                 log.error(error);
//                 mailJob.addWarning(error);
//                 return false;
//             }
//
//             // 是否是招商
//             boolean isZhaoShang = uCodePointUtil.isZhaoShangForS1(user);
//             if (isZhaoShang) {
//                 if (uDividend.getFixed() != 0) {
//                     double min = dataFactory.getDividendConfig().getZhaoShangMinScaleForS1();
//                     double max = dataFactory.getDividendConfig().getZhaoShangMaxScaleForS1();
//                     String error = String.format("契约分红错误提醒;用户%s为招商账号，但分红比例%s不是系统配置比例%s~%s，本次不对其团队进行结算；体系：62体系；", user.getUsername(), uDividend.getScale(), min, max);
//                     log.error(error);
//                     mailJob.addWarning(error);
//                     return false;
//                 }
//
//                 int minValidUser = dataFactory.getDividendConfig().getZhaoShangMinValidUserForS1();
//                 if (uDividend.getMinValidUser() != minValidUser) {
//                     String error = String.format("契约分红错误提醒;用户%s为招商账号，但最低会员人数%s不是系统配置人数%s，本次不对其团队进行结算；体系：62体系；", user.getUsername(), uDividend.getMinValidUser(), minValidUser);
//                     log.error(error);
//                     mailJob.addWarning(error);
//                     return false;
//                 }
//
//                 return true;
//             }
//
//             // 是否是平级招商
//             boolean isPJZhaoShang = uCodePointUtil.isPJZhaoShangForS1(user);
//             if (isPJZhaoShang) {
//                 double min = dataFactory.getDividendConfig().getPjZhaoShangMinScaleForS1();
//                 double max = dataFactory.getDividendConfig().getPjZhaoShangMaxScaleForS1();
//                 if (uDividend.getScale() > max || uDividend.getScale() < min) {
//                     String error = String.format("契约分红错误提醒;用户%s为平级招商账号，但分红比例%s不是系统配置比例%s~%s，本次不对其团队进行结算；体系：62体系；", user.getUsername(), uDividend.getScale(), min, max);
//                     log.error(error);
//                     mailJob.addWarning(error);
//                     return false;
//                 }
//
//                 return true;
//             }
//
//             // 是否是直属
//             boolean isZhiShu = uCodePointUtil.isZhiShuForS1(user);
//             if (isZhiShu) {
//                 double min = dataFactory.getDividendConfig().getZhiShuMinScaleForS1();
//                 double max = dataFactory.getDividendConfig().getZhiShuMaxScaleForS1();
//                 if (uDividend.getScale() > max || uDividend.getScale() < min) {
//                     String error = String.format("契约分红错误提醒;用户%s为直属账号，但分红比例%s不是系统配置比例%s~%s，本次不对其团队进行结算；体系：62体系；", user.getUsername(), uDividend.getScale(), min, max);
//                     log.error(error);
//                     mailJob.addWarning(error);
//                     return false;
//                 }
//
//                 return true;
//             }
//
//             // 直属以下
//             double min = dataFactory.getDividendConfig().getZhiShuBelowMinScaleForS1();
//             double max = dataFactory.getDividendConfig().getZhiShuBelowMaxScaleForS1();
//             if (uDividend.getScale() > max || uDividend.getScale() < min) {
//                 String error = String.format("契约分红错误提醒;用户%s为直属以下号，但分红比例%s不是系统配置比例%s~%s，本次不对其团队进行结算；体系：62体系；", user.getUsername(), uDividend.getScale(), min, max);
//                 log.error(error);
//                 mailJob.addWarning(error);
//                 return false;
//             }
//
//             return true;
//         }
//     }
//
//
//     /**
//      * 结算某一个用户，返回整条线的账单
//      */
//     private UserDividendBillAdapter settleUpWithUser(User user, String sTime, String eTime, boolean settleLowers, int issueType) {
//         // 查找契约配置
//         UserDividend userDividend = uDividendService.getByUserId(user.getId());
//         if (userDividend == null || userDividend.getStatus() != Global.DIVIDEND_VALID) {
//             return null;
//         }
//
//         boolean checked = check(user, userDividend);
//         // 检查不合法，不再往下结算
//         if (!checked) {
//             log.error("用户{}契约分红设置不合法，本次不对该团队进行结算；体系：62体系；", user.getUsername());
//             return null;
//         }
//
//         UserDividendBill upperBill = createBill(user.getId(), sTime, eTime, userDividend, issueType);
//
//         // 查找所有直属下级及他自己的报表(含彩票和第三方游戏)
//         List<UserDividendReportVO> reports = reportTeam(user.getId(), sTime, eTime);
//         if (CollectionUtils.isNotEmpty(reports)) {
//             // 汇总
//             summaryUpReports(reports, user.getId(), sTime, eTime, upperBill);
//         }
//
//         // 浮动分红比例
//         if (userDividend.getFixed() == 0) {
//             DividendConfigRule rule = null;
//             boolean isZhuGuan = uCodePointUtil.isZhuGuanForS1(user);
//             if (isZhuGuan) {
//                 // 主管浮动分红
//                 rule = dataFactory.determineZhuGuanDividendRuleForS1(Math.abs(upperBill.getTotalLoss()));
//             }
//             else {
//                 // 招商浮动分红
//                 rule = dataFactory.determineZhaoShangDividendRuleForS1(upperBill.getBillingOrder());
//                 upperBill.setMinValidUser(dataFactory.getDividendConfig().getZhaoShangMinValidUserForS1());
//             }
//
//             if (rule == null) {
//                 upperBill.setStatus(Global.DIVIDEND_BILL_NOT_REACHED); // 未达标
//                 // upperBill.setCalAmount(0); // 没有达到契约要求
//                 upperBill.setScale(0);
//                 upperBill.setRemarks("销量或人数未达标");
//             }
//             else {
//                 upperBill.setScale(rule.getScale());
//             }
//         }
//         else {
//             upperBill.setScale(userDividend.getScale());
//             upperBill.setMinValidUser(userDividend.getMinValidUser());
//         }
//
//         if (upperBill.getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
//             // 最小销量要求 = 契约最小人数要求 * 有效会员最小消费
//             double minBilling = upperBill.getMinValidUser() * dataFactory.getDividendConfig().getMinBillingOrder();
//
//             double calAmount = 0; // 账单计算金额
//             if (upperBill.getTotalLoss() < 0) { // 亏损必须是小于0，才能有分红
//                 calAmount = MathUtil.multiply(Math.abs(upperBill.getTotalLoss()), upperBill.getScale()); // 账单计算金额
//             }
//             upperBill.setCalAmount(calAmount);
//
//             // 没有达到销量要求
//             if (upperBill.getBillingOrder() < minBilling
//                     || upperBill.getValidUser() < upperBill.getMinValidUser()) {
//                 upperBill.setCalAmount(0);
//                 upperBill.setStatus(Global.DIVIDEND_BILL_NOT_REACHED);
//                 upperBill.setRemarks("销量或人数未达标");
//             }
//         }
//
//
//         // 往下每级结算，并把总共要结的钱一层层减掉
//         double lowerTotalAmount = 0; // 下级共需要发放多少钱
//         List<UserDividendBillAdapter> lowerBills = new ArrayList<>();
//         if (settleLowers) {
//             for (UserDividendReportVO report : reports) {
//                 if (!"总计".equals(report.getName())
//                         && !report.getName().equalsIgnoreCase(user.getUsername())) {
//                     User subUser = userDao.getByUsername(report.getName());
//
//                     // 继续往下结
//                     UserDividendBillAdapter lowerBillAdapter = settleUpWithUser(subUser, sTime, eTime, true, Global.DIVIDEND_ISSUE_TYPE_UPPER);
//                     if (lowerBillAdapter != null) {
//
//                         if (lowerBillAdapter.getUpperBill().getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
//                             lowerTotalAmount = MathUtil.add(lowerTotalAmount, lowerBillAdapter.getUpperBill().getCalAmount());
//                         }
//
//                         lowerBills.add(lowerBillAdapter);
//                     }
//                 }
//             }
//         }
//         upperBill.setLowerTotalAmount(lowerTotalAmount);
//         return new UserDividendBillAdapter(upperBill, lowerBills);
//     }
//
//     /**
//      * 处理一整条线的订单
//      */
//     private void processLineBill(UserDividendBillAdapter uDividendBillAdapter) {
//         UserDividendBill upperBill = uDividendBillAdapter.getUpperBill();
//         List<UserDividendBillAdapter> lowerBills = uDividendBillAdapter.getLowerBills();
//
//         // 不需要向下级发放
//         if (CollectionUtils.isEmpty(lowerBills)) {
//             if (upperBill.getStatus() == Global.DIVIDEND_BILL_NOT_REACHED) {
//                 // 未达标
//                 double userAmount = 0;
//                 upperBill.setUserAmount(userAmount); // 实际金额
//                 upperBill.setRemarks("销量或人数未达标");
//                 saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
//             }
//             else {
//                 double userAmount = upperBill.getCalAmount();
//                 upperBill.setUserAmount(userAmount); // 实际金额
//                 if (userAmount == 0) {
//                     saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
//                 }
//                 else {
//                     saveBill(upperBill, Global.DIVIDEND_BILL_UNAPPROVE);
//                 }
//             }
//
//             return;
//         }
//
//         // 保存下级的账单
//         for (UserDividendBillAdapter lowerBill : lowerBills) {
//             processLineBill(lowerBill);
//         }
//
//         if (upperBill.getCalAmount() == 0 && upperBill.getLowerTotalAmount() == 0) {
//             upperBill.setUserAmount(0); // 实际金额
//             upperBill.setRemarks("销量或人数未达标");
//             saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
//         }
//         else {
//             // 需要向下级发放
//             double calAmount = upperBill.getCalAmount();
//             double lowerTotalAmount = upperBill.getLowerTotalAmount();
//
//             double userAmount = MathUtil.subtract(calAmount, lowerTotalAmount);
//             userAmount = userAmount < 0 ? 0 : userAmount;
//             upperBill.setUserAmount(userAmount); // 实际金额
//             saveBill(upperBill, Global.DIVIDEND_BILL_UNAPPROVE);
//         }
//     }
//
//     private void saveBill(UserDividendBill upperBill, int status) {
//         upperBill.setSettleTime(new Moment().toSimpleTime());
//         upperBill.setStatus(status);
//         uDividendBillService.add(upperBill);
//
//         // User user = userDao.getById(dividendBill.getUserId());
//         //
//         // String time = new Moment().toSimpleTime();
//         // // 如果用户无效，那他的账单也设置为无效，并且不加钱
//         // if (user.getAStatus() != 0 && user.getAStatus() != -1) {
//         //     dividendBill.setStatus(Global.DIVIDEND_BILL_DENIED);
//         //     // dividendBill.setUserAmount(0);
//         //     dividendBill.setRemarks("用户状态为非正常，系统自动拒绝发放");
//         // }
//         // else if (dividendBill.getStatus() == Global.DIVIDEND_BILL_ISSUED) {
//         //     dividendBill.setCollectTime(time);
//         // }
//         //
//         // // 保存数据
//         // dividendBill.setSettleTime(time);
//         // uDividendBillService.add(dividendBill);
//         //
//         // return dividendBill;
//     }
//
//     /**
//      * 统计用户团队报表，含彩票和第三方游戏
//      */
//     private List<UserDividendReportVO> reportTeam(int userId, String sTime, String eTime) {
//         // 先查找彩票报表
//         List<UserLotteryReportVO> lotteryReportVOs = uLotteryReportService.report(userId, sTime, eTime);
//
//         // 再查找游戏报表
//         List<UserGameReportVO> gameReportVOs = uGameReportService.report(userId, sTime, eTime);
//
//         // 取彩票报表和游戏报表的并集
//         HashMap<String, UserDividendReportVO> compareMap = new HashMap<>();
//         for (UserLotteryReportVO lotteryReportVO : lotteryReportVOs) {
//             if (!compareMap.containsKey(lotteryReportVO.getName())) {
//                 compareMap.put(lotteryReportVO.getName(), new UserDividendReportVO(lotteryReportVO, null));
//             }
//         }
//
//         for (UserGameReportVO gameReportVO : gameReportVOs) {
//             if (!compareMap.containsKey(gameReportVO.getName())) {
//                 compareMap.put(gameReportVO.getName(), new UserDividendReportVO(null, gameReportVO));
//             }
//             else {
//                 compareMap.get(gameReportVO.getName()).addGame(gameReportVO);
//             }
//         }
//
//
//         List<UserDividendReportVO> dividendReportVOs = new ArrayList<>(compareMap.values());
//         return dividendReportVOs;
//     }
//
//     /**
//      * 汇总报表
//      */
//     private void summaryUpReports(List<UserDividendReportVO> reports, int userId, String sTime, String eTime, UserDividendBill dividendBill) {
//         double lotteryBillingOrder = 0.d; // 彩票消费
//         double gameBillingOrder = 0.d; // 游戏消费
//         double billingOrder = 0.d; // 总消费
//         // 总销量
//         for (UserDividendReportVO report : reports) {
//             if ("总计".equals(report.getName())) {
//                 lotteryBillingOrder = report.getLotteryBillingOrder();
//                 gameBillingOrder = report.getGameBillingOrder();
//                 billingOrder = MathUtil.add(lotteryBillingOrder, gameBillingOrder);
//                 break;
//             }
//         }
//
//         // NOTE: 彩票活跃人数和游戏活跃人数会重复计算的（如A在彩票有数据，A在游戏也在数据，那么有效人数就是2个）
//
//         double minBillingOrder = dataFactory.getDividendConfig().getMinBillingOrder();
//
//         // 彩票活跃人数
//         List<User> userLowers = userDao.getUserLower(userId);
//         int validUser = 0;
//         for (User lowerUser : userLowers) {
//             double lowerUserBillingOrder = summaryUpLotteryLowerUserBillingOrder(lowerUser.getId(), sTime, eTime);
//             if (lowerUserBillingOrder >= minBillingOrder) {
//                 validUser++;
//             }
//         }
//
//         // 游戏活跃人数
//         for (User lowerUser : userLowers) {
//             double lowerUserBillingOrder = summaryUpGameLowerUserBillingOrder(lowerUser.getId(), sTime, eTime);
//             if (lowerUserBillingOrder >= minBillingOrder) {
//                 validUser++;
//             }
//         }
//
//         // 自己的彩票数据
//         double selfLotteryBilling = summaryUpLotteryLowerUserBillingOrder(userId, sTime, eTime);
//         if (selfLotteryBilling >= minBillingOrder) {
//             validUser++;
//         }
//
//         // 自己的游戏数据
//         double selfGameBilling = summaryUpGameLowerUserBillingOrder(userId, sTime, eTime);
//         if (selfGameBilling >= minBillingOrder) {
//             validUser++;
//         }
//
//         double lotteryThisLoss = calculateLotteryLossByDividendReport(reports); // 本次彩票亏损
//         double lotteryLastLoss = calculateLotteryLastLoss(userId, sTime, eTime); // 上半月彩票亏损
//         double lotteryTotalLoss = lotteryLastLoss > 0 ? lotteryThisLoss + lotteryLastLoss : lotteryThisLoss; // 累计彩票亏损
//
//         double gameThisLoss = calculateGameLossByDividendReport(reports); // 本次游戏亏损
//         double gameLastLoss = calculateGameLastLoss(userId, sTime, eTime); // 上半月游戏亏损
//         double gameTotalLoss = gameLastLoss > 0 ? gameThisLoss + gameLastLoss : gameThisLoss; // 累计游戏亏损
//
//         double thisLoss = MathUtil.add(lotteryThisLoss, gameThisLoss); // 本次总亏损
//         double lastLoss = MathUtil.add(lotteryLastLoss, gameLastLoss); // 上半月总亏损
//         double totalLoss = lastLoss > 0 ? thisLoss + lastLoss : thisLoss; // 累计总亏损
//
//         dividendBill.setLotteryBillingOrder(lotteryBillingOrder); // 彩票消费
//         dividendBill.setLotteryThisLoss(lotteryThisLoss);  // 本次彩票亏损
//         dividendBill.setLotteryLastLoss(lotteryLastLoss); // 上半月彩票亏损
//         dividendBill.setLotteryTotalLoss(lotteryTotalLoss); // 累计彩票亏损
//         dividendBill.setGameBillingOrder(gameBillingOrder); // 游戏消费
//         dividendBill.setGameThisLoss(gameThisLoss); // 本次游戏亏损
//         dividendBill.setGameLastLoss(gameLastLoss); // 上半月游戏亏损
//         dividendBill.setGameTotalLoss(gameTotalLoss); // 累计游戏亏损
//         dividendBill.setBillingOrder(billingOrder); // 总消费
//         dividendBill.setThisLoss(thisLoss); // 本次总亏损
//         dividendBill.setLastLoss(lastLoss); // 上半月总亏损
//         dividendBill.setTotalLoss(totalLoss); // 累计总亏损
//         dividendBill.setValidUser(validUser); // 实际有效会员数
//     }
//
//     /**
//      * 汇总用户时间段内彩票消费
//      */
//     private double summaryUpLotteryLowerUserBillingOrder(int userId, String sTime, String eTime) {
//         List<UserLotteryReport> lowerUserReports = uLotteryReportDao.list(userId, sTime, eTime);
//         if (CollectionUtils.isEmpty(lowerUserReports)) {
//             return 0;
//         }
//
//         double billingOrder = 0;
//         for (UserLotteryReport lowerUserReport : lowerUserReports) {
//             billingOrder = MathUtil.add(billingOrder, lowerUserReport.getBillingOrder());
//         }
//
//         return billingOrder;
//     }
//
//     /**
//      * 汇总用户时间段内游戏消费
//      */
//     private double summaryUpGameLowerUserBillingOrder(int userId, String sTime, String eTime) {
//         List<UserGameReport> lowerUserReports = uGameReportDao.list(userId, sTime, eTime);
//         if (CollectionUtils.isEmpty(lowerUserReports)) {
//             return 0;
//         }
//
//         double billingOrder = 0;
//         for (UserGameReport lowerUserReport : lowerUserReports) {
//             billingOrder = MathUtil.add(billingOrder, lowerUserReport.getBillingOrder());
//         }
//
//         return billingOrder;
//     }
//
//     /**
//      * 计算彩票亏损
//      */
//     private double calculateLotteryLossByDividendReport(List<UserDividendReportVO> reports) {
//         double lotteryLoss = 0;
//         // 总销量
//         for (UserDividendReportVO report : reports) {
//             if ("总计".equals(report.getName())) {
//                 lotteryLoss = report.getLotteryPrize() + report.getLotterySpendReturn() + report.getLotteryProxyReturn() + report.getLotteryActivity() + report.getRechargeFee() - report.getLotteryBillingOrder();
//                 break;
//             }
//         }
//         return lotteryLoss;
//     }
//
//     /**
//      * 计算彩票亏损
//      */
//     private double calculateLotteryLossByLotteryReport(List<UserLotteryReportVO> reports) {
//         double lotteryLoss = 0;
//         // 总销量
//         for (UserLotteryReportVO report : reports) {
//             if ("总计".equals(report.getName())) {
//                 lotteryLoss = report.getPrize() + report.getSpendReturn() + report.getProxyReturn() + report.getActivity() + report.getRechargeFee() - report.getBillingOrder();
//                 break;
//             }
//         }
//         return lotteryLoss;
//     }
//
//     /**
//      * 计算游戏亏损
//      */
//     private double calculateGameLossByDividendReport(List<UserDividendReportVO> reports) {
//         double gameLoss = 0;
//         // 总销量
//         for (UserDividendReportVO report : reports) {
//             if ("总计".equals(report.getName())) {
//                 gameLoss = report.getGamePrize() + report.getGameWaterReturn() + report.getGameProxyReturn() + report.getGameActivity() - report.getGameBillingOrder();
//                 break;
//             }
//         }
//         return gameLoss;
//     }
//
//     /**
//      * 计算游戏亏损
//      */
//     private double calculateGameLossByGameReport(List<UserGameReportVO> reports) {
//         double gameLoss = 0;
//         // 总销量
//         for (UserGameReportVO report : reports) {
//             if ("总计".equals(report.getName())) {
//                 gameLoss = report.getPrize() + report.getWaterReturn() + report.getProxyReturn() + report.getActivity() - report.getBillingOrder();
//                 break;
//             }
//         }
//         return gameLoss;
//     }
//
//     /**
//      * 彩票上半月亏损
//      */
//     private double calculateLotteryLastLoss(int userId, String sTime, String eTime) {
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
//                 List<UserLotteryReportVO> lastReports = uLotteryReportService.report(userId, lastStartDate, lastEndDate);
//                 double lastLoss = calculateLotteryLossByLotteryReport(lastReports);
//                 return lastLoss;
//             }
//         }
//
//         return 0;
//     }
//
//     /**
//      * 游戏上半月亏损
//      */
//     private double calculateGameLastLoss(int userId, String sTime, String eTime) {
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
//                 double lastLoss = calculateGameLossByGameReport(lastReports);
//                 return lastLoss;
//             }
//         }
//
//         return 0;
//     }
//
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
//     private void sendMail(List<UserDividendBillAdapter> bills, String sTime, String eTime) {
//         double platformTotalLoss = 0; // 平级总亏损
//         double platformTotalAmount = 0; // 平级总发放
//
//         if (CollectionUtils.isNotEmpty(bills)) {
//
//             List<UserDividendBill> allBills = new ArrayList<>();
//             getAllBills(bills, allBills);
//
//             for (UserDividendBill bill : allBills) {
//                 if (bill.getIssueType() != Global.DIVIDEND_ISSUE_TYPE_PLATFORM) continue;
//
//                 if (bill.getStatus() != Global.DIVIDEND_BILL_ISSUED
//                         && bill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
//                         && bill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
//                         && bill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT) {
//                     continue;
//                 }
//
//                 platformTotalAmount = MathUtil.add(platformTotalAmount, bill.getCalAmount());
//
//                 if (bill.getTotalLoss() >= 0) continue;
//
//                 platformTotalLoss = MathUtil.add(platformTotalLoss, bill.getTotalLoss());
//             }
//         }
//
//         double totalBillingOrder = 0; // 总销量
//         double totalLoss = 0; // 总亏损
//         List<UserLotteryReportVO> lotteryReportVOs = uLotteryReportService.report(sTime, eTime);
//         if (CollectionUtils.isNotEmpty(lotteryReportVOs)) {
//             for (UserLotteryReportVO report : lotteryReportVOs) {
//                 if ("总计".equals(report.getName())) {
//                     totalBillingOrder += report.getBillingOrder();
//                     totalLoss += calculateLotteryLossByLotteryReport(lotteryReportVOs);
//                     break;
//                 }
//             }
//         }
//
//         List<UserGameReportVO> gameReportVOs = uGameReportService.report(sTime, eTime);
//         if (CollectionUtils.isNotEmpty(gameReportVOs)) {
//             for (UserGameReportVO report : gameReportVOs) {
//                 if ("总计".equals(report.getName())) {
//                     totalBillingOrder += report.getBillingOrder();
//                     totalLoss += calculateGameLossByGameReport(gameReportVOs);
//                     break;
//                 }
//             }
//         }
//
//         mailJob.sendDividend(sTime, eTime, totalBillingOrder, totalLoss, platformTotalLoss, platformTotalAmount, Global.DAIYU_SERIES_S1);
//     }
//
//     private List<UserDividendBill> getAllBills (List<UserDividendBillAdapter> bills, List<UserDividendBill> container) {
//         if (CollectionUtils.isEmpty(bills)) {
//             return container;
//         }
//
//         for (UserDividendBillAdapter bill : bills) {
//             container.add(bill.getUpperBill());
//
//             getAllBills(bill.getLowerBills(), container);
//         }
//
//         return container;
//     }
// }