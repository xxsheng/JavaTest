package admin.domains.content.vo;

import admin.domains.content.entity.AdminUserCriticalLog;
import admin.domains.pool.AdminDataFactory;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class AdminUserCriticalLogVO {
	
	private String username;
	private String adminUsername;
	private AdminUserCriticalLog bean;
	
	public AdminUserCriticalLogVO(AdminUserCriticalLog bean, AdminDataFactory df,LotteryDataFactory ldf) {
		this.bean = bean;
		AdminUserBaseVO adminUser = df.getAdminUser(bean.getAdminUserId());
		if(adminUser != null) {
			this.adminUsername = adminUser.getUsername();
		}
		UserVO user=ldf.getUser(bean.getUserId());
		if (user !=null) {
			this.username = user.getUsername();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AdminUserCriticalLog getBean() {
		return bean;
	}

	public void setBean(AdminUserCriticalLog bean) {
		this.bean = bean;
	}

	public String getAdminUsername() {
		return adminUsername;
	}

	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}

}