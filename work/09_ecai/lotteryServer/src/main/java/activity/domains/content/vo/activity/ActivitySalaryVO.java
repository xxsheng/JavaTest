package activity.domains.content.vo.activity;

import java.util.List;

import activity.domains.content.entity.activity.RebateRulesSalary;

public class ActivitySalaryVO {

	private double totalMoney;
	private double totalProfit;
	private List<RebateRulesSalary> rlist;
	private boolean isReceived;
	private int people;
	
	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public ActivitySalaryVO() {
		
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public double getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(double totalProfit) {
		this.totalProfit = totalProfit;
	}

	public List<RebateRulesSalary> getRlist() {
		return rlist;
	}

	public void setRlist(List<RebateRulesSalary> rlist) {
		this.rlist = rlist;
	}

	public boolean isReceived() {
		return isReceived;
	}

	public void setReceived(boolean isReceived) {
		this.isReceived = isReceived;
	}
	
}