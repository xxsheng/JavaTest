package lottery.domains.content.jobs;

import admin.domains.jobs.MailJob;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.*;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.user.UserDailySettleBillAdapter;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 日结任务
 */
@Component
public class DailySettleJob {
    private static final Logger log = LoggerFactory.getLogger(DailySettleJob.class);
    @Autowired
    private UserDao uDao;
    @Autowired
    private UserService uService;
    @Autowired
    private UserDailySettleService uDailySettleService;
    @Autowired
    private UserDailySettleBillService uDailySettleBillService;
    @Autowired
    private UserBillService uBillService;
    @Autowired
    private UserLotteryReportService uLotteryReportService;
    @Autowired
    private UserLotteryReportDao uLotteryReportDao;
    @Autowired
    private UserSysMessageService uSysMessageService;
    @Autowired
    private UserCodePointUtil uCodePointUtil;
    @Autowired
    private MailJob mailJob;

    @Autowired
    private LotteryDataFactory dataFactory;

    /**
     * 调度任务
     */
//    @Scheduled(cron = "0 32 * * * *")
    @Scheduled(cron = "0 5 2 0/1 * *")
//     @PostConstruct
    public void schedule() {
        try {
            if (!dataFactory.getDailySettleConfig().isEnable()) {
                log.info("日结没有开启，不发放");
                return;
            }

            // 获取昨天yyyy-MM-dd
            String yesterday = new Moment().subtract(1, "days").toSimpleDate();
            String today = new Moment().toSimpleDate();

            log.info("发放日结开始：{}~{}", yesterday, today);

            // 开始日结
            settleUp(yesterday, today);

            log.info("发放日结完成：{}~{}", yesterday, today);
        } catch (Exception e) {
            log.error("发放日结出错", e);
        }
    }

    /**
     * 开始结算
     */
    public List<UserDailySettleBillAdapter> settleUp(String sTime, String eTime) {
        // 查找所有内部招商号
        List<User> neibuZhaoShangs = uService.findNeibuZhaoShang();
        if (CollectionUtils.isEmpty(neibuZhaoShangs)) {
            log.error("没有找到任何内部招商账号，本次未产生任何日结数据");
            return null;
        }
        
        List<User> zhaoShangs = uService.findZhaoShang(neibuZhaoShangs);
        if (CollectionUtils.isEmpty(zhaoShangs)) {
            log.error("没有找到任何招商账号，本次未产生任何日结数据");
            return null;
        }
        
        List<User> zhishus = uService.findZhaoShang(zhaoShangs);
        if (CollectionUtils.isEmpty(zhishus)) {
            log.error("没有找到任何直属账号，本次未产生任何日结数据");
            return null;
        }

        // 结算内部招商号
        List<UserDailySettleBillAdapter> neibuZhaoShangBills = null;
        try {
            log.info("发放直属日结开始：{}~{}", sTime, eTime);
            neibuZhaoShangBills = settleUpNeibuZhaoShangs(zhishus, sTime, eTime);
            log.info("发放直属日结完成：{}~{}", sTime, eTime);
        } catch (Exception e) {
            log.error("发放直属日结出错", e);
        }

        // 查找所有招商号
       /* List<UserDailySettleBillAdapter> zhaoShangBills = null;
        List<User> zhaoShangs = uService.findZhaoShang(neibuZhaoShangs);
        if (CollectionUtils.isNotEmpty(zhaoShangs)) {
            // 结算招商号
            try {
                log.info("发放招商日结开始：{}~{}", sTime, eTime);
                zhaoShangBills = settleUpZhaoShangs(zhaoShangs, sTime, eTime);
                log.info("发放招商日结完成：{}~{}", sTime, eTime);
            } catch (Exception e) {
                log.error("发放招商日结出错", e);
            }
        }*/

        // 发送邮件
        List<UserDailySettleBillAdapter> allBills = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(neibuZhaoShangBills)) allBills.addAll(neibuZhaoShangBills);
//        if (CollectionUtils.isNotEmpty(zhaoShangBills)) allBills.addAll(zhaoShangBills);
        sendMail(allBills, sTime, eTime);

