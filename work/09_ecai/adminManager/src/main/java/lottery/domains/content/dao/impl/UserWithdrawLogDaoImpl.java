package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserWithdrawLogDao;
import lottery.domains.content.entity.UserWithdrawLog;
@Repository
public class UserWithdrawLogDaoImpl implements UserWithdrawLogDao {
	private final String tab = UserWithdrawLogDao.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserWithdrawLog> superDao;
	@Override
	public boolean add(UserWithdrawLog entity) {
		return superDao.save(entity);
	}

	@Override
	public List<UserWithdrawLog> getByUserId(int userId,int tpye) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserWithdrawLog.class, propertyName, criterions, orders, start, limit);
	}

}
