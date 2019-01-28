package admin.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.domains.content.biz.AdminUserRoleService;
import admin.domains.content.biz.utils.TreeUtil;
import admin.domains.content.dao.AdminUserRoleDao;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.content.vo.AdminUserRoleVO;
import admin.domains.pool.AdminDataFactory;
import javautils.StringUtil;

@Service
public class AdminUserRoleServiceImpl implements AdminUserRoleService {
	
	@Autowired
	private AdminUserRoleDao adminUserRoleDao;
	
	@Autowired
	private AdminDataFactory adminDataFactory;
	
	public void listRoleChild(AdminUserRole adminUserRole, List<AdminUserRole> alist, List<AdminUserRole> rlist) {
		for (AdminUserRole tmpRole : alist) {
			tmpRole.setMenus(null);
			tmpRole.setActions(null);
			if(tmpRole.getUpid() == adminUserRole.getId()) {
				rlist.add(tmpRole);
				listRoleChild(tmpRole, alist, rlist);
			}
		}
	}
	
	@Override
	public List<AdminUserRole> listAll(int id) {
		List<AdminUserRole> rlist = new ArrayList<>();
		AdminUserRole adminUserRole = adminDataFactory.getAdminUserRole(id);
		adminUserRole.setMenus(null);
		adminUserRole.setActions(null);
		adminUserRole.setUpid(0);
		rlist.add(adminUserRole);
		List<AdminUserRole> alist = adminDataFactory.listAdminUserRole();
		listRoleChild(adminUserRole, alist, rlist);
		return rlist;
	}
	
	@Override
	public List<AdminUserRole> listTree(int id) {
		List<AdminUserRole> rlist = listAll(id);
		List<AdminUserRole> list = TreeUtil.listRoleRoot(rlist);
		return list;
	}

	@Override
	public boolean add(String name, int upid, String description, int sort) {
		int status = 0;
		String menus = null;
		String actions = null;
		AdminUserRole entity = new AdminUserRole(name, upid, description, sort, status, menus, actions);
		boolean result = adminUserRoleDao.save(entity);
		if(result) {
			adminDataFactory.initAdminUserRole();
		}
		return result;
	}

	@Override
	public boolean edit(int id, String name, int upid, String description, int sort) {
		AdminUserRole entity = adminUserRoleDao.getById(id);
		if(entity != null) {
			entity.setName(name);
			entity.setUpid(upid);
			entity.setDescription(description);
			entity.setSort(sort);
			boolean result = adminUserRoleDao.update(entity);
			if(result) {
				adminDataFactory.initAdminUserRole();
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		AdminUserRole entity = adminUserRoleDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean result = adminUserRoleDao.update(entity);
			if(result) {
				adminDataFactory.initAdminUserRole();
			}
			return result;
		}
		return false;
	}

	@Override
	public AdminUserRoleVO getByName(String name) {
		AdminUserRole entity = adminUserRoleDao.getByName(name);
		if(entity != null) {
			AdminUserRoleVO bean = new AdminUserRoleVO(entity, adminDataFactory);
			return bean;
		}
		return null;
	}

	@Override
	public AdminUserRoleVO getById(int id) {
		AdminUserRole entity = adminUserRoleDao.getById(id);
		if(entity != null) {
			AdminUserRoleVO bean = new AdminUserRoleVO(entity, adminDataFactory);
			return bean;
		}
		return null;
	}
	
	@Override
	public boolean saveAccess(int id, String ids) {
		String[] arr = ids.split(",");
		Set<Integer> mSet = new HashSet<>();
		Set<Integer> aSet = new HashSet<>();
		for (String s : arr) {
			if(StringUtil.isIntegerString(s)) {
				aSet.add(Integer.parseInt(s));
			}
		}
		for (int action : aSet) {
			Set<Integer> tmpList = adminDataFactory.getAdminUserMenuIdsByAction(action);
			mSet.addAll(tmpList);
		}
		AdminUserRole entity = adminUserRoleDao.getById(id);
		if(entity != null) {
			entity.setMenus(JSONArray.fromObject(mSet).toString());
			entity.setActions(JSONArray.fromObject(aSet).toString());
			boolean result = adminUserRoleDao.update(entity);
			if(result) {
				//获取下一级角色
				List<AdminUserRole> adminUserRoles = adminUserRoleDao.getByUpid(id);
				recursivelyUserRolesMenusActions(adminUserRoles, mSet,aSet);
				adminDataFactory.initAdminUserRole();
			}
			return result;
		}
		return false;
	}
	
	
	public void recursivelyUserRolesMenusActions(List<AdminUserRole> adminUserRoles,Set<Integer> mSet,Set<Integer> aSet){
		for (AdminUserRole adminUserRole : adminUserRoles) {
				JSONArray jsonArrayMenus=JSONArray.fromObject(adminUserRole.getMenus());
				JSONArray jsonArrayActions=JSONArray.fromObject(adminUserRole.getActions());
					Object [] menus=jsonArrayMenus.toArray();
					for (Object object : menus) {
						if (!mSet.contains(object)) {
							jsonArrayMenus.remove(object);
						}
					}
					Object [] actions=jsonArrayActions.toArray();
					for (Object object : actions) {
						if (!aSet.contains(object)) {
							jsonArrayActions.remove(object);
						}
					}
				adminUserRole.setMenus(jsonArrayMenus.toString());
				adminUserRole.setActions(jsonArrayActions.toString());
				adminUserRoleDao.update(adminUserRole);
				List<AdminUserRole> adminUserRoles1 = adminUserRoleDao.getByUpid(adminUserRole.getId());
				if (adminUserRoles1 != null && adminUserRoles1.size()>0) {
					recursivelyUserRolesMenusActions(adminUserRoles1, mSet,aSet);
				}
		}
	}
}