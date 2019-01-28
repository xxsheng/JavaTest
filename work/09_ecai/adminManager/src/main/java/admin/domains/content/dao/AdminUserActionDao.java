package admin.domains.content.dao;

import java.util.List;

import admin.domains.content.entity.AdminUserAction;

public interface AdminUserActionDao {
	
	/**
	 * 列出所有行为列表
	 * @return
	 */
	List<AdminUserAction> listAll();
	
	/**
	 * 更新行为
	 * @param entity
	 * @return
	 */
	boolean update(AdminUserAction entity);
	
	/**
	 * 保存行为
	 * @param entity
	 * @return
	 */
	boolean save(AdminUserAction entity);
	
	/**
	 * 通过id获取到行为
	 * @param id
	 * @return
	 */
	AdminUserAction getById(int id);
	
}
