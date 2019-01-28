package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserSecurityDao;
import lottery.domains.content.entity.UserSecurity;

@Repository
public class UserSecurityDaoImpl implements UserSecurityDao {
	
	private final String tab = UserSecurity.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserSecurity> superDao;
	
	@Override
	public UserSecurity getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserSecurity) superDao.unique(hql, values);
	}

	@Override
	public List<UserSecurity> getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}
	
	@Override
	public boolean delete(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		return superDao.findPageList(UserSecurity.class, criterions, orders, start, limit);
	}

	@Override
	public boolean updateValue(int id, String md5Value) {
		String hql = "update " + tab + " set value = ?1 where id = ?0";
		Object[] values = {id, md5Value};
		return superDao.update(hql, values);
	}
}