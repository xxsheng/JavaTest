package lottery.domains.content.vo.activity;

import java.util.List;

/**
 * Created by Nick on 2017/11/27.
 */
public class ActivityRebateWheelConfigVO {
    private int id;
    private int status;
    private double minCost; // 最小消费
    private List<ActivityRebateWheelRule> rules; // 所有规则

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public List<ActivityRebateWheelRule> getRules() {
        return rules;
    }

    public void setRules(List<ActivityRebateWheelRule> rules) {
        this.rules = rules;
    }
}
