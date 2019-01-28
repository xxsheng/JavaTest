package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserWithdrawDao;
import lottery.domains.content.entity.UserWithdraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserWithdrawDaoImpl implements UserWithdrawDao {

	private final String tab = UserWithdraw.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserWithdraw> superDao;

	@Override
	public boolean add(UserWithdraw entity) {
		return superDao.save(entity);
	}

	@Override
	public int getDateCashCount(int userId, String date) {
		String sTime = new Moment().fromDate(date).toSimpleDate();
		String eTime = new Moment().fromDate(date).add(1, "days").toSimpleDate();
		String hql = "select count(id) from " + tab + " where userId = ?0 and time >= ?1 and time <= ?2 and checkStatus not in(-1) and remitStatus not in(-2)";
		Object[] values = {userId, sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public double getDateCashMoney(int userId, String date) {
		String sTime = new Moment().fromDate(date).toSimpleDate();
		String eTime = new Moment().fromDate(date).add(1, "days").toSimpleDate();
		String hql = "select sum(money) from " + tab + " where userId = ?0 and time >= ?1 and time <= ?2 and checkStatus not in(-1) and remitStatus not in(-2)";
		Object[] values = {userId, sTime, eTime};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).doubleValue() : 0;
	}
}