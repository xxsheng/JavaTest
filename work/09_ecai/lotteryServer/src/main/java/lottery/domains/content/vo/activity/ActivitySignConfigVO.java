package lottery.domains.content.vo.activity;

import com.alibaba.fastjson.JSON;

/**
 * Created by Nick on 2017/3/24.
 */
public class ActivitySignConfigVO {
    private int day; // 天数
    private double minCost; // 最低消费要求
    private double rewardPercent; // 奖励百分比
    private double max; // 最大赠送金额
    private int status; // 状态

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public double getRewardPercent() {
        return rewardPercent;
    }

    public void setRewardPercent(double rewardPercent) {
        this.rewardPercent = rewardPercent;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
