package activity.domains.content.entity.activity;

/**
 * 薪资活动
 */
public class RebateRulesSalary {

	private double money;
	private double reward;
	private int people;

	public RebateRulesSalary() {

	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}
	
	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

}