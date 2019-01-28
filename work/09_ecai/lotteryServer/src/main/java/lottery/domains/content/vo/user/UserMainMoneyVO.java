package lottery.domains.content.vo.user;

import lottery.domains.content.entity.User;

public class UserMainMoneyVO {

	private String username;
	private double totalMoney;
	private double lotteryMoney;
	private double baccaratMoney;
	private double freezeMoney;
	private double dividendMoney;

	public UserMainMoneyVO(User bean) {
		this.username = bean.getUsername();
		this.totalMoney = bean.getTotalMoney();
		this.lotteryMoney = bean.getLotteryMoney();
		this.baccaratMoney = bean.getBaccaratMoney();
		this.freezeMoney = bean.getFreezeMoney();
		this.dividendMoney = bean.getDividendMoney();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public double getLotteryMoney() {
		return lotteryMoney;
	}

	public void setLotteryMoney(double lotteryMoney) {
		this.lotteryMoney = lotteryMoney;
	}

	public double getBaccaratMoney() {
		return baccaratMoney;
	}

	public void setBaccaratMoney(double baccaratMoney) {
		this.baccaratMoney = baccaratMoney;
	}

	public double getFreezeMoney() {
		return freezeMoney;
	}

	public void setFreezeMoney(double freezeMoney) {
		this.freezeMoney = freezeMoney;
	}

	public double getDividendMoney() {
		return dividendMoney;
	}

	public void setDividendMoney(double dividendMoney) {
		this.dividendMoney = dividendMoney;
	}

}