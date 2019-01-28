package admin.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import admin.domains.content.dao.AdminUserMenuDao;
import admin.domains.content.entity.AdminUserMenu;

@Repository
public class AdminUserMenuDaoImpl implements AdminUserMenuDao {

	private final String tab = AdminUserMenu.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<AdminUserMenu> superDao;
	
	@Override
	public List<AdminUserMenu> listAll() {
		String hql = "from " + tab + " order by sort";
		return superDao.list(hql);
	}

	@Override
	public boolean update(AdminUserMenu entity) {
		return superDao.update(entity);
	}

	@Override
	public AdminUserMenu getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (AdminUserMenu) superDao.unique(hql, values);
	}

	@Override
	public boolean modsort(int id, int sort) {
		String hql = "update " + tab + " set  sort= sort+ ?1 where id = ?0";
		Object[] values = {id, sort};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateSort(int id, int sort) {
		String hql = "update " + tab + " set  sort= ?1 where id = ?0";
		Object[] values = {id, sort};
		return superDao.update(hql, values);
	}

	@Override
	public List<AdminUserMenu> getBySortUp(int upid,int sort) {
		String hql = "from " + tab + " where upid = ?0 and sort < ?1 order by sort desc";
		Object[] values = {upid,sort};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<AdminUserMenu> getBySortDown(int upid,int sort) {
		String hql = "from " + tab + " where upid = ?0 and sort > ?1 order by sort asc";
		Object[] values = {upid,sort};
		return superDao.list(hql, values);
	}
	
	@Override
	public int getMaxSort(int upid) {
		String hql = "select max(sort) from " + tab + " where upid =?0";
		Object[] values = {upid};
		Object result = superDao.unique(hql,values);
		return result != null ? ((Number) result).intValue() : 0;
	}
}