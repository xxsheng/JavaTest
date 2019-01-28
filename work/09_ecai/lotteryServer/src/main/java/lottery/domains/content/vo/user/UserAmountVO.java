package lottery.domains.content.vo.user;

public class UserAmountVO {

	private double amount3;
	private double amount7;

	public UserAmountVO() {

	}

	public UserAmountVO(double amount3, double amount7) {
		this.amount3 = amount3;
		this.amount7 = amount7;
	}

	public double getAmount3() {
		return amount3;
	}

	public void setAmount3(double amount3) {
		this.amount3 = amount3;
	}

	public double getAmount7() {
		return amount7;
	}

	public void setAmount7(double amount7) {
		this.amount7 = amount7;
	}

}