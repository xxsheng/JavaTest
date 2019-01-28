package admin.domains.content.biz;

import java.util.List;

import admin.domains.content.entity.AdminUserMenu;

public interface AdminUserMenuService {

	List<AdminUserMenu> listAll();
	
	AdminUserMenu getById(int id);
	
	boolean updateStatus(int id, int status);
	
	boolean moveUp(int id);

	boolean moveDown(int id);
	
}