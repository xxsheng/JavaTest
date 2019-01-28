package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.UserLotteryDetailsReport;

public class HistoryUserLotteryDetailsReportVO {

	private String name;
	private String key;
	
	private double spend;
	private double prize;
	private double spendReturn;
	private double proxyReturn;
	private double cancelOrder;
	private double billingOrder;
	
	public HistoryUserLotteryDetailsReportVO() {
		
	}
	
	public HistoryUserLotteryDetailsReportVO(String name, String key) {
		this.name = name;
		this.key = key;
	}
	
	public void addBean(UserLotteryDetailsReport bean) {
		this.spend += bean.getSpend();
		this.prize += bean.getPrize();
		this.spendReturn += bean.getSpendReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.cancelOrder += bean.getCancelOrder();
		this.billingOrder += bean.getBillingOrder();
	}
	
	public void addBean(HistoryUserLotteryDetailsReportVO bean) {
		this.spend += bean.getSpend();
		this.prize += bean.getPrize();
		this.spendReturn += bean.getSpendReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.cancelOrder += bean.getCancelOrder();
		this.billingOrder += bean.getBillingOrder();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

}