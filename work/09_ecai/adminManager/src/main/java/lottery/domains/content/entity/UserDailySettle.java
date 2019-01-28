package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 契约日结
 */
@Entity
@Table(name = "user_daily_settle", catalog = Database.name)
public class UserDailySettle {
	private int id;
	private int userId; // 用户ID
//	private double scale; // 日结比例 x.x，0.015代表1.5%，即1万150
	private String scaleLevel; // 分红比率，阶梯 逗号隔开  x.xx，0.3代表30%
	private String lossLevel; // 亏损，多阶梯逗号隔开 xx.xx 单位万
	private String salesLevel; // 销量，阶梯 逗号隔开 单位万 xx.xx
	private String userLevel;//人数阶梯，逗号隔开
	private int minValidUser; // 最低有效会员要求
	private String createTime; // 发起契约时间
	private String agreeTime; // 接受契约时间
	private String startDate; // 契约开始日期
	private String endDate; // 契约结束日期
	private double totalAmount; // 累计日结总金额 x.xxxx
	private int status; // 1：生效。2：待同意。3：已过期。4：无效。5：已拒绝
	private int fixed; // 0：浮动比例。1：固定比例
	private double minScale; // 浮动最小比例
	private double maxScale; // 浮动最大比例

	public UserDailySettle() {
	}

	public UserDailySettle(int id, int userId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String createTime,
						   String agreeTime, String startDate, String endDate, double totalAmount, int status, int fixed, double minScale, double maxScale, String userLevel) {
		this.id = id;
		this.userId = userId;
//		this.scale = scale;
		this.scaleLevel = scaleLevel;
		this.lossLevel = lossLevel;
		this.salesLevel = salesLevel;
		this.userLevel = userLevel;
		this.minValidUser = minValidUser;
		this.createTime = createTime;
		this.agreeTime = agreeTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalAmount = totalAmount;
		this.status = status;
		this.fixed = fixed;
		this.minScale = minScale;
		this.maxScale = maxScale;
	}

	public UserDailySettle(int userId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String createTime,
						   String agreeTime, String startDate, String endDate, double totalAmount, int status, int fixed, double minScale, double maxScale, String userLevel) {
		this.userId = userId;
//		this.scale = scale;
		this.scaleLevel = scaleLevel;
		this.lossLevel = lossLevel;
		this.salesLevel = salesLevel;
		this.userLevel = userLevel;
		this.minValidUser = minValidUser;
		this.createTime = createTime;
		this.agreeTime = agreeTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalAmount = totalAmount;
		this.status = status;
		this.fixed = fixed;
		this.minScale = minScale;
		this.maxScale = maxScale;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
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

//	@Column(name = "scale", nullable = false, precision = 4, scale = 3)
//	public double getScale() {
//		return scale;
//	}
//
//	public void setScale(double scale) {
//		this.scale = scale;
//	}
	@Column(name = "scale_level", length = 255)
	public String getScaleLevel() {
		return scaleLevel;
	}

	public void setScaleLevel(String scaleLevel) {
		this.scaleLevel = scaleLevel;
	}
	
	@Column(name = "loss_level", length = 255)
	public String getLossLevel() {
		return lossLevel;
	}

	public void setLossLevel(String lossLevel) {
		this.lossLevel = lossLevel;
	}
	
	@Column(name = "sales_level", length = 255)
	public String getSalesLevel() {
		return salesLevel;
	}

	public void setSalesLevel(String salesLevel) {
		this.salesLevel = salesLevel;
	}

	@Column(name = "min_valid_user", nullable = false)
	public int getMinValidUser() {
		return minValidUser;
	}

	public void setMinValidUser(int minValidUser) {
		this.minValidUser = minValidUser;
	}

	@Column(name = "create_time", nullable = false, length = 19)
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Column(name = "agree_time", length = 19)
	public String getAgreeTime() {
		return agreeTime;
	}

	public void setAgreeTime(String agreeTime) {
		this.agreeTime = agreeTime;
	}

	@Column(name = "start_date", nullable = false, length = 10)
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date", nullable = false, length = 10)
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Column(name = "total_amount", precision = 16, scale = 4)
	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "fixed", nullable = false)
	public int getFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed;
	}

	@Column(name = "min_scale", nullable = false, precision = 4, scale = 3)
	public double getMinScale() {
		return minScale;
	}

	public void setMinScale(double minScale) {
		this.minScale = minScale;
	}

	@Column(name = "max_scale", nullable = false, precision = 4, scale = 3)
	public double getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(double maxScale) {
		this.maxScale = maxScale;
	}

	@Column(name = "user_level", length = 255)
	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
	
	
}