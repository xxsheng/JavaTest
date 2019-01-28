package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_game_report", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"user_id", "platform_id", "time" }))
public class HistoryUserGameReport implements java.io.Serializable {
	private int id;
	private int userId;
	private int platformId;
	private double transIn;
	private double transOut;
	private double waterReturn;
	private double proxyReturn;
	private double activity;
	private double billingOrder;
	private double prize;
	private String time;

	public HistoryUserGameReport() {
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

	@Column(name = "platform_id", nullable = false)
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
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