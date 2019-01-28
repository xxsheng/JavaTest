package admin.domains.content.vo;

import admin.domains.content.entity.AdminUserRole;
import admin.domains.pool.AdminDataFactory;

public class AdminUserRoleVO {

	private AdminUserRole bean;

	public AdminUserRoleVO(AdminUserRole bean, AdminDataFactory df) {
		this.bean = bean;
	}

	public AdminUserRole getBean() {
		return bean;
	}

	public void setBean(AdminUserRole bean) {
		this.bean = bean;
	}

}