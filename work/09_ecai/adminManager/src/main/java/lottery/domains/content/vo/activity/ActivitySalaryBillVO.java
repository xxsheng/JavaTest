package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivitySalaryBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivitySalaryBillVO {

	private String username;
	private ActivitySalaryBill bean;

	public ActivitySalaryBillVO(ActivitySalaryBill bean, LotteryDataFactory lotteryDataFactory) {
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

	public ActivitySalaryBill getBean() {
		return bean;
	}

	public void setBean(ActivitySalaryBill bean) {
		this.bean = bean;
	}

}