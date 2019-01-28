package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2016/11/01
 */
public class UserDailySettleBillVO {
	private int id;
	private String username; // 用户名
	private double scale; // 日结比例 x.x
	private int validUser; // 实际有效会员数
	private int minValidUser; // 最低有效会员要求
	private String indicateDate; // 结算日期，结算的是哪一天的
	private double billingOrder; // 消费
	private double thisLoss;
	private double calAmount; // 计算金额
	private double userAmount; // 实际金额
	private double lowerTotalAmount; // 下级共需发放
	private double lowerPaidAmount; // 下级累计发放
	private double totalReceived; // 目前累计领取
	private String settleTime; // 系统结算时间
	private int issueType; // 发放类型。1：平台发放；2：上级发放
	private int status; // 1：已发放；2：部分发放；3：余额不足；4：未达标；5：已拒绝；
	private String remarks; // 备注

	public UserDailySettleBillVO(UserDailySettleBill bean, LotteryDataFactory dataFactory) {
		this.id = bean.getId();
		UserVO user = dataFactory.getUser(bean.getUserId());
		if (user == null) {
			this.username = "未知[" + bean.getUserId() + "]";
		} else {
			this.username = user.getUsername();
		}
		this.thisLoss = bean.getThisLoss();
		this.scale = bean.getScale();
		this.validUser = bean.getValidUser();
		this.minValidUser = bean.getMinValidUser();
		this.indicateDate = bean.getIndicateDate();
		this.billingOrder = bean.getBillingOrder();
		this.calAmount = bean.getCalAmount();
		this.userAmount = bean.getUserAmount();
		this.lowerTotalAmount = bean.getLowerTotalAmount();
		this.lowerPaidAmount = bean.getLowerPaidAmount();
		this.totalReceived = bean.getTotalReceived();
		this.settleTime = bean.getSettleTime();
		this.issueType = bean.getIssueType();
		this.status = bean.getStatus();
		this.remarks = bean.getRemarks();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public int getValidUser() {
		return validUser;
	}

	public void setValidUser(int validUser) {
		this.validUser = validUser;
	}

	public int getMinValidUser() {
		return minValidUser;
	}

	public void setMinValidUser(int minValidUser) {
		this.minValidUser = minValidUser;
	}

	public String getIndicateDate() {
		return indicateDate;
	}

	public void setIndicateDate(String indicateDate) {
		this.indicateDate = indicateDate;
	}

	public double getBillingOrder() {
		return billingOrder;
	}

	public void setBillingOrder(double billingOrder) {
		this.billingOrder = billingOrder;
	}

	public double getCalAmount() {
		return calAmount;
	}

	public void setCalAmount(double calAmount) {
		this.calAmount = calAmount;
	}

	public double getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(double userAmount) {
		this.userAmount = userAmount;
	}

	public double getLowerTotalAmount() {
		return lowerTotalAmount;
	}

	public void setLowerTotalAmount(double lowerTotalAmount) {
		this.lowerTotalAmount = lowerTotalAmount;
	}

	public double getLowerPaidAmount() {
		return lowerPaidAmount;
	}

	public void setLowerPaidAmount(double lowerPaidAmount) {
		this.lowerPaidAmount = lowerPaidAmount;
	}

	public double getTotalReceived() {
		return totalReceived;
	}

	public void setTotalReceived(double totalReceived) {
		this.totalReceived = totalReceived;
	}

	public String getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}

	public int getIssueType() {
		return issueType;
	}

	public void setIssueType(int issueType) {
		this.issueType = issueType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public double getThisLoss() {
		return thisLoss;
	}

	public void setThisLoss(double thisLoss) {
		this.thisLoss = thisLoss;
	}

}
