package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * UserBetsPlan entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_bets_plan", catalog = Database.name)
public class UserBetsPlan implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String billno;
	private int userId;
	private int orderId;
	private int lotteryId;
	private String expect;
	private int ruleId;
	private int nums;
	private String model;
	private int multiple;
	private double money;
	private double maxPrize;
	private String title;
	private double rate;
	private int followCount;
	private double rewardMoney;
	private double prizeMoney;
	private String time;
	private int status;
	private int part;

	// Constructors

	/** default constructor */
	public UserBetsPlan() {
	}

	/** full constructor */
	public UserBetsPlan(String billno, int userId, int orderId, int lotteryId,
			String expect, int ruleId, int nums, String model, int multiple,
			double money, double maxPrize, String title, double rate,
			int followCount, double rewardMoney, String time, int status,
			double prizeMoney, int part) {
		this.billno = billno;
		this.userId = userId;
		this.orderId = orderId;
		this.lotteryId = lotteryId;
		this.expect = expect;
		this.ruleId = ruleId;
		this.nums = nums;
		this.model = model;
		this.multiple = multiple;
		this.money = money;
		this.maxPrize = maxPrize;
		this.title = title;
		this.rate = rate;
		this.followCount = followCount;
		this.rewardMoney = rewardMoney;
		this.time = time;
		this.status = status;
		this.prizeMoney = prizeMoney;
		this.part = part;
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

	@Column(name = "billno", nullable = false, length = 32)
	public String getBillno() {
		return this.billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "order_id", nullable = false)
	public int getOrderId() {
		return this.orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	@Column(name = "lottery_id", nullable = false)
	public int getLotteryId() {
		return this.lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}

	@Column(name = "expect", nullable = false, length = 32)
	public String getExpect() {
		return this.expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	@Column(name = "rule_id", nullable = false)
	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	@Column(name = "nums", nullable = false)
	public int getNums() {
		return this.nums;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

	@Column(name = "model", nullable = false, length = 16)
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name = "multiple", nullable = false)
	public int getMultiple() {
		return this.multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	@Column(name = "money", nullable = false, precision = 16, scale = 5)
	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "max_prize", nullable = false, precision = 16, scale = 5)
	public double getMaxPrize() {
		return maxPrize;
	}

	public void setMaxPrize(double maxPrize) {
		this.maxPrize = maxPrize;
	}

	@Column(name = "title", nullable = false, length = 64)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "rate", nullable = false, precision = 11)
	public double getRate() {
		return this.rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	@Column(name = "follow_count", nullable = false)
	public int getFollowCount() {
		return followCount;
	}

	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}

	@Column(name = "reward_money", nullable = false, precision = 16, scale = 5)
	public double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	@Column(name = "prize_money", nullable = false, precision = 16, scale = 5)
	public double getPrizeMoney() {
		return prizeMoney;
	}

	public void setPrizeMoney(double prizeMoney) {
		this.prizeMoney = prizeMoney;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "part", nullable = false)
	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}

}