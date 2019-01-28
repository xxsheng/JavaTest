package admin.domains.content.vo;

import admin.domains.content.entity.AdminUserAction;
import admin.domains.pool.AdminDataFactory;

public class AdminUserActionVO {
	
	private AdminUserAction bean;
	
	public AdminUserActionVO(AdminUserAction bean, AdminDataFactory df) {
		this.bean = bean;
	}

	public AdminUserAction getBean() {
		return bean;
	}

	public void setBean(AdminUserAction bean) {
		this.bean = bean;
	}
	
}