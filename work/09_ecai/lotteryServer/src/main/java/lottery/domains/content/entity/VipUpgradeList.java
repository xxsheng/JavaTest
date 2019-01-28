package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * VipUpgradeList entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "vip_upgrade_list", catalog = Database.name)
public class VipUpgradeList implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private int beforeLevel;
	private int afterLevel;
	private double monthRecharge;
	private double monthCost;
	private String time;
	private int status;

	// Constructors

	/** default constructor */
	public VipUpgradeList() {
	}

	/** full constructor */
	public VipUpgradeList(int userId, int beforeLevel,
			int afterLevel, double monthRecharge, double monthCost,
			String time, int status) {
		this.userId = userId;
		this.beforeLevel = beforeLevel;
		this.afterLevel = afterLevel;
		this.monthRecharge = monthRecharge;
		this.monthCost = monthCost;
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

	@Column(name = "before_level", nullable = false)
	public int getBeforeLevel() {
		return this.beforeLevel;
	}

	public void setBeforeLevel(int beforeLevel) {
		this.beforeLevel = beforeLevel;
	}

	@Column(name = "after_level", nullable = false)
	public int getAfterLevel() {
		return this.afterLevel;
	}

	public void setAfterLevel(int afterLevel) {
		this.afterLevel = afterLevel;
	}

	@Column(name = "month_recharge", nullable = false, precision = 16, scale = 5)
	public double getMonthRecharge() {
		return this.monthRecharge;
	}

	public void setMonthRecharge(double monthRecharge) {
		this.monthRecharge = monthRecharge;
	}

	@Column(name = "month_cost", nullable = false, precision = 16, scale = 5)
	public double getMonthCost() {
		return this.monthCost;
	}

	public void setMonthCost(double monthCost) {
		this.monthCost = monthCost;
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