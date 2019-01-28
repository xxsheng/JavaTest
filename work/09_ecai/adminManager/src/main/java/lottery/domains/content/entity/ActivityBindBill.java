package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * ActivityBindBill entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "activity_bind_bill", catalog = Database.name)
public class ActivityBindBill implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private String registTime;
	private String ip;
	private String bindName;
	private int bindBank;
	private String bindCard;
	private double money;
	private String time;
	private int status;

	// Constructors

	/** default constructor */
	public ActivityBindBill() {
	}

	/** full constructor */
	public ActivityBindBill(int userId, String registTime, String ip,
			String bindName, int bindBank, String bindCard,
			double money, String time, int status) {
		this.userId = userId;
		this.registTime = registTime;
		this.ip = ip;
		this.bindName = bindName;
		this.bindBank = bindBank;
		this.bindCard = bindCard;
		this.money = money;
		this.time = time;
		this.status = status;
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

	@Column(name = "regist_time", nullable = false, length = 19)
	public String getRegistTime() {
		return this.registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	@Column(name = "ip", nullable = false, length = 128)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "bind_name", nullable = false, length = 64)
	public String getBindName() {
		return this.bindName;
	}

	public void setBindName(String bindName) {
		this.bindName = bindName;
	}

	@Column(name = "bind_bank", nullable = false)
	public int getBindBank() {
		return this.bindBank;
	}

	public void setBindBank(int bindBank) {
		this.bindBank = bindBank;
	}

	@Column(name = "bind_card", nullable = false, length = 128)
	public String getBindCard() {
		return this.bindCard;
	}

	public void setBindCard(String bindCard) {
		this.bindCard = bindCard;
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

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}