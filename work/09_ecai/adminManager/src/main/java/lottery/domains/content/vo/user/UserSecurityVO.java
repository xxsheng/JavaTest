package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserSecurity;
import lottery.domains.pool.LotteryDataFactory;

public class UserSecurityVO {
	
	private UserSecurity bean;
	private String username;

	public UserSecurityVO(UserSecurity bean, LotteryDataFactory df) {
		this.bean = bean;
		this.bean.setValue("***");
		UserVO user = df.getUser(bean.getUserId());
		if (user != null) {
			this.username = user.getUsername();
		}
	}

	public UserSecurity getBean() {
		return bean;
	}

	public void setBean(UserSecurity bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
