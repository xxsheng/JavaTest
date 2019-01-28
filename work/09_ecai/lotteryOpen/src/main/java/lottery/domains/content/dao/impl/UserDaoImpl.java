package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
	
	public static final String tab = User.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<User> superDao;

	@Override
	public List<User> list(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(User.class, criterions, orders);
	}

	@Override
	public List<User> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

	@Override
	public boolean updateLotteryMoney(int id, double lotteryAmount, double freezeAmount) {
		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1, freezeMoney = freezeMoney + ?2 where id = ?0";
		if(lotteryAmount < 0) {
			hql += " and lotteryMoney >= " + (-lotteryAmount);
		}
		Object[] values = {id, lotteryAmount, freezeAmount};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateLotteryMoney(int id, double lotteryAmount) {
		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1 where id = ?0";
		if(lotteryAmount < 0) {
			hql += " and lotteryMoney >= " + (-lotteryAmount);
		}
		Object[] values = {id, lotteryAmount};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateFreezeMoney(int id, double freezeAmount) {
		String hql = "update " + tab + " set freezeMoney = freezeMoney + ?1 where id = ?0";
		if(freezeAmount < 0) {
			hql += " and freezeMoney >= " + (-freezeAmount);
		}
		Object[] values = {id, freezeAmount};
		return superDao.update(hql, values);
	}

	@Override
	public User getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (User) superDao.unique(hql, values);
	}

	@Override
	public int clearUserNegateFreezeMoney() {
		String hql = "update " + tab + " set freezeMoney = 0 where freezeMoney < 0";
		return superDao.updateByHql(hql);
	}

	@Override
	public List<User> getUserLower(int id) {
		String hql = "from " + tab + " where upids like ?0";
		Object[] values = {"%[" + id + "]%"};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserDirectLower(int id) {
		String hql = "from " + tab + " where upid = ?0";
		Object[] values = {id};
		return superDao.list(hql, values);
	}
}