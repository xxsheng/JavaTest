package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.HistoryUserGameReport;

public class HistoryUserGameReportVO {

	private String name;

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
	private boolean hasMore;

	public HistoryUserGameReportVO() {

	}

	public HistoryUserGameReportVO(String name) {
		this.name = name;
	}
	
	public void addBean(HistoryUserGameReport bean) {
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.waterReturn += bean.getWaterReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.activity += bean.getActivity();
		this.billingOrder += bean.getBillingOrder();
		this.prize += bean.getPrize();
	}
	
	public void addBean(HistoryUserGameReportVO bean) {
		this.transIn += bean.getTransIn();
		this.transOut += bean.getTransOut();
		this.waterReturn += bean.getWaterReturn();
		this.proxyReturn += bean.getProxyReturn();
		this.activity += bean.getActivity();
		this.billingOrder += bean.getBillingOrder();
		this.prize += bean.getPrize();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
}