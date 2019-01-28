package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserWithdrawLimitDao;
import lottery.domains.content.entity.UserWithdrawLimit;

@Repository
public class UserWithdrawLimitDaoImpl implements UserWithdrawLimitDao {
	
	private final String tab = UserWithdrawLimit.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserWithdrawLimit> superDao;
	
	@Override
	public UserWithdrawLimit getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return (UserWithdrawLimit) superDao.unique(hql, values);
	}

	@Override
	public boolean add(UserWithdrawLimit entity) {
		return superDao.save(entity);
	}

	@Override
	public List<UserWithdrawLimit> getUserWithdrawLimits(int userId, String time) {
		String hql = "from " + tab + " where userId = ?0 and rechargeTime <= ?1 order by rechargeTime asc";
		Object[] values = {userId, time};
		return superDao.list(hql, values);
	}

	@Override
	public boolean delByUserId(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		superDao.delete(hql, values);
		return true;
	}

}