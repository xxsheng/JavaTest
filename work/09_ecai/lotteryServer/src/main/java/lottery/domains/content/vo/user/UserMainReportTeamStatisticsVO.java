package lottery.domains.content.vo.user;

/**
 * 用户团队充提统计
 * Created by Nick on 2017-06-07.
 */
public class UserMainReportTeamStatisticsVO {
    private double totalRecharge = 0; // 团队充值
    private int totalRechargePerson = 0; // 团队充值人数
    private double totalWithdraw = 0; // 团队取款

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
}
