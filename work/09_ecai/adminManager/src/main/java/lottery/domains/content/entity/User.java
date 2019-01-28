package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * User entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = "username") )
public class User implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String username;
	private String password;
	private String imgPassword;
	private String nickname;
	private double totalMoney;
	private double lotteryMoney;
	private double baccaratMoney;
	private double freezeMoney;
	private double dividendMoney;
	private int type;
	private int upid;
	private String upids;
	private int code;
	private double locatePoint;
	private double notLocatePoint;
	private int codeType;
	private double extraPoint;
	private String withdrawName;
	private String withdrawPassword;
	private String registTime;
	private String loginTime;
	private String lockTime;
	private int AStatus;
	private int BStatus;
	private String message;
	private String sessionId;
	private int onlineStatus;
	private int allowEqualCode;
	private int allowTransfers;
	private int allowPlatformTransfers;//是否允许平台转账 1允许 -1禁止
	private int allowWithdraw;
	private int loginValidate;
	private int bindStatus;
	private int vipLevel;
	private double integral;
	private String secretKey;
	private int isBindGoogle;
	private int relatedUpid; // 关联上级ID
	private String relatedLowers; // 关联下级ID列表,与related_upid数据是冗余的，这里更方便查询使用
	private double relatedPoint; // 关联上级吃返点百分比
	private String relatedUsers; // 关联会员ID列表
	private int isCjZhaoShang; // 0：不是超级招商；1：是超级招商

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String username, String password, String nickname, double totalMoney, double lotteryMoney,
			double baccaratMoney, double freezeMoney, double dividendMoney, int type, int upid, int code,
			double locatePoint, double notLocatePoint, int codeType, double extraPoint, String registTime, int AStatus,
			int BStatus, int onlineStatus, int allowEqualCode, int allowTransfers, int loginValidate, int bindStatus,
			int vipLevel, double integral,int allowWithdraw,int allowPlatformTransfers) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.totalMoney = totalMoney;
		this.lotteryMoney = lotteryMoney;
		this.baccaratMoney = baccaratMoney;
		this.freezeMoney = freezeMoney;
		this.dividendMoney = dividendMoney;
		this.type = type;
		this.upid = upid;
		this.code = code;
		this.locatePoint = locatePoint;
		this.notLocatePoint = notLocatePoint;
		this.codeType = codeType;
		this.extraPoint = extraPoint;
		this.registTime = registTime;
		this.AStatus = AStatus;
		this.BStatus = BStatus;
		this.onlineStatus = onlineStatus;
		this.allowEqualCode = allowEqualCode;
		this.allowTransfers = allowTransfers;
		this.loginValidate = loginValidate;
		this.bindStatus = bindStatus;
		this.vipLevel = vipLevel;
		this.integral = integral;
		this.allowWithdraw = allowWithdraw;
		this.allowPlatformTransfers = allowPlatformTransfers;
	}

	/** full constructor */
	public User(String username, String password, String imgPassword, String nickname, double totalMoney,
			double lotteryMoney, double baccaratMoney, double freezeMoney, double dividendMoney, int type, int upid,
			String upids, int code, double locatePoint, double notLocatePoint, int codeType, double extraPoint,
			String withdrawName, String withdrawPassword, String registTime, String loginTime, String lockTime,
			int AStatus, int BStatus, String message, String sessionId, int onlineStatus, int allowEqualCode,
			int allowTransfers, int loginValidate, int bindStatus, int vipLevel, double integral,int allowWithdraw,int allowPlatformTransfers) {
		this.username = username;
		this.password = password;
		this.imgPassword = imgPassword;
		this.nickname = nickname;
		this.totalMoney = totalMoney;
		this.lotteryMoney = lotteryMoney;
		this.baccaratMoney = baccaratMoney;
		this.freezeMoney = freezeMoney;
		this.dividendMoney = dividendMoney;
		this.type = type;
		this.upid = upid;
		this.upids = upids;
		this.code = code;
		this.locatePoint = locatePoint;
		this.notLocatePoint = notLocatePoint;
		this.codeType = codeType;
		this.extraPoint = extraPoint;
		this.withdrawName = withdrawName;
		this.withdrawPassword = withdrawPassword;
		this.registTime = registTime;
		this.loginTime = loginTime;
		this.lockTime = lockTime;
		this.AStatus = AStatus;
		this.BStatus = BStatus;
		this.message = message;
		this.sessionId = sessionId;
		this.onlineStatus = onlineStatus;
		this.allowEqualCode = allowEqualCode;
		this.allowTransfers = allowTransfers;
		this.loginValidate = loginValidate;
		this.bindStatus = bindStatus;
		this.vipLevel = vipLevel;
		this.integral = integral;
		this.allowWithdraw = allowWithdraw;
		this.allowPlatformTransfers = allowPlatformTransfers;
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

	@Column(name = "username", unique = true, nullable = false, length = 20)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false, length = 32)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "img_password", length = 32)
	public String getImgPassword() {
		return this.imgPassword;
	}

	public void setImgPassword(String imgPassword) {
		this.imgPassword = imgPassword;
	}

	@Column(name = "nickname", nullable = false, length = 20)
	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "total_money", nullable = false, precision = 16, scale = 5)
	public double getTotalMoney() {
		return this.totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}

	@Column(name = "lottery_money", nullable = false, precision = 16, scale = 5)
	public double getLotteryMoney() {
		return this.lotteryMoney;
	}

	public void setLotteryMoney(double lotteryMoney) {
		this.lotteryMoney = lotteryMoney;
	}

	@Column(name = "baccarat_money", nullable = false, precision = 16, scale = 5)
	public double getBaccaratMoney() {
		return this.baccaratMoney;
	}

	public void setBaccaratMoney(double baccaratMoney) {
		this.baccaratMoney = baccaratMoney;
	}

	@Column(name = "freeze_money", nullable = false, precision = 16, scale = 5)
	public double getFreezeMoney() {
		return this.freezeMoney;
	}

	public void setFreezeMoney(double freezeMoney) {
		this.freezeMoney = freezeMoney;
	}

	@Column(name = "dividend_money", nullable = false, precision = 16, scale = 5)
	public double getDividendMoney() {
		return this.dividendMoney;
	}

	public void setDividendMoney(double dividendMoney) {
		this.dividendMoney = dividendMoney;
	}

	@Column(name = "type", nullable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "upid", nullable = false)
	public int getUpid() {
		return this.upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
	}

	@Column(name = "upids")
	public String getUpids() {
		return this.upids;
	}

	public void setUpids(String upids) {
		this.upids = upids;
	}

	@Column(name = "code", nullable = false)
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Column(name = "locate_point", nullable = false, precision = 11, scale = 2)
	public double getLocatePoint() {
		return this.locatePoint;
	}

	public void setLocatePoint(double locatePoint) {
		this.locatePoint = locatePoint;
	}

	@Column(name = "not_locate_point", nullable = false, precision = 11, scale = 2)
	public double getNotLocatePoint() {
		return this.notLocatePoint;
	}

	public void setNotLocatePoint(double notLocatePoint) {
		this.notLocatePoint = notLocatePoint;
	}

	@Column(name = "code_type", nullable = false)
	public int getCodeType() {
		return codeType;
	}

	public void setCodeType(int codeType) {
		this.codeType = codeType;
	}

	@Column(name = "extra_point", nullable = false, precision = 11, scale = 3)
	public double getExtraPoint() {
		return this.extraPoint;
	}

	public void setExtraPoint(double extraPoint) {
		this.extraPoint = extraPoint;
	}

	@Column(name = "withdraw_name", length = 32)
	public String getWithdrawName() {
		return this.withdrawName;
	}

	public void setWithdrawName(String withdrawName) {
		this.withdrawName = withdrawName;
	}

	@Column(name = "withdraw_password", length = 32)
	public String getWithdrawPassword() {
		return this.withdrawPassword;
	}

	public void setWithdrawPassword(String withdrawPassword) {
		this.withdrawPassword = withdrawPassword;
	}

	@Column(name = "regist_time", nullable = false, length = 19)
	public String getRegistTime() {
		return this.registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	@Column(name = "login_time", length = 19)
	public String getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	@Column(name = "lock_time", length = 19)
	public String getLockTime() {
		return lockTime;
	}

	public void setLockTime(String lockTime) {
		this.lockTime = lockTime;
	}

	@Column(name = "a_status", nullable = false)
	public int getAStatus() {
		return this.AStatus;
	}

	public void setAStatus(int AStatus) {
		this.AStatus = AStatus;
	}

	@Column(name = "b_status", nullable = false)
	public int getBStatus() {
		return this.BStatus;
	}

	public void setBStatus(int BStatus) {
		this.BStatus = BStatus;
	}

	@Column(name = "message")
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Column(name = "session_id", length = 128)
	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "online_status", nullable = false)
	public int getOnlineStatus() {
		return this.onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	@Column(name = "allow_equal_code", nullable = false)
	public int getAllowEqualCode() {
		return this.allowEqualCode;
	}

	public void setAllowEqualCode(int allowEqualCode) {
		this.allowEqualCode = allowEqualCode;
	}

	@Column(name = "allow_transfers", nullable = false)
	public int getAllowTransfers() {
		return this.allowTransfers;
	}

	public void setAllowTransfers(int allowTransfers) {
		this.allowTransfers = allowTransfers;
	}

	@Column(name = "login_validate", nullable = false)
	public int getLoginValidate() {
		return loginValidate;
	}

	public void setLoginValidate(int loginValidate) {
		this.loginValidate = loginValidate;
	}

	@Column(name = "bind_status", nullable = false)
	public int getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(int bindStatus) {
		this.bindStatus = bindStatus;
	}

	@Column(name = "vip_level", nullable = false)
	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	@Column(name = "integral", nullable = false, precision = 16, scale = 5)
	public double getIntegral() {
		return integral;
	}

	public void setIntegral(double integral) {
		this.integral = integral;
	}

	@Column(name = "secret_key")
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Column(name = "is_bind_google", nullable = false)
	public int getIsBindGoogle() {
		return isBindGoogle;
	}

	public void setIsBindGoogle(int isBindGoogle) {
		this.isBindGoogle = isBindGoogle;
	}

	@Column(name = "related_upid")
	public int getRelatedUpid() {
		return relatedUpid;
	}

	public void setRelatedUpid(int relatedUpid) {
		this.relatedUpid = relatedUpid;
	}

	@Column(name = "related_lowers")
	public String getRelatedLowers() {
		return relatedLowers;
	}

	public void setRelatedLowers(String relatedLowers) {
		this.relatedLowers = relatedLowers;
	}

	@Column(name = "related_point", nullable = false, precision = 3, scale = 2)
	public double getRelatedPoint() {
		return relatedPoint;
	}

	public void setRelatedPoint(double relatedPoint) {
		this.relatedPoint = relatedPoint;
	}

	@Column(name = "related_users")
	public String getRelatedUsers() {
		return relatedUsers;
	}

	public void setRelatedUsers(String relatedUsers) {
		this.relatedUsers = relatedUsers;
	}
	
	@Column(name = "allow_withdraw", nullable = false)
	public int getAllowWithdraw() {
		return allowWithdraw;
	}

	public void setAllowWithdraw(int allowWithdraw) {
		this.allowWithdraw = allowWithdraw;
	}
	@Column(name = "allow_platform_transfers", nullable = false)
	public int getAllowPlatformTransfers() {
		return allowPlatformTransfers;
	}

	public void setAllowPlatformTransfers(int allowPlatformTransfers) {
		this.allowPlatformTransfers = allowPlatformTransfers;
	}

	@Column(name = "is_cj_zhao_shang")
	public int getIsCjZhaoShang() {
		return isCjZhaoShang;
	}

	public void setIsCjZhaoShang(int isCjZhaoShang) {
		this.isCjZhaoShang = isCjZhaoShang;
	}
}