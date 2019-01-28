package lottery.domains.content.vo.config;

public class VipConfig {
	
	private double[] birthdayGifts;
	private double[] freeChips;
	private double[] upgradeGifts;
	private double[] withdraw;
	
	private int exchangeRate;
	private int maxExchangeMultiple;
	private int maxExchangeTimes;
	
	public VipConfig() {
		
	}

	public double[] getBirthdayGifts() {
		return birthdayGifts;
	}

	public void setBirthdayGifts(double[] birthdayGifts) {
		this.birthdayGifts = birthdayGifts;
	}

	public double[] getFreeChips() {
		return freeChips;
	}

	public void setFreeChips(double[] freeChips) {
		this.freeChips = freeChips;
	}

	public double[] getUpgradeGifts() {
		return upgradeGifts;
	}

	public void setUpgradeGifts(double[] upgradeGifts) {
		this.upgradeGifts = upgradeGifts;
	}

	public double[] getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(double[] withdraw) {
		this.withdraw = withdraw;
	}

	public int getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(int exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public int getMaxExchangeMultiple() {
		return maxExchangeMultiple;
	}

	public void setMaxExchangeMultiple(int maxExchangeMultiple) {
		this.maxExchangeMultiple = maxExchangeMultiple;
	}

	public int getMaxExchangeTimes() {
		return maxExchangeTimes;
	}

	public void setMaxExchangeTimes(int maxExchangeTimes) {
		this.maxExchangeTimes = maxExchangeTimes;
	}

}