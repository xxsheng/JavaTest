package lottery.domains.content.vo.activity;

import java.util.List;

/**
 * Created by Nick on 2017/3/24.
 */
public class ActivityRedPacketRainConfigVO {
    private int id;
    private String rules;
    private String hours;
    private int durationMinutes;
    private int status;

    private double minCost; // 最小消费
    private List<ActivityRedPacketRainConfigRule> ruleVOs; // 所有规则

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public List<ActivityRedPacketRainConfigRule> getRuleVOs() {
        return ruleVOs;
    }

    public void setRuleVOs(List<ActivityRedPacketRainConfigRule> ruleVOs) {
        this.ruleVOs = ruleVOs;
    }
}
