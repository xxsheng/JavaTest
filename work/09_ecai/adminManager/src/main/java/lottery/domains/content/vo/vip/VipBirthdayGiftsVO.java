package lottery.domains.content.vo.vip;

import lottery.domains.content.entity.VipBirthdayGifts;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class VipBirthdayGiftsVO {
	
	private String username;
	private VipBirthdayGifts bean;
	
	public VipBirthdayGiftsVO(VipBirthdayGifts bean, LotteryDataFactory lotteryDataFactory) {
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

	public VipBirthdayGifts getBean() {
		return bean;
	}

	public void setBean(VipBirthdayGifts bean) {
		this.bean = bean;
	}

}