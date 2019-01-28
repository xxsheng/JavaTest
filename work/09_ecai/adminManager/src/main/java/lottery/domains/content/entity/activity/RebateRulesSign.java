package lottery.domains.content.entity.activity;

/**
 * 签到活动
 */
public class RebateRulesSign {

	private int day;
	private double minCost;
	private double rewardPercent;
	private double max;

	public RebateRulesSign() {
		
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public double getMinCost() {
		return minCost;
	}

	public void setMinCost(double minCost) {
		this.minCost = minCost;
	}

	public double getRewardPercent() {
		return rewardPercent;
	}

	public void setRewardPercent(double rewardPercent) {
		this.rewardPercent = rewardPercent;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}
}