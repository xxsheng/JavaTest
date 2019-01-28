package lottery.domains.content.vo.user;

/**
 * 用户团队游戏报表统计
 * Created by Nick on 2017-06-07.
 */
public class UserGameReportTeamStatisticsVO {
    private double totalGameBillingOrder = 0; // 游戏投注总额
    private double totalGamePrize = 0; // 游戏中奖总额
    private double totalGameProfit = 0; // 游戏团队盈亏
    private double totalGameWaterReturn = 0; // 游戏团队投注返水
    private double totalGameProxyReturn = 0; // 游戏团队代理返水
    private double totalGameActivity = 0; // 游戏团队活动总额

    public double getTotalGameBillingOrder() {
        return totalGameBillingOrder;
    }

    public void setTotalGameBillingOrder(double totalGameBillingOrder) {
        this.totalGameBillingOrder = totalGameBillingOrder;
    }

    public double getTotalGamePrize() {
        return totalGamePrize;
    }

    public void setTotalGamePrize(double totalGamePrize) {
        this.totalGamePrize = totalGamePrize;
    }

    public double getTotalGameProfit() {
        return totalGameProfit;
    }

    public void setTotalGameProfit(double totalGameProfit) {
        this.totalGameProfit = totalGameProfit;
    }

    public double getTotalGameWaterReturn() {
        return totalGameWaterReturn;
    }

    public void setTotalGameWaterReturn(double totalGameWaterReturn) {
        this.totalGameWaterReturn = totalGameWaterReturn;
    }

    public double getTotalGameProxyReturn() {
        return totalGameProxyReturn;
    }

    public void setTotalGameProxyReturn(double totalGameProxyReturn) {
        this.totalGameProxyReturn = totalGameProxyReturn;
    }

    public double getTotalGameActivity() {
        return totalGameActivity;
    }

    public void setTotalGameActivity(double totalGameActivity) {
        this.totalGameActivity = totalGameActivity;
    }
}
