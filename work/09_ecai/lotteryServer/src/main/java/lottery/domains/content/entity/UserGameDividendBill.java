package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 游戏分红账单
 */
@Entity
@Table(name = "user_game_dividend_bill", catalog = Database.name)
public class UserGameDividendBill {
	private int id;
	private int userId; // 用户ID
	private String indicateStartDate; // 分红开始日期
	private String indicateEndDate; // 分红结束日期
	private double scale; // 分红比例 x.xx，0.3代表30%
	private double billingOrder; // 消费
	private double thisLoss; // 本次亏损
	private double lastLoss; // 上半月亏损
	private double totalLoss; // 累计亏损
	private double userAmount; // 本次分红金额
	private String settleTime; // 系统结算时间
	private String collectTime; // 用户领取时间
	private int status; // 1：已发放。2：待审核。3：待领取。4：已拒绝(审核拒绝)。5：未达标。
	private String remarks; // 备注

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "user_id", nullable = false, unique = true)
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "indicate_start_date", length = 10)
	public String getIndicateStartDate() {
		return indicateStartDate;
	}

	public void setIndicateStartDate(String indicateStartDate) {
		this.indicateStartDate = indicateStartDate;
	}

	@Column(name = "indicate_end_date", length = 10)
	public String getIndicateEndDate() {
		return indicateEndDate;
	}

	public void setIndicateEndDate(String indicateEndDate) {
		this.indicateEndDate = indicateEndDate;
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

	@Column(name = "this_loss", nullable = false, precision = 16, scale = 4)
	public double getThisLoss() {
		return thisLoss;
	}

	public void setThisLoss(double thisLoss) {
		this.thisLoss = thisLoss;
	}

	@Column(name = "last_loss", nullable = false, precision = 16, scale = 4)
	public double getLastLoss() {
		return lastLoss;
	}

	public void setLastLoss(double lastLoss) {
		this.lastLoss = lastLoss;
	}

	@Column(name = "total_loss", nullable = false, precision = 16, scale = 4)
	public double getTotalLoss() {
		return totalLoss;
	}

	public void setTotalLoss(double totalLoss) {
		this.totalLoss = totalLoss;
	}

	@Column(name = "user_amount", nullable = false, precision = 16, scale = 4)
	public double getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(double userAmount) {
		this.userAmount = userAmount;
	}

	@Column(name = "settle_time", length = 19)
	public String getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}

	@Column(name = "collect_time", length = 19, nullable = true)
	public String getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "remarks", length = 255, nullable = true)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}