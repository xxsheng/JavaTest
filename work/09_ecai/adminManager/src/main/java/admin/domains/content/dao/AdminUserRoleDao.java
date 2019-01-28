package admin.domains.content.dao;

import java.util.List;

import admin.domains.content.entity.AdminUserRole;

public interface AdminUserRoleDao {
	
	/**
	 * 列出所有角色
	 * @return
	 */
	List<AdminUserRole> listAll();
	
	/**
	 * 更新角色
	 * @param entity
	 * @return
	 */
	boolean update(AdminUserRole entity);
	
	/**
	 * 保存角色
	 * @param entity
	 * @return
	 */
	boolean save(AdminUserRole entity);
	
	/**
	 * 通过名称获取角色
	 * @param name
	 * @return
	 */
	AdminUserRole getByName(String name);
	
	/**
	 * 通过id获取角色
	 * @param id
	 * @return
	 */
	AdminUserRole getById(int id);
	
	List<AdminUserRole> getByUpid(int upid);
	
}