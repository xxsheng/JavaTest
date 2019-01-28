package lottery.domains.content.jobs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import admin.domains.jobs.MailJob;
import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserDividendBillService;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserGameReportService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameReportDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.user.UserDividendBillAdapter;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;

/**
 * 每月1号，16号凌晨发放半个周期分红，每次发放前清零待领取、部分领取、余额不足
 */
@Component
public class DividendRankJob {
	private static final Logger log = LoggerFactory.getLogger(DividendRankJob.class);
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserService uService;
	@Autowired
	private UserDividendService uDividendService;
	@Autowired
	private UserDividendBillService uDividendBillService;
	@Autowired
	private UserLotteryReportService uLotteryReportService;
	@Autowired
	private UserLotteryReportDao uLotteryReportDao;
	@Autowired
	private UserGameReportService uGameReportService;
	@Autowired
	private UserGameReportDao uGameReportDao;
	@Autowired
	private UserCodePointUtil uCodePointUtil;
	@Autowired
	private MailJob mailJob;
	
	private final boolean ISTOP = true; //是否二级分红 

	@Autowired
	private LotteryDataFactory dataFactory;

	/**
	 * 调度任务
	 */
//	 @Scheduled(cron = "0 33 * * * *")
	@Scheduled(cron = "0 10 2 1,16 * *")
	public void schedule() {
		try {
			if (!dataFactory.getDividendConfig().isEnable()) {
				log.info("分红没有开启，不发放");
				return;
			}

			// 获取结算周期
			String startDate = getStartDate();
			int startDay = Integer.valueOf(startDate.substring(8));
			if (startDay != 1 && startDay != 16) {
				return;
			}

			String endDate = getEndDate();

			log.info("发放分红开始：{}~{}", startDate, endDate);

			// 将待领取、部分领取、余额不足的分红置为已过期
			// updateAllExpire();

			// 开始分红
			settleUp(startDate, endDate);

			log.info("发放分红完成：{}-{}", startDate, endDate);
		} catch (Exception e) {
			log.error("分红发放出错", e);
		}
	}

	private void updateAllExpire() {
		uDividendBillService.updateAllExpire();
	}

	/**
	 * 开始结算
	 */
	public void settleUp(String sTime, String eTime) {
		// 查找所有1997下级
		 List<User> neibuZhaoShangs = uService.findNeibuZhaoShang();
			 if (CollectionUtils.isEmpty(neibuZhaoShangs)) {
			 log.error("没有找到任何内部招商账号，本次未产生任何分红数据");
			 return;
		 }

		// 查找所有招商号 1997下级开始发放分红
		List<User> zhaoShangs = uService.findZhaoShang(neibuZhaoShangs);
		if (CollectionUtils.isEmpty(zhaoShangs)) {
			log.error("没有找到任何招商账号，本次未产生任何分红数据");
			return;
		}

		List<UserDividendBillAdapter> bills = new ArrayList<>();

		try {
			log.info("发放招商及以下分红开始：{}~{}", sTime, eTime);
			List<UserDividendBillAdapter> zhaoShangBills = settleUpZhaoShangs(zhaoShangs, sTime, eTime);
			if (CollectionUtils.isNotEmpty(zhaoShangBills)) {
				bills.addAll(zhaoShangBills);
			}
			log.info("发放招商及以下分红完成：{}~{}", sTime, eTime);
		} catch (Exception e) {
			log.error("发放招商及以下分红出错", e);
		}

		sendMail(bills, sTime, eTime);
	}

	/**
	 * 发放招商分红，往下结
	 */
	private List<UserDividendBillAdapter> settleUpZhaoShangs(List<User> zhaoShangs, String sTime, String eTime) {
		List<UserDividendBillAdapter> bills = new ArrayList<>();
		for (User zhaoShang : zhaoShangs) {
			// 招商需要逐级往下进行结算
			if(zhaoShang.getUpid() != 0){
				UserDividendBillAdapter billAdapter = settleUpWithUser(zhaoShang, sTime, eTime, true,
						Global.DIVIDEND_ISSUE_TYPE_PLATFORM);
				if (billAdapter != null) {
					bills.add(billAdapter);
				}
			}
		}

		// 保存账单
		if (CollectionUtils.isNotEmpty(bills)) {
			for (UserDividendBillAdapter bill : bills) {
				processLineBill(bill);
			}
		}

		return bills;
	}

