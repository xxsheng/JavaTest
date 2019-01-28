package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 支付通道
 */
@Entity
@Table(name = "payment_channel", catalog = Database.name)
public class PaymentChannel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name; // 后台名称
	private String mobileName; // 手机端显示名称
	private String frontName; // 前端显示名称
	private String channelCode; // 通道编码
	private String merCode; // 商户编码
	private double totalCredits; // 总额度
	private double usedCredits; // 已用额度
	private double minTotalRecharge; // 累计最低充值
	private double maxTotalRecharge; // 累计最高充值
	private double minUnitRecharge; // 单次最低充值
	private double maxUnitRecharge; // 单次最高充值
	private String payUrl; // 支付跳转地址
	private String armourUrl; // 马甲地址
	private int status; // 状态：0：启用；-1：禁用
	private int sequence; // 排序号
	private String maxRegisterTime; // 用户最大注册时间
	private double thirdFee; // 第三方手续费手续费,百分比,如0.8，即千分之8
	private int thirdFeeFixed; // 第三方手续费是否固定,1：固定；0：不固定
	private String qrUrlCode; // 二维码,base64编码
	private int fixedQRAmount; // 是否是固定金额二维码类型 1：是；2：不是
	private int type; // 支付类型；1：网银充值；2：手机充值；3：管理员增；4：上下级转账；
	private int subType; // 支付子类型；1：网银充值(1：网银在线；2：网银转账；3：快捷支付；4：银联扫码；5：网银扫码转账；)；2：手机充值（1：微信在线；2：微信扫码转账；3：支付宝在线；4：支付宝扫码转账；5：QQ在线、6：QQ扫码转账；7：京东钱包）；3：系统充值（1：充值未到账；2：优惠活动；3：管理员增；4：管理员减)；4：上下级转账(1：转入、2：转出)
	private double consumptionPercent; // 消费比例，百分比
	private String whiteUsernames; // 白名单用户列表
	private String startTime; // 开放开始时间
	private String endTime; // 开放结束时间
	private int addMoneyType; // 上分类型，1：自动；2：手动
	private String md5Key; // MD5密钥
	private String rsaPublicKey; // RSA公钥
	private String rsaPrivateKey; // RSA私钥
	private String rsaPlatformPublicKey; // RSA平台公钥
	private String apiPayBankChannelCode; // API代付时使用哪个通道的银行编码
	private int apiPayStatus; // 是否开启API代付，1：开启：其它：未开启；
	private String ext1; // 扩展字段1
	private String ext2; // 扩展字段2
	private String ext3; // 扩展字段3,字段较大，可以存储较大数据

	/** default constructor */
	public PaymentChannel() {
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

	@Column(name = "name", nullable = false, length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "mobile_name", nullable = false, length = 64)
	public String getMobileName() {
		return mobileName;
	}

	public void setMobileName(String mobileName) {
		this.mobileName = mobileName;
	}

	@Column(name = "front_name", nullable = false, length = 64)
	public String getFrontName() {
		return frontName;
	}

	public void setFrontName(String frontName) {
		this.frontName = frontName;
	}

	@Column(name = "channel_code", nullable = false, length = 64)
	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Column(name = "mer_code", nullable = false)
	public String getMerCode() {
		return merCode;
	}

	public void setMerCode(String merCode) {
		this.merCode = merCode;
	}

	@Column(name = "total_credits", nullable = false, precision = 12, scale = 3)
	public double getTotalCredits() {
		return totalCredits;
	}

	public void setTotalCredits(double totalCredits) {
		this.totalCredits = totalCredits;
	}

	@Column(name = "used_credits", nullable = false, precision = 12, scale = 3)
	public double getUsedCredits() {
		return usedCredits;
	}

	public void setUsedCredits(double usedCredits) {
		this.usedCredits = usedCredits;
	}

	@Column(name = "min_total_recharge", nullable = false, precision = 12, scale = 3)
	public double getMinTotalRecharge() {
		return minTotalRecharge;
	}

	public void setMinTotalRecharge(double minTotalRecharge) {
		this.minTotalRecharge = minTotalRecharge;
	}

	@Column(name = "max_total_recharge", nullable = false, precision = 12, scale = 3)
	public double getMaxTotalRecharge() {
		return maxTotalRecharge;
	}

	public void setMaxTotalRecharge(double maxTotalRecharge) {
		this.maxTotalRecharge = maxTotalRecharge;
	}

	@Column(name = "min_unit_recharge", nullable = false, precision = 12, scale = 3)
	public double getMinUnitRecharge() {
		return minUnitRecharge;
	}

	public void setMinUnitRecharge(double minUnitRecharge) {
		this.minUnitRecharge = minUnitRecharge;
	}

	@Column(name = "max_unit_recharge", nullable = false, precision = 12, scale = 3)
	public double getMaxUnitRecharge() {
		return maxUnitRecharge;
	}

	public void setMaxUnitRecharge(double maxUnitRecharge) {
		this.maxUnitRecharge = maxUnitRecharge;
	}

	@Column(name = "pay_url")
	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	@Column(name = "armour_url")
	public String getArmourUrl() {
		return armourUrl;
	}

	public void setArmourUrl(String armourUrl) {
		this.armourUrl = armourUrl;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "sequence", nullable = false)
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Column(name = "max_register_time", length = 20)
	public String getMaxRegisterTime() {
		return maxRegisterTime;
	}

	public void setMaxRegisterTime(String maxRegisterTime) {
		this.maxRegisterTime = maxRegisterTime;
	}

	@Column(name = "third_fee", nullable = false, precision = 4, scale = 2)
	public double getThirdFee() {
		return thirdFee;
	}

	public void setThirdFee(double thirdFee) {
		this.thirdFee = thirdFee;
	}

	@Column(name = "third_fee_fixed")
	public int getThirdFeeFixed() {
		return thirdFeeFixed;
	}

	public void setThirdFeeFixed(int thirdFeeFixed) {
		this.thirdFeeFixed = thirdFeeFixed;
	}

	@Column(name = "qr_url_code", length = 3072)
	public String getQrUrlCode() {
		return qrUrlCode;
	}

	public void setQrUrlCode(String qrUrlCode) {
		this.qrUrlCode = qrUrlCode;
	}

	@Column(name = "fixed_qr_amount")
	public int getFixedQRAmount() {
		return fixedQRAmount;
	}

	public void setFixedQRAmount(int fixedQRAmount) {
		this.fixedQRAmount = fixedQRAmount;
	}

	@Column(name = "type", nullable = false)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "sub_type", nullable = false)
	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}

	@Column(name = "consumption_percent", nullable = false, precision = 4, scale = 2)
	public double getConsumptionPercent() {
		return consumptionPercent;
	}

	public void setConsumptionPercent(double consumptionPercent) {
		this.consumptionPercent = consumptionPercent;
	}

	@Column(name = "white_usernames", length = 2048)
	public String getWhiteUsernames() {
		return whiteUsernames;
	}

	public void setWhiteUsernames(String whiteUsernames) {
		this.whiteUsernames = whiteUsernames;
	}

	@Column(name = "start_time", length = 20)
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time", length = 20)
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "add_money_type", nullable = false)
	public int getAddMoneyType() {
		return addMoneyType;
	}

	public void setAddMoneyType(int addMoneyType) {
		this.addMoneyType = addMoneyType;
	}

	@Column(name = "md5_key", nullable = false, length = 512)
	public String getMd5Key() {
		return md5Key;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

	@Column(name = "rsa_public_key", length = 3072)
	public String getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(String rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	@Column(name = "rsa_private_key", length = 3072)
	public String getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(String rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}

	@Column(name = "rsa_platform_public_key", length = 3072)
	public String getRsaPlatformPublicKey() {
		return rsaPlatformPublicKey;
	}

	public void setRsaPlatformPublicKey(String rsaPlatformPublicKey) {
		this.rsaPlatformPublicKey = rsaPlatformPublicKey;
	}

	@Column(name = "api_pay_bank_channel_code", length = 64)
	public String getApiPayBankChannelCode() {
		return apiPayBankChannelCode;
	}

	public void setApiPayBankChannelCode(String apiPayBankChannelCode) {
		this.apiPayBankChannelCode = apiPayBankChannelCode;
	}

	@Column(name = "api_pay_status")
	public int getApiPayStatus() {
		return apiPayStatus;
	}

	public void setApiPayStatus(int apiPayStatus) {
		this.apiPayStatus = apiPayStatus;
	}

	@Column(name = "ext1", length = 512)
	public String getExt1() {
		return ext1;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	@Column(name = "ext2", length = 512)
	public String getExt2() {
		return ext2;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	@Column(name = "ext3", length = 3072)
	public String getExt3() {
		return ext3;
	}

	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
}