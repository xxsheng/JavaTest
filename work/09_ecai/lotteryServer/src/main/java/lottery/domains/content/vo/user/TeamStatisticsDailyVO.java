package lottery.domains.content.vo.user;

/**
 * Created by Nick on 2017-11-19.
 */
public class TeamStatisticsDailyVO {
    private String date; // 日期
    private double lotteryBillingOrder; // 彩票投注总额
    private double lotteryPrize; // 彩票中奖总额
    private double lotterySpendReturn; // 团队投注返点
    private double lotteryProxyReturn; // 团队代理返点
    private double register; // 团队注册人数
    private double recharge; // 团队充值
    private double withdraw; // 团队取款

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getRegister() {
        return register;
    }

    public void setRegister(double register) {
        this.register = register;
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

    public double getLotteryBillingOrder() {
        return lotteryBillingOrder;
    }

    public void setLotteryBillingOrder(double lotteryBillingOrder) {
        this.lotteryBillingOrder = lotteryBillingOrder;
    }

    public double getLotteryPrize() {
        return lotteryPrize;
    }

    public void setLotteryPrize(double lotteryPrize) {
        this.lotteryPrize = lotteryPrize;
    }

    public double getLotterySpendReturn() {
        return lotterySpendReturn;
    }

    public void setLotterySpendReturn(double lotterySpendReturn) {
        this.lotterySpendReturn = lotterySpendReturn;
    }

    public double getLotteryProxyReturn() {
        return lotteryProxyReturn;
    }

    public void setLotteryProxyReturn(double lotteryProxyReturn) {
        this.lotteryProxyReturn = lotteryProxyReturn;
    }
}
