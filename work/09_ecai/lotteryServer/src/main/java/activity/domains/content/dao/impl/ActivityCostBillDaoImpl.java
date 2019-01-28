package activity.domains.content.dao.impl;


import activity.domains.content.dao.ActivityCostBillDao;
import activity.domains.content.entity.ActivityCostBill;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ActivityCostBillDaoImpl implements ActivityCostBillDao {
	private final String tab = ActivityCostBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityCostBill> superDao;
	
	@Override
	public ActivityCostBill getDrawInfo(int userId, String sTime, String eTime) {
		String hql = "from " + tab + " where userId= ?0 and time >= ?1 and time <= ?2";
		Object[] values = {userId,sTime, eTime};
		Object result = superDao.unique(hql, values);
		return (ActivityCostBill) result;
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
//		String hql = " from " + tab + " where userId = ?0  order by time desc  limit 1";
//		Object[] values = {userId};
//		Object result = superDao.unique(hql, values);
//		return (ActivityCostBill) result;
		String propertyName = "id";
		return superDao.findPageList(ActivityCostBill.class, propertyName, criterions, orders, start, limit);
	}
	
	
	@Override
	public boolean add(ActivityCostBill entity) {
		return superDao.save(entity);
	}
}
