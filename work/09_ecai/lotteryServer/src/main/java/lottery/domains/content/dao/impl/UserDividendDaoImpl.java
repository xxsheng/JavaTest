package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDividendDao;
import lottery.domains.content.entity.UserDividend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDividendDaoImpl implements UserDividendDao {
	
	private final String tab = UserDividend.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserDividend> superDao;

	@Override
	public UserDividend getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return (UserDividend) superDao.unique(hql, values);
	}

	@Override
	public UserDividend getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserDividend) superDao.unique(hql, values);
	}

	@Override
	public void add(UserDividend entity) {
		superDao.save(entity);
	}

	@Override
	public void updateStatus(int id, int status, int beforeStatus, int userId) {
		String hql = "update " + tab + " set status = ?0, agreeTime = ?1 where id = ?2 and status=?3 and userId=?4";
		Object[] values = {status, new Moment().toSimpleTime(), id, beforeStatus, userId};
		superDao.update(hql, values);
	}

	@Override
	public void updateSomeFields(int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, int fixed, double minScale, double maxScale, int status) {
		String agreeTime = new Moment().toSimpleTime();
		String hql = "update " + tab + " set scaleLevel = ?0, lossLevel = ?1, salesLevel = ?2, minValidUser = ?3, fixed = ?4, minScale = ?5, maxScale = ?6, status = ?7, agreeTime = ?8 where id = ?9";
		Object[] values = {scaleLevel, lossLevel, salesLevel, minValidUser, fixed, minScale, maxScale, status, agreeTime, id};
		superDao.update(hql, values);
	}

	@Override
	public void deleteByUser(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		superDao.delete(hql, values);
	}

	@Override
	public void deleteByTeam(int upUserId) {
		String hql = "delete from " + tab + " where userId in(select id from User where id = ?0 or upids like ?1)";
		Object[] values = {upUserId, "%["+upUserId+"]%"};
		superDao.delete(hql, values);
	}

	@Override
	public void deleteLowers(int upUserId) {
		String hql = "delete from " + tab + " where userId in(select id from User where upids like ?0)";
		Object[] values = {"%["+upUserId+"]%"};
		superDao.delete(hql, values);
	}
}