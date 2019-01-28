package lottery.domains.content.vo.config;

/**
 * 自主彩配置
 * Created by Nick on 2016/11/24.
 */
public class SelfLotteryConfig {
    private boolean control; // 自主彩是否控制开奖
    private double probability; // 自主彩杀率

    public boolean isControl() {
        return control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