	private UserDividendBill createBill(int userId, String sTime, String eTime, UserDividend userDividend,
			int issueType) {
		int minValidUserCfg = dataFactory.getDividendConfig().getMinValidUserl();
		UserDividendBill dividendBill = new UserDividendBill();
		dividendBill.setUserId(userId);
		dividendBill.setIndicateStartDate(sTime);
		dividendBill.setIndicateEndDate(eTime);
		if (userDividend.getMinValidUser() >= minValidUserCfg) {
			dividendBill.setMinValidUser(userDividend.getMinValidUser());
		} else {
			dividendBill.setMinValidUser(minValidUserCfg);
		}
		dividendBill.setValidUser(0);
		dividendBill.setScale(0);
		dividendBill.setBillingOrder(0);
		dividendBill.setUserAmount(0);
		dividendBill.setCalAmount(0);
		dividendBill.setIssueType(issueType);
		return dividendBill;
	}

	/**
	 * 检查是否合法
	 */
	private boolean check(User user, UserDividend uDividend) {
		if (user.getId() == Global.USER_TOP_ID) {
			String error = String.format("契约分红错误提醒;用户%s为总账号，但查找到其拥有分红配置，本次不对其进行结算，不影响整体结算；", user.getUsername());
			log.error(error);
			mailJob.addWarning(error);
			return false;
		}

		// 是否是内部招商 1997
		boolean isNeibuZhaoShang = uCodePointUtil.isLevel2Proxy(user);
//		if (isNeibuZhaoShang) {
//			String error = String.format("契约分红错误提醒;用户%s为内部招商，但查找到其拥有分红配置，本次不对其进行结算；", user.getUsername());
//			log.error(error);
//			mailJob.addWarning(error);
//			return false;
//		}

		// 是否是内部招商 1996
//		boolean isZhaoShang = uCodePointUtil.isLevel2Proxy(user);
//		if (isZhaoShang) {
//			String error = String.format("契约分红错误提醒;用户%s为招商，但查找到其拥有分红配置，本次不对其进行结算；", user.getUsername());
//			log.error(error);
//			mailJob.addWarning(error);
//			return false;
//		}

		// 非1997下级 必须要有先有上级的配置
		UserDividend upUserDividend = uDividendService.getByUserId(user.getUpid());
		if (!isNeibuZhaoShang && upUserDividend == null) {
			String error = String.format("契约分红错误提醒;用户%s没有找到上级的分红配置，本次不对其团队进行结算；", user.getUsername());
			log.error(error);
			mailJob.addWarning(error);
			return false;
		}

		String[] scaleLevels = uDividend.getScaleLevel().split(",");
		double minScale = dataFactory.getDividendConfig().getLevelsScale()[0];
		double maxScale = dataFactory.getDividendConfig().getLevelsScale()[1];

		// double minSales =
		// dataFactory.getDividendConfig().getLevelsSales()[0];
		// double maxSales =
		// dataFactory.getDividendConfig().getLevelsSales()[1];
		//
		// double minLoss = dataFactory.getDividendConfig().getLevelsLoss()[0];
		// double maxLoss = dataFactory.getDividendConfig().getLevelsLoss()[1];

		if (!isNeibuZhaoShang) {
			String[] upScaleLevels = upUserDividend.getScaleLevel().split(",");
			maxScale = Double.valueOf(upScaleLevels[upScaleLevels.length - 1]);
		}

		if ((Double.valueOf(scaleLevels[scaleLevels.length - 1]) > maxScale
				|| Double.valueOf(scaleLevels[0]) < minScale)) {
			String error = String.format("契约分红错误提醒;用户%s为直属号，但分红比例%s不是有效比例%s~%s，本次不对其团队进行结算；", user.getUsername(),
					uDividend.getScaleLevel(), minScale, maxScale);
			log.error(error);
			mailJob.addWarning(error);
			return false;
		}

		if (!uDividendService.checkValidLevel(uDividend.getScaleLevel(), uDividend.getSalesLevel(),uDividend.getLossLevel(), upUserDividend, uDividend.getUserLevel())) {
			String error = String.format("契约分红错误提醒;用户%s，所签订分红条款为无效条款，条款内容：分红比例[%s]，销量[%s]，亏损[%s]，本次不对其团队进行结算；",
					user.getUsername(), uDividend.getScaleLevel(), uDividend.getSalesLevel(), uDividend.getLossLevel());
			log.error(error);
			mailJob.addWarning(error);
			return false;
		}

		return true;
	}

