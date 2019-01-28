package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserWithdrawLimit;

public class UserWithdrawLimitVO {
	private UserWithdrawLimit bean;
	private String username;
	private double totalBilling;//当前已消费量
	private double remainConsumption;//当前剩余消费量

	public UserWithdrawLimitVO(UserWithdrawLimit bean, String username, double totalBilling, double remainConsumption) {
		super();
		this.bean = bean;
		this.username = username;
		this.totalBilling = totalBilling;
		this.remainConsumption = remainConsumption;
	}

	public UserWithdrawLimit getBean() {
		return bean;
	}

	public void setBean(UserWithdrawLimit bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getTotalBilling() {
		return totalBilling;
	}

	public void setTotalBilling(double totalBilling) {
		this.totalBilling = totalBilling;
	}

	public double getRemainConsumption() {
		return remainConsumption;
	}

	public void setRemainConsumption(double remainConsumption) {
		this.remainConsumption = remainConsumption;
	}
}
