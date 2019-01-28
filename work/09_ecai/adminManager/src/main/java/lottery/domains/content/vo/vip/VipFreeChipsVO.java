package lottery.domains.content.vo.vip;

import lottery.domains.content.entity.VipFreeChips;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class VipFreeChipsVO {
	
	private String username;
	private VipFreeChips bean;
	
	public VipFreeChipsVO(VipFreeChips bean, LotteryDataFactory lotteryDataFactory) {
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

	public VipFreeChips getBean() {
		return bean;
	}

	public void setBean(VipFreeChips bean) {
		this.bean = bean;
	}

}