        return allBills;
    }

    private List<UserDailySettleBillAdapter> settleUpNeibuZhaoShangs(List<User> neibuZhaoShangs, String sTime, String eTime) {
        List<UserDailySettleBillAdapter> bills = new ArrayList<>();
        for (User neibuZhaoShang : neibuZhaoShangs) {
            UserDailySettleBillAdapter billAdapter = settleUpWithUser(neibuZhaoShang, sTime, eTime, true, Global.DAILY_SETTLE_ISSUE_TYPE_PLATFORM);
            if (billAdapter != null) {
                bills.add(billAdapter);
            }
        }

        // 保存账单
        if (CollectionUtils.isNotEmpty(bills)) {
            for (UserDailySettleBillAdapter bill : bills) {
                processLineBill(bill);
            }
        }

        return bills;
    }

    private List<UserDailySettleBillAdapter> settleUpZhaoShangs(List<User> zhaoShangs, String sTime, String eTime) {
        List<UserDailySettleBillAdapter> bills = new ArrayList<>();
        for (User zhaoShang : zhaoShangs) {
            UserDailySettleBillAdapter billAdapter = settleUpWithUser(zhaoShang, sTime, eTime, true, Global.DAILY_SETTLE_ISSUE_TYPE_PLATFORM);
            if (billAdapter != null) {
                bills.add(billAdapter);
            }
        }

        // 保存账单
        if (CollectionUtils.isNotEmpty(bills)) {
            for (UserDailySettleBillAdapter bill : bills) {
                processLineBill(bill);
            }
        }

        return bills;
    }

    private UserDailySettleBill createBill(int userId, String eTime, UserDailySettle dailySettle) {
    	int minValidUserCfg = dataFactory.getDailySettleConfig().getMinValidUserl();
        UserDailySettleBill dailySettleBill = new UserDailySettleBill();
        dailySettleBill.setUserId(userId);
        dailySettleBill.setIndicateDate(eTime);
        if (dailySettle.getMinValidUser() >= minValidUserCfg) {
        	dailySettleBill.setMinValidUser(dailySettle.getMinValidUser());
		} else {
			dailySettleBill.setMinValidUser(minValidUserCfg);
		}
        dailySettleBill.setValidUser(0);
        dailySettleBill.setScale(0);
        dailySettleBill.setBillingOrder(0);
        dailySettleBill.setUserAmount(0);
        return dailySettleBill;
    }

    /**
     * 检查是否合法
     */
    private boolean check(User user, UserDailySettle dailySettle) {
        if (user.getId() == Global.USER_TOP_ID) {
            String error = String.format("契约日结错误提醒;用户%s为总账号，但查找到其拥有日结配置，本次不对其进行结算，不影响整体结算；", user.getUsername());
            log.error(error);
            mailJob.addWarning(error);
            return false;
        }

        // 是否是内部招商
//        boolean isNeibuZhaoShang = uCodePointUtil.isLevel1Proxy(user);
//        if (isNeibuZhaoShang) {
//            return true;
//        }

        boolean isZhaoShang = uCodePointUtil.isLevel3Proxy(user);
        if (isZhaoShang) {
            return true;
        }

        // 必须要有先有上级的配置
        UserDailySettle upUserDailySettle = uDailySettleService.getByUserId(user.getUpid());
        if (upUserDailySettle == null) {
            String error = String.format("契约日结错误提醒;用户%s没有找到上级的日结配置，本次不对其团队进行结算；", user.getUsername());
            log.error(error);
            mailJob.addWarning(error);
            return false;
        }

        if (!uDailySettleService.checkValidLevel(dailySettle.getScaleLevel(), dailySettle.getSalesLevel(),dailySettle.getLossLevel(), upUserDailySettle, dailySettle.getUserLevel())) {
			String error = String.format("日结分红错误提醒;用户%s，所签订分红条款为无效条款，条款内容：分红比例[%s]，销量[%s]，亏损[%s]，本次不对其团队进行结算；",
					user.getUsername(), dailySettle.getScaleLevel(), dailySettle.getSalesLevel(), dailySettle.getLossLevel());
			log.error(error);
			mailJob.addWarning(error);
			return false;
		}

        return true;
    }

    /**
     * 结算某一个用户，返回整条线的账单，并且带有层级，数放据里只包含每个人要分多少钱、账单要发多少钱、以下所有直属下级账单
     */
    public UserDailySettleBillAdapter settleUpWithUser(User user, String sTime, String eTime, boolean settleLowers, int issueType) {
        // 查找契约配置
        UserDailySettle dailySettle = uDailySettleService.getByUserId(user.getId());
        if (dailySettle == null || dailySettle.getStatus() != Global.DAILY_SETTLE_VALID) {
            return null;
        }

        boolean checked = check(user, dailySettle);
        if (!checked) {
            // 检查不合法，不生成数据
            return null;
        }

        // 查找所有下级及他自己的报表
        List<UserLotteryReportVO> reports = uLotteryReportService.report(user.getId(), sTime, eTime);
        if (CollectionUtils.isEmpty(reports)) {
            // 没有销量，不生成数据
            return null;
        }

        // 自己账单
        UserDailySettleBill upperBill = createBill(user.getId(), eTime, dailySettle);
        upperBill.setIssueType(issueType); // 账单发放类型

        // 汇总销量
        UserDailySettleBill summaryBill = summaryUpReports(reports, user.getId(), sTime, eTime);
        upperBill.setBillingOrder(summaryBill.getBillingOrder());
        upperBill.setThisLoss(summaryBill.getThisLoss());
        upperBill.setValidUser(summaryBill.getValidUser()); 

        if (summaryBill.getBillingOrder() <= 0) {
            // 没有销量，不生成数据
            return null;
        }
        
        boolean isCheckLossCfg = dataFactory.getDailySettleConfig().isCheckLoss();
		double billingOrder = MathUtil.divide(upperBill.getBillingOrder(), 10000, 2); // 本次销量 万单位
		double thisLoss = MathUtil.divide(Math.abs(upperBill.getThisLoss()), 10000, 2); // 本次亏损 万单位
		int thisUser = upperBill.getValidUser();

		String[] scaleLevels = dailySettle.getScaleLevel().split(",");// 签订比率
		String[] salesLevels = dailySettle.getSalesLevel().split(",");// 签订销量
		String[] lossLevels = dailySettle.getLossLevel().split(",");// 签订亏损
		String[] userLevels = dailySettle.getUserLevel().split(",");//签订人数

		// 核算销量 和 人数
		// int level = -1;
		List<Integer> levels = new ArrayList<>();
		for (int i = 0; i < salesLevels.length; i++) {
			if ((billingOrder >= Double.parseDouble(salesLevels[i])) && (thisUser >= Integer.valueOf(userLevels[i]))) {
				levels.add(i);
			}
		}

		// 设置分红比率
		if (levels.size() > 0) {
			Collections.sort(levels);
			upperBill.setScale(Double.valueOf(scaleLevels[levels.get(levels.size() - 1)]));
		}

       /* // 最小销量要求 = 契约最小人数要求 * 有效会员最小消费
        double minBilling = upperBill.getMinValidUser() * dataFactory.getDailySettleConfig().getMinBillingOrder();

        // 计算金额
        double scale = dailySettle.getScale();

        double calAmount = MathUtil.multiply(upperBill.getBillingOrder(), scale);
        upperBill.setCalAmount(calAmount);

        // 没有达到销量要求
        if (upperBill.getBillingOrder() < minBilling
                || upperBill.getValidUser() < upperBill.getMinValidUser()) {
            upperBill.setStatus(Global.DAILY_SETTLE_BILL_NOT_REACHED); // 未达标
            // upperBill.setCalAmount(0); // 没有达到契约要求
            upperBill.setRemarks("销量或人数未达标");
            return new UserDailySettleBillAdapter(upperBill, null); // 上级没有达标，下级不再计算
        }*/
		
		if (upperBill.getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
			double calAmount = 0; // 账单计算金额
			if (levels.size() > 0 &&  upperBill.getValidUser() >= upperBill.getMinValidUser()) { // 亏损必须是小于0，最小日均用户达到
				double scale = MathUtil.divide(upperBill.getScale(), 100, 6);
				// 账单计算金额
				calAmount = MathUtil	.decimalFormat(new BigDecimal(MathUtil.multiply(Math.abs(upperBill.getBillingOrder()), scale)), 2);

				if (isCheckLossCfg) { // 是否开启亏损核算
						// 核算亏损 重新核算比率
					levels.clear();
					calAmount = 0;
					for (int i = 0; i < salesLevels.length; i++) {
						if (billingOrder >= Double.parseDouble(salesLevels[i]) && thisLoss >= Double.parseDouble(lossLevels[i]) && (thisUser >= Integer.valueOf(userLevels[i]))) {
							levels.add(i);
						}
					}
					if (levels.size() > 0) {
						Collections.sort(levels);
						upperBill.setScale(Double.valueOf(scaleLevels[levels.get(levels.size() - 1)]));
						// 重新 账单计算金额
						scale = MathUtil.divide(upperBill.getScale(), 100, 4);
						calAmount = MathUtil.decimalFormat(new BigDecimal(MathUtil.multiply(Math.abs(upperBill.getBillingOrder()), scale)), 2);
					}
				}
			}
			upperBill.setCalAmount(calAmount);

			// 没有达到销量要求
			if (calAmount == 0) {
				upperBill.setCalAmount(0);
				upperBill.setScale(0);
				upperBill.setStatus(Global.DIVIDEND_BILL_NOT_REACHED);
				upperBill.setRemarks("日结分红条款未达标");
				return new UserDailySettleBillAdapter(upperBill, null); // 上级没有达标，下级不再计算
			}
		}

        // 往下每级结算
        double lowerTotalAmount = 0; // 下级共需要发放多少钱
        List<UserDailySettleBillAdapter> lowerBills = new ArrayList<>();
        if (settleLowers) {
            for (UserLotteryReportVO report : reports) {
                if (!"总计".equals(report.getName())
                        && !report.getName().equalsIgnoreCase(user.getUsername())) {
                    User subUser = uService.getByUsername(report.getName());

                    // 继续往下结
                    UserDailySettleBillAdapter lowerBillAdapter = settleUpWithUser(subUser, sTime, eTime, true, Global.DAILY_SETTLE_ISSUE_TYPE_UPPER);
                    if (lowerBillAdapter != null) {
                        UserDailySettleBill lowerUpperBill = lowerBillAdapter.getUpperBill();
                        lowerTotalAmount = MathUtil.add(lowerTotalAmount, lowerUpperBill.getCalAmount());
                        lowerBills.add(lowerBillAdapter);
                    }
                }
            }
        }

        // 返回整条线的账单
        upperBill.setLowerTotalAmount(lowerTotalAmount); // 下级共需要发放多少钱
        return new UserDailySettleBillAdapter(upperBill, lowerBills);
    }

    /**
     * 处理一整条线的订单，这里最顶层要么是招商号，要么是直属号，不可能会是其它的情况
     */
    private void processLineBill(UserDailySettleBillAdapter uDailySettleBillAdapter) {
        UserDailySettleBill upperBill = uDailySettleBillAdapter.getUpperBill();
        List<UserDailySettleBillAdapter> lowerBills = uDailySettleBillAdapter.getLowerBills();

        // 平台发放，且不需要向下级发放
        if (upperBill.getIssueType() == Global.DAILY_SETTLE_ISSUE_TYPE_PLATFORM && CollectionUtils.isEmpty(lowerBills)) {
            if (upperBill.getStatus() == Global.DAILY_SETTLE_BILL_NOT_REACHED) {
                // 未达标
                double amount = 0;
                upperBill.setUserAmount(amount); // 实际金额
                upperBill.setTotalReceived(amount); // 已领取金额
                saveBill(upperBill, Global.DAILY_SETTLE_BILL_NOT_REACHED, amount);
            }
            else {
                double amount = upperBill.getCalAmount();
                upperBill.setUserAmount(amount); // 实际金额
                upperBill.setTotalReceived(amount); // 已领取金额
                saveBill(upperBill, Global.DAILY_SETTLE_BILL_ISSUED, amount);
            }

            return;
        }

        // 上级发放，且不需要向下级发放
        if (upperBill.getIssueType() == Global.DAILY_SETTLE_ISSUE_TYPE_UPPER && CollectionUtils.isEmpty(lowerBills)) {
            if (upperBill.getStatus() == Global.DAILY_SETTLE_BILL_NOT_REACHED) {
                // 未达标
                double amount = 0;
                upperBill.setUserAmount(amount); // 实际金额
                upperBill.setTotalReceived(amount); // 已领取金额
                saveBill(upperBill, Global.DAILY_SETTLE_BILL_NOT_REACHED, amount);
            }
            else {
                int status = upperBill.getTotalReceived() >= upperBill.getCalAmount() ? Global.DAILY_SETTLE_BILL_ISSUED : Global.DAILY_SETTLE_BILL_PART_RECEIVED;
                upperBill.setUserAmount(upperBill.getCalAmount()); // 实际金额
                saveBill(upperBill, status, upperBill.getTotalReceived()); // 本次只发放账上有多少加多少钱
            }
            return;
        }

        // 需要向下级发放
        double upperBillMoney; // 目前日结账单可供发放的金额
        if (upperBill.getIssueType() == Global.DAILY_SETTLE_ISSUE_TYPE_PLATFORM) {
            // 如果是平台发放
            upperBillMoney = upperBill.getCalAmount();
        }
        else {
            // 如果是上发放
            upperBillMoney = upperBill.getTotalReceived();
        }

        double upperThisTimePaid = 0; // 本次共计发放
        for (UserDailySettleBillAdapter lowerBill : lowerBills) {
            // 直属下级的钱
            UserDailySettleBill lowerUpperBill = lowerBill.getUpperBill();

            // 下级总共需要领取的钱
            double lowerUpperAmount = lowerUpperBill.getCalAmount();
            if (lowerUpperAmount > 0) {
                // 下级还剩余多少没有领取
                double lowerRemainReceived = lowerUpperAmount;

                double billGive = 0; // 上级的账单中可以给他发放多少钱
                // 先从上级的账单金额中扣除
                if (upperBillMoney > 0 && lowerRemainReceived > 0) {
                    billGive = lowerUpperAmount >= upperBillMoney ? upperBillMoney : lowerUpperAmount;
                    upperBillMoney = MathUtil.subtract(upperBillMoney, billGive);
                    lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, billGive);
                }

                // 下级还没有领完且上级的账单金额也扣完了，再看从上级的主账户中扣
                double totalMoneyGive = 0;
                User upperUser = uDao.getById(upperBill.getUserId());
                if (lowerRemainReceived > 0 && upperUser.getTotalMoney() > 0) {
                    totalMoneyGive = lowerRemainReceived >= upperUser.getTotalMoney() ? upperUser.getTotalMoney() : lowerRemainReceived;
                    lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, totalMoneyGive);
                }

                // 下级还没有领完且上级的主账户金额也扣完了，再看从上级的彩票账户中扣
                double lotteryMoneyGive = 0;
                if (lowerRemainReceived > 0 && upperUser.getLotteryMoney() > 0) {
                    lotteryMoneyGive = lowerRemainReceived >= upperUser.getLotteryMoney() ? upperUser.getLotteryMoney() : lowerRemainReceived;
                    lowerRemainReceived = MathUtil.subtract(lowerRemainReceived, lotteryMoneyGive);
                }

                double totalGive = MathUtil.add(MathUtil.add(billGive, totalMoneyGive), lotteryMoneyGive);
                if (totalGive > 0) {
                    if (totalMoneyGive > 0) {
                        UserVO subUser = dataFactory.getUser(lowerUpperBill.getUserId());
                        uDao.updateTotalMoney(upperUser.getId(), -totalMoneyGive); // 从上级账户中抽除相应金额
                        // 增加上级的账单，但不计入优惠  扣钱不需要加报表
                        uBillService.addDailySettleBill(upperUser, Global.BILL_ACCOUNT_MAIN, -totalMoneyGive, "系统自动扣发" + totalMoneyGive + "日结金额到" + subUser.getUsername(), false);
                    }
                    if (lotteryMoneyGive > 0) {
                        UserVO subUser = dataFactory.getUser(lowerUpperBill.getUserId());
                        uDao.updateLotteryMoney(upperUser.getId(), -lotteryMoneyGive); // 从上级账户中抽除相应金额
                        // 增加上级的账单，但不计入优惠 扣钱不需要加报表
                        uBillService.addDailySettleBill(upperUser, Global.BILL_ACCOUNT_LOTTERY, -lotteryMoneyGive, "系统自动扣发" + lotteryMoneyGive + "日结金额到" + subUser.getUsername(), false);
                    }
                    upperThisTimePaid = MathUtil.add(upperThisTimePaid, totalGive); // 增加上级本次已派发金额

                    lowerUpperBill.setTotalReceived(totalGive); // 增加下级的已领取金额
                }
            }

            // 继续往下级派发
            processLineBill(lowerBill);
        }

        upperBill.setLowerPaidAmount(upperThisTimePaid);

        // 账单上还有钱
        if (upperBillMoney > 0) {
            // 已发放
            upperBill.setUserAmount(upperBillMoney); // 实际金额
            upperBill.setTotalReceived(upperBillMoney); // 已领取金额
            saveBill(upperBill, Global.DAILY_SETTLE_BILL_ISSUED, upperBillMoney);
        }
        else {
            double notYetPay = MathUtil.subtract(upperBill.getLowerTotalAmount(), upperBill.getLowerPaidAmount());

            if (notYetPay > 0) {
                // 余额不足
                double amount = 0;
                upperBill.setUserAmount(amount); // 实际金额
                upperBill.setRemarks("余额不足,请充值！");
                saveBill(upperBill, Global.DAILY_SETTLE_BILL_INSUFFICIENT, 0);
            }
            else {
                // 已发放
                double amount = 0;
                upperBill.setUserAmount(amount); // 实际金额
                saveBill(upperBill, Global.DAILY_SETTLE_BILL_ISSUED, 0);
            }
        }
    }

    /**
     * @param upperBill
     * @param status
     * @param userAmount 实际已领取金额
     */
    private void saveBill(UserDailySettleBill upperBill, int status, double userAmount) {
        upperBill.setSettleTime(new Moment().toSimpleTime());
        upperBill.setStatus(status);

        if (userAmount > 0) {
            User user = uDao.getById(upperBill.getUserId());
            if (user != null) {

                String remarks = String.format("日结,销量：%s", new BigDecimal(upperBill.getBillingOrder()).setScale(4, RoundingMode.FLOOR).toString());
                boolean addedBill = uBillService.addDailySettleBill(user, Global.BILL_ACCOUNT_LOTTERY, userAmount, remarks, true);
                if (addedBill) {
                    uDao.updateLotteryMoney(user.getId(), userAmount);
                    uSysMessageService.addDailySettleBill(user.getId(), upperBill.getIndicateDate());
                }
            }
        }

        uDailySettleBillService.add(upperBill);
    }

    private UserDailySettleBill summaryUpReports(List<UserLotteryReportVO> reports, int userId, String sTime, String eTime) {
        double billingOrder = 0.d;
        // 总销量
        for (UserLotteryReportVO report : reports) {
            if ("总计".equals(report.getName())) {
                billingOrder = report.getBillingOrder();
                break;
            }
        }

        double minBillingOrder = dataFactory.getDailySettleConfig().getMinBillingOrder();
        // 总活跃人数
        List<User> userLowers = uDao.getUserLower(userId);
        int validUser = 0;
        for (User lowerUser : userLowers) {
            double lowerUserBillingOrder = summaryUpLowerUserReports(lowerUser.getId(), sTime, eTime);
            if (lowerUserBillingOrder >= minBillingOrder) {
                validUser++;
            }
        }

        double selfBilling = summaryUpLowerUserReports(userId, sTime, eTime);
        if (selfBilling >= minBillingOrder) {
            validUser++;
        }
        double thisLoss = calculateLotteryLossByLotteryReport(reports);

        UserDailySettleBill bill = new UserDailySettleBill();
        bill.setBillingOrder(billingOrder);
        bill.setValidUser(validUser);
        bill.setThisLoss(thisLoss);
        return bill;
    }
    
    /**
	 * 计算彩票亏损
	 */
	private double calculateLotteryLossByLotteryReport(List<UserLotteryReportVO> reports) {
		double lotteryLoss = 0;
		// 总销量
		for (UserLotteryReportVO report : reports) {
			if ("总计".equals(report.getName())) {
				lotteryLoss = report.getPrize() + report.getSpendReturn() + report.getProxyReturn()
						+ report.getActivity() + report.getRechargeFee() - report.getBillingOrder();
				break;
			}
		}
		return lotteryLoss;
	}

    private double summaryUpLowerUserReports(int userId, String sTime, String eTime) {
        List<UserLotteryReport> lowerUserReports = uLotteryReportDao.list(userId, sTime, eTime);
        if (CollectionUtils.isEmpty(lowerUserReports)) {
            return 0;
        }

        double billingOrder = 0;
        for (UserLotteryReport lowerUserReport : lowerUserReports) {
            billingOrder = MathUtil.add(billingOrder, lowerUserReport.getBillingOrder());
        }

        return billingOrder;
    }


    private void sendMail(List<UserDailySettleBillAdapter> bills, String sTime, String eTime) {
        try {
            double totalBillingOrder = 0;
            double totalAmount = 0;

            if (CollectionUtils.isNotEmpty(bills)) {

                List<UserDailySettleBill> allBills = new ArrayList<>();
                getAllBills(bills, allBills);

                for (UserDailySettleBill bill : allBills) {
                    if (bill.getIssueType() == Global.DAILY_SETTLE_ISSUE_TYPE_PLATFORM) {
                        totalAmount = MathUtil.add(totalAmount, bill.getCalAmount());
                    }
                }
            }

            List<UserLotteryReportVO> reports = uLotteryReportService.report(sTime, eTime);
            if (CollectionUtils.isNotEmpty(reports)) {
                for (UserLotteryReportVO report : reports) {
                    if ("总计".equals(report.getName())) {
                        totalBillingOrder = report.getBillingOrder();
                        break;
                    }
                }
            }

            mailJob.sendDailySettle(sTime, totalBillingOrder, totalAmount);
        } catch (Exception e) {
            log.error("发送契约日结邮件出错", e == null ? "" : e.getMessage());
        }
    }

    private List<UserDailySettleBill> getAllBills (List<UserDailySettleBillAdapter> bills, List<UserDailySettleBill> container) {
        if (CollectionUtils.isEmpty(bills)) {
            return container;
        }

        for (UserDailySettleBillAdapter bill : bills) {
            container.add(bill.getUpperBill());

            getAllBills(bill.getLowerBills(), container);
        }

        return container;
    }
}