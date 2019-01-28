package lottery.domains.content.vo.activity;

/**
 * Created by Nick on 2017/11/27.
 */
public class ActivityRebateWheelRule {
    private double minCost;
    private double maxCost;
    private double[] amounts;

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public double[] getAmounts() {
        return amounts;
    }

    public void setAmounts(double[] amounts) {
        this.amounts = amounts;
    }
}
