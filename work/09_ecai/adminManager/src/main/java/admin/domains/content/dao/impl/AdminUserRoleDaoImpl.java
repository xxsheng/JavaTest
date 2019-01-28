package admin.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserRoleDao;
import admin.domains.content.entity.AdminUserRole;

@Repository
public class AdminUserRoleDaoImpl implements AdminUserRoleDao {

	private final String tab = AdminUserRole.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<AdminUserRole> superDao;
	
	@Override
	public List<AdminUserRole> listAll() {
		String hql = "from " + tab + " order by sort";
		return superDao.list(hql);
	}

	@Override
	public boolean update(AdminUserRole entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean save(AdminUserRole entity) {
		return superDao.save(entity);
	}

	@Override
	public AdminUserRole getByName(String name) {
		String hql = "from " + tab + " where name = ?0";
		Object[] values = {name};
		return (AdminUserRole) superDao.unique(hql, values);
	}

	@Override
	public AdminUserRole getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (AdminUserRole) superDao.unique(hql, values);
	}
	
	@Override
	public List<AdminUserRole> getByUpid(int upid) {
		String hql = "from " + tab + " where upid = ?0";
		Object[] values = {upid};
		return superDao.list(hql, values);
	}
}
