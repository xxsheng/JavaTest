package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "sys_code_amount", catalog = Database.name)
public class SysCodeAmount implements java.io.Serializable {
	private int id;
	private int code;
	private int days;
	private double dailyBillingOrder;

	public SysCodeAmount() {
	}

	public SysCodeAmount(int id, int code, int days, double dailyBillingOrder) {
		this.id = id;
		this.code = code;
		this.days = days;
		this.dailyBillingOrder = dailyBillingOrder;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "code", nullable = false)
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Column(name = "days", nullable = false)
	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Column(name = "daily_billing_order", nullable = false, precision = 16, scale = 4)
	public double getDailyBillingOrder() {
		return dailyBillingOrder;
	}

	public void setDailyBillingOrder(double dailyBillingOrder) {
		this.dailyBillingOrder = dailyBillingOrder;
	}
}