package lottery.domains.content.entity.activity;

import java.util.List;

/**
 * Created by Nick on 2017/3/24.
 */
public class ActivityFirstRechargeConfigVO {
    private int id;
    private String rules;
    private int status;

    private double minRecharge; // 最小消费
    private List<ActivityFirstRechargeConfigRule> ruleVOs; // 所有规则


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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getMinRecharge() {
        return minRecharge;
    }

    public void setMinRecharge(double minRecharge) {
        this.minRecharge = minRecharge;
    }

    public List<ActivityFirstRechargeConfigRule> getRuleVOs() {
        return ruleVOs;
    }

    public void setRuleVOs(List<ActivityFirstRechargeConfigRule> ruleVOs) {
        this.ruleVOs = ruleVOs;
    }
}