	/**
	 * 结算某一个用户，返回整条线的账单
	 */
	private UserDividendBillAdapter settleUpWithUser(User user, String sTime, String eTime, boolean settleLowers,
			int issueType) {
		// 查找契约配置
		UserDividend userDividend = uDividendService.getByUserId(user.getId());
		if (userDividend == null || userDividend.getStatus() != Global.DIVIDEND_VALID) {
			return null;
		}

		boolean checked = check(user, userDividend);
		// 检查不合法，不再往下结算
		if (!checked) {
			return null;
		}

		UserDividendBill upperBill = createBill(user.getId(), sTime, eTime, userDividend, issueType);

		// 查找所有直属下级及他自己的报表
		List<UserLotteryReportVO> reports = uLotteryReportService.report(user.getId(), sTime, eTime);
		if (CollectionUtils.isNotEmpty(reports)) {
			// 汇总销量
			summaryUpReports(reports, user.getId(), sTime, eTime, upperBill);
		}

		/*
		 * TODO 统一阶梯式分红 例如：用户签订： 1.销量=30W 人数=3 亏损=1W 分红比率=3% 2.销量=50W 人数=3 亏损=3W
		 * 分红比率=5% 3.销量=100W 人数=3 亏损=5W 分红比率=10% 4.销量=300W 人数=3 亏损=7W 分红比率=15%
		 * 5.销量=500W 人数=3 亏损=10W 分红比率=20% 6.销量=1000W 人数=3 亏损=20W 分红比率=25%
		 * 7.销量=3000W 人数=3 亏损=50W 分红比率=30% 8.销量=5000W 人数=3 亏损=50W 分红比率=35%
		 * 
		 * 半月销量：销量=80W 日人均=5人 亏损=25000 浮动比率（规则匹配 - 销量/人数）： 当月分红按 2 规则分红 5%
		 * 分红为：1037.50= (1250=25000*5%) * (亏损系数 0.83=2.5/3) 固定比率（规则匹配 -
		 * 销量/人数/亏损）： 当月分红按 1 规则分红 3% 分红为：750=25000*3%
		 */

		boolean isCheckLossCfg = dataFactory.getDividendConfig().isCheckLoss();
		double billingOrder = MathUtil.divide(upperBill.getBillingOrder(), 10000, 2); // 本次销量
		double thisLoss = MathUtil.divide(Math.abs(upperBill.getThisLoss()), 10000, 2); // 本次亏损
		int vaildUser = upperBill.getValidUser();
		
		String[] scaleLevels = userDividend.getScaleLevel().split(",");// 签订比率
		String[] salesLevels = userDividend.getSalesLevel().split(",");// 签订销量
		String[] lossLevels = userDividend.getLossLevel().split(",");// 签订亏损
		String[] userLevels = userDividend.getUserLevel().split(",");//签订人数

		// 核算销量 和 人数
		// int level = -1;
		List<Integer> levels = new ArrayList<>();
		for (int i = 0; i < salesLevels.length; i++) {
			if ((billingOrder >= Double.parseDouble(salesLevels[i])) && (vaildUser >= Integer.valueOf(userLevels[i]))) {
				levels.add(i);
			}
		}

		// 设置分红比率
		if (levels.size() > 0) {
			Collections.sort(levels);
			upperBill.setScale(Double.valueOf(scaleLevels[levels.get(levels.size() - 1)]));
		}

		if (upperBill.getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
			double calAmount = 0; // 账单计算金额
			if (levels.size() > 0 && upperBill.getThisLoss() < 0 && upperBill.getValidUser() >= upperBill.getMinValidUser()) { // 亏损必须是小于0，最小日均用户达到
				double scale = MathUtil.divide(upperBill.getScale(), 100, 6);
				// 账单计算金额
				calAmount = MathUtil	.decimalFormat(new BigDecimal(MathUtil.multiply(Math.abs(upperBill.getThisLoss()), scale)), 2);

				if (isCheckLossCfg) { // 是否开启亏损核算
					if (userDividend.getFixed() == Global.DAIYU_FIXED_FLOAT) { // 浮动分红比例
						calAmount = 0;
						for (Integer l : levels) {
							scale = MathUtil.divide(Double.valueOf(scaleLevels[l]), 100, 4);
							
							double tempCal = MathUtil.decimalFormat(new BigDecimal(MathUtil.multiply(Math.abs(upperBill.getThisLoss()), scale)), 2);
							
							if(Double.parseDouble(lossLevels[l]) > 0){
								double lossRate = MathUtil.divide(thisLoss, Double.parseDouble(lossLevels[l]), 4);
								if (lossRate < 1) {
									tempCal = MathUtil.decimalFormat(new BigDecimal(String.valueOf(MathUtil.multiply(tempCal, lossRate))), 2);
								}
							}
							
							if(tempCal > calAmount){
								calAmount = tempCal;
								upperBill.setScale(Double.valueOf(scaleLevels[l]));
							}
						}
					} else if (userDividend.getFixed() == Global.DAIYU_FIXED_FIXED) { // 固定分红比例
						// 核算亏损 重新核算比率
						levels.clear();
						calAmount = 0;
						for (int i = 0; i < salesLevels.length; i++) {
							if (billingOrder >= Double.parseDouble(salesLevels[i]) && thisLoss >= Double.parseDouble(lossLevels[i])  && (vaildUser >= Integer.valueOf(userLevels[i]))) {
								levels.add(i);
							}
						}
						if (levels.size() > 0) {
							Collections.sort(levels);
							upperBill.setScale(Double.valueOf(scaleLevels[levels.get(levels.size() - 1)]));
							// 重新 账单计算金额
							scale = MathUtil.divide(upperBill.getScale(), 100, 4);
							calAmount = MathUtil.decimalFormat(new BigDecimal(MathUtil.multiply(Math.abs(upperBill.getThisLoss()), scale)), 2);
						}
					}
				}
			}
			upperBill.setCalAmount(calAmount);

			// 没有达到销量要求
			if (calAmount == 0) {
				upperBill.setCalAmount(0);
				upperBill.setScale(0);
				upperBill.setStatus(Global.DIVIDEND_BILL_NOT_REACHED);
				upperBill.setRemarks("契约分红条款未达标");
			}
		}
		
		int billType = Global.DIVIDEND_ISSUE_TYPE_PLATFORM;
		if(ISTOP){
			if (uCodePointUtil.isLevel2Proxy(user)) {
				upperBill.setCalAmount(0);
				upperBill.setScale(0);
				upperBill.setStatus(Global.DIVIDEND_BILL_ISSUED);
				upperBill.setRemarks("内部招商分红，不结算");
			}else{
				billType = Global.DIVIDEND_ISSUE_TYPE_UPPER;
			}
		}else{
			billType = Global.DIVIDEND_ISSUE_TYPE_UPPER;
		}

		// 往下每级结算，并把总共要结的钱一层层减掉
		double lowerTotalAmount = 0; // 下级共需要发放多少钱
		List<UserDividendBillAdapter> lowerBills = new ArrayList<>();
		if (settleLowers) {
			for (UserLotteryReportVO report : reports) {
				if (!"总计".equals(report.getName()) && !report.getName().equalsIgnoreCase(user.getUsername())) {
					User subUser = userDao.getByUsername(report.getName());

					// 继续往下结
					UserDividendBillAdapter lowerBillAdapter = settleUpWithUser(subUser, sTime, eTime, true, billType);
					if (lowerBillAdapter != null) {

						if (lowerBillAdapter.getUpperBill().getStatus() != Global.DIVIDEND_BILL_NOT_REACHED) {
							lowerTotalAmount = MathUtil.add(lowerTotalAmount,
									lowerBillAdapter.getUpperBill().getCalAmount());
						}

						lowerBills.add(lowerBillAdapter);
					}
				}
			}
		}
		upperBill.setLowerTotalAmount(lowerTotalAmount);
		if(ISTOP && uCodePointUtil.isLevel2Proxy(user)){
			upperBill.setLowerTotalAmount(0);
		}
		return new UserDividendBillAdapter(upperBill, lowerBills);
	}

