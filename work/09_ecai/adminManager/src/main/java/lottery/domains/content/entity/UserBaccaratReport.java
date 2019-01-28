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
 * UserBaccaratReport entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_baccarat_report", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"user_id", "time" }))
public class UserBaccaratReport implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userId;
	private double transIn;
	private double transOut;
	private double spend;
	private double prize;
	private double waterReturn;
	private double proxyReturn;
	private double cancelOrder;
	private double activity;
	private double billingOrder;
	private String time;

	// Constructors

	/** default constructor */
	public UserBaccaratReport() {
	}

	/** full constructor */
	public UserBaccaratReport(int userId, double transIn, double transOut,
			double spend, double prize, double waterReturn, double proxyReturn,
			double cancelOrder, double activity, double billingOrder,
			String time) {
		this.userId = userId;
		this.transIn = transIn;
		this.transOut = transOut;
		this.spend = spend;
		this.prize = prize;
		this.waterReturn = waterReturn;
		this.proxyReturn = proxyReturn;
		this.cancelOrder = cancelOrder;
		this.activity = activity;
		this.billingOrder = billingOrder;
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

	@Column(name = "spend", nullable = false, precision = 16, scale = 5)
	public double getSpend() {
		return this.spend;
	}

	public void setSpend(double spend) {
		this.spend = spend;
	}

	@Column(name = "prize", nullable = false, precision = 16, scale = 5)
	public double getPrize() {
		return this.prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}

	@Column(name = "water_return", nullable = false, precision = 16, scale = 5)
	public double getWaterReturn() {
		return this.waterReturn;
	}

	public void setWaterReturn(double waterReturn) {
		this.waterReturn = waterReturn;
	}

	@Column(name = "proxy_return", nullable = false, precision = 16, scale = 5)
	public double getProxyReturn() {
		return this.proxyReturn;
	}

	public void setProxyReturn(double proxyReturn) {
		this.proxyReturn = proxyReturn;
	}

	@Column(name = "cancel_order", nullable = false, precision = 16, scale = 5)
	public double getCancelOrder() {
		return this.cancelOrder;
	}

	public void setCancelOrder(double cancelOrder) {
		this.cancelOrder = cancelOrder;
	}

	@Column(name = "activity", nullable = false, precision = 16, scale = 5)
	public double getActivity() {
		return this.activity;
	}

	public void setActivity(double activity) {
		this.activity = activity;
	}

	@Column(name = "billing_order", nullable = false, precision = 16, scale = 5)
	public double getBillingOrder() {
		return this.billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	@Column(name = "time", nullable = false, length = 10)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}