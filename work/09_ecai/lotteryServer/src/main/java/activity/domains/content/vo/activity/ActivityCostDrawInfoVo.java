package activity.domains.content.vo.activity;

public class ActivityCostDrawInfoVo {
	private double todayCost;//今日已消费金额
	private double mayDraw;//可领取金额/或已领金额
	private int isReceived; // 是否已领取 0:不可领取 、1:可领取、2已领取
	public double getTodayCost() {
		return todayCost;
	}
	public void setTodayCost(double todayCost) {
		this.todayCost = todayCost;
	}
	public double getMayDraw() {
		return mayDraw;
	}
	public void setMayDraw(double mayDraw) {
		this.mayDraw = mayDraw;
	}
	public int getIsReceived() {
		return isReceived;
	}
	public void setIsReceived(int isReceived) {
		this.isReceived = isReceived;
	}
}
