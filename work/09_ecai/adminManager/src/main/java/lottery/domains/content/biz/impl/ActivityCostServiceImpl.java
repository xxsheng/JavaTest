package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityCostService;
import lottery.domains.content.dao.ActivityCostBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.ActivityCostBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.activity.ActivityCostBillVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class ActivityCostServiceImpl implements ActivityCostService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ActivityCostBillDao activityCostBillDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public PageList searchBill(String username, String date, int start, int limit) {
		if(start < 0) start = 0;
		if(limit < 0) limit = 10;
		if(limit > 100) limit = 100;
		List<Criterion> criterions = new ArrayList<Criterion>();
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("id"));
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User uBean = userDao.getByUsername(username);
			if(uBean != null) {
				criterions.add(Restrictions.eq("userId", uBean.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(isSearch) {
			PageList pList = activityCostBillDao.find(criterions, orders, start, limit);
			List<ActivityCostBillVO> list = new ArrayList<>();
			for (Object o : pList.getList()) {
				list.add(new ActivityCostBillVO((ActivityCostBill) o, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

}
