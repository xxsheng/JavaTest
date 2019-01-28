package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDividendBillDao;
import lottery.domains.content.entity.UserDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDividendBillDaoImpl implements UserDividendBillDao  {
	private final String tab = UserDividendBill.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserDividendBill> superDao;

	@Override
	public List<UserDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(UserDividendBill.class, criterions, orders);
	}

	@Override
	public UserDividendBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserDividendBill) superDao.unique(hql, values);
	}

	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?0, collectTime = ?1 where id = ?2";
		Object[] values = {status, new Moment().toSimpleTime(), id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateStatus(int id, int status, String remarks) {
		String hql = "update " + tab + " set status = ?0, remarks = ?1, collectTime = ?2 where id = ?3";
		Object[] values = {status, remarks, new Moment().toSimpleTime(), id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean update(int id, int status, double availableAmount, double totalReceived) {
		String hql = "update " + tab + " set status = ?0, availableAmount = ?1, totalReceived = ?2, collectTime = ?3 where id = ?4";
		Object[] values = {status, availableAmount, totalReceived, new Moment().toSimpleTime(), id};
		return superDao.update(hql, values);
	}

	@Override
	public double getTotalUnIssue(int userId) {
		String hql = "select sum(lowerTotalAmount)-sum(lowerPaidAmount) from " + tab + " where userId = ?0 and status in (2,6)";
		Object[] values = {userId};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}

	@Override
	public boolean addAvailableMoney(int id, double money) {
		String hql = "update " + tab + " set availableAmount = availableAmount+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean addTotalReceived(int id, double money) {
		String hql = "update " + tab + " set totalReceived = totalReceived+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}

	@Override
	public boolean addLowerPaidAmount(int id, double money) {
		String hql = "update " + tab + " set lowerPaidAmount = lowerPaidAmount+?1 where id = ?0";
		Object[] values = {id, money};
		return superDao.update(hql, values);
	}
}