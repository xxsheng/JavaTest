package lottery.domains.content.vo.config;

/**
 * Created by Nick on 2017-06-30.
 */
public class SettleConfig {
    private double minBillingOrder; // 有效会员最低消费

    public double getMinBillingOrder() {
        return minBillingOrder;
    }

    public void setMinBillingOrder(double minBillingOrder) {
        this.minBillingOrder = minBillingOrder;
    }
}
