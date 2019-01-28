package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityRechargeService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.UserWithdrawLimitService;
import lottery.domains.content.dao.ActivityRechargeBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.ActivityRechargeBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.activity.ActivityRechargeBillVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class ActivityRechargeServiceImpl implements ActivityRechargeService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private ActivityRechargeBillDao aRechargeBillDao;
	
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserSysMessageService uSysMessageService;
	
	@Autowired
	private UserWithdrawLimitService uWithdrawLimitService;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList search(String username, String date, String keyword, Integer status, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(StringUtil.isNotNull(keyword)) {
			Disjunction disjunction = Restrictions.or();
			disjunction.add(Restrictions.eq("ip", keyword));
			criterions.add(disjunction);
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status));
		}
 		orders.add(Order.desc("id"));
		if(isSearch) {
			List<ActivityRechargeBillVO> list = new ArrayList<>();
			PageList pList = aRechargeBillDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new ActivityRechargeBillVO((ActivityRechargeBill) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public boolean agree(int id) {
		ActivityRechargeBill entity = aRechargeBillDao.getById(id);
		if(entity != null) {
			if(entity.getStatus() == 0) {
				String thisTime = new Moment().toSimpleTime();
				entity.setStatus(1);
				entity.setTime(thisTime);
				boolean aFlag = aRechargeBillDao.update(entity);
				if(aFlag) {
					User uBean = uDao.getById(entity.getUserId());
					boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), entity.getMoney());
					if(uFlag) {
						// 需要消费量
						int type = Global.PAYMENT_CHANNEL_TYPE_SYSTEM;
						int subType = Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ACTIVITY;
						double percent = lotteryDataFactory.getWithdrawConfig().getSystemConsumptionPercent();
						uWithdrawLimitService.add(uBean.getId(), entity.getMoney(),thisTime, type, subType, percent);
						// 添加活动账单
						String remarks = "开业大酬宾。";
						int refType = 1; // 其他活动类型
						uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, entity.getMoney(), refType, remarks);
						uSysMessageService.addActivityRecharge(uBean.getId(), entity.getMoney());
					}
					return uFlag;
				}
			}
		}
		return false;
	}

	@Override
	public boolean refuse(int id) {
		ActivityRechargeBill entity = aRechargeBillDao.getById(id);
		if(entity != null) {
			if(entity.getStatus() == 0) {
				entity.setStatus(-1);
				return aRechargeBillDao.update(entity);
			}
		}
		return false;
	}

	@Override
	public boolean check(int id) {
		ActivityRechargeBill entity = aRechargeBillDao.getById(id);
		if(entity != null) {
			String date = new Moment().fromTime(entity.getPayTime()).toSimpleDate();
			List<ActivityRechargeBill> list = aRechargeBillDao.get(entity.getIp(), date);
			if(list != null && list.size() > 1) {
				return true;
			}
		}
		return false;
	}

}