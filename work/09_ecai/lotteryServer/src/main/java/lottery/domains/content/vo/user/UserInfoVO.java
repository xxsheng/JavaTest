package lottery.domains.content.vo.user;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserInfo;

public class UserInfoVO {

	private String username;
	private double totalMoney;
	private UserInfo bean;
	
	public UserInfoVO() {
		
	}
	
	public UserInfoVO(User uBean, UserInfo iBean) {
		this.totalMoney = uBean.getTotalMoney();
		this.username = uBean.getUsername();
		this.bean = iBean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public UserInfo getBean() {
		return bean;
	}

	public void setBean(UserInfo bean) {
		this.bean = bean;
	}

}