	/**
	 * 处理一整条线的订单
	 */
	private void processLineBill(UserDividendBillAdapter uDividendBillAdapter) {
		UserDividendBill upperBill = uDividendBillAdapter.getUpperBill();
		List<UserDividendBillAdapter> lowerBills = uDividendBillAdapter.getLowerBills();

		// 不需要向下级发放
		if (CollectionUtils.isEmpty(lowerBills)) {
			if (upperBill.getStatus() == Global.DIVIDEND_BILL_NOT_REACHED) {
				// 未达标
				double userAmount = 0;
				upperBill.setUserAmount(userAmount); // 实际金额
				upperBill.setRemarks("销量或人数未达标");
				saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
			} else {
				double userAmount = upperBill.getCalAmount();
				upperBill.setUserAmount(userAmount); // 实际金额
				if (userAmount == 0) {
					saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
				} else {
					saveBill(upperBill, Global.DIVIDEND_BILL_UNAPPROVE);
				}
			}

			return;
		}

		// 保存下级的账单
		for (UserDividendBillAdapter lowerBill : lowerBills) {
			processLineBill(lowerBill);
		}

		if (upperBill.getCalAmount() == 0 && upperBill.getLowerTotalAmount() == 0) {
			upperBill.setUserAmount(0); // 实际金额
			upperBill.setRemarks("销量或人数未达标");
			saveBill(upperBill, Global.DIVIDEND_BILL_NOT_REACHED);
		} else {
			// 需要向下级发放
			double calAmount = upperBill.getCalAmount();
			double lowerTotalAmount = upperBill.getLowerTotalAmount();

			double userAmount = MathUtil.subtract(calAmount, lowerTotalAmount);
			userAmount = userAmount < 0 ? 0 : userAmount;
			upperBill.setUserAmount(userAmount); // 实际金额
			saveBill(upperBill, Global.DIVIDEND_BILL_UNAPPROVE);
		}
	}

