package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.pool.DataFactory;

public class LotteryOpenCodeVO {
	private Integer lotteryId;
	private Integer userId;
	private String name;
	private String expect;
	private String code;

	public LotteryOpenCodeVO() {
	}

	public LotteryOpenCodeVO(LotteryOpenCode bean, DataFactory dataFactory) {
		if ("jsmmc".equals(bean.getLottery())) {
			this.expect = "";
		}
		else {
			this.expect = bean.getExpect();
		}
		this.code = bean.getCode();
		this.userId = bean.getUserId();
		Lottery lottery = dataFactory.getLottery(bean.getLottery());
		this.lotteryId = lottery.getId();
		this.name = lottery.getShowName();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getExpect() {
		return expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(Integer lotteryId) {
		this.lotteryId = lotteryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}