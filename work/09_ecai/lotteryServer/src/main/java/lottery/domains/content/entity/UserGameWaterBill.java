package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 游戏返水账单
 */
@Entity
@Table(name = "user_game_water_bill", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "indicate_date", "from_user"}))
public class UserGameWaterBill {
	private int id;
	private int userId; // 用户ID
	private String indicateDate; // 返水日期
	private int fromUser; // 来自用户
	private String settleTime; // 系统结算时间
	private double scale; // 比例,百分比
	private double billingOrder; // 消费
	private double userAmount; // 返水金额
	private int type; // 类型，1：消费返水；2：代理返水
	private int status; // 状态，1：已发放；2：系统拒绝
	private String remark; // 备注

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "indicate_date", length = 10)
	public String getIndicateDate() {
		return indicateDate;
	}

	public void setIndicateDate(String indicateDate) {
		this.indicateDate = indicateDate;
	}

	@Column(name = "from_user", nullable = false)
	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}

	@Column(name = "settle_time", length = 19)
	public String getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}

	@Column(name = "scale", nullable = false, precision = 5, scale = 4)
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	@Column(name = "billing_order", nullable = false, precision = 16, scale = 4)
	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	@Column(name = "user_amount", nullable = false, precision = 16, scale = 4)
	public double getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(double userAmount) {
		this.userAmount = userAmount;
	}

	@Column(name = "type", nullable = false)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}