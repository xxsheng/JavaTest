package admin.domains.content.biz;

import java.util.List;

import admin.domains.content.entity.AdminUserRole;
import admin.domains.content.vo.AdminUserRoleVO;

public interface AdminUserRoleService {

	List<AdminUserRole> listAll(int id);
	
	List<AdminUserRole> listTree(int id);
	
	boolean add(String name, int upid, String description, int sort);
	
	boolean edit(int id, String name, int upid, String description, int sort);
	
	boolean updateStatus(int id, int status);
	
	AdminUserRoleVO getByName(String name);
	
	AdminUserRoleVO getById(int id);
	
	boolean saveAccess(int id, String ids);
	
}