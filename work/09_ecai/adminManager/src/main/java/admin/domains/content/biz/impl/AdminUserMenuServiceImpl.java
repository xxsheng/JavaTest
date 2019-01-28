package admin.domains.content.biz.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.domains.content.biz.AdminUserMenuService;
import admin.domains.content.biz.utils.TreeUtil;
import admin.domains.content.dao.AdminUserMenuDao;
import admin.domains.content.entity.AdminUserMenu;
import admin.domains.pool.AdminDataFactory;

@Service
public class AdminUserMenuServiceImpl implements AdminUserMenuService {

	@Autowired
	private AdminUserMenuDao adminUserMenuDao;
	
	@Autowired
	private AdminDataFactory adminDataFactory;
	
	@Override
	public List<AdminUserMenu> listAll() {
		List<AdminUserMenu> mlist = adminUserMenuDao.listAll();
		List<AdminUserMenu> list = TreeUtil.listMenuRoot(mlist);
		return list;
	}
	
	@Override
	public AdminUserMenu getById(int id) {
		return adminUserMenuDao.getById(id);
	}
	
	@Override
	public boolean updateStatus(int id, int status) {
		AdminUserMenu entity = adminUserMenuDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean result = adminUserMenuDao.update(entity);
			if(result) {
				adminDataFactory.initAdminUserMenu();
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean moveUp(int id) {
		AdminUserMenu entity = adminUserMenuDao.getById(id);
		if(entity != null && entity.getSort() != 1) {//看是否是第一个
			// 获取上一位
			List<AdminUserMenu> prev = adminUserMenuDao.getBySortUp(entity.getUpid(), entity.getSort());
			if (prev !=null && prev.size()>0) {
				AdminUserMenu prevAdminUserMenu=prev.get(0);//上一个
				int adminUserMenuSort=entity.getSort()-prevAdminUserMenu.getSort();
				if (adminUserMenuSort>1) {
					// 上移
					adminUserMenuDao.modsort(entity.getId(), -1);
				}else{
					adminUserMenuDao.updateSort(entity.getId(), prev.get(0).getSort());
					// 上一位下移
					adminUserMenuDao.updateSort(prev.get(0).getId(), entity.getSort());
				}
				adminDataFactory.initAdminUserMenu();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean moveDown(int id) {
		AdminUserMenu entity = adminUserMenuDao.getById(id);

		int total = adminUserMenuDao.getMaxSort(entity.getUpid());

		if(entity != null && entity.getSort() != total) {//看是否是已经是最后一个了
			// 获取下一位
			List<AdminUserMenu> nexts = adminUserMenuDao.getBySortDown(entity.getUpid(), entity.getSort());
			if (nexts !=null && nexts.size()>0) {
				AdminUserMenu nextAdminUserMenu=nexts.get(0);
				// 下移
				int nextAdminUserMenuSort=nextAdminUserMenu.getSort()-entity.getSort();
				if (nextAdminUserMenuSort>1) {
					adminUserMenuDao.modsort(entity.getId(), +1);
				}else{
					adminUserMenuDao.updateSort(entity.getId(), nextAdminUserMenu.getSort());
					// 下一位上移
					adminUserMenuDao.updateSort(nextAdminUserMenu.getId(), entity.getSort());
				}
				adminDataFactory.initAdminUserMenu();
				return true;
			}
		}
		return false;
	}
}