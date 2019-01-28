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
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityBindService;
import lottery.domains.content.dao.ActivityBindBillDao;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.entity.ActivityBindBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.activity.ActivityBindBillVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class ActivityBindServiceImpl implements ActivityBindService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private ActivityBindBillDao aBindBillDao;
	
	@Autowired
	private UserBetsDao uBetsDao;
	
	@Autowired
	private UserRechargeDao uRechargeDao;
//	
//	@Autowired
//	private UserBillService uBillService;
//	
//	@Autowired
//	private UserSysMessageService uSysMessageService;
//	
//	@Autowired
//	private UserWithdrawLimitService uWithdrawLimitService;
//	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList search(String username, String upperUser, String date, String keyword, Integer status, int start, int limit) {
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
		if(StringUtil.isNotNull(upperUser)) {
			User user = uDao.getByUsername(upperUser);
			if(user != null) {
				List<User> lowers = uDao.getUserLower(user.getId());
				if(lowers.size() > 0) {
					Integer[] ids = new Integer[lowers.size()];
					for (int i = 0; i < lowers.size(); i++) {
						ids[i] = lowers.get(i).getId();
					}
					criterions.add(Restrictions.in("userId", ids));
				} else {
					isSearch = false;
				}
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
			disjunction.add(Restrictions.eq("bindName", keyword));
			disjunction.add(Restrictions.eq("bindCard", keyword));
			criterions.add(disjunction);
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status));
		}
 		orders.add(Order.desc("id"));
		if(isSearch) {
			List<ActivityBindBillVO> list = new ArrayList<>();
			PageList pList = aBindBillDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				ActivityBindBill bBean = (ActivityBindBill) tmpBean;
				User uBean = uDao.getById(bBean.getUserId());
				ActivityBindBillVO vBean = new ActivityBindBillVO(bBean, uBean, lotteryDataFactory);
				// 获取是否充值
				boolean isRecharge = uRechargeDao.isRecharge(bBean.getUserId());
				vBean.setRecharge(isRecharge);
				// 获取是否消费
				boolean isCost = uBetsDao.isCost(bBean.getUserId());
				vBean.setCost(isCost);
				list.add(vBean);
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

//	@Override
//	public boolean agree(int id) {
//		ActivityBindBill entity = aBindBillDao.getById(id);
//		if(entity != null) {
//			if(entity.getStatus() == 0) {
//				String thisTime = new Moment().toSimpleTime();
//				entity.setStatus(1);
//				entity.setTime(thisTime);
//				boolean aFlag = aBindBillDao.update(entity);
//				if(aFlag) {
//					User uBean = uDao.getById(entity.getUserId());
//					boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), entity.getMoney());
//					if(uFlag) {
//						// 需要消费量
//						uWithdrawLimitService.updateTotal(uBean.getId(), entity.getMoney(),thisTime);
//						// 添加活动账单
//						String remarks = "绑定资料体验金。";
//						int refType = 1; // 其他活动类型
//						uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, entity.getMoney(), refType, remarks);
//						uSysMessageService.addActivityBind(uBean.getId(), entity.getMoney());
//					}
//					return uFlag;
//				}
//			}
//		}
//		return false;
//	}

	@Override
	public boolean refuse(int id) {
		ActivityBindBill entity = aBindBillDao.getById(id);
		if(entity != null) {
			if(entity.getStatus() == 0) {
				entity.setStatus(-1);
				return aBindBillDao.update(entity);
			}
		}
		return false;
	}

	@Override
	public boolean check(int id) {
		ActivityBindBill entity = aBindBillDao.getById(id);
		if(entity != null) {
			List<ActivityBindBill> list = aBindBillDao.get(entity.getIp(), entity.getBindName(), entity.getBindCard());
			if(list != null && list.size() > 1) {
				return true;
			}
		}
		return false;
	}

}