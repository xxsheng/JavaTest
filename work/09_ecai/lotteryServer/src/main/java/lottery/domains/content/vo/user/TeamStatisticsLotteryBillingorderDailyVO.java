package lottery.domains.content.vo.user;

/**
 * Created by Nick on 2017-11-19.
 */
public class TeamStatisticsLotteryBillingorderDailyVO {
    private String date;
    private double lotteryBillingOrder;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLotteryBillingOrder() {
        return lotteryBillingOrder;
    }

    public void setLotteryBillingOrder(double lotteryBillingOrder) {
        this.lotteryBillingOrder = lotteryBillingOrder;
    }
}