	private void saveBill(UserDividendBill upperBill, int status) {
		upperBill.setSettleTime(new Moment().toSimpleTime());
		upperBill.setStatus(status);
		uDividendBillService.add(upperBill);

		// User user = userDao.getById(dividendBill.getUserId());
		//
		// String time = new Moment().toSimpleTime();
		// // 如果用户无效，那他的账单也设置为无效，并且不加钱
		// if (user.getAStatus() != 0 && user.getAStatus() != -1) {
		// dividendBill.setStatus(Global.DIVIDEND_BILL_DENIED);
		// // dividendBill.setUserAmount(0);
		// dividendBill.setRemarks("用户状态为非正常，系统自动拒绝发放");
		// }
		// else if (dividendBill.getStatus() == Global.DIVIDEND_BILL_ISSUED) {
		// dividendBill.setCollectTime(time);
		// }
		//
		// // 保存数据
		// dividendBill.setSettleTime(time);
		// uDividendBillService.add(dividendBill);
		//
		// return dividendBill;
	}

	/**
	 * 汇总报表
	 */
	private void summaryUpReports(List<UserLotteryReportVO> reports, int userId, String sTime, String eTime,
			UserDividendBill dividendBill) {
		double billingOrder = 0.d; // 总消费
		// 总销量
		for (UserLotteryReportVO report : reports) {
			if ("总计".equals(report.getName())) {
				billingOrder = report.getBillingOrder();
				break;
			}
		}

		// 日均销量
		double dailyBillingOrder = 0;
		if (billingOrder > 0) {
			dailyBillingOrder = MathUtil.divide(billingOrder, 15.0, 4);
		}

		double minBillingOrder = dataFactory.getDividendConfig().getMinBillingOrder();

		 int validUser = sumUserLottery(userId, sTime, eTime, minBillingOrder);
		 // TODO 彩票日均活跃人数
//		int validUser = dayMeanLotteryUser(userId, sTime, eTime, minBillingOrder);

		double thisLoss = calculateLotteryLossByLotteryReport(reports); // 本次彩票亏损
		double lastLoss = calculateLotteryLastLoss(userId, sTime, eTime); // 上半月彩票亏损
//		double thisPeriodCollect =  uDividendBillService.queryPeriodCollect(userId, sTime, eTime);//本周期内领取的分红，计算做当前周期盈利
//		thisLoss = MathUtil.add(thisLoss, thisPeriodCollect);
		double totalLoss = lastLoss > 0 ? MathUtil.add(thisLoss, lastLoss) : thisLoss; // 累计彩票亏损

		dividendBill.setDailyBillingOrder(dailyBillingOrder); // 日均销量
		dividendBill.setBillingOrder(billingOrder); // 销量
		dividendBill.setThisLoss(thisLoss); // 本次亏损
		dividendBill.setLastLoss(lastLoss); // 上半月亏损
		dividendBill.setTotalLoss(totalLoss); // 累计亏损
		dividendBill.setValidUser(validUser); // 实际有效会员数
	}

