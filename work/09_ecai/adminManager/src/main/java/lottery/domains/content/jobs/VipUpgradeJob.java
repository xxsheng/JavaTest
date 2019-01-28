 // package lottery.domains.content.jobs;
 //
 // import javautils.date.Moment;
 // import lottery.domains.content.biz.UserSysMessageService;
 // import lottery.domains.content.biz.VipUpgradeGiftsService;
 // import lottery.domains.content.biz.VipUpgradeListService;
 // import lottery.domains.content.dao.UserDao;
 // import lottery.domains.content.dao.UserLotteryReportDao;
 // import lottery.domains.content.dao.UserMainReportDao;
 // import lottery.domains.content.entity.User;
 // import lottery.domains.content.entity.UserLotteryReport;
 // import lottery.domains.content.entity.UserMainReport;
 // import lottery.domains.content.vo.bill.UserLotteryReportVO;
 // import lottery.domains.content.vo.bill.UserMainReportVO;
 // import org.slf4j.Logger;
 // import org.slf4j.LoggerFactory;
 // import org.springframework.beans.factory.annotation.Autowired;
 // import org.springframework.scheduling.annotation.Scheduled;
 // import org.springframework.stereotype.Component;
 //
 // import java.util.List;
 //
 // /**
 //  * VIP晋级任务
 //  */
 // @Component
 // public class VipUpgradeJob {
 // 	private static final Logger logger = LoggerFactory.getLogger(VipUpgradeJob.class);
 //
 // 	@Autowired
 // 	private UserDao uDao;
 //
 // 	@Autowired
 // 	private UserMainReportDao uMainReportDao;
 //
 // 	@Autowired
 // 	private UserLotteryReportDao uLotteryReportDao;
 //
 // 	@Autowired
 // 	private UserSysMessageService uSysMessageService;
 //
 // 	@Autowired
 // 	private VipUpgradeGiftsService vUpgradeGiftsService;
 //
 // 	@Autowired
 // 	private VipUpgradeListService vUpgradeListService;
 //
 // 	final double[] upRecharge = {10000, 30000, 150000, 500000, 1000000};
 // 	final double[] upCost = {50000, 150000, 1500000, 5000000, 10000000};
 // 	final double[] keepRecharge = {8000, 20000, 100000, 400000, 800000};
 //
 // 	final String[] vipName = {"普通会员", "青铜 VIP", "紫晶 VIP", "白银 VIP", "黄金 VIP", "钻石 VIP", "至尊 VIP"};
 //
 // 	/**
 // 	 * 判断晋级
 // 	 */
 // 	int testLevelUp(double recharge, double cost) {
 // 		int level = 0;
 // 		for (int i = 0; i < 5; i++) {
 // 			if(recharge >= upRecharge[i] && cost >= upCost[i]) {
 // 				level = i + 1;
 // 			}
 // 		}
 // 		return level;
 // 	}
 //
 // 	/**
 // 	 * 判断是否保留当前VIP席位
 // 	 */
 // 	boolean testKeepLevel(int level, double recharge) {
 // 		if(recharge >= keepRecharge[level - 1]) {
 // 			return true;
 // 		}
 // 		return false;
 // 	}
 //
 // 	boolean isRunning = false;
 // 	@Scheduled(cron = "0 0 1 10 * *")
 // 	public void start() {
 // 		if(!isRunning) {
 // 			isRunning = true;
 // 			try {
 // 				String sTime = new Moment().day(1).subtract(1, "months").toSimpleDate();
 // 				String eTime = new Moment().day(1).toSimpleDate();
 // 				String month = new Moment().format("yyyy-MM");
 // 				System.out.println("VIP晋级检测..." + sTime + "至" + eTime);
 // 				List<User> list = uDao.listAll();
 // 				for (User tmpBean : list) {
 // 					try {
 // 						int vipLevel = tmpBean.getVipLevel();
 // 						if(vipLevel < 6) {
 // 							UserMainReportVO main = mainReport(tmpBean.getId(), sTime, eTime);
 // 							UserLotteryReportVO lottery = lotteryReport(tmpBean.getId(), sTime, eTime);
 // 							double lastMonthRecharge = main.getRecharge();
 // 							double lastMonthCost = lottery.getBillingOrder();
 // 							logger.info(tmpBean.getUsername() + "上个月充值：" + lastMonthRecharge + "，消费：" + lastMonthCost);
 // 							int levelUp = testLevelUp(lastMonthRecharge, lastMonthCost);
 // 							if(levelUp > vipLevel) {
 // 								// 可以晋级
 // 								logger.info(tmpBean.getUsername() + "可以从vip" + vipLevel + "晋级到vip" + levelUp);
 // 								boolean vFlag = vUpgradeListService.add(tmpBean.getId(), vipLevel, levelUp, lastMonthRecharge, lastMonthCost, month);
 // 								if(vFlag) {
 // 									doLevelUp(tmpBean.getId(), vipLevel, levelUp);
 // 								}
 // 							} else {
 // 								if(vipLevel > 0) {
 // 									if(testKeepLevel(vipLevel, lastMonthRecharge)) {
 // 										// 可以保留席位
 // 										logger.info(tmpBean.getUsername() + "可以保持vip" + vipLevel + "席位");
 // 									} else {
 // 										// 需要降一级
 // 										int levelDown = vipLevel - 1;
 // 										logger.info(tmpBean.getUsername() + "需要从vip" + vipLevel + "降级到" + levelDown);
 // 										boolean vFlag = vUpgradeListService.add(tmpBean.getId(), vipLevel, levelDown, lastMonthRecharge, lastMonthCost, month);
 // 										if(vFlag) {
 // 											doLevelDown(tmpBean.getId(), levelDown);
 // 										}
 // 									}
 // 								}
 // 							}
 // 						}
 // 					} catch (Exception e) {
 // 						e.printStackTrace();
 // 					}
 // 				}
 // 			} catch (Exception e) {
 // 				e.printStackTrace();
 // 			} finally {
 // 				isRunning = false;
 // 			}
 // 		}
 // 	}
 //
 // 	/**
 // 	 * 晋级
 // 	 */
 // 	boolean doLevelUp(int userId, int beforeLevel, int afterLevel) {
 // 		boolean uFlag = uDao.updateVipLevel(userId, afterLevel);
 // 		if(uFlag) {
 // 			// 发送晋级通知
 // 			uSysMessageService.addVipLevelUp(userId, vipName[afterLevel]);
 // 			// 开始发放晋级礼包
 // 			vUpgradeGiftsService.doIssuingGift(userId, beforeLevel, afterLevel);
 // 		}
 // 		return uFlag;
 // 	}
 //
 // 	/**
 // 	 * 降级
 // 	 */
 // 	boolean doLevelDown(int userId, int level) {
 // 		return uDao.updateVipLevel(userId, level);
 // 	}
 //
 // 	UserMainReportVO mainReport(int userId, String sTime, String eTime) {
 // 		List<UserMainReport> list = uMainReportDao.list(userId, sTime, eTime);
 // 		UserMainReportVO result = new UserMainReportVO();
 // 		for (UserMainReport tmpBean : list) {
 // 			result.addBean(tmpBean);
 // 		}
 // 		return result;
 // 	}
 //
 // 	UserLotteryReportVO lotteryReport(int userId, String sTime, String eTime) {
 // 		List<UserLotteryReport> list = uLotteryReportDao.list(userId, sTime, eTime);
 // 		UserLotteryReportVO result = new UserLotteryReportVO();
 // 		for (UserLotteryReport tmpBean : list) {
 // 			result.addBean(tmpBean);
 // 		}
 // 		return result;
 // 	}
 //
 // }