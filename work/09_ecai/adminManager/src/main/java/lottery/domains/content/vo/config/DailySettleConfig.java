package lottery.domains.content.vo.config;

public class DailySettleConfig {
	private double neibuZhaoShangScale; // 内部招商日结比例
	private int neibuZhaoShangMinValidUser; // 内疗招商最低活跃人数
	private double zhaoShangScale; // 招商日结比例
	private double cjZhaoShangScale; // 超级招商日结比例
	private int zhaoShangMinValidUser; // 招商最低活跃人数（含招商或超级招商）
	private boolean enable; // 是否开启契约日结
	private double minBillingOrder;
	
	private int maxSignLevel;// 允许签订契约最多条款数目
	private int minValidUserl;// 平台要求最小有效用户
	private boolean checkLoss;// 分红结算 是否检查亏损
	private double[] levelsLoss;// 亏损范围定义
	private double[] levelsSales;// 销量范围定义
	private double[] levelsScale;// 分红比率范围定义

	public double getNeibuZhaoShangScale() {
		return neibuZhaoShangScale;
	}

	public void setNeibuZhaoShangScale(double neibuZhaoShangScale) {
		this.neibuZhaoShangScale = neibuZhaoShangScale;
	}

	public int getNeibuZhaoShangMinValidUser() {
		return neibuZhaoShangMinValidUser;
	}

	public void setNeibuZhaoShangMinValidUser(int neibuZhaoShangMinValidUser) {
		this.neibuZhaoShangMinValidUser = neibuZhaoShangMinValidUser;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public double getMinBillingOrder() {
		return minBillingOrder;
	}

	public double getZhaoShangScale() {
		return zhaoShangScale;
	}

	public void setZhaoShangScale(double zhaoShangScale) {
		this.zhaoShangScale = zhaoShangScale;
	}

	public double getCjZhaoShangScale() {
		return cjZhaoShangScale;
	}

	public void setCjZhaoShangScale(double cjZhaoShangScale) {
		this.cjZhaoShangScale = cjZhaoShangScale;
	}

	public int getZhaoShangMinValidUser() {
		return zhaoShangMinValidUser;
	}

	public void setZhaoShangMinValidUser(int zhaoShangMinValidUser) {
		this.zhaoShangMinValidUser = zhaoShangMinValidUser;
	}

	public void setMinBillingOrder(double minBillingOrder) {
		this.minBillingOrder = minBillingOrder;
	}

	public int getMaxSignLevel() {
		return maxSignLevel;
	}

	public void setMaxSignLevel(int maxSignLevel) {
		this.maxSignLevel = maxSignLevel;
	}

	public int getMinValidUserl() {
		return minValidUserl;
	}

	public void setMinValidUserl(int minValidUserl) {
		this.minValidUserl = minValidUserl;
	}

	public boolean isCheckLoss() {
		return checkLoss;
	}

	public void setCheckLoss(boolean checkLoss) {
		this.checkLoss = checkLoss;
	}

	public double[] getLevelsLoss() {
		return levelsLoss;
	}

	public void setLevelsLoss(double[] levelsLoss) {
		this.levelsLoss = levelsLoss;
	}

	public double[] getLevelsSales() {
		return levelsSales;
	}

	public void setLevelsSales(double[] levelsSales) {
		this.levelsSales = levelsSales;
	}

	public double[] getLevelsScale() {
		return levelsScale;
	}

	public void setLevelsScale(double[] levelsScale) {
		this.levelsScale = levelsScale;
	}
	
	
}