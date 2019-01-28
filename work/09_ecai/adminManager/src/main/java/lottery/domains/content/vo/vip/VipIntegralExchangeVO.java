package lottery.domains.content.vo.vip;

import lottery.domains.content.entity.VipIntegralExchange;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class VipIntegralExchangeVO {
	
	private String username;
	private VipIntegralExchange bean;
	
	public VipIntegralExchangeVO(VipIntegralExchange bean, LotteryDataFactory lotteryDataFactory) {
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

	public VipIntegralExchange getBean() {
		return bean;
	}

	public void setBean(VipIntegralExchange bean) {
		this.bean = bean;
	}

}