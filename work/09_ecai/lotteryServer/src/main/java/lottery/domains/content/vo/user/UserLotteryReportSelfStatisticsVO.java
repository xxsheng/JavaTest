package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.entity.UserMainReport;

/**
 * 用户盈亏报表
 * Created by Nick on 2017-06-07.
 */
public class UserLotteryReportSelfStatisticsVO {
    private int userId;
    private String time;
    private double recharge;
    private double withdraw;
    private double billingOrder;
    private double prize;
    private double spendReturn;
    private double proxyReturn;
    private double activity;
    private double rechargeFee; // 充值手续费，平台这边的,主报表是实际的

    public UserLotteryReportSelfStatisticsVO(UserLotteryReport uLotteryReport, UserMainReport uMainReport) {
        this.setUserId(uLotteryReport.getUserId());
        this.setTime(uLotteryReport.getTime());
        this.setRecharge(uMainReport.getRecharge());
        this.setWithdraw(uMainReport.getWithdrawals());
        this.setBillingOrder(uLotteryReport.getBillingOrder());
        this.setPrize(uLotteryReport.getPrize());
        this.setSpendReturn(uLotteryReport.getSpendReturn());
        this.setProxyReturn(uLotteryReport.getProxyReturn());
        this.setActivity(uLotteryReport.getActivity());
        this.setRechargeFee(uLotteryReport.getRechargeFee());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public double getBillingOrder() {
        return billingOrder;
    }

    public void setBillingOrder(double billingOrder) {
        this.billingOrder = billingOrder;
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

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public double getRechargeFee() {
        return rechargeFee;
    }

    public void setRechargeFee(double rechargeFee) {
        this.rechargeFee = rechargeFee;
    }
}
