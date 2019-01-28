package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityGrabBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivityGrabBillVO {
	
	private String username;
	public ActivityGrabBill bean;
	
	public ActivityGrabBillVO() {
		
	}
	
	public ActivityGrabBillVO(ActivityGrabBill bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
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

	public ActivityGrabBill getBean() {
		return bean;
	}

	public void setBean(ActivityGrabBill bean) {
		this.bean = bean;
	}

}