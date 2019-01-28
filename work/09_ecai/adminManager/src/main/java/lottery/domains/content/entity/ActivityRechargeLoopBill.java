package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * ActivityRechargeLoopBill entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "activity_recharge_loop_bill", catalog = Database.name)
public class ActivityRechargeLoopBill implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private String ip;
	private int step;
	private double totalMoney;
	private double totalCost;
	private double money;
	private String time;

	// Constructors

	/** default constructor */
	public ActivityRechargeLoopBill() {
	}

	/** full constructor */
	public ActivityRechargeLoopBill(int userId, String ip, int step,
			double totalMoney, double totalCost, double money, String time) {
		this.userId = userId;
		this.ip = ip;
		this.step = step;
		this.totalMoney = totalMoney;
		this.totalCost = totalCost;
		this.money = money;
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

	@Column(name = "ip", nullable = false, length = 128)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "step", nullable = false)
	public int getStep() {
		return this.step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Column(name = "total_money", nullable = false, precision = 16, scale = 5)
	public double getTotalMoney() {
		return this.totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	@Column(name = "total_cost", nullable = false, precision = 16, scale = 5)
	public double getTotalCost() {
		return this.totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
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

}