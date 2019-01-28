package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_bets", catalog = Database.name)
public class UserBetsNoCode implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String billno;
	private int userId;
	private int type;
	private int lotteryId;
	private String expect;
	private int ruleId;
	private int nums;
	private String model;
	private int multiple;
	private int code;
	private double point;
	private double money;
	private String time;
	private String stopTime;
	private String openTime;
	private int status;
	private String openCode;
	private Double prizeMoney;
	private String prizeTime;
	private String chaseBillno;
	private Integer chaseStop;
	private String planBillno;
	private Double rewardMoney;
	private int isAll;
	private String ip; // 投注IP
	private int locked;

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

	@Column(name = "type", nullable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
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

	@Column(name = "code", nullable = false)
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Column(name = "point", nullable = false, precision = 11, scale = 1)
	public double getPoint() {
		return this.point;
	}

	public void setPoint(double point) {
		this.point = point;
	}

	@Column(name = "money", nullable = false, precision = 16, scale = 5)
	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "stop_time", nullable = false, length = 19)
	public String getStopTime() {
		return this.stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	@Column(name = "open_time", nullable = false, length = 19)
	public String getOpenTime() {
		return this.openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "open_code", length = 128)
	public String getOpenCode() {
		return this.openCode;
	}

	public void setOpenCode(String openCode) {
		this.openCode = openCode;
	}

	@Column(name = "prize_money", precision = 16, scale = 5)
	public Double getPrizeMoney() {
		return this.prizeMoney;
	}

	public void setPrizeMoney(Double prizeMoney) {
		this.prizeMoney = prizeMoney;
	}

	@Column(name = "prize_time", length = 19)
	public String getPrizeTime() {
		return this.prizeTime;
	}

	public void setPrizeTime(String prizeTime) {
		this.prizeTime = prizeTime;
	}

	@Column(name = "chase_billno", length = 64)
	public String getChaseBillno() {
		return this.chaseBillno;
	}

	public void setChaseBillno(String chaseBillno) {
		this.chaseBillno = chaseBillno;
	}

	@Column(name = "chase_stop")
	public Integer getChaseStop() {
		return this.chaseStop;
	}

	public void setChaseStop(Integer chaseStop) {
		this.chaseStop = chaseStop;
	}

	@Column(name = "plan_billno", length = 64)
	public String getPlanBillno() {
		return this.planBillno;
	}

	public void setPlanBillno(String planBillno) {
		this.planBillno = planBillno;
	}

	@Column(name = "reward_money", precision = 16, scale = 5)
	public Double getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(Double rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	@Column(name = "is_all", nullable = false)
	public int getIsAll() {
		return isAll;
	}

	public void setIsAll(int isAll) {
		this.isAll = isAll;
	}

	@Column(name = "ip", length = 20)
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	@Column(name = "locked", nullable = false)
	public int getLocked() {
		return locked;
	}

	public void setLocked(int locked) {
		this.locked = locked;
	}

	/**
	 * 转换成正常的bean
	 */
	public UserBets formatBean() {
		UserBets entity = new UserBets();
		entity.setId(this.id);
		entity.setBillno(this.billno);
		entity.setUserId(this.userId);
		entity.setType(this.type);
		entity.setLotteryId(this.lotteryId);
		entity.setExpect(this.expect);
		entity.setRuleId(this.ruleId);
		entity.setNums(this.nums);
		entity.setModel(this.model);
		entity.setMultiple(this.multiple);
		entity.setCode(this.code);
		entity.setPoint(this.point);
		entity.setMoney(this.money);
		entity.setTime(this.time);
		entity.setStopTime(this.stopTime);
		entity.setOpenTime(this.openTime);
		entity.setStatus(this.status);
		entity.setOpenCode(this.openCode);
		entity.setPrizeMoney(this.prizeMoney);
		entity.setPrizeTime(this.prizeTime);
		entity.setChaseBillno(this.chaseBillno);
		entity.setChaseStop(this.chaseStop);
		entity.setPlanBillno(this.planBillno);
		entity.setRewardMoney(this.rewardMoney);
		entity.setIp(this.ip);
		entity.setLocked(this.locked);
		return entity;
	}

}