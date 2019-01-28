// package lottery.domains.content.biz.impl;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.hibernate.criterion.Criterion;
// import org.hibernate.criterion.Order;
// import org.hibernate.criterion.Restrictions;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
//
// import javautils.StringUtil;
// import javautils.date.Moment;
// import javautils.jdbc.PageList;
// import lottery.domains.content.biz.UserBetsPlanService;
// import lottery.domains.content.biz.UserPlanInfoService;
// import lottery.domains.content.dao.UserBetsDao;
// import lottery.domains.content.dao.UserBetsPlanDao;
// import lottery.domains.content.entity.Lottery;
// import lottery.domains.content.entity.LotteryPlayRules;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.UserBets;
// import lottery.domains.content.entity.UserBetsPlan;
// import lottery.domains.content.entity.UserPlanInfo;
// import lottery.domains.content.global.Global;
// import lottery.domains.content.vo.bets.UserBetsPlanListVO;
// import lottery.domains.content.vo.bets.UserBetsPlanVO;
// import lottery.domains.content.vo.config.LotteryConfig;
// import lottery.domains.content.vo.config.PlanConfig;
// import lottery.domains.pool.DataFactory;
// import lottery.domains.utils.prize.PrizeUtils;
// import lottery.web.WebJSON;
// import org.springframework.transaction.annotation.Transactional;
//
// @Service
// public class UserBetsPlanServiceImpl implements UserBetsPlanService {
//
// 	/**
// 	 * DAO
// 	 */
// 	@Autowired
// 	private UserBetsDao uBetsDao;
//
// 	@Autowired
// 	private UserBetsPlanDao uBetsPlanDao;
//
// 	@Autowired
// 	private UserPlanInfoService uPlanInfoService;
//
// 	/**
// 	 * DataFactory
// 	 */
// 	@Autowired
// 	private DataFactory dataFactory;
//
// 	@Override
// 	public boolean publish(WebJSON json, String billno, int orderId, User uEntity, int tid, int rate) {
// 		UserBets bBean = uBetsDao.getById(orderId, uEntity.getId());
// 		if(bBean != null) {
// 			// 发表订单金额必须大于最低金额
// 			PlanConfig planConfig = dataFactory.getPlanConfig();
// 			if(bBean.getMoney() >= planConfig.getMinMoney()) {
// 				Moment thisTime = new Moment();
// 				Moment stopTime = new Moment().fromTime(bBean.getStopTime());
// 				// 只有普通订单才可以发表成计划
// 				if(bBean.getType() == Global.USER_BETS_TYPE_GENERAL) {
// 					if (bBean.getStatus() == 0 && thisTime.lt(stopTime)) {
// 						// 检查该用户本期有没有发表过计划
// 						boolean hasRecord = uBetsPlanDao.hasRecord(uEntity.getId(), bBean.getLotteryId(), bBean.getExpect());
// 						if(!hasRecord) {
// 							UserPlanInfo uPlanInfo = uPlanInfoService.get(uEntity.getId());
// 							String title = planConfig.getTitle().get(tid);
// 							List<Integer> rateList = planConfig.getRate();
// 							Integer maxRate = rateList.get(uPlanInfo.getLevel());
// 							if(rate < 0 || rate > maxRate) {
// 								// 佣金率不正确
// 								json.set(2, "2-1123");
// 								return false;
// 							}
// 							// 计算盈利金额
// 							LotteryConfig config = dataFactory.getLotteryConfig();
// 							int bUnitMoney = config.getbUnitMoney();
// 							// 彩票
// 							Lottery lottery = dataFactory.getLottery(bBean.getLotteryId());
// 							if(lottery == null) return false;
// 							LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getType(), bBean.getMethod());
// 							double maxUnitPrize = PrizeUtils.getMoney(rule, bBean.getModel(), bUnitMoney, uEntity.getCode());
// 							double maxPrize = maxUnitPrize * bBean.getMultiple();
//
// 							String time = new Moment().toSimpleTime();
//
// 							UserBetsPlan entity = new UserBetsPlan(billno, uEntity.getId(), orderId, bBean.getLotteryId(), bBean.getExpect(), bBean.getMethod(), bBean.getNums(), bBean.getModel(), bBean.getMultiple(), bBean.getMoney(), maxPrize, title, rate, 0, 0, time, 0, 0, 0);
// 							boolean flag = uBetsPlanDao.add(entity);
// 							if(flag) {
// 								// 更新为计划订单
// 								return uBetsDao.updateToPlan(bBean.getId(), Global.USER_BETS_TYPE_PLAN, billno);
// 							}
// 							return flag;
// 						} else {
// 							json.set(2, "2-1114");
// 						}
// 					} else {
// 						json.set(2, "2-1113");
// 					}
// 				} else {
// 					json.set(2, "2-1116");
// 				}
// 			} else {
// 				json.set(2, "2-1121", planConfig.getMinMoney());
// 			}
// 		} else {
// 			json.set(2, "2-1028");
// 		}
// 		return false;
// 	}
//
// 	@Override
// 	public PageList search(String billno, int lotteryId, String expect, String method, int start, int limit) {
// 		start = start < 0 ? 0 : start;
// 		limit = limit < 0 ? 0 : limit;
// 		limit = limit > 10 ? 10 : limit;
// 		List<Criterion> criterions = new ArrayList<>();
// 		if(StringUtil.isNotNull(billno)) {
// 			criterions.add(Restrictions.eq("billno", billno));
// 		}
// 		criterions.add(Restrictions.eq("lotteryId", lotteryId));
// 		criterions.add(Restrictions.eq("expect", expect));
// 		if(StringUtil.isNotNull(method)) {
// 			criterions.add(Restrictions.eq("method", method));
// 		}
// 		List<Order> orders = new ArrayList<>();
// 		orders.add(Order.desc("id"));
// 		PageList pList = uBetsPlanDao.search(criterions, orders, start, limit);
// 		List<UserBetsPlanListVO> list = new ArrayList<>();
// 		for (Object tmpBean : pList.getList()) {
// 			UserBetsPlan planBean = (UserBetsPlan) tmpBean;
// 			UserPlanInfo infoBean = uPlanInfoService.get(planBean.getUserId());
// 			list.add(new UserBetsPlanListVO(planBean, infoBean, dataFactory));
// 		}
// 		pList.setList(list);
// 		return pList;
// 	}
//
// 	@Override
// 	public PageList search(Integer[] targetUsers, Integer lotteryId,
// 			String expect, String sTime, String eTime, int start, int limit) {
// 		start = start < 0 ? 0 : start;
// 		limit = limit < 0 ? 0 : limit;
// 		limit = limit > 10 ? 10 : limit;
// 		List<Criterion> criterions = new ArrayList<>();
// 		criterions.add(Restrictions.in("userId", targetUsers));
// 		if(lotteryId != null) {
// 			criterions.add(Restrictions.eq("lotteryId", lotteryId.intValue()));
// 		}
// 		if(StringUtil.isNotNull(expect)) {
// 			criterions.add(Restrictions.eq("expect", expect));
// 		}
// 		if(StringUtil.isNotNull(sTime)) {
// 			criterions.add(Restrictions.ge("time", sTime));
// 		}
// 		if(StringUtil.isNotNull(eTime)) {
// 			criterions.add(Restrictions.lt("time", eTime));
// 		}
// 		List<Order> orders = new ArrayList<>();
// 		orders.add(Order.desc("id"));
// 		PageList pList = uBetsPlanDao.search(criterions, orders, start, limit);
// 		List<UserBetsPlanVO> list = new ArrayList<>();
// 		for (Object tmpBean : pList.getList()) {
// 			list.add(new UserBetsPlanVO((UserBetsPlan) tmpBean, dataFactory));
// 		}
// 		pList.setList(list);
// 		return pList;
// 	}
//
// }