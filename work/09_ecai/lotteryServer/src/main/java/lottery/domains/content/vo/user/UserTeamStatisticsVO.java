package lottery.domains.content.vo.user;

/**
 * Created by Nick on 2017-06-07.
 */
public class UserTeamStatisticsVO {
    private int totalUser = 0; // 团队人数
    private int onlineUser = 0; // 团队在线人数
    private int totalRegister = 0; // 团队注册人数
    private double totalBalance = 0; // 团队主账余额
    private double lotteryBalance = 0; // 团队彩票余额

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
}
