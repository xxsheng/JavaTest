package admin.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserActionDao;
import admin.domains.content.entity.AdminUserAction;

@Repository
public class AdminUserActionDaoImpl implements AdminUserActionDao {

	private final String tab = AdminUserAction.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<AdminUserAction> superDao;
	
	@Override
	public List<AdminUserAction> listAll() {
		String hql = "from " + tab + " order by sort";
		return superDao.list(hql);
	}

	@Override
	public boolean update(AdminUserAction entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean save(AdminUserAction entity) {
		return superDao.save(entity);
	}

	@Override
	public AdminUserAction getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (AdminUserAction) superDao.unique(hql, values);
	}

}