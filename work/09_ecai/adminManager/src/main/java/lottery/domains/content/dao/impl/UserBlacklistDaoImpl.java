package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserBlacklistDao;
import lottery.domains.content.entity.UserBlacklist;

@Repository
public class UserBlacklistDaoImpl implements UserBlacklistDao {
	
	private final String tab = UserBlacklist.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBlacklist> superDao;
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		return superDao.findPageList(UserBlacklist.class, criterions, orders, start, limit);
	}

	@Override
	public boolean add(UserBlacklist entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}

	@Override
	public int getByIp(String ip) {
		String hql = "select count(id) from " + tab + " where ip = ?0";
		Object[] values = {ip};
		Object result = superDao.unique(hql, values);
		return result == null ? 0 : Integer.parseInt(result.toString());
	}

}
