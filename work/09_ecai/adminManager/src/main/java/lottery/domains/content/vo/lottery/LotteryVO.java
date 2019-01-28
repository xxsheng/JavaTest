package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryVO {
	
	private Lottery bean;
	private String lotteryType;
	
	public LotteryVO(Lottery bean, LotteryDataFactory df) {
		this.bean = bean;
		LotteryType lotteryType = df.getLotteryType(bean.getType());
		if(lotteryType != null) {
			this.lotteryType = lotteryType.getName();
		}
	}

	public Lottery getBean() {
		return bean;
	}

	public void setBean(Lottery bean) {
		this.bean = bean;
	}

	public String getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(String lotteryType) {
		this.lotteryType = lotteryType;
	}
	
}