package lottery.domains.content.vo.user;

/**
 * 用户团队彩票报表统计
 * Created by Nick on 2017-06-07.
 */
public class UserLotteryReportTeamStatisticsVO {
    private double totalLotteryBillingOrder = 0; // 彩票投注总额
    private double totalLotteryPrize = 0; // 彩票中奖总额
    private double totalLotteryCancelOrder = 0; // 团队撤单总额
    private double totalLotterySpendReturn = 0; // 团队投注返点
    private double totalLotteryProxyReturn = 0; // 团队代理返点
    private double totalLotteryActivity = 0; // 团队活动总额
    private double totalLotteryProfit = 0; // 团队彩票盈亏
    private double totalRechargeFee = 0; // 充值手续费

    public double getTotalLotteryBillingOrder() {
        return totalLotteryBillingOrder;
    }

    public void setTotalLotteryBillingOrder(double totalLotteryBillingOrder) {
        this.totalLotteryBillingOrder = totalLotteryBillingOrder;
    }

    public double getTotalLotteryPrize() {
        return totalLotteryPrize;
    }

    public void setTotalLotteryPrize(double totalLotteryPrize) {
        this.totalLotteryPrize = totalLotteryPrize;
    }

    public double getTotalLotteryCancelOrder() {
        return totalLotteryCancelOrder;
    }

    public void setTotalLotteryCancelOrder(double totalLotteryCancelOrder) {
        this.totalLotteryCancelOrder = totalLotteryCancelOrder;
    }

    public double getTotalLotterySpendReturn() {
        return totalLotterySpendReturn;
    }

    public void setTotalLotterySpendReturn(double totalLotterySpendReturn) {
        this.totalLotterySpendReturn = totalLotterySpendReturn;
    }

    public double getTotalLotteryProxyReturn() {
        return totalLotteryProxyReturn;
    }

    public void setTotalLotteryProxyReturn(double totalLotteryProxyReturn) {
        this.totalLotteryProxyReturn = totalLotteryProxyReturn;
    }

    public double getTotalLotteryActivity() {
        return totalLotteryActivity;
    }

    public void setTotalLotteryActivity(double totalLotteryActivity) {
        this.totalLotteryActivity = totalLotteryActivity;
    }

    public double getTotalLotteryProfit() {
        return totalLotteryProfit;
    }

    public void setTotalLotteryProfit(double totalLotteryProfit) {
        this.totalLotteryProfit = totalLotteryProfit;
    }

    public double getTotalRechargeFee() {
        return totalRechargeFee;
    }

    public void setTotalRechargeFee(double totalRechargeFee) {
        this.totalRechargeFee = totalRechargeFee;
    }
}
