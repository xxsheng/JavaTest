package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * ActivitySalaryBill entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "activity_salary_bill", catalog = Database.name)
public class ActivitySalaryBill implements java.io.Serializable {

	// Fields
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private int type;
	private double totalMoney;
	private double money;
	private String date;
	private String time;

	// Constructors

	/** default constructor */
	public ActivitySalaryBill() {
	}

	/** full constructor */
	public ActivitySalaryBill(int userId, int type, double totalMoney, double money, String date, String time) {
		this.userId = userId;
		this.type = type;
		this.totalMoney = totalMoney;
		this.money = money;
		this.date = date;
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

	@Column(name = "type", nullable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "total_money", nullable = false, precision = 16, scale = 5)
	public double getTotalMoney() {
		return this.totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	@Column(name = "money", nullable = false, precision = 16, scale = 5)
	public double getMoney() {
		return this.money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	@Column(name = "date", nullable = false, length = 10)
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}