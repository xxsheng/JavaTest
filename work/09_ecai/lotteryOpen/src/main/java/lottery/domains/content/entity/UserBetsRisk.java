package lottery.domains.content.entity;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * UserBets entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_bets_risk", uniqueConstraints = @UniqueConstraint(columnNames = {
		"billno", "user_id" }))
public class UserBetsRisk implements java.io.Serializable, Cloneable {

	// Fields

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int id; // ID
	private String billno; // 账单号
	private int userId; // 用户ID
	private int type; // 投注类型；0：普通订单；1：追号订单；2：计划订单；
	private int lotteryId; // 彩票ID
	private String expect; // 投注期号
	private int ruleId; // 玩法ID
	private String codes; // 投注内容
	private int nums; // 注数
	private String model; // 资金模式；yuan,jiao,fen,li,1yuan,1jiao,1fen,1li
	private int multiple; // 倍数
	private int code; // 投注等级；如1956
	private double point; // 选择返点，如7.8
	private double money; // 投注金额
	private String time; // 投注时间；yyyy-MM-dd HH:mm:ss
	private String stopTime; // 截止时间；yyyy-MM-dd HH:mm:ss
	private String openTime; // 开奖时间，开奖后有；yyyy-MM-dd HH:mm:ss
	private int status; // 状态；0：未开奖；1：未中奖；2：已中奖；3：已撤单；4：风控中
	private String openCode; // 开奖号码
	private Double prizeMoney; // 中奖金额
	private String prizeTime; // 开奖时间；yyyy-MM-dd HH:mm:ss
	private String chaseBillno; // 追号单
	private Integer chaseStop; // 中奖后追停；1：是；0：否
	private String planBillno; // 计划单号；暂未使用
	private Double rewardMoney; // 奖励金额；暂未使用
	private int compressed; // 号码是否已经压缩，0:未压缩；1:已压缩
	private String ip; // 投注IP
	private int winNum; // 中奖注数

	@Override
	public UserBetsRisk clone() {
		try {
			return (UserBetsRisk) super.clone();
		} catch (Exception e) {}
		return null;
	}

	public UserBetsRisk() {
	}

	public UserBetsRisk(UserBets userBets) {
		this.billno = userBets.getBillno();
		this.userId = userBets.getUserId();
		this.type = userBets.getType();
		this.lotteryId = userBets.getLotteryId();
		this.expect = userBets.getExpect();
		this.ruleId = userBets.getRuleId();
		this.codes = userBets.getCodes();
		this.nums = userBets.getNums();
		this.model = userBets.getModel();
		this.multiple = userBets.getMultiple();
		this.code = userBets.getCode();
		this.point = userBets.getPoint();
		this.money = userBets.getMoney();
		this.time = userBets.getTime();
		this.stopTime = userBets.getStopTime();
		this.openTime = userBets.getOpenTime();
		this.status = userBets.getStatus();
		this.openCode = userBets.getOpenCode();
		this.prizeMoney = userBets.getPrizeMoney();
		this.prizeTime = userBets.getPrizeTime();
		this.chaseBillno = userBets.getChaseBillno();
		this.chaseStop = userBets.getChaseStop();
		this.planBillno = userBets.getPlanBillno();
		this.rewardMoney = userBets.getRewardMoney();
		this.compressed = userBets.getCompressed();
//		this.ip = userBets.getIp();
//		this.winNum = userBets.getWinNum();
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

	@Column(name = "billno", nullable = false, length = 32, updatable = false)
	public String getBillno() {
		return this.billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	@Column(name = "user_id", nullable = false, updatable = false)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "type", nullable = false, updatable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "lottery_id", nullable = false, updatable = false)
	public int getLotteryId() {
		return this.lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}

	@Column(name = "expect", nullable = false, length = 32, updatable = false)
	public String getExpect() {
		return this.expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	@Column(name = "rule_id", nullable = false, updatable = false)
	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	@Column(name = "codes", nullable = false, length = 16777215, updatable = false)
	public String getCodes() {
		return this.codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	@Column(name = "nums", nullable = false, updatable = false)
	public int getNums() {
		return this.nums;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

	@Column(name = "model", nullable = false, length = 16, updatable = false)
	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name = "multiple", nullable = false, updatable = false)
	public int getMultiple() {
		return this.multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	@Column(name = "code", nullable = false, updatable = false)
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Column(name = "point", nullable = false, precision = 11, scale = 1, updatable = false)
	public double getPoint() {
		return this.point;
	}

	public void setPoint(double point) {
		this.point = point;
	}

	@Column(name = "money", nullable = false, precision = 16, scale = 5, updatable = false)
	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "time", nullable = false, length = 19, updatable = false)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "stop_time", nullable = false, length = 19, updatable = false)
	public String getStopTime() {
		return this.stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	@Column(name = "open_time", nullable = false, length = 19, updatable = false)
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

	@Column(name = "compressed")
	public int getCompressed() {
		return compressed;
	}

	public void setCompressed(int compressed) {
		this.compressed = compressed;
	}

	@Column(name = "ip", length = 20)
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "win_num", length = 11)
	public int getWinNum() {
		return winNum;
	}

	public void setWinNum(int winNum) {
		this.winNum = winNum;
	}

	public UserBets toUserBets() {
		UserBets userBets = new UserBets();

		userBets.setBillno(this.billno);
		userBets.setUserId(this.userId);
		userBets.setType(this.type);
		userBets.setLotteryId(this.lotteryId);
		userBets.setExpect(this.expect);
		userBets.setRuleId(this.ruleId);
		userBets.setCodes(this.codes);
		userBets.setNums(this.nums);
		userBets.setModel(this.model);
		userBets.setMultiple(this.multiple);
		userBets.setCode(this.code);
		userBets.setPoint(this.point);
		userBets.setMoney(this.money);
		userBets.setTime(this.time);
		userBets.setStopTime(this.stopTime);
		userBets.setOpenTime(this.openTime);
		userBets.setStatus(this.status);
		userBets.setOpenCode(this.openCode);
		userBets.setPrizeMoney(this.prizeMoney);
		userBets.setPrizeTime(this.prizeTime);
		userBets.setChaseBillno(this.chaseBillno);
		userBets.setChaseStop(this.chaseStop);
		userBets.setPlanBillno(this.planBillno);
		userBets.setRewardMoney(this.rewardMoney);
		userBets.setCompressed(this.compressed);
//		userBets.setIp(this.ip);
//		userBets.setWinNum(this.winNum);
		return userBets;
	}
}