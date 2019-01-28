package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserActionLog;
import lottery.domains.pool.LotteryDataFactory;

public class UserActionLogVO {

	private String username;
	private UserActionLog bean;
	
	public UserActionLogVO(UserActionLog bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if (user != null) {
			this.username = user.getUsername();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserActionLog getBean() {
		return bean;
	}

	public void setBean(UserActionLog bean) {
		this.bean = bean;
	}
	
}