package lottery.domains.content.vo.chart;

/**
 * Created by Nick on 2017-04-24.
 */
public class RechargeWithdrawTotal {
    private int totalRechargeCount; // 总充值订单数
    private double totalRechargeMoney; // 总充值金额
    private double totalReceiveFeeMoney; // 总充值第三方手续费
    private int totalWithdrawCount; // 总提款订单数
    private double totalWithdrawMoney; // 总提款金额
    private double totalActualReceiveMoney; // 实际收款
    private double totalRechargeWithdrawDiff; // 充提差

    public RechargeWithdrawTotal(int totalRechargeCount, double totalRechargeMoney, double totalReceiveFeeMoney, int totalWithdrawCount, double totalWithdrawMoney, double totalActualReceiveMoney, double totalRechargeWithdrawDiff) {
        this.totalRechargeCount = totalRechargeCount;
        this.totalRechargeMoney = totalRechargeMoney;
        this.totalReceiveFeeMoney = totalReceiveFeeMoney;
        this.totalWithdrawCount = totalWithdrawCount;
        this.totalWithdrawMoney = totalWithdrawMoney;
        this.totalActualReceiveMoney = totalActualReceiveMoney;
        this.totalRechargeWithdrawDiff = totalRechargeWithdrawDiff;
    }

    public int getTotalRechargeCount() {
        return totalRechargeCount;
    }

    public void setTotalRechargeCount(int totalRechargeCount) {
        this.totalRechargeCount = totalRechargeCount;
    }

    public double getTotalRechargeMoney() {
        return totalRechargeMoney;
    }

    public void setTotalRechargeMoney(double totalRechargeMoney) {
        this.totalRechargeMoney = totalRechargeMoney;
    }

    public double getTotalReceiveFeeMoney() {
        return totalReceiveFeeMoney;
    }

    public void setTotalReceiveFeeMoney(double totalReceiveFeeMoney) {
        this.totalReceiveFeeMoney = totalReceiveFeeMoney;
    }

    public int getTotalWithdrawCount() {
        return totalWithdrawCount;
    }

    public void setTotalWithdrawCount(int totalWithdrawCount) {
        this.totalWithdrawCount = totalWithdrawCount;
    }

    public double getTotalWithdrawMoney() {
        return totalWithdrawMoney;
    }

    public void setTotalWithdrawMoney(double totalWithdrawMoney) {
        this.totalWithdrawMoney = totalWithdrawMoney;
    }

    public double getTotalActualReceiveMoney() {
        return totalActualReceiveMoney;
    }

    public void setTotalActualReceiveMoney(double totalActualReceiveMoney) {
        this.totalActualReceiveMoney = totalActualReceiveMoney;
    }

    public double getTotalRechargeWithdrawDiff() {
        return totalRechargeWithdrawDiff;
    }

    public void setTotalRechargeWithdrawDiff(double totalRechargeWithdrawDiff) {
        this.totalRechargeWithdrawDiff = totalRechargeWithdrawDiff;
    }
}
