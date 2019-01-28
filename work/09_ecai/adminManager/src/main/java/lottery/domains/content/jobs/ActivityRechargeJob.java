// package lottery.domains.content.jobs;
//
// import java.util.List;
//
// import javautils.date.Moment;
// import lottery.domains.content.dao.ActivityRebateDao;
// import lottery.domains.content.dao.ActivityRechargeBillDao;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.UserLoginLogDao;
// import lottery.domains.content.dao.UserRechargeDao;
// import lottery.domains.content.entity.ActivityRebate;
// import lottery.domains.content.entity.ActivityRechargeBill;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.UserLoginLog;
// import lottery.domains.content.entity.UserRecharge;
// import lottery.domains.content.entity.activity.RebateRulesRecharge;
// import lottery.domains.content.global.Global;
// import net.sf.json.JSONArray;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// /**
//  * 开业大酬宾活动
//  */
// @Component
// public class ActivityRechargeJob {
//
// 	@Autowired
// 	private ActivityRebateDao aRebateDao;
//
// 	@Autowired
// 	private ActivityRechargeBillDao aRechargeBillDao;
//
// 	@Autowired
// 	private UserDao uDao;
//
// 	@Autowired
// 	private UserRechargeDao uRechargeDao;
//
// 	@Autowired
// 	private UserLoginLogDao uLoginLogDao;
//
// 	@SuppressWarnings("unchecked")
// 	List<RebateRulesRecharge> listRules(String rules) {
// 		return (List<RebateRulesRecharge>) JSONArray.toCollection(JSONArray.fromObject(rules), RebateRulesRecharge.class);
// 	}
//
// 	boolean isRunning = false;
// 	@Scheduled(cron = "0 0/1 * * * *")
// 	public void start() {
// 		if(!isRunning) {
// 			isRunning = true;
// 			try {
// 				System.out.println("开业大酬宾活动...");
// 				String thisDate = new Moment().toSimpleDate();
// 				ActivityRebate activity = aRebateDao.getByType(Global.ACTIVITY_REBATE_RECHARGE);
// 				if(activity.getStatus() == 0) {
// 					List<RebateRulesRecharge> rList = listRules(activity.getRules());
// 					List<UserRecharge> cList = uRechargeDao.listByDate(thisDate);
// 					for (UserRecharge tmpBean : cList) {
// 						try {
// 							System.out.println("RechargeMoney:" + tmpBean.getMoney());
// 							double totalReward = 0;
// 							for (RebateRulesRecharge rules : rList) {
// 								if(tmpBean.getMoney() >= rules.getMoney()) {
// 									if(rules.getReward() > totalReward) {
// 										totalReward = rules.getReward();
// 									}
// 								}
// 							}
// 							System.out.println("RewardMoney:" + totalReward);
// 							if(totalReward > 0) {
// 								doCheck(tmpBean, totalReward);
// 							}
// 						} catch (Exception e) {
// 							e.printStackTrace();
// 						}
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
// 	void doCheck(UserRecharge cBean, double money) {
// 		Moment thisTime = new Moment();
// 		boolean hasRecord = aRechargeBillDao.hasDateRecord(cBean.getUserId(), thisTime.toSimpleDate());
// 		if(!hasRecord) {
// 			User uBean = uDao.getById(cBean.getUserId());
// 			if(uBean != null) {
// 				if(uBean.getBindStatus() == 1) {
// 					UserLoginLog loginLog = uLoginLogDao.getLastLogin(uBean.getId());
// 					if(loginLog != null) {
// 						int userId = uBean.getId();
// 						String ip = loginLog.getIp();
// 						double totalMoney = cBean.getMoney();
// 						String payTime = cBean.getPayTime();
// 						String time = thisTime.toSimpleTime();
// 						int status = 0;
// 						ActivityRechargeBill entity = new ActivityRechargeBill(userId, ip, totalMoney, money, payTime, time, status);
// 						aRechargeBillDao.add(entity);
// 					} else {
// 						System.out.println("No login log");
// 					}
// 				} else {
// 					System.out.println("User not Bind");
// 				}
// 			} else {
// 				System.out.println("User is null");
// 			}
// 		} else {
// 			System.out.println("User hasRecord");
// 		}
// 	}
//
// }