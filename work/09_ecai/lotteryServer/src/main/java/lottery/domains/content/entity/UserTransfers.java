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
 * UserTransfers entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_transfers", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = "billno"))
public class UserTransfers implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String billno;
	private int toUid;
	private int fromUid;
	private int toAccount;
	private int fromAccount;
	private double money;
	private double beforeMoney;
	private double afterMoney;
	private String time;
	private int status;
	private int type;
	private String infos;

	// Constructors

	/** default constructor */
	public UserTransfers() {
	}

	/** minimal constructor */
	public UserTransfers(String billno, int toUid, int fromUid,
			int toAccount, int fromAccount, double money,
			double beforeMoney, double afterMoney, String time, int status,
			int type) {
		this.billno = billno;
		this.toUid = toUid;
		this.fromUid = fromUid;
		this.toAccount = toAccount;
		this.fromAccount = fromAccount;
		this.money = money;
		this.beforeMoney = beforeMoney;
		this.afterMoney = afterMoney;
		this.time = time;
		this.status = status;
		this.type = type;
	}

	/** full constructor */
	public UserTransfers(String billno, int toUid, int fromUid,
			int toAccount, int fromAccount, double money,
			double beforeMoney, double afterMoney, String time, int status,
			int type, String infos) {
		this.billno = billno;
		this.toUid = toUid;
		this.fromUid = fromUid;
		this.toAccount = toAccount;
		this.fromAccount = fromAccount;
		this.money = money;
		this.beforeMoney = beforeMoney;
		this.afterMoney = afterMoney;
		this.time = time;
		this.status = status;
		this.type = type;
		this.infos = infos;
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

	@Column(name = "to_uid", nullable = false)
	public int getToUid() {
		return this.toUid;
	}

	public void setToUid(int toUid) {
		this.toUid = toUid;
	}

	@Column(name = "from_uid", nullable = false)
	public int getFromUid() {
		return this.fromUid;
	}

	public void setFromUid(int fromUid) {
		this.fromUid = fromUid;
	}

	@Column(name = "to_account", nullable = false)
	public int getToAccount() {
		return this.toAccount;
	}

	public void setToAccount(int toAccount) {
		this.toAccount = toAccount;
	}

	@Column(name = "from_account", nullable = false)
	public int getFromAccount() {
		return this.fromAccount;
	}

	public void setFromAccount(int fromAccount) {
		this.fromAccount = fromAccount;
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

	@Column(name = "type", nullable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "infos")
	public String getInfos() {
		return this.infos;
	}

	public void setInfos(String infos) {
		this.infos = infos;
	}

}