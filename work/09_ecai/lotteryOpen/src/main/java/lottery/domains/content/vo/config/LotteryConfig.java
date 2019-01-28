package lottery.domains.content.vo.config;

public class LotteryConfig {

	private int bUnitMoney;
	private int fenModelDownCode;
	private int liModelDownCode;
	private boolean autoHitRanking;
	private int hitRankingSize;

	public LotteryConfig() {

	}

	public int getbUnitMoney() {
		return bUnitMoney;
	}

	public void setbUnitMoney(int bUnitMoney) {
		this.bUnitMoney = bUnitMoney;
	}

	public int getFenModelDownCode() {
		return fenModelDownCode;
	}

	public void setFenModelDownCode(int fenModelDownCode) {
		this.fenModelDownCode = fenModelDownCode;
	}

	public int getLiModelDownCode() {
		return liModelDownCode;
	}

	public void setLiModelDownCode(int liModelDownCode) {
		this.liModelDownCode = liModelDownCode;
	}

	public boolean isAutoHitRanking() {
		return autoHitRanking;
	}

	public void setAutoHitRanking(boolean autoHitRanking) {
		this.autoHitRanking = autoHitRanking;
	}

	public int getHitRankingSize() {
		return hitRankingSize;
	}

	public void setHitRankingSize(int hitRankingSize) {
		this.hitRankingSize = hitRankingSize;
	}
}