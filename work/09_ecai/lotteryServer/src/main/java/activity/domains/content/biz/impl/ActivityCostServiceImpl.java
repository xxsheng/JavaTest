package activity.domains.content.biz.impl;


import activity.domains.content.biz.ActivityCostService;
import activity.domains.content.dao.ActivityCostBillDao;
import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.entity.ActivityCostBill;
import activity.domains.content.entity.ActivityRebate;
import activity.domains.content.vo.activity.ActivityCostDrawInfoVo;
import activity.domains.content.vo.activity.ActivityRebateVo;
import javautils.activity.PrizeUtils;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.read.UserBetsReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消费佣金活动
* <p>Title: ActivityCostServiceImpl</p>  
* <p>Description: </p>  
* @author James  
* @date 2018年2月4日
 */
@Service
public class ActivityCostServiceImpl implements ActivityCostService{

	@Autowired
	private ActivityRebateDao activityRebateDao;
	
	@Autowired
	private ActivityCostBillDao activityCostBillDao;
	
	@Autowired
	private UserBetsReadDao userBetsReadDao;
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserBillService uBillService;

	@Override
	@Transactional(readOnly = true)
	public ActivityRebateVo getCostInfo(int type) {
		ActivityRebate bean = activityRebateDao.getByStatusAndType(type,Global.ACTIVITY_STATUS_OPEN);
		if(bean != null){
			ActivityRebateVo vo = new ActivityRebateVo();
			String res = bean.getRules();
			vo.setCostInfo(PrizeUtils.costConfigInfo(res));
			vo.setStatus(bean.getStatus());
			return vo;
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ActivityCostDrawInfoVo getCostDarwInfo(int userId, String startTime, String endTime) {
		ActivityCostDrawInfoVo vo = new ActivityCostDrawInfoVo();
		ActivityCostBill bill = activityCostBillDao.getDrawInfo(userId, startTime, endTime);
		if(bill != null){
			vo.setIsReceived(Global.ACTIVITY_COST_STATUS_Y);//已领取
			vo.setMayDraw(bill.getMoney());//已领金额
			vo.setTodayCost(bill.getTotalCost());//领取时消费的金额
			return vo;
		}else{
			double darwMoney = 0;
			ActivityRebate bean = activityRebateDao.getByStatusAndType(Global.ACTIVITY_REBATE_COST,
					Global.ACTIVITY_STATUS_OPEN);
			if(bean != null){
				double costMoney = userBetsReadDao.getBillingOrder(userId, startTime, endTime);
				vo.setTodayCost(costMoney);//当前消费金额
				String res = bean.getRules();
				darwMoney = PrizeUtils.costConfigPrize(res,costMoney);
				vo.setMayDraw(darwMoney);//可领金额
				if(darwMoney > 0){
					vo.setIsReceived(Global.ACTIVITY_COST_STATUS_K);//可领取
				}else{
					vo.setIsReceived(Global.ACTIVITY_COST_STATUS_B);//未达标
				}
				return vo;
			}
			return null;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public ActivityCostBill getLastCostBillInfo(int userId) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		PageList pList = activityCostBillDao.search(criterions, orders, 0, 1);
		if(null != pList && pList.getList() != null && pList.getList().size() >0){
			ActivityCostBill costbill = (ActivityCostBill) pList.getList().get(0);
			return costbill;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ActivityCostBill getToDayDrawInfo(int userId, String startTime, String endTime) {
		return activityCostBillDao.getDrawInfo(userId, startTime, endTime);
	}
	
	@Override
	public boolean darwCostAward(int userId,Double costMoney,Double prizeMoney,Date now,String ip) {
		User uBean = uDao.getById(userId);
		if(uBean != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ActivityCostBill bill = new ActivityCostBill(userId,costMoney,prizeMoney,df.format(now),ip);
			boolean bFlag = activityCostBillDao.add(bill);
			if(bFlag) {
				boolean uFlag = uDao.updateLotteryMoney(userId, prizeMoney);
				if(uFlag) {
					String remarks = "消费奖励礼金";
					Integer refType = 1; // 其他活动类型
					return uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, prizeMoney, 
							refType, remarks);
				}
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public ActivityCostBill getLastCostBillInfoByIp(String ip) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("drawIp", ip));
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		PageList pList = activityCostBillDao.search(criterions, orders, 0, 1);
		if(null != pList && pList.getList() != null && pList.getList().size() >0){
			ActivityCostBill costbill = (ActivityCostBill) pList.getList().get(0);
			return costbill;
		}
		return null;
	}
}
