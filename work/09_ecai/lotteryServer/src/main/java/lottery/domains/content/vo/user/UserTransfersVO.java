package lottery.domains.content.vo.user;

import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.UserTransfers;
import lottery.domains.pool.DataFactory;

public class UserTransfersVO {

	private String toAccount;
	private String fromAccount;
	private UserTransfers bean;
	
	public UserTransfersVO(UserTransfers bean, DataFactory dataFactory) {
		this.bean = bean;
		SysPlatform toAccount = dataFactory.getSysPlatform(bean.getToAccount());
		if(toAccount != null) {
			this.toAccount = toAccount.getName();
		}
		SysPlatform fromAccount = dataFactory.getSysPlatform(bean.getFromAccount());
		if(fromAccount != null) {
			this.fromAccount = fromAccount.getName();
		}
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public UserTransfers getBean() {
		return bean;
	}

	public void setBean(UserTransfers bean) {
		this.bean = bean;
	}

}
