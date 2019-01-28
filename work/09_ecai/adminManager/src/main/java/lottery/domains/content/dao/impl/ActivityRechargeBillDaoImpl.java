package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRechargeBillDao;
import lottery.domains.content.entity.ActivityRechargeBill;

@Repository
public class ActivityRechargeBillDaoImpl implements ActivityRechargeBillDao {
	
	private final String tab = ActivityRechargeBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityRechargeBill> superDao;
	
	@Override
	public ActivityRechargeBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (ActivityRechargeBill) superDao.unique(hql, values);
	}
	
	@Override
	public int getWaitTodo() {
		String hql = "select count(id) from " + tab + " where status = 0";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}
	
	@Override
	public boolean update(ActivityRechargeBill entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean add(ActivityRechargeBill entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean hasDateRecord(int userId, String date) {
		String hql = "from " + tab + " where userId = ?0 and payTime like ?1";
		Object[] values = {userId, "%" + date + "%"};
		List<ActivityRechargeBill> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public List<ActivityRechargeBill> get(String ip, String date) {
		String hql = "from " + tab + " where ip = ?0 and payTime like ?1";
		Object[] values = {ip, "%" + date + "%"};
		return superDao.list(hql, values);
	}
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(ActivityRechargeBill.class, criterions, orders, start, limit);
	}

	@Override
	public double total(String sTime, String eTime) {
		String hql = "select sum(money) from " + tab + " where status = 1 and time >= ?0 and time < ?1";
		Object[] values = {sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

}