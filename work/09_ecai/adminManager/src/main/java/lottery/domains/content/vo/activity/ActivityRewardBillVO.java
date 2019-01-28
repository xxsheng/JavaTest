package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityRewardBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivityRewardBillVO {
	
	private String toUser;
	private String fromUser;
	private ActivityRewardBill bean;
	
	public ActivityRewardBillVO(ActivityRewardBill bean, LotteryDataFactory lotteryDataFactory) {
		this.bean = bean;
		UserVO toUser = lotteryDataFactory.getUser(bean.getToUser());
		if(toUser != null) {
			this.toUser = toUser.getUsername();
		}
		UserVO fromUser = lotteryDataFactory.getUser(bean.getFromUser());
		if(toUser != null) {
			this.fromUser = fromUser.getUsername();
		}
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public ActivityRewardBill getBean() {
		return bean;
	}

	public void setBean(ActivityRewardBill bean) {
		this.bean = bean;
	}

}