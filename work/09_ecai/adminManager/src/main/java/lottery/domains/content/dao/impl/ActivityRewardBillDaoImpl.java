package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRewardBillDao;
import lottery.domains.content.entity.ActivityRewardBill;

@Repository
public class ActivityRewardBillDaoImpl implements ActivityRewardBillDao {
	
	private final String tab = ActivityRewardBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityRewardBill> superDao;

	@Override
	public boolean add(ActivityRewardBill entity) {
		return superDao.save(entity);
	}
	
	@Override
	public ActivityRewardBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (ActivityRewardBill) superDao.unique(hql, values);
	}
	
	@Override
	public List<ActivityRewardBill> getUntreated(String date) {
		String hql = "from " + tab + " where date = ?0 and status = 0";
		Object[] values = {date};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<ActivityRewardBill> getLatest(int toUser, int status, int count) {
		String hql = "from " + tab + " where toUser = ?0 and status = 1";
		Object[] values = {toUser};
		return superDao.list(hql, values, 0, count);
	}

	@Override
	public boolean hasRecord(int toUser, int fromUser, int type, String date) {
		String hql = "from " + tab + " where toUser = ?0 and fromUser = ?1 and type = ?2 and date = ?3";
		Object[] values = {toUser, fromUser, type, date};
		List<ActivityRewardBill> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean update(ActivityRewardBill entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(ActivityRewardBill.class, criterions, orders, start, limit);
	}
	
	@Override
	public double total(String sTime, String eTime, int type) {
		String hql = "select sum(money) from " + tab + " where status = 1 and time >= ?0 and time < ?1 and type = ?2";
		Object[] values = {sTime, eTime, type};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
	
}