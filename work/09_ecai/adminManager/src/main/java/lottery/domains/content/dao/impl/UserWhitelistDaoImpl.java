package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserWhitelistDao;
import lottery.domains.content.entity.UserWhitelist;

@Repository
public class UserWhitelistDaoImpl implements UserWhitelistDao {
	
	private final String tab = UserWhitelist.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserWhitelist> superDao;
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		return superDao.findPageList(UserWhitelist.class, criterions, orders, start, limit);
	}

	@Override
	public boolean add(UserWhitelist entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

}
