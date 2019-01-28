package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.entity.HistoryUserBill;
import lottery.domains.content.entity.UserBill;

@Repository
public class UserBillDaoImpl implements UserBillDao {
	
	private final String tab = UserBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBill> superDao;
	@Autowired
	private HibernateSuperDao<HistoryUserBill> dao;
	@Override
	public boolean add(UserBill entity) {
		return superDao.save(entity);
	}
	
	@Override
	public UserBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBill) superDao.unique(hql, values);
	}
	
	@Override
	public double getTotalMoney(String sTime, String eTime, int type, int[] refType) {
		String hql = "select sum(money) from " + tab + " where time >= ?0 and time < ?1 and type = ?2";
		if(refType != null && refType.length > 0) {
			hql += " and refType in (" + ArrayUtils.transInIds(refType) + ")";
		}
		Object[] values = {sTime, eTime, type};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
	
	@Override
	public List<UserBill> getLatest(int userId, int type, int count) {
		String hql = "from " + tab + " where userId = ?0 and type = ?1 order by id desc";
		Object[] values = {userId, type};
		return superDao.list(hql, values, 0, count);
	}
	
	@Override
	public List<UserBill> listByDateAndType(String date, int type, int[] refType) {
		String hql = "from " + tab + " where time like ?0 and type = ?1";
		if(refType != null && refType.length > 0) {
			hql += " and refType in (" + ArrayUtils.transInIds(refType) + ")";
		}
		Object[] values = {"%" + date + "%", type};
		return superDao.list(hql, values);
	}
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id"; // 根据id来查询
		return superDao.findPageList(UserBill.class, propertyName, criterions, orders, start, limit);
	}
	@Override
	public PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id"; // 根据id来查询
		return dao.findPageList(HistoryUserBill.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public PageList findNoDemoUserBill(String sql, int start, int limit) {
		String hsql = "select b.* from user_bill b, user u where b.user_id = u.id  ";
			PageList pageList = superDao.findPageEntityList(hsql+sql, UserBill.class,new HashMap<String, Object>(), start, limit);
			return pageList;
		
	}


}