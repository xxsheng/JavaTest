package lottery.domains.content.entity.activity;

/**
 * 佣金活动
 */
public class RebateRulesReward {

	private double money;
	private double rewardUp1;
	private double rewardUp2;
	
	public RebateRulesReward() {
		
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getRewardUp1() {
		return rewardUp1;
	}

	public void setRewardUp1(double rewardUp1) {
		this.rewardUp1 = rewardUp1;
	}

	public double getRewardUp2() {
		return rewardUp2;
	}

	public void setRewardUp2(double rewardUp2) {
		this.rewardUp2 = rewardUp2;
	}

}