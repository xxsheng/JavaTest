package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.UserBaccaratReport;

public class UserBaccaratReportVO {

	private String name;
	
	private double transIn;
	private double transOut;
	private double spend;
	private double prize;
	private double waterReturn;
	private double proxyReturn;
	private double cancelOrder;
	private double activity;
	private double billingOrder;
	
	public UserBaccaratReportVO() {
		
	}
	
	public UserBaccaratReportVO(String name) {
		this.name = name;
	}
	
	public void addBean(UserBaccaratReport bean) {
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.spend += bean.getSpend();
		this.prize += bean.getPrize();
		this.waterReturn += bean.getWaterReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.cancelOrder += bean.getCancelOrder();
		this.activity += bean.getActivity();
		this.billingOrder += bean.getBillingOrder();
	}
	
	public void addBean(UserBaccaratReportVO bean) {
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.spend += bean.getSpend();
		this.prize += bean.getPrize();
		this.waterReturn += bean.getWaterReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.cancelOrder += bean.getCancelOrder();
		this.activity += bean.getActivity();
		this.billingOrder += bean.getBillingOrder();
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
	
}