package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.HistoryUserBill;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.UserBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class HistoryUserBillVO {

	private String username;
	private String account;
	private HistoryUserBill bean;

	public HistoryUserBillVO(HistoryUserBill bean, LotteryDataFactory lotteryDataFactory) {
		this.bean = bean;
		SysPlatform platform = lotteryDataFactory.getSysPlatform(bean.getAccount());
		if(platform != null) {
			this.account = platform.getName();
		}
		UserVO uBean = lotteryDataFactory.getUser(bean.getUserId());
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

	public HistoryUserBill getBean() {
		return bean;
	}

	public void setBean(HistoryUserBill bean) {
		this.bean = bean;
	}



}