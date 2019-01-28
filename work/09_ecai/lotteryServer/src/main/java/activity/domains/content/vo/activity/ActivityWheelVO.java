package activity.domains.content.vo.activity;

public class ActivityWheelVO {
	private double todayCost; //今日已消费金额
	private boolean todayDrew; // 今天是否已经参与过活动了
	private double remainCost; // 今日剩余消费可参与活动
	private double todayPrize; // 今日中奖
	private String date;
	private boolean enabled; // 活动是否开启中

	public double getTodayCost() {
		return todayCost;
	}

	public void setTodayCost(double todayCost) {
		this.todayCost = todayCost;
	}

	public boolean isTodayDrew() {
		return todayDrew;
	}

	public void setTodayDrew(boolean todayDrew) {
		this.todayDrew = todayDrew;
	}

	public double getRemainCost() {
		return remainCost;
	}

	public void setRemainCost(double remainCost) {
		this.remainCost = remainCost;
	}

	public double getTodayPrize() {
		return todayPrize;
	}

	public void setTodayPrize(double todayPrize) {
		this.todayPrize = todayPrize;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}