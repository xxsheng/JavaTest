package lottery.domains.content.vo.bets;

import java.util.List;

public class UserBetsPlanPrepareVO {

	private int id;
	private String billno;
	private String lottery;
	private String expect;
	private double money;
	private double maxPrize;

	private int maxRate;
	private List<String> titleList;

	public UserBetsPlanPrepareVO() {

	}

	public UserBetsPlanPrepareVO(int id, String billno, String lottery, String expect, double money, double maxPrize,
			int maxRate, List<String> titleList) {
		this.id = id;
		this.billno = billno;
		this.lottery = lottery;
		this.expect = expect;
		this.money = money;
		this.maxPrize = maxPrize;
		this.maxRate = maxRate;
		this.titleList = titleList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
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

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getMaxPrize() {
		return maxPrize;
	}

	public void setMaxPrize(double maxPrize) {
		this.maxPrize = maxPrize;
	}

	public int getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(int maxRate) {
		this.maxRate = maxRate;
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}

}