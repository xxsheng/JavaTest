package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.pool.LotteryDataFactory;

public class UserWithdrawVO {
	
	private UserWithdraw bean;
	private String username;

	public UserWithdrawVO(UserWithdraw bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if (user != null) {
			this.username = user.getUsername();
		}
	}

	public UserWithdraw getBean() {
		return bean;
	}

	public void setBean(UserWithdraw bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
