package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 契约日结账单
 */
@Entity
@Table(name = "user_daily_settle_bill", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "indicate_date"}))
public class UserDailySettleBill {
    private int id;
    private int userId; // 用户ID
    private String indicateDate; // 结算日期，结算的是哪一天的
    private int minValidUser; // 最低有效会员数
    private int validUser; // 实际有效会员数
    private double scale; // 日结比例
    private double billingOrder; // 消费
    private double thisLoss;//亏损
    private double calAmount; // 计算金额
    private double userAmount; // 实际应得金额
    private double lowerTotalAmount; // 下级共需发放
    private double lowerPaidAmount; // 下级累计发放
    private double availableAmount; // 目前可供发放给下级的金额
    private double totalReceived; // 目前累计领取
    private String settleTime; // 系统结算时间
    private int issueType; // 发放类型。1：平台发放；2：上级发放
    private int status; // 1：已发放；2：部分发放；3：余额不足；4：未达标；5：已拒绝；
    private String remarks; // 备注

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

    @Column(name = "settle_time", length = 19)
    public String getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
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

    @Column(name = "cal_amount", nullable = false, precision = 16, scale = 4)
    public double getCalAmount() {
        return calAmount;
    }

    public void setCalAmount(double calAmount) {
        this.calAmount = calAmount;
    }

    @Column(name = "user_amount", nullable = false, precision = 16, scale = 4)
    public double getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(double userAmount) {
        this.userAmount = userAmount;
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

    @Column(name = "total_received", nullable = false, precision = 16, scale = 4)
    public double getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(double totalReceived) {
        this.totalReceived = totalReceived;
    }

    @Column(name = "available_amount", nullable = false, precision = 16, scale = 4)
    public double getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(double availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Column(name = "issue_type", nullable = false)
    public int getIssueType() {
        return issueType;
    }

    public void setIssueType(int issueType) {
        this.issueType = issueType;
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
    
    @Column(name = "this_loss", nullable = false, precision = 16, scale = 4)
	public double getThisLoss() {
		return thisLoss;
	}

	public void setThisLoss(double thisLoss) {
		this.thisLoss = thisLoss;
	}
}