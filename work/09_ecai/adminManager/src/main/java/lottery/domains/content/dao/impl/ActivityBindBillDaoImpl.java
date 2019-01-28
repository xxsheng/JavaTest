package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityBindBillDao;
import lottery.domains.content.entity.ActivityBindBill;

@Repository
public class ActivityBindBillDaoImpl implements ActivityBindBillDao {
	
	private final String tab = ActivityBindBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityBindBill> superDao;
	
	@Override
	public ActivityBindBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (ActivityBindBill) superDao.unique(hql, values);
	}
	
	@Override
	public int getWaitTodo() {
		String hql = "select count(id) from " + tab + " where status = 0";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public List<ActivityBindBill> get(String ip, String bindName, String bindCard) {
		String hql = "from " + tab + " where ip = ?0 or bindName = ?1 or bindCard = ?2";
		Object[] values = {ip, bindName, bindCard};
		return superDao.list(hql, values);
	}
	
	@Override
	public boolean update(ActivityBindBill entity) {
		return superDao.update(entity);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(ActivityBindBill.class, criterions, orders, start, limit);
	}

	@Override
	public double total(String sTime, String eTime) {
		String hql = "select sum(money) from " + tab + " where status = 1 and time >= ?0 and time < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

}