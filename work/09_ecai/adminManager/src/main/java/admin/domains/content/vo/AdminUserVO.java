package admin.domains.content.vo;

import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.pool.AdminDataFactory;

public class AdminUserVO {

	private String role;
	private AdminUser bean;

	public AdminUserVO(AdminUser bean, AdminDataFactory df) {
		this.bean = bean;
		this.bean.setPassword("***");
		this.bean.setSecretKey("***");
		if (!"notset".equalsIgnoreCase(bean.getWithdrawPwd())) {
			this.bean.setWithdrawPwd("***");
		}
		AdminUserRole role = df.getAdminUserRole(bean.getRoleId());
		this.role = role.getName();
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public AdminUser getBean() {
		return bean;
	}

	public void setBean(AdminUser bean) {
		this.bean = bean;
	}

}