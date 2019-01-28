package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * UserLotteryDetailsReport entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_lottery_details_report", catalog = Database.name, 
	uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lottery_id", "rule_id", "time" }))
public class UserLotteryDetailsReport implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // ID
	private int userId; // 用户ID
	private int lotteryId; // 彩票ID
	private int ruleId; // 玩法ID
	private double spend; // 消费，投注即增加
	private double prize; // 奖金，派奖后增加
	private double spendReturn; // 投注返点
	private double proxyReturn; // 代理返点
	private double cancelOrder; // 撤单
	private double billingOrder; // 消费，派奖后增加
	private String time; // 日期,yyyy-MM-dd

	// Constructors

	/** default constructor */
	public UserLotteryDetailsReport() {
	}

	/** full constructor */
	public UserLotteryDetailsReport(int userId, int lotteryId,
			int ruleId, double spend, double prize, double spendReturn,
			double proxyReturn, double cancelOrder, double billingOrder,
			String time) {
		this.userId = userId;
		this.lotteryId = lotteryId;
		this.ruleId = ruleId;
		this.spend = spend;
		this.prize = prize;
		this.spendReturn = spendReturn;
		this.proxyReturn = proxyReturn;
		this.cancelOrder = cancelOrder;
		this.billingOrder = billingOrder;
		this.time = time;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "lottery_id", nullable = false)
	public int getLotteryId() {
		return this.lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}

	@Column(name = "rule_id", nullable = false)
	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	@Column(name = "spend", nullable = false, precision = 16, scale = 5)
	public double getSpend() {
		return this.spend;
	}

	public void setSpend(double spend) {
		this.spend = spend;
	}

	@Column(name = "prize", nullable = false, precision = 16, scale = 5)
	public double getPrize() {
		return this.prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}

	@Column(name = "spend_return", nullable = false, precision = 16, scale = 5)
	public double getSpendReturn() {
		return this.spendReturn;
	}

	public void setSpendReturn(double spendReturn) {
		this.spendReturn = spendReturn;
	}

	@Column(name = "proxy_return", nullable = false, precision = 16, scale = 5)
	public double getProxyReturn() {
		return this.proxyReturn;
	}

	public void setProxyReturn(double proxyReturn) {
		this.proxyReturn = proxyReturn;
	}

	@Column(name = "cancel_order", nullable = false, precision = 16, scale = 5)
	public double getCancelOrder() {
		return this.cancelOrder;
	}

	public void setCancelOrder(double cancelOrder) {
		this.cancelOrder = cancelOrder;
	}

	@Column(name = "billing_order", nullable = false, precision = 16, scale = 5)
	public double getBillingOrder() {
		return this.billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	@Column(name = "time", nullable = false, length = 10)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}