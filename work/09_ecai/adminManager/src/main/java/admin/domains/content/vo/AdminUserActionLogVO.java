package admin.domains.content.vo;

import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserActionLog;
import admin.domains.pool.AdminDataFactory;

public class AdminUserActionLogVO {
	
	private String username;
	private String actionName;
	private AdminUserActionLog bean;
	
	public AdminUserActionLogVO(AdminUserActionLog bean, AdminDataFactory df) {
		this.bean = bean;
		AdminUserBaseVO user = df.getAdminUser(bean.getUserId());
		if(user != null) {
			this.username = user.getUsername();
		}
		AdminUserAction action = df.getAdminUserAction(bean.getActionId());
		if(action != null) {
			this.actionName = action.getName();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public AdminUserActionLog getBean() {
		return bean;
	}

	public void setBean(AdminUserActionLog bean) {
		this.bean = bean;
	}

}