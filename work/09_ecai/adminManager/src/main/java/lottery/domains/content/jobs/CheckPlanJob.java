// package lottery.domains.content.jobs;
//
// import lottery.domains.content.biz.UserBillService;
// import lottery.domains.content.dao.*;
// import lottery.domains.content.entity.*;
// import lottery.domains.content.global.Global;
// import lottery.domains.pool.LotteryDataFactory;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
//
// @Component
// public class CheckPlanJob {
//
// 	private static final Logger logger = LoggerFactory.getLogger(CheckPlanJob.class);
//
// 	@Autowired
// 	private UserDao uDao;
//
// 	@Autowired
// 	private UserBetsDao uBetsDao;
//
// 	@Autowired
// 	private UserBetsPlanDao uBetsPlanDao;
//
// 	@Autowired
// 	private UserPlanInfoDao uPlanInfoDao;
//
// 	@Autowired
// 	private LotteryOpenCodeDao lotteryOpenCodeDao;
//
// 	@Autowired
// 	private UserBillService uBillService;
//
// 	@Autowired
// 	private LotteryDataFactory lotteryDataFactory;
//
// 	private boolean isRunning = false;
//
// 	/**
// 	 * 每10秒检测一次
// 	 */
// 	@Scheduled(cron = "0/10 * * * * *")
// 	public void run() {
// 		if(!isRunning) {
// 			try {
// 				isRunning = true;
// 				System.out.println("开始检测计划...");
// 				List<UserBetsPlan> list = uBetsPlanDao.listUnsettled();
// 				System.out.println("未处理计划数量：" + list.size());
// 				for (UserBetsPlan tmpPlanBean : list) {
// 					Lottery thisLottery = lotteryDataFactory.getLottery(tmpPlanBean.getLotteryId());
// 					LotteryOpenCode thisOpenCode = lotteryOpenCodeDao.get(thisLottery.getShortName(), tmpPlanBean.getExpect());
// 					if(thisOpenCode != null && thisOpenCode.getOpenStatus() == 1) {
// 						if(tmpPlanBean.getStatus() == 0) {
// 							UserBets betBean = uBetsDao.getById(tmpPlanBean.getOrderId());
// 							//bill add
// 							if(betBean == null){
// 								continue;
// 							}
// 							// *********
// 							// 如果是已经开奖状态
// 							if(betBean.getStatus() > 0) {
// 								System.out.println("本期已开奖，可以进行结算：" + thisOpenCode.getExpect());
// 								// 1.更新计划为已处理状态 2.更新用户计划中奖累计
// 								boolean pFlag = uBetsPlanDao.updateStatus(tmpPlanBean.getId(), 1, betBean.getPrizeMoney());
// 								if(pFlag) {
// 									int planCount = 1;
// 									int prizeCount = 0;
// 									double totalMoney = betBean.getMoney();
// 									double totalPrize = 0;
// 									List<UserBets> followList = uBetsDao.getByFollowBillno(tmpPlanBean.getBillno(), false);
// 									// 中奖
// 									if(betBean.getStatus() == Global.USER_BETS_STATUS_WIN) {
// 										prizeCount = 1;
// 										totalPrize = betBean.getPrizeMoney();
// 										// 清空冻结佣金，给发起用户价钱
// 										double totalRewardMoney = 0;
// 										for (UserBets tmpFollow : followList) {
// 											Double rewardMoney = tmpFollow.getRewardMoney();
// 											totalRewardMoney += rewardMoney.doubleValue();
// 											uDao.updateFreezeMoney(tmpFollow.getUserId(), -rewardMoney.doubleValue());
// 										}
// 										// 给发起用户加钱
// 										User planUser = uDao.getById(tmpPlanBean.getUserId());
// 										if(planUser != null) {
// 											boolean tFlag = uDao.updateLotteryMoney(planUser.getId(), totalRewardMoney);
// 											if(tFlag) {
// 												String incomeRemarks = "用户跟单获取佣金";
// 												uBillService.addRewardIncomeBill(planUser, Global.BILL_ACCOUNT_LOTTERY, totalRewardMoney, incomeRemarks);
// 											}
// 										}
// 									} else {
// 										// 返还冻结佣金
// 										for (UserBets tmpFollow : followList) {
// 											Double rewardMoney = tmpFollow.getRewardMoney();
// 											// 清空冻结，并返还佣金
// 											try {
// 												User followUser = uDao.getById(tmpFollow.getUserId());
// 												if(followUser != null) {
// 													boolean returnFlag = uDao.updateLotteryMoney(tmpFollow.getUserId(), rewardMoney.doubleValue(), -rewardMoney.doubleValue());
// 													if(returnFlag) {
// 														// 账单
// 														String returnRemarks = "订单未中奖退还佣金";
// 														uBillService.addRewardReturnBill(followUser, Global.BILL_ACCOUNT_LOTTERY, rewardMoney.doubleValue(), returnRemarks);
// 													}
// 												}
// 											} catch (Exception e) {
// 												e.printStackTrace();
// 											}
// 										}
// 									}
// 									boolean flag = uPlanInfoDao.update(tmpPlanBean.getUserId(), planCount, prizeCount, totalMoney, totalPrize);
// 									if(flag) {
// 										// 检查等级，并更新等级
// 										UserPlanInfo uPlanInfo = uPlanInfoDao.get(tmpPlanBean.getUserId());
// 										List<Integer> levelList = lotteryDataFactory.getPlanConfig().getLevel();
// 										int level = 0;
// 										for (int i = 0; i < levelList.size(); i++) {
// 											if(uPlanInfo.getTotalPrize() > levelList.get(i).intValue()) {
// 												level = i;
// 											}
// 										}
// 										uPlanInfoDao.updateLevel(tmpPlanBean.getUserId(), level);
// 									}
// 								}
// 							}
// 						}
// 					}
// 				}
// 			} catch (Exception e) {
// 				logger.error("处理合买计划订单检查出错！", e);
// 			} finally {
// 				isRunning = false;
// 			}
// 		}
// 	}
//
// }