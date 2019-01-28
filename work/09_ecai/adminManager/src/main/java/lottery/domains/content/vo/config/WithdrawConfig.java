package lottery.domains.content.vo.config;

public class WithdrawConfig {

	private int status;
	private double minAmount;
	private double maxAmount;
	private String serviceTime;
	private String serviceMsg;
	private int maxTimes;
	private int freeTimes;
	private double fee;
	private double maxFee;
	private int registerHours;
	private double systemConsumptionPercent; // 系统充值消费比例
	private double transferConsumptionPercent; //上下级转账消费比例
	private String apiPayNotifyUrl; // API代付异步通知地址
	
	public WithdrawConfig() {

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}

	public String getServiceMsg() {
		return serviceMsg;
	}

	public void setServiceMsg(String serviceMsg) {
		this.serviceMsg = serviceMsg;
	}

	public int getMaxTimes() {
		return maxTimes;
	}

	public void setMaxTimes(int maxTimes) {
		this.maxTimes = maxTimes;
	}

	public int getFreeTimes() {
		return freeTimes;
	}

	public void setFreeTimes(int freeTimes) {
		this.freeTimes = freeTimes;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

	public double getMaxFee() {
		return maxFee;
	}

	public void setMaxFee(double maxFee) {
		this.maxFee = maxFee;
	}

	public int getRegisterHours() {
		return registerHours;
	}

	public void setRegisterHours(int registerHours) {
		this.registerHours = registerHours;
	}

	public double getSystemConsumptionPercent() {
		return systemConsumptionPercent;
	}

	public void setSystemConsumptionPercent(double systemConsumptionPercent) {
		this.systemConsumptionPercent = systemConsumptionPercent;
	}

	public double getTransferConsumptionPercent() {
		return transferConsumptionPercent;
	}

	public void setTransferConsumptionPercent(double transferConsumptionPercent) {
		this.transferConsumptionPercent = transferConsumptionPercent;
	}

	public String getApiPayNotifyUrl() {
		return apiPayNotifyUrl;
	}

	public void setApiPayNotifyUrl(String apiPayNotifyUrl) {
		this.apiPayNotifyUrl = apiPayNotifyUrl;
	}
}