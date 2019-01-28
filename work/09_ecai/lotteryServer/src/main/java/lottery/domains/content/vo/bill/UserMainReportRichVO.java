package lottery.domains.content.vo.bill;

public class UserMainReportRichVO {

	private int id;
	private int userId;
	private double recharge;
	private double receiveFeeMoney; // 实际收款手续费，第三方收的
	private double withdrawals;
	private double transIn;
	private double transOut;
	private double accountIn;
	private double accountOut;
	private double activity;
	private String time;

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

	public double getRecharge() {
		return recharge;
	}

	public void setRecharge(double recharge) {
		this.recharge = recharge;
	}

	public double getReceiveFeeMoney() {
		return receiveFeeMoney;
	}

	public void setReceiveFeeMoney(double receiveFeeMoney) {
		this.receiveFeeMoney = receiveFeeMoney;
	}

	public double getWithdrawals() {
		return withdrawals;
	}

	public void setWithdrawals(double withdrawals) {
		this.withdrawals = withdrawals;
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

	public double getAccountIn() {
		return accountIn;
	}

	public void setAccountIn(double accountIn) {
		this.accountIn = accountIn;
	}

	public double getAccountOut() {
		return accountOut;
	}

	public void setAccountOut(double accountOut) {
		this.accountOut = accountOut;
	}

	public double getActivity() {
		return activity;
	}

	public void setActivity(double activity) {
		this.activity = activity;
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