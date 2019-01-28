package lottery.domains.content.vo.config;

import java.util.List;

public class PlanConfig {
	
	private double minMoney;
	private List<String> title;
	private List<Integer> rate;
	private List<Integer> level;
	
	public PlanConfig() {
		
	}

	public double getMinMoney() {
		return minMoney;
	}

	public void setMinMoney(double minMoney) {
		this.minMoney = minMoney;
	}

	public List<String> getTitle() {
		return title;
	}

	public void setTitle(List<String> title) {
		this.title = title;
	}

	public List<Integer> getRate() {
		return rate;
	}

	public void setRate(List<Integer> rate) {
		this.rate = rate;
	}

	public List<Integer> getLevel() {
		return level;
	}

	public void setLevel(List<Integer> level) {
		this.level = level;
	}
	
}