package lottery.domains.content.vo.activity;

import java.util.List;

/**
 * Created by Nick on 2017/3/18.
 */
public class ActivityRedPacketRainConfigRule {
    private double minCost;
    private double maxCost;
    private List<ActivityRedPacketRainConfigProbability> probabilities;

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

    public List<ActivityRedPacketRainConfigProbability> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<ActivityRedPacketRainConfigProbability> probabilities) {
        this.probabilities = probabilities;
    }
}
