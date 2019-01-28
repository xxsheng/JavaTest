package activity.domains.content.vo.activity;

public class ActivitySignVO {

	private int days; // 已经连续签到天数
	private boolean todaySigned; // 今天是否已经签到过了

	public ActivitySignVO() {}

	public ActivitySignVO(int days, boolean todaySigned) {
		this.days = days;
		this.todaySigned = todaySigned;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public boolean isTodaySigned() {
		return todaySigned;
	}

	public void setTodaySigned(boolean todaySigned) {
		this.todaySigned = todaySigned;
	}
}