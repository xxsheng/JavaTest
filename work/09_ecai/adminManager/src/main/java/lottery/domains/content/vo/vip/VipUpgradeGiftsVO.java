package lottery.domains.content.vo.vip;

import lottery.domains.content.entity.VipUpgradeGifts;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class VipUpgradeGiftsVO {
	
	private String username;
	private VipUpgradeGifts bean;
	
	public VipUpgradeGiftsVO(VipUpgradeGifts bean, LotteryDataFactory lotteryDataFactory) {
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

	public VipUpgradeGifts getBean() {
		return bean;
	}

	public void setBean(VipUpgradeGifts bean) {
		this.bean = bean;
	}

}