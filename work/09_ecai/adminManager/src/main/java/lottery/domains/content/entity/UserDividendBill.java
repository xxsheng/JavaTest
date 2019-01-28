package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 契约分红账单
 */
@Entity
@Table(name = "user_dividend_bill", catalog = Database.name)
public class UserDividendBill {
	private int id;
	private int userId; // 用户ID
	private String indicateStartDate; // 分红开始日期
	private String indicateEndDate; // 分红结束日期
	private int minValidUser; // 最低有效会员数
	private int validUser; // 实际有效会员数
	private double scale; // 分红比例 x.xx，0.3代表30%
	private double dailyBillingOrder; // 日均销量
	private double billingOrder; // 销量
	private double thisLoss; // 本次亏损
	private double lastLoss; // 上半月亏损
	private double totalLoss; // 累计亏损
	private double calAmount; // 计算金额
	private double userAmount; // 本次分红金额
	private double lowerTotalAmount; // 下级共需发放
	private double lowerPaidAmount; // 下级累计发放
	private double availableAmount; // 目前可以领取
	private double totalReceived; // 目前累计领取
	private int issueType; // 发放类型。1：平台发放；2：上级发放
	private String settleTime; // 系统结算时间
	private String collectTime; // 用户领取时间
	private int status; // 1：已发放。2：待审核。3：待领取。4：已拒绝(审核拒绝)。5：未达标。6：余额不足(余额不足是指当上级不够钱给下级发时，上级需充值发给下级)。7：部分领取；8：已过期
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

	@Column(name = "min_valid_user", nullable = false)
	public int getMinValidUser() {
		return minValidUser;
	}

	public void setMinValidUser(int minValidUser) {
		this.minValidUser = minValidUser;
	}

	@Column(name = "valid_user", nullable = false)
	public int getValidUser() {
		return validUser;
	}

	public void setValidUser(int validUser) {
		this.validUser = validUser;
	}

	@Column(name = "scale", nullable = false, precision = 6, scale = 1)
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	@Column(name = "daily_billing_order", nullable = false, precision = 16, scale = 4)
	public double getDailyBillingOrder() {
		return dailyBillingOrder;
	}

	public void setDailyBillingOrder(double dailyBillingOrder) {
		this.dailyBillingOrder = dailyBillingOrder;
	}

	@Column(name = "billing_order", nullable = false, precision = 16, scale = 4)
	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	@Column(name = "cal_amount", nullable = false, precision = 16, scale = 4)
	public double getCalAmount() {
		return calAmount;
	}

	public void setCalAmount(double calAmount) {
		this.calAmount = calAmount;
	}

	@Column(name = "lower_total_amount", nullable = false, precision = 16, scale = 4)
	public double getLowerTotalAmount() {
		return lowerTotalAmount;
	}

	public void setLowerTotalAmount(double lowerTotalAmount) {
		this.lowerTotalAmount = lowerTotalAmount;
	}

	@Column(name = "lower_paid_amount", nullable = false, precision = 16, scale = 4)
	public double getLowerPaidAmount() {
		return lowerPaidAmount;
	}

	public void setLowerPaidAmount(double lowerPaidAmount) {
		this.lowerPaidAmount = lowerPaidAmount;
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

	@Column(name = "available_amount", nullable = false, precision = 16, scale = 4)
	public double getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(double availableAmount) {
		this.availableAmount = availableAmount;
	}

	@Column(name = "total_received", nullable = false, precision = 16, scale = 4)
	public double getTotalReceived() {
		return totalReceived;
	}

	public void setTotalReceived(double totalReceived) {
		this.totalReceived = totalReceived;
	}

	@Column(name = "user_amount", nullable = false, precision = 16, scale = 4)
	public double getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(double userAmount) {
		this.userAmount = userAmount;
	}

	@Column(name = "issue_type", nullable = false)
	public int getIssueType() {
		return issueType;
	}

	public void setIssueType(int issueType) {
		this.issueType = issueType;
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