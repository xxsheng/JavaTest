package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivitySignBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivitySignBillVO {
	
	private String username;
	public ActivitySignBill bean;
	
	public ActivitySignBillVO() {
		
	}
	
	public ActivitySignBillVO(ActivitySignBill bean, LotteryDataFactory df) {
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

	public ActivitySignBill getBean() {
		return bean;
	}

	public void setBean(ActivitySignBill bean) {
		this.bean = bean;
	}

}