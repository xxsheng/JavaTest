package lottery.domains.content.vo.user;

import admin.domains.content.entity.AdminUser;
import lottery.domains.content.entity.UserWithdrawLog;
import lottery.domains.pool.LotteryDataFactory;

public class UserWithdrawLogVO {
	
	private int id;
	private int userId;
	private String username;
	private int adminUserId;
	private String action;
	private String time;
	public UserWithdrawLogVO() {
	}
	public UserWithdrawLogVO(UserWithdrawLog bean, AdminUser df) {
		if (df != null) {
			this.username = df.getUsername();
		}
		this.id = bean.getId();
		this.userId = bean.getUserId();
		this.action = bean.getAction();
		this.time = bean.getTime();
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	
}