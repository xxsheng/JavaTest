package admin.domains.content.dao;

import java.util.List;

import admin.domains.content.entity.AdminUserMenu;

public interface AdminUserMenuDao {
	
	List<AdminUserMenu> listAll();
	
	boolean update(AdminUserMenu entity);
	
	AdminUserMenu getById(int id);
	
	boolean updateSort(int id, int sort);
	
	boolean modsort(int id, int sort);

	List<AdminUserMenu> getBySortUp(int upid,int sort);
	
	List<AdminUserMenu> getBySortDown(int upid,int sort);
	int getMaxSort(int upid);
	
}