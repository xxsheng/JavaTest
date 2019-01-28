package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.LotteryCrawlerStatus;
import lottery.domains.utils.lottery.open.OpenTime;

public class LotteryCrawlerStatusVO {

	private OpenTime openTime;
	private LotteryCrawlerStatus bean;

	public LotteryCrawlerStatusVO() {

	}

	public OpenTime getOpenTime() {
		return openTime;
	}

	public void setOpenTime(OpenTime openTime) {
		this.openTime = openTime;
	}

	public LotteryCrawlerStatus getBean() {
		return bean;
	}

	public void setBean(LotteryCrawlerStatus bean) {
		this.bean = bean;
	}

}