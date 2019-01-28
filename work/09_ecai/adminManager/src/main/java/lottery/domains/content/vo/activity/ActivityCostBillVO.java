package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityCostBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivityCostBillVO {
	private String username;
	public ActivityCostBill bean;
	
	public ActivityCostBillVO() {
		
	}
	
	public ActivityCostBillVO(ActivityCostBill bean, LotteryDataFactory df) {
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

	public ActivityCostBill getBean() {
		return bean;
	}

	public void setBean(ActivityCostBill bean) {
		this.bean = bean;
	}
}
