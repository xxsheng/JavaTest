package lottery.domains.content.vo.bets;

public class UserBetsProfitVO {

	private String lottery;
	private String expect;
	private int totalCount;
	private double totalMoney;
	private double totalReturn;
	private double totalPrize;
	
	public UserBetsProfitVO() {
		
	}

	public String getLottery() {
		return lottery;
	}

	public void setLottery(String lottery) {
		this.lottery = lottery;
	}

	public String getExpect() {
		return expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public double getTotalReturn() {
		return totalReturn;
	}

	public void setTotalReturn(double totalReturn) {
		this.totalReturn = totalReturn;
	}

	public double getTotalPrize() {
		return totalPrize;
	}

	public void setTotalPrize(double totalPrize) {
		this.totalPrize = totalPrize;
	}

}