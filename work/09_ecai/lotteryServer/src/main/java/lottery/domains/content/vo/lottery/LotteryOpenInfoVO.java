package lottery.domains.content.vo.lottery;

public class LotteryOpenInfoVO {

	private int lotteryId;
	private String openExpect;
	private String prizeExpect;
	
	public LotteryOpenInfoVO(int lotteryId, String openExpect,
			String prizeExpect) {
		this.lotteryId = lotteryId;
		this.openExpect = openExpect;
		this.prizeExpect = prizeExpect;
	}

	public int getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}

	public String getOpenExpect() {
		return openExpect;
	}

	public void setOpenExpect(String openExpect) {
		this.openExpect = openExpect;
	}

	public String getPrizeExpect() {
		return prizeExpect;
	}

	public void setPrizeExpect(String prizeExpect) {
		this.prizeExpect = prizeExpect;
	}

}