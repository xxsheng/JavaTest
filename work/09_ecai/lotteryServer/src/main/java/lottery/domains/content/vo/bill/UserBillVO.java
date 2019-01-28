package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.UserBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

public class UserBillVO {

	private String username;
	private String account;
	private UserBill bean;

	public UserBillVO(UserBill bean, DataFactory dataFactory) {
		this.bean = bean;
		SysPlatform platform = dataFactory.getSysPlatform(bean.getAccount());
		if(platform != null) {
			this.account = platform.getName();
		}
		UserVO uBean = dataFactory.getUser(bean.getUserId());
		if(uBean != null) {
			this.username = uBean.getUsername();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public UserBill getBean() {
		return bean;
	}

	public void setBean(UserBill bean) {
		this.bean = bean;
	}

}