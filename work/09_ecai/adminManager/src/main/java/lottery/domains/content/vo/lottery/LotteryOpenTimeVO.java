package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryOpenTimeVO {
	
	private LotteryOpenTime bean;
	private String lottery;
	
	public LotteryOpenTimeVO(LotteryOpenTime bean, LotteryDataFactory df) {
		this.bean = bean;
		Lottery lottery = df.getLottery(bean.getLottery());
		if(lottery != null) {
			this.lottery = lottery.getShowName();
		}
	}

	public LotteryOpenTime getBean() {
		return bean;
	}

	public void setBean(LotteryOpenTime bean) {
		this.bean = bean;
	}

	public String getLottery() {
		return lottery;
	}

	public void setLottery(String lottery) {
		this.lottery = lottery;
	}
	
}
