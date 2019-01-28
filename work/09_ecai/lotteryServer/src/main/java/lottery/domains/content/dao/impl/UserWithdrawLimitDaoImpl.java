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
	public List<UserWithdrawLimit> getUserWithdrawLimits(int userId) {
		String hql = "from " + tab + " where userId = ?0 order by rechargeTime asc";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}
	
	@Override
	public boolean resetLimit(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		superDao.delete(hql, values);
		return true;
	}
//
//	@Override
//	public boolean updateTotal(int userId, double totalAmount) {
//		String hql = "update " + tab + " set totalMoney = totalMoney + ?1 where userId = ?0";
//		Object[] values = {userId, totalAmount};
//		return superDao.update(hql, values);
//	}
//	
//	@Override
//	public boolean updateTotal(int userId, double totalAmount, String totalTime) {
//		String hql = "update " + tab + " set totalMoney = totalMoney + ?1, totalTime = ?2 where userId = ?0";
//		Object[] values = {userId, totalAmount, totalTime};
//		return superDao.update(hql, values);
//	}
//
//	@Override
//	public boolean resetLotteryAccount(int userId) {
//		String hql = "update " + tab + " set lotteryMoney = 0, lotteryTime = null where userId = ?0";
//		Object[] values = {userId};
//		return superDao.update(hql, values);
//	}
//
//	@Override
//	public boolean updateLotteryAccount(int userId, double totalAmount) {
//		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1 where userId = ?0";
//		Object[] values = {userId, totalAmount};
//		return superDao.update(hql, values);
//	}
//
//	@Override
//	public boolean updateLotteryAccount(int userId, double totalAmount, String lotteryTime) {
//		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1, lotteryTime = ?2 where userId = ?0";
//		Object[] values = {userId, totalAmount, lotteryTime};
//		return superDao.update(hql, values);
//	}

}