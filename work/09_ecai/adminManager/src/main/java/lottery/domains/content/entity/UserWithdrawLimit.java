package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserWithdrawLimit entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_withdraw_limit", catalog = Database.name)
public class UserWithdrawLimit implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private double rechargeMoney;
	private String rechargeTime;
	private double consumptionRequirements;
	private double proportion;
	private int type;
	private int subType;

	// Constructors

	/** default constructor */
	public UserWithdrawLimit() {
	}

	/** full constructor */
	public UserWithdrawLimit(int userId, double rechargeMoney, String rechargeTime,
			double consumptionRequirements, double proportion,int type, int subType) {
		this.userId = userId;
		this.rechargeMoney = rechargeMoney;
		this.rechargeTime = rechargeTime;
		this.consumptionRequirements = consumptionRequirements;
		this.proportion = proportion;
		this.type = type;
		this.subType = subType;
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
	
	@Column(name = "recharge_money", nullable = false, precision = 12, scale = 3)
	public double getRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(double rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	
	@Column(name = "recharge_time", length = 19)
	public String getRechargeTime() {
		return rechargeTime;
	}

	public void setRechargeTime(String rechargeTime) {
		this.rechargeTime = rechargeTime;
	}
	@Column(name = "consumption_requirements", nullable = false, precision = 12, scale = 3)
	public double getConsumptionRequirements() {
		return consumptionRequirements;
	}

	public void setConsumptionRequirements(double consumptionRequirements) {
		this.consumptionRequirements = consumptionRequirements;
	}
	@Column(name = "proportion", nullable = false, precision = 12, scale = 3)
	public double getProportion() {
		return proportion;
	}

	public void setProportion(double proportion) {
		this.proportion = proportion;
	}

	@Column(name = "type", nullable = false)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "sub_type", nullable = false)
	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}
}