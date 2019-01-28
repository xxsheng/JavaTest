package lottery.domains.content.vo.vip;

import lottery.domains.content.entity.VipUpgradeList;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class VipUpgradeListVO {
	
	private String username;
	private VipUpgradeList bean;
	
	public VipUpgradeListVO(VipUpgradeList bean, LotteryDataFactory lotteryDataFactory) {
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

	public VipUpgradeList getBean() {
		return bean;
	}

	public void setBean(VipUpgradeList bean) {
		this.bean = bean;
	}

}