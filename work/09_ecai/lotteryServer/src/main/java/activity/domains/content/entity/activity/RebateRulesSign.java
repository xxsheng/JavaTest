package activity.domains.content.entity.activity;

/**
 * 签到活动
 */
public class RebateRulesSign {

	private double day;
	private double cost;
	private double reward;
	
	public RebateRulesSign() {
		
	}

	public double getDay() {
		return day;
	}

	public void setDay(double day) {
		this.day = day;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

}