package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivitySalaryBillDao;
import lottery.domains.content.entity.ActivitySalaryBill;

@Repository
public class ActivitySalaryBillDaoImpl implements ActivitySalaryBillDao {
	
	private final String tab = ActivitySalaryBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivitySalaryBill> superDao;
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(ActivitySalaryBill.class, criterions, orders, start, limit);
	}

	@Override
	public double total(String sTime, String eTime, int type) {
		String hql = "select sum(money) from " + tab + " where time >= ?0 and time < ?1 and type = ?2";
		Object[] values = {sTime, eTime, type};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

}
