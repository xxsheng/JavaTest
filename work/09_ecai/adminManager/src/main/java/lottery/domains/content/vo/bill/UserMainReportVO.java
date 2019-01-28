package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.UserMainReport;

public class UserMainReportVO {

	private String name;
	
	private double recharge;
	private double withdrawals;
	private double transIn;
	private double transOut;
	private double accountIn;
	private double accountOut;
	private double activity;
	private boolean hasMore;
	
	public UserMainReportVO() {
		
	}
	
	public UserMainReportVO(String name) {
		this.name = name;
	}
	
	public void addBean(UserMainReport bean) {
		this.recharge += bean.getRecharge();
		this.withdrawals += bean.getWithdrawals();
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.accountIn += bean.getAccountIn();
		this.accountOut += bean.getAccountOut();
		this.activity += bean.getActivity();
	}
	
	public void addBean(UserMainReportVO bean) {
		this.recharge += bean.getRecharge();
		this.withdrawals += bean.getWithdrawals();
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.accountIn += bean.getAccountIn();
		this.accountOut += bean.getAccountOut();
		this.activity += bean.getActivity();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRecharge() {
		return recharge;
	}

	public void setRecharge(double recharge) {
		this.recharge = recharge;
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

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
}