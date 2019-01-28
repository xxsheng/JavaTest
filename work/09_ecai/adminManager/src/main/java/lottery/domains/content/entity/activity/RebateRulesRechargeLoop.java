package lottery.domains.content.entity.activity;

/**
 * 开始大酬宾，连环嗨不停
 */
public class RebateRulesRechargeLoop {
	
	private double s1Recharge;
	private double s1Cost;
	private double s1Reward;
	
	private double s2Cost;
	private double s2Reward;
	private int s2Times;
	
	private double s3Reward;
	private int s3Limit;
	
	public RebateRulesRechargeLoop() {
		
	}

	public double getS1Recharge() {
		return s1Recharge;
	}

	public void setS1Recharge(double s1Recharge) {
		this.s1Recharge = s1Recharge;
	}

	public double getS1Cost() {
		return s1Cost;
	}

	public void setS1Cost(double s1Cost) {
		this.s1Cost = s1Cost;
	}

	public double getS1Reward() {
		return s1Reward;
	}

	public void setS1Reward(double s1Reward) {
		this.s1Reward = s1Reward;
	}

	public double getS2Cost() {
		return s2Cost;
	}

	public void setS2Cost(double s2Cost) {
		this.s2Cost = s2Cost;
	}

	public double getS2Reward() {
		return s2Reward;
	}

	public void setS2Reward(double s2Reward) {
		this.s2Reward = s2Reward;
	}

	public int getS2Times() {
		return s2Times;
	}

	public void setS2Times(int s2Times) {
		this.s2Times = s2Times;
	}

	public double getS3Reward() {
		return s3Reward;
	}

	public void setS3Reward(double s3Reward) {
		this.s3Reward = s3Reward;
	}

	public int getS3Limit() {
		return s3Limit;
	}

	public void setS3Limit(int s3Limit) {
		this.s3Limit = s3Limit;
	}

}