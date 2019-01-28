package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.entity.UserRecharge;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRechargeDaoImpl implements UserRechargeDao {

	private final String tab = UserRecharge.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserRecharge> superDao;
	
	@Override
	public boolean add(UserRecharge entity) {
		return superDao.save(entity);
	}
	
	@Override
	public boolean update(UserRecharge entity) {
		return superDao.update(entity);
	}
	
	@Override
	public double getTotalRechargeMoney(int userId) {
		String hql = "select sum(money) from " + tab + " where userId = ?0 and status = 1";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? (Double) result : 0;
	}
	
	@Override
	public UserRecharge getByBillno(String billno) {
		String hql = "from " + tab + " where billno = ?0";
		Object[] values = {billno};
		return (UserRecharge) superDao.unique(hql, values);
	}
	
	@Override
	public List<UserRecharge> listByDate(int userId, String date) {
		String hql = "from " + tab + " where userId = ?0 and status = 1 and payTime like ?1";
		Object[] values = {userId, "%" + date + "%"};
		return superDao.list(hql, values);
	}

	@Override
	public double listByTypeAndDateTotal(int userId,String date,int type) {
		String hql = "select sum(money) from " + tab + " where userId = ?0 and status = 1 and payTime >= ?1 and type=?2";
		Object[] values = {userId, date,type};
		Object result = superDao.unique(hql, values);
		return result != null ? (Double) result : 0;
	}

	@Override
	public List<UserRecharge> list(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(UserRecharge.class, criterions, orders);
	}

	@Override
	public int countTotal(int userId, int status, String sPayDate, String ePayDate) {
		String hql = "select count(id) from " + tab + " where userId = ?0 and status = ?1 and payTime >= ?2 and payTime < ?2";
		Object[] values = {userId, status, sPayDate, ePayDate};
		Object result = superDao.unique(hql, values);
		if (result == null) return 0;
		Number numResult = (Number) result;
		return numResult.intValue();
	}
}