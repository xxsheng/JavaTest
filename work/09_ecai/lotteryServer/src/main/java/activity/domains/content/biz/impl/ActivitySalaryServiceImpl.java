// package activity.domains.content.biz.impl;
//
// import java.util.List;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
//
// import javautils.date.Moment;
// import activity.domains.content.biz.ActivitySalaryService;
// import activity.domains.content.dao.ActivityRebateDao;
// import activity.domains.content.dao.ActivitySalaryBillDao;
// import activity.domains.content.entity.ActivityRebate;
// import activity.domains.content.entity.ActivitySalaryBill;
// import activity.domains.content.entity.activity.RebateRulesSalary;
// import activity.domains.content.vo.activity.ActivitySalaryVO;
// import lottery.domains.content.biz.UserBillService;
// import lottery.domains.content.biz.UserLotteryReportService;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.UserLotteryReportDao;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.global.Global;
// import lottery.domains.content.vo.bill.UserLotteryReportVO;
// import lottery.web.WebJSON;
// import net.sf.json.JSONArray;
//
// @Service
// public class ActivitySalaryServiceImpl implements ActivitySalaryService {
//
// 	@Autowired
// 	private UserDao uDao;
//
// 	@Autowired
// 	private ActivityRebateDao aRebateDao;
//
// 	@Autowired
// 	private ActivitySalaryBillDao aSalaryBillDao;
//
// 	@Autowired
// 	private UserLotteryReportDao mUserLotteryReportDao;
//
// 	@Autowired
// 	private UserBillService uBillService;
//
// 	@Autowired
// 	private UserLotteryReportService uLotteryReportService;
//
// 	@SuppressWarnings("unchecked")
// 	List<RebateRulesSalary> listRules(String rules) {
// 		return (List<RebateRulesSalary>) JSONArray.toCollection(JSONArray.fromObject(rules), RebateRulesSalary.class);
// 	}
//
// 	UserLotteryReportVO report(int userId, String sTime, String eTime) {
// 		List<UserLotteryReportVO> report = uLotteryReportService.report(userId, sTime, eTime);
// 		return report.get(0);
// 	}
//
// 	@Override
// 	public ActivitySalaryVO get(int userId, int type) {
// 		// 统计活跃会员人数
// 		List<User> list = uDao.getUserLower(userId);
// 		int[] targetUsers = new int[list.size()];
// 		for (int i = 0, j = list.size(); i < j; i++) {
// 			targetUsers[i] = list.get(i).getId();
// 		}
// 		String yesterday = new Moment().subtract(1,"d").toSimpleDate();
// 		String today = new Moment().toSimpleDate();
// 		int activityUser = mUserLotteryReportDao.getActivityUser(targetUsers, yesterday, today);
//
// 		ActivityRebate activity = null;
// 		if(type == 1) {
// 			activity = aRebateDao.getByType(Global.ACTIVITY_REBATE_SALARY_ZHISHU);
// 		}
// 		if(type == 2) {
// 			activity = aRebateDao.getByType(Global.ACTIVITY_REBATE_SALARY_ZONGDAI);
// 		}
// 		if(activity != null) {
// 			// 这个是计算昨天的消费量
// 			Moment thisTime = new Moment();
// 			Moment lastTime = new Moment().subtract(1, "days");
// 			UserLotteryReportVO report = report(userId, lastTime.toSimpleDate(), thisTime.toSimpleDate());
// 			double totalMoney = report.getBillingOrder();
// 			//double totalProfit = report.getPrize() + report.getSpendReturn() + report.getProxyReturn() + report.getActivity() - report.getBillingOrder();
// 			List<RebateRulesSalary> rlist = listRules(activity.getRules());
// 			boolean isReceived = false;
// 			boolean hasRecord = aSalaryBillDao.hasRecord(userId, type, lastTime.toSimpleDate());
// 			if(hasRecord) {
// 				isReceived = true;
// 			}
// 			ActivitySalaryVO bean = new ActivitySalaryVO();
// 			bean.setTotalMoney(totalMoney);
// 			bean.setRlist(rlist);
// 			bean.setPeople(activityUser);
// 			bean.setReceived(isReceived);
// 			return bean;
// 		}
// 		return null;
// 	}
//
// 	@Override
// 	public synchronized boolean receive(WebJSON json, int userId, int type) {
// 		ActivitySalaryVO activity = get(userId, type);
// 		if (activity != null) {
// 			if (!activity.isReceived()) {
// 				double totalMoney = activity.getTotalMoney();
// 				int people = activity.getPeople();
// 				double totalReward = 0;
// 				for (RebateRulesSalary rules : activity.getRlist()) {
// 					if (totalMoney >= rules.getMoney()
// 							&& people >= rules.getPeople()) {
// 						if (rules.getReward() > totalReward) {
// 							totalReward = rules.getReward();
// 						}
// 					}
// 				}
// 				if (totalReward > 0) {
// 					User uBean = uDao.getById(userId);
// 					if (uBean != null) {
// 						Moment thisTime = new Moment();
// 						Moment lastTime = new Moment().subtract(1, "days");
// 						ActivitySalaryBill entity = new ActivitySalaryBill(
// 								userId, type, totalMoney, totalReward,
// 								lastTime.toSimpleDate(),
// 								thisTime.toSimpleTime());
// 						boolean bFlag = aSalaryBillDao.add(entity);
// 						if (bFlag) {
// 							boolean uFlag = uDao.updateLotteryMoney(
// 									uBean.getId(), totalReward);
// 							if (uFlag) {
// 								if (type == 1) {
// 									String remarks = "直属日工资活动。";
// 									Integer refType = 1; // 其他活动类型
// 									uBillService.addActivityBill(uBean,
// 											Global.BILL_ACCOUNT_LOTTERY,
// 											totalReward, refType, remarks);
// 								}
// 								if (type == 2) {
// 									String remarks = "总代日工资活动。";
// 									Integer refType = 1; // 其他活动类型
// 									uBillService.addActivityBill(uBean,
// 											Global.BILL_ACCOUNT_LOTTERY,
// 											totalReward, refType, remarks);
// 								}
// 							}
// 							return uFlag;
// 						}
// 					}
// 				} else {
// 					json.set(2, "2-4006");
// 				}
// 			} else {
// 				json.set(2, "2-4005");
// 			}
// 		} else {
// 			json.set(2, "2-4002");
// 		}
// 		return false;
// 	}
//
// }