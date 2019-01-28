package lottery.domains.content.entity.activity;

/**
 * Created by Nick on 2017/3/18.
 */
public class ActivityFirstRechargeConfigRule {
    private double minRecharge;
    private double maxRecharge;
    private double amount;

    public double getMinRecharge() {
        return minRecharge;
    }

    public void setMinRecharge(double minRecharge) {
        this.minRecharge = minRecharge;
    }

    public double getMaxRecharge() {
        return maxRecharge;
    }

    public void setMaxRecharge(double maxRecharge) {
        this.maxRecharge = maxRecharge;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
