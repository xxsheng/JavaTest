package admin.domains.content.vo;

import admin.domains.content.entity.AdminUserLog;
import admin.domains.pool.AdminDataFactory;

public class AdminUserLogVO {
	
	private String username;
	private AdminUserLog bean;
	
	public AdminUserLogVO(AdminUserLog bean, AdminDataFactory df) {
		this.bean = bean;
		AdminUserBaseVO user = df.getAdminUser(bean.getUserId());
		if(user != null) {
			this.username = user.getUsername();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AdminUserLog getBean() {
		return bean;
	}

	public void setBean(AdminUserLog bean) {
		this.bean = bean;
	}

}