package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.UniqueConstraint;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserWithdraw entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_withdraw", catalog = Database.backup, uniqueConstraints = @UniqueConstraint(columnNames = "billno") )
public class HistoryUserWithdraw implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String billno;
	private int userId;
	private double money;
	private double beforeMoney;
	private double afterMoney;
	private double recMoney;
	private double feeMoney;
	private String time;
	private int status;
	private String infos; // 前台看的备注
	private String bankName;
	private String bankBranch;
	private String cardName;
	private String cardId;
	private String payBillno;
	private String operatorUser;
	private String operatorTime;
	private String remarks; // 后台的备注
	private int lockStatus; // 锁定状态
	private int checkStatus; // 风控审核状态
	private int remitStatus; // 打款状态
	private Integer payType; // 打款类型：1：手动API代付；2手动；3：自动API代付
	private Integer paymentChannelId; // 第三方支付ID

	// Constructors

	/** default constructor */
	public HistoryUserWithdraw() {
	}

	/** minimal constructor */
	public HistoryUserWithdraw(String billno, int userId, double money, double beforeMoney, double afterMoney, double recMoney,
			double feeMoney, String time, int status, int lockStatus, int checkStatus, int remitStatus) {
		this.billno = billno;
		this.userId = userId;
		this.money = money;
		this.beforeMoney = beforeMoney;
		this.afterMoney = afterMoney;
		this.recMoney = recMoney;
		this.feeMoney = feeMoney;
		this.time = time;
		this.status = status;
		this.lockStatus = lockStatus;
		this.checkStatus = checkStatus;
		this.remitStatus = remitStatus;
	}

	/** full constructor */
	public HistoryUserWithdraw(String billno, int userId, double money, double beforeMoney, double afterMoney, double recMoney,
			double feeMoney, String time, int status, String infos, String bankName, String bankBranch, String cardName,
			String cardId, String payBillno, String operatorUser, String operatorTime, String remarks, int lockStatus,
			int checkStatus, int remitStatus) {
		this.billno = billno;
		this.userId = userId;
		this.money = money;
		this.beforeMoney = beforeMoney;
		this.afterMoney = afterMoney;
		this.recMoney = recMoney;
		this.feeMoney = feeMoney;
		this.time = time;
		this.status = status;
		this.infos = infos;
		this.bankName = bankName;
		this.bankBranch = bankBranch;
		this.cardName = cardName;
		this.cardId = cardId;
		this.payBillno = payBillno;
		this.operatorUser = operatorUser;
		this.operatorTime = operatorTime;
		this.remarks = remarks;
		this.lockStatus = lockStatus;
		this.checkStatus = checkStatus;
		this.remitStatus = remitStatus;
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

	@Column(name = "money", nullable = false, precision = 11, scale = 3)
	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "before_money", nullable = false, precision = 11, scale = 3)
	public double getBeforeMoney() {
		return this.beforeMoney;
	}

	public void setBeforeMoney(double beforeMoney) {
		this.beforeMoney = beforeMoney;
	}

	@Column(name = "after_money", nullable = false, precision = 11, scale = 3)
	public double getAfterMoney() {
		return this.afterMoney;
	}

	public void setAfterMoney(double afterMoney) {
		this.afterMoney = afterMoney;
	}

	@Column(name = "rec_money", nullable = false, precision = 11, scale = 3)
	public double getRecMoney() {
		return this.recMoney;
	}

	public void setRecMoney(double recMoney) {
		this.recMoney = recMoney;
	}

	@Column(name = "fee_money", nullable = false, precision = 11, scale = 3)
	public double getFeeMoney() {
		return this.feeMoney;
	}

	public void setFeeMoney(double feeMoney) {
		this.feeMoney = feeMoney;
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
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "infos")
	public String getInfos() {
		return this.infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}

	@Column(name = "bank_name", length = 64)
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "bank_branch", length = 128)
	public String getBankBranch() {
		return this.bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	@Column(name = "card_name", length = 64)
	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	@Column(name = "card_id", length = 128)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "pay_billno", length = 128)
	public String getPayBillno() {
		return this.payBillno;
	}

	public void setPayBillno(String payBillno) {
		this.payBillno = payBillno;
	}

	@Column(name = "operator_user", length = 64)
	public String getOperatorUser() {
		return this.operatorUser;
	}

	public void setOperatorUser(String operatorUser) {
		this.operatorUser = operatorUser;
	}

	@Column(name = "operator_time", length = 19)
	public String getOperatorTime() {
		return this.operatorTime;
	}

	public void setOperatorTime(String operatorTime) {
		this.operatorTime = operatorTime;
	}

	@Column(name = "remarks")
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "lock_status", nullable = false)
	public int getLockStatus() {
		return this.lockStatus;
	}

	public void setLockStatus(int lockStatus) {
		this.lockStatus = lockStatus;
	}

	@Column(name = "check_status", nullable = false)
	public int getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(int checkStatus) {
		this.checkStatus = checkStatus;
	}
	
	@Column(name = "remit_status", nullable = false)
	public int getRemitStatus() {
		return remitStatus;
	}

	public void setRemitStatus(int remitStatus) {
		this.remitStatus = remitStatus;
	}

	@Column(name = "pay_type")
	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Column(name = "payment_channel_id")
	public Integer getPaymentChannelId() {
		return paymentChannelId;
	}

	public void setPaymentChannelId(Integer paymentChannelId) {
		this.paymentChannelId = paymentChannelId;
	}

}