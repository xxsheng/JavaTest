package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryOpenCodeVO {

	private String lotteryName;
	private LotteryOpenCode bean;
	private String username;

	public LotteryOpenCodeVO(LotteryOpenCode bean, LotteryDataFactory df) {
		this.bean = bean;
		Lottery lottery = df.getLottery(bean.getLottery());
		if(lottery != null) {
			this.lotteryName = lottery.getShowName();
		}
		if (bean.getUserId() != null) {
			UserVO user = df.getUser(bean.getUserId());
			if (user != null) {
				username = user.getUsername();
			}
		}
	}

	public String getLotteryName() {
		return lotteryName;
	}

	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}

	public LotteryOpenCode getBean() {
		return bean;
	}

	public void setBean(LotteryOpenCode bean) {
		this.bean = bean;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}