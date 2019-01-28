package lottery.domains.content.vo.bill;

public class UserGameReportRichVO {

	private int userId;
	private int platformId;
	private double transIn;
	private double transOut;
	private double prize;
	private double waterReturn;
	private double proxyReturn;
	private double activity;
	private double billingOrder;
	private String time;

	private String username;
	private int upid;
	private String upids;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public double getTransIn() {
		return transIn;
	}

	public void setTransIn(double transIn) {
		this.transIn = transIn;
	}

	public double getTransOut() {
		return transOut;
	}

	public void setTransOut(double transOut) {
		this.transOut = transOut;
	}

	public double getPrize() {
		return prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}

	public double getWaterReturn() {
		return waterReturn;
	}

	public void setWaterReturn(double waterReturn) {
		this.waterReturn = waterReturn;
	}

	public double getProxyReturn() {
		return proxyReturn;
	}

	public void setProxyReturn(double proxyReturn) {
		this.proxyReturn = proxyReturn;
	}

	public double getActivity() {
		return activity;
	}

	public void setActivity(double activity) {
		this.activity = activity;
	}

	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUpid() {
		return upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
	}

	public String getUpids() {
		return upids;
	}

	public void setUpids(String upids) {
		this.upids = upids;
	}
}