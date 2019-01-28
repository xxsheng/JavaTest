package lottery.domains.content.vo.user;

import java.util.List;

/**
 * 团队统计数据
 * Created by Nick on 2017-06-07.
 */
public class TeamStatisticsVO {
    private int totalUser = 0; // 团队人数
    private int onlineUser = 0; // 团队在线人数
    private int totalRegister = 0; // 团队注册人数
    private double totalBalance = 0; // 团队主账余额
    private double lotteryBalance = 0; // 团队彩票余额
    private double totalRecharge = 0; // 团队充值
    private int totalRechargePerson = 0; // 团队充值人数
    private double totalWithdraw = 0; // 团队取款
    private double totalLotteryBillingOrder = 0; // 彩票投注总额
    private double totalLotteryPrize = 0; // 彩票中奖总额
    private double totalLotteryCancelOrder = 0; // 团队撤单总额
    private double totalLotterySpendReturn = 0; // 团队投注返点
    private double totalLotteryProxyReturn = 0; // 团队代理返点
    private double totalLotteryActivity = 0; // 团队活动总额
    private double totalLotteryProfit = 0; // 团队彩票盈亏
    private double totalRechargeFee = 0; // 充值手续费
    private double totalGameBillingOrder = 0; // 游戏投注总额
    private double totalGameProfit = 0; // 游戏团队盈亏
    private double totalGameWaterReturn = 0; // 游戏团队投注返水
    private double totalGameProxyReturn = 0; // 游戏团队代理返水
    private double totalGameActivity = 0; // 游戏团队活动总额
    private double totalProfit = 0; // 综合盈亏(团队彩票盈亏+游戏团队盈亏)

    private List<TeamStatisticsDailyVO> dailyData; // 每日数据

    public TeamStatisticsVO(){}

    public TeamStatisticsVO(UserTeamStatisticsVO utsv, UserMainReportTeamStatisticsVO urtsv, UserLotteryReportTeamStatisticsVO ulrtsv, UserGameReportTeamStatisticsVO ugrtsv) {
        this.totalUser = utsv.getTotalUser();
        this.onlineUser = utsv.getOnlineUser();
        this.totalRegister = utsv.getTotalRegister();
        this.totalBalance = utsv.getTotalBalance();
        this.lotteryBalance = utsv.getLotteryBalance();
        this.totalRecharge = urtsv.getTotalRecharge();
        this.totalRechargePerson = urtsv.getTotalRechargePerson();
        this.totalWithdraw = urtsv.getTotalWithdraw();
        this.totalLotteryBillingOrder = ulrtsv.getTotalLotteryBillingOrder();
        this.totalLotteryPrize = ulrtsv.getTotalLotteryPrize();
        this.totalLotteryCancelOrder = ulrtsv.getTotalLotteryCancelOrder();
        this.totalLotterySpendReturn = ulrtsv.getTotalLotterySpendReturn();
        this.totalLotteryProxyReturn = ulrtsv.getTotalLotteryProxyReturn();
        this.totalLotteryActivity = ulrtsv.getTotalLotteryActivity();
        this.totalLotteryProfit = ulrtsv.getTotalLotteryProfit();
        this.totalRechargeFee = ulrtsv.getTotalRechargeFee();

        this.totalGameBillingOrder = ugrtsv.getTotalGameBillingOrder();
        this.totalGameProfit = ugrtsv.getTotalGameProfit();
        this.totalGameWaterReturn = ugrtsv.getTotalGameWaterReturn();
        this.totalGameProxyReturn = ugrtsv.getTotalGameProxyReturn();
        this.totalGameActivity = ugrtsv.getTotalGameActivity();

        this.totalProfit = this.totalLotteryProfit + this.totalGameProfit;
    }

    public int getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(int totalUser) {
        this.totalUser = totalUser;
    }

    public int getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(int onlineUser) {
        this.onlineUser = onlineUser;
    }

    public int getTotalRegister() {
        return totalRegister;
    }

    public void setTotalRegister(int totalRegister) {
        this.totalRegister = totalRegister;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public double getLotteryBalance() {
        return lotteryBalance;
    }

    public void setLotteryBalance(double lotteryBalance) {
        this.lotteryBalance = lotteryBalance;
    }

    public double getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(double totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public int getTotalRechargePerson() {
        return totalRechargePerson;
    }

    public void setTotalRechargePerson(int totalRechargePerson) {
        this.totalRechargePerson = totalRechargePerson;
    }

    public double getTotalWithdraw() {
        return totalWithdraw;
    }

    public void setTotalWithdraw(double totalWithdraw) {
        this.totalWithdraw = totalWithdraw;
    }

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

    public double getTotalGameBillingOrder() {
        return totalGameBillingOrder;
    }

    public void setTotalGameBillingOrder(double totalGameBillingOrder) {
        this.totalGameBillingOrder = totalGameBillingOrder;
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

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public List<TeamStatisticsDailyVO> getDailyData() {
        return dailyData;
    }

    public void setDailyData(List<TeamStatisticsDailyVO> dailyData) {
        this.dailyData = dailyData;
    }
}
