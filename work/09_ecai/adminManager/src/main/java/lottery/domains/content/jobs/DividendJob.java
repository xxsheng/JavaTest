//package lottery.domains.content.jobs;
//
//import admin.domains.jobs.MailJob;
//import javautils.StringUtil;
//import javautils.date.Moment;
//import javautils.math.MathUtil;
//import lottery.domains.content.biz.*;
//import lottery.domains.content.dao.UserDao;
//import lottery.domains.content.dao.UserGameReportDao;
//import lottery.domains.content.dao.UserLotteryReportDao;
//import lottery.domains.content.entity.*;
//import lottery.domains.content.global.Global;
//import lottery.domains.content.vo.bill.UserGameReportVO;
//import lottery.domains.content.vo.bill.UserLotteryReportVO;
//import lottery.domains.content.vo.config.DividendConfigRule;
//import lottery.domains.content.vo.user.UserDividendBillAdapter;
//import lottery.domains.pool.LotteryDataFactory;
//import lottery.web.content.utils.UserCodePointUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 每月1号，16号凌晨发放半个周期分红，每次发放前清零待领取、部分领取、余额不足
// */
//@Component
//public class DividendJob {
//    private static final Logger log = LoggerFactory.getLogger(DividendJob.class);
//    @Autowired
//    private UserDao userDao;
//    @Autowired
//    private UserService uService;
//    @Autowired
//    private UserDividendService uDividendService;
//    @Autowired
//    private UserDividendBillService uDividendBillService;
//    @Autowired
//    private UserLotteryReportService uLotteryReportService;
//    @Autowired
//    private UserLotteryReportDao uLotteryReportDao;
//    @Autowired
//    private UserGameReportService uGameReportService;
//    @Autowired
//    private UserGameReportDao uGameReportDao;
//    @Autowired
//    private UserCodePointUtil uCodePointUtil;
//    @Autowired
//    private MailJob mailJob;
//
//    @Autowired
//    private LotteryDataFactory dataFactory;
//
//    /**
//     * 调度任务
//     */
////    @Scheduled(cron = "0 10 2 1,16 * *")
//    // @PostConstruct
//    public void schedule() {
//        try {
//            if (!dataFactory.getDividendConfig().isEnable()) {
//                log.info("分红没有开启，不发放");
//                return;
//            }
//
//            // 获取结算周期
//            String startDate = getStartDate();
//            int startDay = Integer.valueOf(startDate.substring(8));
//            if (startDay != 1 && startDay != 16) {
//                return;
//            }
//
//            String endDate = getEndDate();
//
//            log.info("发放分红开始：{}~{}", startDate, endDate);
//
//            // 将待领取、部分领取、余额不足的分红置为已过期
//            updateAllExpire();
//
//            // 开始分红
//            settleUp(startDate, endDate);
//
//            log.info("发放分红完成：{}-{}", startDate, endDate);
//        } catch (Exception e) {
//            log.error("分红发放出错", e);
//        }
//    }
//
//    private void updateAllExpire() {
//        uDividendBillService.updateAllExpire();
//    }
//
//    /**
//     * 开始结算
//     */
//    public void settleUp(String sTime, String eTime) {
//        // 查找所有内部招商号
//        List<User> neibuZhaoShangs = uService.findNeibuZhaoShang();
//        if (CollectionUtils.isEmpty(neibuZhaoShangs)) {
//            log.error("没有找到任何内部招商账号，本次未产生任何分红数据");
//            return;
//        }
//
//        // 查找所有招商号
//        List<User> zhaoShangs = uService.findZhaoShang(neibuZhaoShangs);
//        if (CollectionUtils.isEmpty(zhaoShangs)) {
//            log.error("没有找到任何招商账号，本次未产生任何分红数据");
//            return;
//        }
//
//        List<UserDividendBillAdapter> bills = new ArrayList<>();
//
//        try {
//            log.info("发放招商及以下分红开始：{}~{}", sTime, eTime);
//            List<UserDividendBillAdapter> zhaoShangBills = settleUpZhaoShangs(zhaoShangs, sTime, eTime);
//            if (CollectionUtils.isNotEmpty(zhaoShangBills)) {
//                bills.addAll(zhaoShangBills);
//            }
//            log.info("发放招商及以下分红完成：{}~{}", sTime, eTime);
//        } catch (Exception e) {
//            log.error("发放招商及以下分红出错", e);
//        }
//
//        sendMail(bills, sTime, eTime);
//    }
//
//    /**
//     * 发放招商分红，往下结
//     */
//    private List<UserDividendBillAdapter> settleUpZhaoShangs(List<User> zhaoShangs, String sTime, String eTime) {
//        List<UserDividendBillAdapter> bills = new ArrayList<>();
//        for (User zhaoShang : zhaoShangs) {
//            // 招商需要逐级往下进行结算
//            UserDividendBillAdapter billAdapter = settleUpWithUser(zhaoShang, sTime, eTime, true, Global.DIVIDEND_ISSUE_TYPE_PLATFORM);
//            if (billAdapter != null) {
//                bills.add(billAdapter);
//            }
//        }
//
//        // 保存账单
//        if (CollectionUtils.isNotEmpty(bills)) {
//            for (UserDividendBillAdapter bill : bills) {
//                processLineBill(bill);
//            }
//        }
//
//        return bills;
//    }
//
//    private UserDividendBill createBill(int userId, String sTime, String eTime, UserDividend userDividend, int issueType) {
//        UserDividendBill dividendBill = new UserDividendBill();
//        dividendBill.setUserId(userId);
//        dividendBill.setIndicateStartDate(sTime);
//        dividendBill.setIndicateEndDate(eTime);
//        dividendBill.setMinValidUser(userDividend.getMinValidUser());
//        dividendBill.setValidUser(0);
//        dividendBill.setScale(userDividend.getScale());
//        dividendBill.setBillingOrder(0);
//        dividendBill.setUserAmount(0);
//        dividendBill.setIssueType(issueType);
//        return dividendBill;
//    }
//
//    /**
//     * 检查是否合法
//     */
//    private boolean check(User user, UserDividend uDividend) {
//        if (user.getId() == Global.USER_TOP_ID) {
//            String error = String.format("契约分红错误提醒;用户%s为总账号，但查找到其拥有分红配置，本次不对其进行结算，不影响整体结算；", user.getUsername());
//            log.error(error);
//            mailJob.addWarning(error);
//            return false;
//        }
//
//        // 是否是内部招商
//        boolean isNeibuZhaoShang = uCodePointUtil.isLevel1Proxy(user);
//        if (isNeibuZhaoShang) {
//            String error = String.format("契约分红错误提醒;用户%s为内部招商，但查找到其拥有分红配置，本次不对其进行结算；", user.getUsername());
//            log.error(error);
//            mailJob.addWarning(error);
//            return false;
//        }
//
//        boolean isZhaoShang = uCodePointUtil.isLevel2Proxy(user);
//        if (isZhaoShang) {
//            return true;
//        }
//
//        // 必须要有先有上级的配置
//        UserDividend upUserDividend = uDividendService.getByUserId(user.getUpid());
//        if (upUserDividend == null) {
//            String error = String.format("契约分红错误提醒;用户%s没有找到上级的分红配置，本次不对其团队进行结算；", user.getUsername());
//            log.error(error);
//            mailJob.addWarning(error);
//            return false;
//        }
//
//        if (uCodePointUtil.isLevel3Proxy(user)) {
//            double min = dataFactory.getDividendConfig().getZhaoShangBelowMinScale();
//            double max = dataFactory.getDividendConfig().getZhaoShangBelowMaxScale();
//            if (uDividend.getScale() > max || uDividend.getScale() < min) {
//                String error = String.format("契约分红错误提醒;用户%s为直属号，但分红比例%s不是有效比例%s~%s，本次不对其团队进行结算；", user.getUsername(), uDividend.getScale(), min, max);
//                log.error(error);
//                mailJob.addWarning(error);
//                return false;
//            }
//        }
//        else {
//            double min = dataFactory.getDividendConfig().getZhaoShangBelowMinScale();
//            double max = upUserDividend.getScale();
//            if (uDividend.getScale() > max || uDividend.getScale() < min) {
//                String error = String.format("契约分红错误提醒;用户%s为直属以下号，但分红比例%s不是有效比例%s~%s，本次不对其团队进行结算；", user.getUsername(), uDividend.getScale(), min, max);
//                log.error(error);
//                mailJob.addWarning(error);
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//
//    /**
//     * 结算某一个用户，返回整条线的账单
//     */
//    private UserDividendBillAdapter settleUpWithUser(User user, String sTime, String eTime, boolean settleLowers, int issueType) {
//        // 查找契约配置
//        UserDividend userDividend = uDividendService.getByUserId(user.getId());
//        if (userDividend == null || userDividend.getStatus() != Global.DIVIDEND_VALID) {
//            return null;
//        }
//
//        boolean checked = check(user, userDividend);
//        // 检查不合法，不再往下结算
//        if (!checked) {
//            return null;
//        }
//
//        UserDividendBill upperBill = createBill(user.getId(), sTime, eTime, userDividend, issueType);
//
//        // 查找所有直属下级及他自己的报表
//        List<UserLotteryReportVO> reports = uLotteryReportService.report(user.getId(), sTime, eTime);
//        if (CollectionUtils.isNotEmpty(reports)) {
//            // 汇总销量
//            summaryUpReports(reports, user.getId(), sTime, eTime, upperBill);
//        }
//
//        // 浮动分红比例
//        if (uCodePointUtil.isLevel2Proxy(user) && userDividend.getFixed() == Global.DAIYU_FIXED_FLOAT) {
//
//            // 招商浮动分红
//            DividendConfigRule rule;
//            if (user.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
//                rule = dataFactory.determineCJZhaoShangDividendRule(upperBill.getDailyBillingOrder());
//            }
//            else {
//                rule = dataFactory.determineZhaoShangDividendRule(upperBill.getDailyBillingOrder());
//            }
//
//            upperBill.setMinValidUser(dataFactory.getDividendConfig().getZhaoShangMinValidUser());
//
//            if (rule == null) {
//                upperBill.setStatus(Global.DIVIDEND_BILL_NOT_REACHED); // 未达标
//                // upperBill.setCalAmount(0); // 没有达到契约要求
//                upperBill.setScale(0);
//                upperBill.setRemarks("销量或人数未达标");
//            }
//            else {
//                upperBill.setScale(rule.getScale());
//            }
//        }
//        else {
//            upperBill.setScale(userDividend.getScale());
//            upperBill.setMinValidUser(userDividend.getMinValidUser());
//        }
//
//        if (upperBill.getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
//            // 最小销量要求 = 契约最小人数要求 * 有效会员最小消费
//            double minBilling = upperBill.getMinValidUser() * dataFactory.getDividendConfig().getMinBillingOrder();
//
//            double calAmount = 0; // 账单计算金额
//            if (upperBill.getTotalLoss() < 0) { // 亏损必须是小于0，才能有分红
//                calAmount = MathUtil.multiply(Math.abs(upperBill.getTotalLoss()), upperBill.getScale()); // 账单计算金额
//            }
//            upperBill.setCalAmount(calAmount);
//
//            // 没有达到销量要求
//            if (upperBill.getBillingOrder() < minBilling
//                    || upperBill.getValidUser() < upperBill.getMinValidUser()) {
//                upperBill.setCalAmount(0);
//                upperBill.setStatus(Global.DIVIDEND_BILL_NOT_REACHED);
//                upperBill.setRemarks("销量或人数未达标");
//            }
//        }
//
//
//        // 往下每级结算，并把总共要结的钱一层层减掉
//        double lowerTotalAmount = 0; // 下级共需要发放多少钱
//        List<UserDividendBillAdapter> lowerBills = new ArrayList<>();
//        if (settleLowers) {
//            for (UserLotteryReportVO report : reports) {
//                if (!"总计".equals(report.getName())
//                        && !report.getName().equalsIgnoreCase(user.getUsername())) {
//                    User subUser = userDao.getByUsername(report.getName());
//
//                    // 继续往下结
//                    UserDividendBillAdapter lowerBillAdapter = settleUpWithUser(subUser, sTime, eTime, true, Global.DIVIDEND_ISSUE_TYPE_UPPER);
//                    if (lowerBillAdapter != null) {
//
//                        if (lowerBillAdapter.getUpperBill().getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
//                            lowerTotalAmount = MathUtil.add(lowerTotalAmount, lowerBillAdapter.getUpperBill().getCalAmount());
//                        }
//
//                        lowerBills.add(lowerBillAdapter);
//                    }
//                }
//            }
//        }
//        upperBill.setLowerTotalAmount(lowerTotalAmount);
//        return new UserDividendBillAdapter(upperBill, lowerBills);
//    }
//
//    /**
//     * 处理一整条线的订单
//     */
//    private void processLineBill(UserDividendBillAdapter uDividendBillAdapter) {
//        UserDividendBill upperBill = uDividendBillAdapter.getUpperBill();
//        List<UserDividendBillAdapter> lowerBills = uDividendBillAdapter.getLowerBills();
//
//        // 不需要向下级发放
//        if (CollectionUtils.isEmpty(lowerBills)) {
//            if (upperBill.getStatus() == Global.DIVIDEND_BILL_NOT_REACHED) {
//                // 未达标
//                double userAmount = 0;
//                upperBill.setUserAmount(userAmount); // 实际金额
//                upperBill.setRemarks("销量或人数未达标");
//                saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
//            }
//            else {
//                double userAmount = upperBill.getCalAmount();
//                upperBill.setUserAmount(userAmount); // 实际金额
//                if (userAmount == 0) {
//                    saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
//                }
//                else {
//                    saveBill(upperBill, Global.DIVIDEND_BILL_UNAPPROVE);
//                }
//            }
//
//            return;
//        }
//
//        // 保存下级的账单
//        for (UserDividendBillAdapter lowerBill : lowerBills) {
//            processLineBill(lowerBill);
//        }
//
//        if (upperBill.getCalAmount() == 0 && upperBill.getLowerTotalAmount() == 0) {
//            upperBill.setUserAmount(0); // 实际金额
//            upperBill.setRemarks("销量或人数未达标");
//            saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
//        }
//        else {
//            // 需要向下级发放
//            double calAmount = upperBill.getCalAmount();
//            double lowerTotalAmount = upperBill.getLowerTotalAmount();
//
//            double userAmount = MathUtil.subtract(calAmount, lowerTotalAmount);
//            userAmount = userAmount < 0 ? 0 : userAmount;
//            upperBill.setUserAmount(userAmount); // 实际金额
//            saveBill(upperBill, Global.DIVIDEND_BILL_UNAPPROVE);
//        }
//    }
//
//    private void saveBill(UserDividendBill upperBill, int status) {
//        upperBill.setSettleTime(new Moment().toSimpleTime());
//        upperBill.setStatus(status);
//        uDividendBillService.add(upperBill);
//
//        // User user = userDao.getById(dividendBill.getUserId());
//        //
//        // String time = new Moment().toSimpleTime();
//        // // 如果用户无效，那他的账单也设置为无效，并且不加钱
//        // if (user.getAStatus() != 0 && user.getAStatus() != -1) {
//        //     dividendBill.setStatus(Global.DIVIDEND_BILL_DENIED);
//        //     // dividendBill.setUserAmount(0);
//        //     dividendBill.setRemarks("用户状态为非正常，系统自动拒绝发放");
//        // }
//        // else if (dividendBill.getStatus() == Global.DIVIDEND_BILL_ISSUED) {
//        //     dividendBill.setCollectTime(time);
//        // }
//        //
//        // // 保存数据
//        // dividendBill.setSettleTime(time);
//        // uDividendBillService.add(dividendBill);
//        //
//        // return dividendBill;
//    }
//
//
//    /**
//     * 汇总报表
//     */
//    private void summaryUpReports(List<UserLotteryReportVO> reports, int userId, String sTime, String eTime, UserDividendBill dividendBill) {
//        double billingOrder = 0.d; // 总消费
//        // 总销量
//        for (UserLotteryReportVO report : reports) {
//            if ("总计".equals(report.getName())) {
//                billingOrder = report.getBillingOrder();
//                break;
//            }
//        }
//
//        // 日均销量
//        double dailyBillingOrder = 0;
//        if (billingOrder > 0) {
//            dailyBillingOrder = MathUtil.divide(billingOrder, 15.0, 4);
//        }
//
//        double minBillingOrder = dataFactory.getDividendConfig().getMinBillingOrder();
//
//        // 彩票活跃人数
//        List<User> userLowers = userDao.getUserLower(userId);
//        int validUser = 0;
//        for (User lowerUser : userLowers) {
//            double lowerUserBillingOrder = summaryUpLotteryLowerUserBillingOrder(lowerUser.getId(), sTime, eTime);
//            if (lowerUserBillingOrder >= minBillingOrder) {
//                validUser++;
//            }
//        }
//
//        // 自己的彩票数据
//        double selfLotteryBilling = summaryUpLotteryLowerUserBillingOrder(userId, sTime, eTime);
//        if (selfLotteryBilling >= minBillingOrder) {
//            validUser++;
//        }
//
//
//        double thisLoss = calculateLotteryLossByLotteryReport(reports); // 本次彩票亏损
//        double lastLoss = calculateLotteryLastLoss(userId, sTime, eTime); // 上半月彩票亏损
//        double totalLoss = lastLoss > 0 ? MathUtil.add(thisLoss, lastLoss) : thisLoss; // 累计彩票亏损
//
//        dividendBill.setDailyBillingOrder(dailyBillingOrder); // 日均销量
//        dividendBill.setBillingOrder(billingOrder); // 销量
//        dividendBill.setThisLoss(thisLoss); // 本次亏损
//        dividendBill.setLastLoss(lastLoss); // 上半月亏损
//        dividendBill.setTotalLoss(totalLoss); // 累计亏损
//        dividendBill.setValidUser(validUser); // 实际有效会员数
//    }
//
//    /**
//     * 汇总用户时间段内彩票消费
//     */
//    private double summaryUpLotteryLowerUserBillingOrder(int userId, String sTime, String eTime) {
//        List<UserLotteryReport> lowerUserReports = uLotteryReportDao.list(userId, sTime, eTime);
//        if (CollectionUtils.isEmpty(lowerUserReports)) {
//            return 0;
//        }
//
//        double billingOrder = 0;
//        for (UserLotteryReport lowerUserReport : lowerUserReports) {
//            billingOrder = MathUtil.add(billingOrder, lowerUserReport.getBillingOrder());
//        }
//
//        return billingOrder;
//    }
//
//    /**
//     * 汇总用户时间段内游戏消费
//     */
//    private double summaryUpGameLowerUserBillingOrder(int userId, String sTime, String eTime) {
//        List<UserGameReport> lowerUserReports = uGameReportDao.list(userId, sTime, eTime);
//        if (CollectionUtils.isEmpty(lowerUserReports)) {
//            return 0;
//        }
//
//        double billingOrder = 0;
//        for (UserGameReport lowerUserReport : lowerUserReports) {
//            billingOrder = MathUtil.add(billingOrder, lowerUserReport.getBillingOrder());
//        }
//
//        return billingOrder;
//    }
//
//    /**
//     * 计算彩票亏损
//     */
//    private double calculateLotteryLossByLotteryReport(List<UserLotteryReportVO> reports) {
//        double lotteryLoss = 0;
//        // 总销量
//        for (UserLotteryReportVO report : reports) {
//            if ("总计".equals(report.getName())) {
//                lotteryLoss = report.getPrize() + report.getSpendReturn() + report.getProxyReturn() + report.getActivity() + report.getRechargeFee() - report.getBillingOrder();
//                break;
//            }
//        }
//        return lotteryLoss;
//    }
//
//    /**
//     * 计算游戏亏损
//     */
//    private double calculateGameLossByGameReport(List<UserGameReportVO> reports) {
//        double gameLoss = 0;
//        // 总销量
//        for (UserGameReportVO report : reports) {
//            if ("总计".equals(report.getName())) {
//                gameLoss = report.getPrize() + report.getWaterReturn() + report.getProxyReturn() + report.getActivity() - report.getBillingOrder();
//                break;
//            }
//        }
//        return gameLoss;
//    }
//
//    /**
//     * 彩票上半月亏损
//     */
//    private double calculateLotteryLastLoss(int userId, String sTime, String eTime) {
//        String currDate = sTime;
//        if (StringUtil.isNotNull(currDate)) {
//
//            // 确定是否是下半月
//            int currDay = Integer.valueOf(currDate.substring(8));
//            if (currDay == 16) { // 今天16号，那今天就是下半月
//                String lastStartDate = currDate.substring(0, 8) + "01";
//                String lastEndDate = currDate;
//
//                // // 上半月分红情况
//                List<UserLotteryReportVO> lastReports = uLotteryReportService.report(userId, lastStartDate, lastEndDate);
//                double lastLoss = calculateLotteryLossByLotteryReport(lastReports);
//                return lastLoss;
//            }
//        }
//
//        return 0;
//    }
//
//    /**
//     * 游戏上半月亏损
//     */
//    private double calculateGameLastLoss(int userId, String sTime, String eTime) {
//        String currDate = sTime;
//        if (StringUtil.isNotNull(currDate)) {
//
//            // 确定是否是下半月
//            int currDay = Integer.valueOf(currDate.substring(8));
//            if (currDay == 16) { // 今天16号，那今天就是下半月
//                String lastStartDate = currDate.substring(0, 8) + "01";
//                String lastEndDate = currDate;
//
//                // // 上半月分红情况
//                List<UserGameReportVO> lastReports = uGameReportService.report(userId, lastStartDate, lastEndDate);
//                double lastLoss = calculateGameLossByGameReport(lastReports);
//                return lastLoss;
//            }
//        }
//
//        return 0;
//    }
//
//
//    private String getStartDate() {
//        Moment moment = new Moment().add(-1, "days");
//        int day = moment.day();
//
//        if (day <= 15) {
//            moment = moment.day(1);
//        }
//        else {
//            moment = moment.day(16);
//        }
//
//        return moment.toSimpleDate();
//    }
//
//    private String getEndDate() {
//        Moment moment = new Moment().add(-1, "days");
//        int day = moment.day();
//
//        if (day <= 15) {
//            moment = moment.day(16);
//        }
//        else {
//            moment = moment.add(1, "months");
//            moment = moment.day(1);
//        }
//
//        return moment.toSimpleDate();
//    }
//
//    private void sendMail(List<UserDividendBillAdapter> bills, String sTime, String eTime) {
//        double platformTotalLoss = 0; // 平级总亏损
//        double platformTotalAmount = 0; // 平级总发放
//
//        if (CollectionUtils.isNotEmpty(bills)) {
//
//            List<UserDividendBill> allBills = new ArrayList<>();
//            getAllBills(bills, allBills);
//
//            for (UserDividendBill bill : allBills) {
//                if (bill.getIssueType() != Global.DIVIDEND_ISSUE_TYPE_PLATFORM) continue;
//
//                if (bill.getStatus() != Global.DIVIDEND_BILL_ISSUED
//                        && bill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
//                        && bill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
//                        && bill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT) {
//                    continue;
//                }
//
//                platformTotalAmount = MathUtil.add(platformTotalAmount, bill.getCalAmount());
//
//                if (bill.getTotalLoss() >= 0) continue;
//
//                platformTotalLoss = MathUtil.add(platformTotalLoss, bill.getTotalLoss());
//            }
//        }
//
//        double totalBillingOrder = 0; // 总销量
//        double totalLoss = 0; // 总亏损
//        List<UserLotteryReportVO> lotteryReportVOs = uLotteryReportService.report(sTime, eTime);
//        if (CollectionUtils.isNotEmpty(lotteryReportVOs)) {
//            for (UserLotteryReportVO report : lotteryReportVOs) {
//                if ("总计".equals(report.getName())) {
//                    totalBillingOrder += report.getBillingOrder();
//                    totalLoss += calculateLotteryLossByLotteryReport(lotteryReportVOs);
//                    break;
//                }
//            }
//        }
//
//        mailJob.sendDividend(sTime, eTime, totalBillingOrder, totalLoss, platformTotalLoss, platformTotalAmount);
//    }
//
//    private List<UserDividendBill> getAllBills (List<UserDividendBillAdapter> bills, List<UserDividendBill> container) {
//        if (CollectionUtils.isEmpty(bills)) {
//            return container;
//        }
//
//        for (UserDividendBillAdapter bill : bills) {
//            container.add(bill.getUpperBill());
//
//            getAllBills(bill.getLowerBills(), container);
//        }
//
//        return container;
//    }
//}