	private int sumUserLottery(int userId, String sTime, String eTime, double minBillingOrder) {
		List<User> userLowers = userDao.getUserLower(userId);
		int validUser = 0;
		for (User lowerUser : userLowers) {
			double lowerUserBillingOrder = summaryUpLotteryLowerUserBillingOrder(lowerUser.getId(), sTime, eTime);
			if (lowerUserBillingOrder >= minBillingOrder) {
				validUser++;
			}
		}

		// 自己的彩票数据
		double selfLotteryBilling = summaryUpLotteryLowerUserBillingOrder(userId, sTime, eTime);
		if (selfLotteryBilling >= minBillingOrder) {
			validUser++;
		}
		return validUser;
	}

	/**
	 * 汇总用户时间段内彩票消费
	 */
	private double summaryUpLotteryLowerUserBillingOrder(int userId, String sTime, String eTime) {
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

	/*
	 * 日均人数
	 */
	private int dayMeanLotteryUser(int userId, String sTime, String eTime, double minBillingOrder) {
		int sum = 0;
		List<User> userLowers = userDao.getUserLower(userId); // 查询所有下级

		// 构建日期模型
		Map<String, Integer> daySum = new HashMap<>();
		String[] dates = DateUtil.getDateArray(sTime, eTime);
		for (int i = 0; i < dates.length - 1; i++) {
			daySum.put(dates[i], 0);
		}

		// 填充日期用户活跃数 下级
		for (User lowerUser : userLowers) {
			List<UserLotteryReport> lowerUserReports = uLotteryReportDao.list(lowerUser.getId(), sTime, eTime);
			for (UserLotteryReport lowerUserReport : lowerUserReports) {
				if (lowerUserReport.getBillingOrder() >= minBillingOrder) { // 有效人数据销量要求
					int num = daySum.get(lowerUserReport.getTime()) + 1;
					daySum.put(lowerUserReport.getTime(), num);
				}
			}
		}

		// 填充日期用户活跃数 自己
		List<UserLotteryReport> lowerUserReports = uLotteryReportDao.list(userId, sTime, eTime);
		for (UserLotteryReport lowerUserReport : lowerUserReports) {
			if (lowerUserReport.getBillingOrder() >= minBillingOrder) { // 有效人数据销量要求
				int num = daySum.get(lowerUserReport.getTime()) + 1;
				daySum.put(lowerUserReport.getTime(), num);
			}
		}

		for (String key : daySum.keySet()) {
			sum += daySum.get(key);
		}

		int result = sum / (dates.length - 1);
		return result;
	}

	/**
	 * 汇总用户时间段内游戏消费
	 */
	private double summaryUpGameLowerUserBillingOrder(int userId, String sTime, String eTime) {
		List<UserGameReport> lowerUserReports = uGameReportDao.list(userId, sTime, eTime);
		if (CollectionUtils.isEmpty(lowerUserReports)) {
			return 0;
		}

		double billingOrder = 0;
		for (UserGameReport lowerUserReport : lowerUserReports) {
			billingOrder = MathUtil.add(billingOrder, lowerUserReport.getBillingOrder());
		}

		return billingOrder;
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

	/**
	 * 计算游戏亏损
	 */
	private double calculateGameLossByGameReport(List<UserGameReportVO> reports) {
		double gameLoss = 0;
		// 总销量
		for (UserGameReportVO report : reports) {
			if ("总计".equals(report.getName())) {
				gameLoss = report.getPrize() + report.getWaterReturn() + report.getProxyReturn() + report.getActivity()
						- report.getBillingOrder();
				break;
			}
		}
		return gameLoss;
	}

	/**
	 * 彩票上半月亏损
	 */
	private double calculateLotteryLastLoss(int userId, String sTime, String eTime) {
		String currDate = sTime;
		if (StringUtil.isNotNull(currDate)) {

			// 确定是否是下半月
			int currDay = Integer.valueOf(currDate.substring(8));
			if (currDay == 16) { // 今天16号，那今天就是下半月
				String lastStartDate = currDate.substring(0, 8) + "01";
				String lastEndDate = currDate;

				// // 上半月分红情况
				List<UserLotteryReportVO> lastReports = uLotteryReportService.report(userId, lastStartDate,
						lastEndDate);
				double lastLoss = calculateLotteryLossByLotteryReport(lastReports);
				return lastLoss;
			}
		}

		return 0;
	}

	/**
	 * 游戏上半月亏损
	 */
	private double calculateGameLastLoss(int userId, String sTime, String eTime) {
		String currDate = sTime;
		if (StringUtil.isNotNull(currDate)) {

			// 确定是否是下半月
			int currDay = Integer.valueOf(currDate.substring(8));
			if (currDay == 16) { // 今天16号，那今天就是下半月
				String lastStartDate = currDate.substring(0, 8) + "01";
				String lastEndDate = currDate;

				// // 上半月分红情况
				List<UserGameReportVO> lastReports = uGameReportService.report(userId, lastStartDate, lastEndDate);
				double lastLoss = calculateGameLossByGameReport(lastReports);
				return lastLoss;
			}
		}

		return 0;
	}

	private String getStartDate() {
		Moment moment = new Moment().add(-1, "days");
		int day = moment.day();

		if (day <= 15) {
			moment = moment.day(1);
		} else {
			moment = moment.day(16);
		}

		return moment.toSimpleDate();
	}

	private String getEndDate() {
		Moment moment = new Moment().add(-1, "days");
		int day = moment.day();

		if (day <= 15) {
			moment = moment.day(16);
		} else {
			moment = moment.add(1, "months");
			moment = moment.day(1);
		}

		return moment.toSimpleDate();
	}

	private void sendMail(List<UserDividendBillAdapter> bills, String sTime, String eTime) {
		double platformTotalLoss = 0; // 平级总亏损
		double platformTotalAmount = 0; // 平级总发放

		if (CollectionUtils.isNotEmpty(bills)) {

			List<UserDividendBill> allBills = new ArrayList<>();
			getAllBills(bills, allBills);

			for (UserDividendBill bill : allBills) {
				if (bill.getIssueType() != Global.DIVIDEND_ISSUE_TYPE_PLATFORM)
					continue;

				if (bill.getStatus() != Global.DIVIDEND_BILL_ISSUED
						&& bill.getStatus() != Global.DIVIDEND_BILL_UNAPPROVE
						&& bill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT
						&& bill.getStatus() != Global.DIVIDEND_BILL_INSUFFICIENT) {
					continue;
				}

				platformTotalAmount = MathUtil.add(platformTotalAmount, bill.getCalAmount());

				if (bill.getThisLoss() >= 0)
					continue;

				platformTotalLoss = MathUtil.add(platformTotalLoss, bill.getThisLoss());
			}
		}

		double totalBillingOrder = 0; // 总销量
		double totalLoss = 0; // 总亏损
		List<UserLotteryReportVO> lotteryReportVOs = uLotteryReportService.report(sTime, eTime);
		if (CollectionUtils.isNotEmpty(lotteryReportVOs)) {
			for (UserLotteryReportVO report : lotteryReportVOs) {
				if ("总计".equals(report.getName())) {
					totalBillingOrder += report.getBillingOrder();
					totalLoss += calculateLotteryLossByLotteryReport(lotteryReportVOs);
					break;
				}
			}
		}

		mailJob.sendDividend(sTime, eTime, totalBillingOrder, totalLoss, platformTotalLoss, platformTotalAmount);
	}

	private List<UserDividendBill> getAllBills(List<UserDividendBillAdapter> bills, List<UserDividendBill> container) {
		if (CollectionUtils.isEmpty(bills)) {
			return container;
		}

		for (UserDividendBillAdapter bill : bills) {
			container.add(bill.getUpperBill());

			getAllBills(bill.getLowerBills(), container);
		}

		return container;
	}
}