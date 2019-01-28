package lottery.domains.content.vo.bill;

public class UserLotteryReportRichVO {

	private int id;
	private int userId;
	private double transIn;
	private double transOut;
	private double spend;
	private double prize;
	private double spendReturn;
	private double proxyReturn;
	private double cancelOrder;
	private double activity;
	private double dividend;
	private double billingOrder;
	private double packet;
	private String time;
	private double rechargeFee; // 充值手续费，平台这边的,主报表是实际的

	private String username;
	private int upid;
	private String upids;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public double getSpend() {
		return spend;
	}

	public void setSpend(double spend) {
		this.spend = spend;
	}

	public double getPrize() {
		return prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}

	public double getSpendReturn() {
		return spendReturn;
	}

	public void setSpendReturn(double spendReturn) {
		this.spendReturn = spendReturn;
	}

	public double getProxyReturn() {
		return proxyReturn;
	}

	public void setProxyReturn(double proxyReturn) {
		this.proxyReturn = proxyReturn;
	}

	public double getCancelOrder() {
		return cancelOrder;
	}

	public void setCancelOrder(double cancelOrder) {
		this.cancelOrder = cancelOrder;
	}

	public double getActivity() {
		return activity;
	}

	public void setActivity(double activity) {
		this.activity = activity;
	}

	public double getDividend() {
		return dividend;
	}

	public void setDividend(double dividend) {
		this.dividend = dividend;
	}

	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	public double getPacket() {
		return packet;
	}

	public void setPacket(double packet) {
		this.packet = packet;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getRechargeFee() {
		return rechargeFee;
	}

	public void setRechargeFee(double rechargeFee) {
		this.rechargeFee = rechargeFee;
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