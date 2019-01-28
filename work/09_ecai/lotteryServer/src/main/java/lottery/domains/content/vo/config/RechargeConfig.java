package lottery.domains.content.vo.config;

public class RechargeConfig {

	private boolean enable; // 是否开启
	private String serviceTime;
	private double feePercent;
	
	public RechargeConfig() {
		
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}

	public double getFeePercent() {
		return feePercent;
	}

	public void setFeePercent(double feePercent) {
		this.feePercent = feePercent;
	}
}