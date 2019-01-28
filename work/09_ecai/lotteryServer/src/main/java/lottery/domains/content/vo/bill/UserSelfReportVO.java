package lottery.domains.content.vo.bill;

public class UserSelfReportVO {
	private double spend; // 花费(不是消费，值只供参考，无实际意义)
	private double prize; // 奖金
	private double spendReturn; // 投注返点
	private double proxyReturn; // 代理返点
	private double cancelOrder; // 撤单
	private double activity; // 活动(优惠)
	private double billingOrder; // 消费
	private double rechargeFee; // 代理承担充值手续费，彩票报表的(优惠)
	private double recharge; // 充值
	private double withdraw; // 取款

	public UserSelfReportVO(UserLotteryReportVO uLotteryReportVO, UserMainReportVO uMainReportVO) {
		if (uLotteryReportVO != null) {
			this.spend = uLotteryReportVO.getSpend();
			this.prize = uLotteryReportVO.getPrize();
			this.spendReturn = uLotteryReportVO.getSpendReturn();
			this.proxyReturn = uLotteryReportVO.getProxyReturn();
			this.cancelOrder = uLotteryReportVO.getCancelOrder();
			this.activity = uLotteryReportVO.getActivity();
			this.billingOrder = uLotteryReportVO.getBillingOrder();
			this.rechargeFee = uLotteryReportVO.getRechargeFee();
		}
		if (uMainReportVO != null) {
			this.recharge = uMainReportVO.getRecharge();
			this.withdraw = uMainReportVO.getWithdrawals();
		}
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

	public double getRechargeFee() {
		return rechargeFee;
	}

	public void setRechargeFee(double rechargeFee) {
		this.rechargeFee = rechargeFee;
	}

	public double getRecharge() {
		return recharge;
	}

	public void setRecharge(double recharge) {
		this.recharge = recharge;
	}

	public double getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(double withdraw) {
		this.withdraw = withdraw;
	}
}