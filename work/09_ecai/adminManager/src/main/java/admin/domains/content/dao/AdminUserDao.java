package admin.domains.content.dao;

import java.util.List;

import admin.domains.content.entity.AdminUser;

public interface AdminUserDao {
	
	/**
	 * 列出所有用户
	 * @return
	 */
	public List<AdminUser> listAll();
	
	/**
	 * 通过id获取用户
	 * @param id
	 * @return
	 */
	public AdminUser getById(int id);
	
	/**
	 * 通过用户名获取用户
	 * @param username
	 * @return
	 */
	public AdminUser getByUsername(String username);
	
	/**
	 * 添加用户
	 * @param entity
	 * @return
	 */
	public boolean add(AdminUser entity);
	
	/**
	 * 更新用户
	 * @param entity
	 * @return
	 */
	public boolean update(AdminUser entity);
	
}
