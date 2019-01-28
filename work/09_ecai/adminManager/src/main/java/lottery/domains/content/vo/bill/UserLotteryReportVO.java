package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.UserLotteryReport;

public class UserLotteryReportVO {

	private String name;

	private double transIn;
	private double transOut;
	private double cashIn;
	private double cashOut;
	private double spend;
	private double prize;
	private double spendReturn;
	private double proxyReturn;
	private double cancelOrder;
	private double activity;
	private double dividend;
	private double billingOrder;
	private double packet;
	private double rechargeFee;
	private boolean hasMore;

	public UserLotteryReportVO() {

	}

	public UserLotteryReportVO(String name) {
		this.name = name;
	}

	public void addBean(UserLotteryReport bean) {
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.spend += bean.getSpend();
		this.prize += bean.getPrize();
		this.spendReturn += bean.getSpendReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.cancelOrder += bean.getCancelOrder();
		this.activity += bean.getActivity();
		this.dividend += bean.getDividend();
		this.billingOrder += bean.getBillingOrder();
		this.packet += bean.getPacket();
		this.rechargeFee += bean.getRechargeFee();
	}
	
	public void addCash(double pCashIn, double pCashOut){
		this.cashIn += pCashIn;
		this.cashOut += pCashOut;
	}

	public void addBean(UserLotteryReportVO bean) {
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.spend += bean.getSpend();
		this.prize += bean.getPrize();
		this.spendReturn += bean.getSpendReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.cancelOrder += bean.getCancelOrder();
		this.activity += bean.getActivity();
		this.dividend += bean.getDividend();
		this.billingOrder += bean.getBillingOrder();
		this.packet += bean.getPacket();
		this.rechargeFee += bean.getRechargeFee();
	}

	public double getPacket() {
		return packet;
	}

	public void setPacket(double packet) {
		this.packet = packet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	public double getDividend() {
		return dividend;
	}

	public void setDividend(double dividend) {
		this.dividend = dividend;
	}

	public double getRechargeFee() {
		return rechargeFee;
	}

	public void setRechargeFee(double rechargeFee) {
		this.rechargeFee = rechargeFee;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public double getCashIn() {
		return cashIn;
	}

	public void setCashIn(double cashIn) {
		this.cashIn = cashIn;
	}

	public double getCashOut() {
		return cashOut;
	}

	public void setCashOut(double cashOut) {
		this.cashOut = cashOut;
	}

}