package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityRechargeBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivityRechargeBillVO {
	
	private String username;
	private ActivityRechargeBill bean;
	
	public ActivityRechargeBillVO(ActivityRechargeBill bean, LotteryDataFactory lotteryDataFactory) {
		this.bean = bean;
		UserVO user = lotteryDataFactory.getUser(bean.getUserId());
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

	public ActivityRechargeBill getBean() {
		return bean;
	}

	public void setBean(ActivityRechargeBill bean) {
		this.bean = bean;
	}
	
}