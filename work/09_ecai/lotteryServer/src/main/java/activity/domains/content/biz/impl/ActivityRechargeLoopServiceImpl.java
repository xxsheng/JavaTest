package activity.domains.content.biz.impl;

import activity.domains.content.biz.ActivityRechargeLoopService;
import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.dao.ActivityRechargeLoopBillDao;
import activity.domains.content.entity.ActivityRebate;
import activity.domains.content.entity.ActivityRechargeLoopBill;
import activity.domains.content.entity.activity.RebateRulesRechargeLoop;
import activity.domains.content.vo.activity.ActivityRechargeLoopVO;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.dao.read.UserBetsReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.global.Global;
import lottery.web.WebJSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityRechargeLoopServiceImpl implements ActivityRechargeLoopService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private ActivityRebateDao aRebateDao;
	
	@Autowired
	private ActivityRechargeLoopBillDao aRechargeLoopBillDao;
	
	@Autowired
	private UserBetsReadDao uBetsReadDao;
	
	@Autowired
	private UserRechargeDao uRechargeDao;
	
	@Autowired
	private UserBillService uBillService;
	
	RebateRulesRechargeLoop listRules(String rules) {
		return (RebateRulesRechargeLoop) JSONObject.toBean(JSONObject.fromObject(rules), RebateRulesRechargeLoop.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ActivityRechargeLoopVO get(int userId) {
		ActivityRebate activity = aRebateDao.getByType(Global.ACTIVITY_REBATE_RECHARGE_LOOP);
		if(activity != null) {
			Moment thisTime = new Moment();
			ActivityRechargeLoopVO bean = new ActivityRechargeLoopVO();
			RebateRulesRechargeLoop rules = listRules(activity.getRules());
			List<ActivityRechargeLoopBill> bList = aRechargeLoopBillDao.getByDate(userId, thisTime.toSimpleDate());
			int s2Times = 0;
			String s2StartTime = new Moment().toSimpleDate();
			for (ActivityRechargeLoopBill tmpBean : bList) {
				if(tmpBean.getStep() == 1) {
					bean.setS1Recharge(tmpBean.getTotalMoney());
					bean.setS1Cost(tmpBean.getTotalCost());
					bean.setS1Reward(tmpBean.getMoney());
					bean.setS1Available(false);
					bean.setS1Received(true);
					s2StartTime = tmpBean.getTime();
				}
				if(tmpBean.getStep() == 2) {
					s2Times++;
					s2StartTime = tmpBean.getTime();
				}
				if(tmpBean.getStep() == 3) {
					bean.setS3Available(false);
					bean.setS3Received(true);
				}
			}
			// 判断第一步，没有领取记录
			if(!bean.isS1Received()) {
				List<UserRecharge> rList = uRechargeDao.listByDate(userId, thisTime.toSimpleDate());
				for (UserRecharge tmpBean : rList) {
					if(tmpBean.getMoney() >= rules.getS1Recharge()) {
						bean.setS1Recharge(tmpBean.getMoney());
						String startTime = tmpBean.getPayTime();
						String endTime = new Moment().toSimpleTime();
						double s1Cost = uBetsReadDao.getBillingOrder(userId, startTime, endTime);
						double s1CostLimit = tmpBean.getMoney() * rules.getS1Cost() / 100;
						s1CostLimit = MathUtil.doubleFormat(s1CostLimit, 3);
						bean.setS1Cost(s1Cost);
						double s1Reward = tmpBean.getMoney() * rules.getS1Reward() / 100;
						s1Reward = MathUtil.doubleFormat(s1Reward, 3);
						bean.setS1Reward(s1Reward);
						if(s1Cost >= s1CostLimit) {
							bean.setS1Available(true);
							bean.setS1Received(false);
						}
						break;
					}
				}
			}
			// 判断第二步
			if(bean.isS1Received()) {
				if(!bean.isS3Received()) {
					boolean hasNext = true;
					bean.setS2Times(s2Times);
					if(s2Times == rules.getS2Times()) {
						hasNext = false;
						bean.setS2Available(false);
						bean.setS2Received(true);
					}
					if(hasNext) {
						String s2EndTime = new Moment().toSimpleTime();
						// 消费量
						double s2Cost = uBetsReadDao.getBillingOrder(userId, s2StartTime, s2EndTime);
						bean.setS2Cost(s2Cost);
						if(s2Cost >= rules.getS2Cost()) {
							bean.setS2Available(true);
						}
					}
				} else {
					bean.setS2Available(false);
					bean.setS2Received(true);
				}
			} else {
				bean.setS2Available(false);
				bean.setS2Received(false);
			}
			// 判断第三步
			if(!bean.isS3Received()) {
				if(bean.isS1Received() && s2Times >= rules.getS3Limit()) {
					bean.setS3Available(true);
					bean.setS3Received(false);
				}
			}
			// 活动规则
			bean.setRules(rules);
			return bean;
		}
		return null;
	}
	
	@Override
	public boolean receive(WebJSON json, int userId, String ip, int step) {
		ActivityRechargeLoopVO activity = get(userId);
		if(activity != null) {
			// 领取第一步
			if(step == 1) {
				if(activity.isS1Received()) {
					json.set(2, "2-4002");
					return false;
				}
				if(activity.isS1Available()) {
					// s1 bill
					User uBean = uDao.getById(userId);
					if(uBean != null) {
						double totalMoney = activity.getS1Recharge();
						double totalCost = activity.getS1Cost();
						double money = activity.getS1Reward();
						String time = new Moment().toSimpleTime();
						ActivityRechargeLoopBill entity = new ActivityRechargeLoopBill(userId, ip, step, totalMoney, totalCost, money, time);
						boolean bFlag = aRechargeLoopBillDao.add(entity);
						if(bFlag) {
							boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), money);
							if(uFlag) {
								String remarks = "开业大酬宾，连环活动嗨不停。";
								Integer refType = 1; // 其他活动类型
								uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, money, refType, remarks);
							}
							return uFlag;
						}
					}
				} else {
					json.set(2, "2-4007");
				}
			}
			// 领取第二步
			if(step == 2) {
				if(activity.isS2Received()) {
					json.set(2, "2-4002");
					return false;
				}
				if(activity.isS2Available()) {
					// s2 bill
					User uBean = uDao.getById(userId);
					if(uBean != null) {
						double totalMoney = 0;
						double totalCost = activity.getS2Cost();
						double money = activity.getRules().getS2Reward();
						String time = new Moment().toSimpleTime();
						ActivityRechargeLoopBill entity = new ActivityRechargeLoopBill(userId, ip, step, totalMoney, totalCost, money, time);
						boolean bFlag = aRechargeLoopBillDao.add(entity);
						if(bFlag) {
							boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), money);
							if(uFlag) {
								String remarks = "开业大酬宾，连环活动嗨不停。";
								Integer refType = 1; // 其他活动类型
								uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, money, refType, remarks);
							}
							return uFlag;
						}
					}
				} else {
					json.set(2, "2-4005");
				}
			}
			// 领取第三步
			if(step == 3) {
				if(activity.isS3Received()) {
					json.set(2, "2-4002");
					return false;
				}
				if(activity.isS3Available()) {
					// s3 bill
					User uBean = uDao.getById(userId);
					if(uBean != null) {
						double totalMoney = 0;
						double totalCost = 0;
						double money = activity.getRules().getS3Reward();
						String time = new Moment().toSimpleTime();
						ActivityRechargeLoopBill entity = new ActivityRechargeLoopBill(userId, ip, step, totalMoney, totalCost, money, time);
						boolean bFlag = aRechargeLoopBillDao.add(entity);
						if(bFlag) {
							boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), money);
							if(uFlag) {
								String remarks = "开业大酬宾，连环活动嗨不停。";
								Integer refType = 1; // 其他活动类型
								uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, money, refType, remarks);
							}
							return uFlag;
						}
					}
				} else {
					json.set(2, "2-4007");
				}
			}
		}
		return false;
	}

}