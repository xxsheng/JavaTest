package admin.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.domains.content.biz.AdminUserActionService;
import admin.domains.content.dao.AdminUserActionDao;
import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.vo.AdminUserActionVO;
import admin.domains.pool.AdminDataFactory;

@Service
public class AdminUserActionServiceImpl implements AdminUserActionService {
	
	@Autowired
	private AdminUserActionDao adminUserActionDao;
	
	@Autowired
	private AdminDataFactory adminDataFactory;

	@Override
	public List<AdminUserActionVO> listAll() {
		List<AdminUserAction> alist = adminUserActionDao.listAll();
		List<AdminUserActionVO> list = new ArrayList<>();
		for (AdminUserAction tmpBean : alist) {
			list.add(new AdminUserActionVO(tmpBean, adminDataFactory));
		}
		return list;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		AdminUserAction entity = adminUserActionDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean result = adminUserActionDao.update(entity);
			if(result) {
				adminDataFactory.initAdminUserAction();
			}
			return result;
		}
		return false;
	}

}