package admin.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;

@Repository
public class AdminUserDaoImpl implements AdminUserDao {

	private final String tab = AdminUser.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<AdminUser> superDao;
	
	@Override
	public List<AdminUser> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}
	
	@Override
	public AdminUser getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = { id };
		return (AdminUser) superDao.unique(hql, values);
	}

	@Override
	public AdminUser getByUsername(String username) {
		String hql = "from " + tab + " where username = ?0";
		Object[] values = { username };
		return (AdminUser) superDao.unique(hql, values);
	}

	@Override
	public boolean add(AdminUser entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(AdminUser entity) {
		return superDao.update(entity);
	}

}