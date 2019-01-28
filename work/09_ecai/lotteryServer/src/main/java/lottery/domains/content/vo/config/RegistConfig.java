package lottery.domains.content.vo.config;

/**
 * Created by Nick on 2017-07-06.
 */
public class RegistConfig {
    private int defaultCode;
    private boolean enable;
    private int demoCount;
    private String demoPassword;
    private double demoLotteryMoney;
    

    public int getDemoCount() {
		return demoCount;
	}

	public void setDemoCount(int demoCount) {
		this.demoCount = demoCount;
	}

	public String getDemoPassword() {
		return demoPassword;
	}

	public void setDemoPassword(String demoPassword) {
		this.demoPassword = demoPassword;
	}

	public double getDemoLotteryMoney() {
		return demoLotteryMoney;
	}

	public void setDemoLotteryMoney(double demoLotteryMoney) {
		this.demoLotteryMoney = demoLotteryMoney;
	}

	public int getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(int defaultCode) {
        this.defaultCode = defaultCode;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
