package lottery.domains.content.vo.user;

import lottery.domains.content.entity.HistoryUserWithdraw;
import lottery.domains.pool.LotteryDataFactory;

public class HistoryUserWithdrawVO {
	
	private HistoryUserWithdraw bean;
	private String username;

	public HistoryUserWithdrawVO(HistoryUserWithdraw bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if (user != null) {
			this.username = user.getUsername();
		}
	}

	public HistoryUserWithdraw getBean() {
		return bean;
	}

	public void setBean(HistoryUserWithdraw bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
