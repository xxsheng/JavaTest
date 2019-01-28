package admin.domains.pool;

import java.util.List;
import java.util.Set;

import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserMenu;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.content.vo.AdminUserBaseVO;

public interface AdminDataFactory {

	/**
	 * 初始化所有配置
	 */
	void init();
	
	/**
	 * 初始化系统消息
	 */
	void initSysMessage();
	
	/**
	 * 获取消息参数
	 * @param key
	 * @return
	 */
	String getSysMessage(String key);
	
	/**
	 * 初始化管理员用户
	 */
	void initAdminUser();
	
	/**
	 * 获取管理员用户
	 * @param id
	 * @return
	 */
	AdminUserBaseVO getAdminUser(int id);
	
	/**
	 * 初始化管理员角色
	 */
	void initAdminUserRole();

	/**
	 * 获取管理员角色
	 * @param id
	 * @return
	 */
	AdminUserRole getAdminUserRole(int id);
	
	/**
	 * 列出所有管理员角色
	 * @return
	 */
	List<AdminUserRole> listAdminUserRole();
	
	/**
	 * 初始化管理员行为
	 */
	void initAdminUserAction();
	
	/**
	 * 列出管理员行为
	 * @return
	 */
	List<AdminUserAction> listAdminUserAction();
	
	/**
	 * 获取管理员行为
	 * @return
	 */
	AdminUserAction getAdminUserAction(int id);
	
	/**
	 * 获取管理员行为
	 * @param actionKey
	 * @return
	 */
	AdminUserAction getAdminUserAction(String actionKey);
	
	/**
	 * 获取管理员行为列表
	 * @param roleId
	 * @return
	 */
	List<AdminUserAction> getAdminUserActionByRoleId(int roleId);
	
	/**
	 * 初始化管理员菜单
	 */
	void initAdminUserMenu();
	
	/**
	 * 列出管理员菜单
	 * @return
	 */
	List<AdminUserMenu> listAdminUserMenu();
	
	/**
	 * 根据link获取菜单
	 * @param link
	 * @return
	 */
	AdminUserMenu getAdminUserMenuByLink(String link);
	
	/**
	 * 根据角色列出菜单
	 * @param role
	 * @return
	 */
	List<AdminUserMenu> getAdminUserMenuByRoleId(int role);
	
	/**
	 * 根据基础行为列出所需菜单id
	 * @param action
	 * @return
	 */
	Set<Integer> getAdminUserMenuIdsByAction(int action);
	
}