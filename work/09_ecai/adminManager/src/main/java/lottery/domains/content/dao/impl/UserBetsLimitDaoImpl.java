package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserBetsLimitDao;
import lottery.domains.content.entity.UserBetsLimit;
@Repository
public class UserBetsLimitDaoImpl implements UserBetsLimitDao {
	
	private final String tab = UserBetsLimit.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsLimit> superDao;
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		return superDao.findPageList(UserBetsLimit.class, criterions, orders, start, limit);
	}

	@Override
	public boolean save(UserBetsLimit entity) {
		return superDao.save(entity);
	}

	@Override
	public UserBetsLimit getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		List<UserBetsLimit> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public boolean update(UserBetsLimit entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

	@Override
	public UserBetsLimit getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		List<UserBetsLimit> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
