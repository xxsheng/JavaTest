package lottery.domains.content.vo.user;

import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.pool.LotteryDataFactory;

public class UserCardVO {
	
	private UserCard bean;
	private String username;
	private String bankName;
	
	public UserCardVO(UserCard bean, LotteryDataFactory df) {
		this.bean = bean;
		UserVO user = df.getUser(bean.getUserId());
		if(user != null) {
			this.username = user.getUsername();
		}
		PaymentBank bank = df.getPaymentBank(bean.getBankId());
		if(bank != null) {
			this.bankName = bank.getName();
		}
	}

	public UserCard getBean() {
		return bean;
	}

	public void setBean(UserCard bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
}