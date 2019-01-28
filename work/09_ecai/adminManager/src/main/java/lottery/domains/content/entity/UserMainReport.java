package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

/**
 * UserMainReport entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_main_report", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"user_id", "time" }))
public class UserMainReport implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private double recharge;
	private double withdrawals;
	private double transIn;
	private double transOut;
	private double accountIn;
	private double accountOut;
	private double activity;
	private String time;

	// Constructors

	/** default constructor */
	public UserMainReport() {
	}

	/** full constructor */
	public UserMainReport(int userId, double recharge, double withdrawals,
			double transIn, double transOut, double accountIn, double accountOut, double activity, String time) {
		this.userId = userId;
		this.recharge = recharge;
		this.withdrawals = withdrawals;
		this.transIn = transIn;
		this.transOut = transOut;
		this.accountIn = accountIn;
		this.accountOut = accountOut;
		this.activity = activity;
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

	@Column(name = "recharge", nullable = false, precision = 16, scale = 5)
	public double getRecharge() {
		return this.recharge;
	}

	public void setRecharge(double recharge) {
		this.recharge = recharge;
	}

	@Column(name = "withdrawals", nullable = false, precision = 16, scale = 5)
	public double getWithdrawals() {
		return this.withdrawals;
	}

	public void setWithdrawals(double withdrawals) {
		this.withdrawals = withdrawals;
	}

	@Column(name = "trans_in", nullable = false, precision = 16, scale = 5)
	public double getTransIn() {
		return this.transIn;
	}

	public void setTransIn(double transIn) {
		this.transIn = transIn;
	}

	@Column(name = "trans_out", nullable = false, precision = 16, scale = 5)
	public double getTransOut() {
		return this.transOut;
	}

	public void setTransOut(double transOut) {
		this.transOut = transOut;
	}

	@Column(name = "account_in", nullable = false, precision = 16, scale = 5)
	public double getAccountIn() {
		return accountIn;
	}

	public void setAccountIn(double accountIn) {
		this.accountIn = accountIn;
	}

	@Column(name = "account_out", nullable = false, precision = 16, scale = 5)
	public double getAccountOut() {
		return accountOut;
	}

	public void setAccountOut(double accountOut) {
		this.accountOut = accountOut;
	}

	@Column(name = "activity", nullable = false, precision = 16, scale = 5)
	public double getActivity() {
		return this.activity;
	}

	public void setActivity(double activity) {
		this.activity = activity;
	}

	@Column(name = "time", nullable = false, length = 10)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}