package activity.domains.content.vo.activity;

import activity.domains.content.entity.activity.RebateRulesRechargeLoop;

public class ActivityRechargeLoopVO {

	private double s1Recharge; // 当前充值
	private double s1Cost; // 当前消费
	private double s1Reward; // 奖励金额
	private boolean s1Available = false; // 是否达到要求
	private boolean s1Received = false; // 是否领取

	private double s2Cost; // 当前消费
	private int s2Times; // 已进行次数
	private boolean s2Available = false; // 是否达到要求
	private boolean s2Received = false; // 是否领取

	private boolean s3Available = false; // 是否达到要求
	private boolean s3Received = false; // 是否领取
	
	private RebateRulesRechargeLoop rules;

	public ActivityRechargeLoopVO() {

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

	public boolean isS1Available() {
		return s1Available;
	}

	public void setS1Available(boolean s1Available) {
		this.s1Available = s1Available;
	}

	public boolean isS1Received() {
		return s1Received;
	}

	public void setS1Received(boolean s1Received) {
		this.s1Received = s1Received;
	}

	public double getS2Cost() {
		return s2Cost;
	}

	public void setS2Cost(double s2Cost) {
		this.s2Cost = s2Cost;
	}

	public int getS2Times() {
		return s2Times;
	}

	public void setS2Times(int s2Times) {
		this.s2Times = s2Times;
	}

	public boolean isS2Available() {
		return s2Available;
	}

	public void setS2Available(boolean s2Available) {
		this.s2Available = s2Available;
	}

	public boolean isS2Received() {
		return s2Received;
	}

	public void setS2Received(boolean s2Received) {
		this.s2Received = s2Received;
	}

	public boolean isS3Available() {
		return s3Available;
	}

	public void setS3Available(boolean s3Available) {
		this.s3Available = s3Available;
	}

	public boolean isS3Received() {
		return s3Received;
	}

	public void setS3Received(boolean s3Received) {
		this.s3Received = s3Received;
	}

	public RebateRulesRechargeLoop getRules() {
		return rules;
	}

	public void setRules(RebateRulesRechargeLoop rules) {
		this.rules = rules;
	}

}