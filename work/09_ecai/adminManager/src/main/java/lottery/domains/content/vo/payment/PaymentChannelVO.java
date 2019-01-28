package lottery.domains.content.vo.payment;

import lottery.domains.content.entity.PaymentChannel;

/**
 * Created by Nick on 2017/12/8.
 */
public class PaymentChannelVO {
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
    private int status; // 状态：0：启用；-1：禁用
    private int sequence; // 排序号
    private String maxRegisterTime; // 用户最大注册时间
    private String qrUrlCode; // 二维码,base64编码
    private int fixedQRAmount; // 是否是固定金额二维码类型 1：是；2：不是
    private int type; // 支付类型；1：网银充值；2：手机充值；3：管理员增；4：上下级转账；
    private int subType; // 支付子类型；1：网银充值(1：网银在线；2：网银转账；3：快捷支付；4：银联扫码；5：网银扫码转账；)；2：手机充值（1：微信在线；2：微信扫码转账；3：支付宝在线；4：支付宝扫码转账；5：QQ在线、6：QQ扫码转账；7：京东钱包）；3：系统充值（1：充值未到账；2：优惠活动；3：管理员增；4：管理员减)；4：上下级转账(1：转入、2：转出)
    private double consumptionPercent; // 消费比例，百分比
    private String whiteUsernames; // 白名单用户列表
    private String startTime; // 开放开始时间
    private String endTime; // 开放结束时间
    private int addMoneyType; // 上分类型，1：自动；2：手动
    private String apiPayBankChannelCode; // API代付时使用哪个通道的银行编码
    private int apiPayStatus; // 是否开启API代付，1：开启：其它：未开启；

    public PaymentChannelVO(PaymentChannel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.mobileName = channel.getMobileName();
        this.frontName = channel.getFrontName();
        this.channelCode = channel.getChannelCode();
        this.merCode = channel.getMerCode();
        this.totalCredits = channel.getTotalCredits();
        this.usedCredits = channel.getUsedCredits();
        this.minTotalRecharge = channel.getMinTotalRecharge();
        this.maxTotalRecharge = channel.getMaxTotalRecharge();
        this.minUnitRecharge = channel.getMinUnitRecharge();
        this.maxUnitRecharge = channel.getMaxUnitRecharge();
        this.status = channel.getStatus();
        this.sequence = channel.getSequence();
        this.maxRegisterTime = channel.getMaxRegisterTime();
        this.qrUrlCode = channel.getQrUrlCode();
        this.fixedQRAmount = channel.getFixedQRAmount();
        this.type = channel.getType();
        this.subType = channel.getSubType();
        this.consumptionPercent = channel.getConsumptionPercent();
        this.whiteUsernames = channel.getWhiteUsernames();
        this.startTime = channel.getStartTime();
        this.endTime = channel.getEndTime();
        this.addMoneyType = channel.getAddMoneyType();
        this.apiPayBankChannelCode = channel.getApiPayBankChannelCode();
        this.apiPayStatus = channel.getApiPayStatus();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public String getFrontName() {
        return frontName;
    }

    public void setFrontName(String frontName) {
        this.frontName = frontName;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getMerCode() {
        return merCode;
    }

    public void setMerCode(String merCode) {
        this.merCode = merCode;
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(double totalCredits) {
        this.totalCredits = totalCredits;
    }

    public double getUsedCredits() {
        return usedCredits;
    }

    public void setUsedCredits(double usedCredits) {
        this.usedCredits = usedCredits;
    }

    public double getMinTotalRecharge() {
        return minTotalRecharge;
    }

    public void setMinTotalRecharge(double minTotalRecharge) {
        this.minTotalRecharge = minTotalRecharge;
    }

    public double getMaxTotalRecharge() {
        return maxTotalRecharge;
    }

    public void setMaxTotalRecharge(double maxTotalRecharge) {
        this.maxTotalRecharge = maxTotalRecharge;
    }

    public double getMinUnitRecharge() {
        return minUnitRecharge;
    }

    public void setMinUnitRecharge(double minUnitRecharge) {
        this.minUnitRecharge = minUnitRecharge;
    }

    public double getMaxUnitRecharge() {
        return maxUnitRecharge;
    }

    public void setMaxUnitRecharge(double maxUnitRecharge) {
        this.maxUnitRecharge = maxUnitRecharge;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getMaxRegisterTime() {
        return maxRegisterTime;
    }

    public void setMaxRegisterTime(String maxRegisterTime) {
        this.maxRegisterTime = maxRegisterTime;
    }

    public String getQrUrlCode() {
        return qrUrlCode;
    }

    public void setQrUrlCode(String qrUrlCode) {
        this.qrUrlCode = qrUrlCode;
    }

    public int getFixedQRAmount() {
        return fixedQRAmount;
    }

    public void setFixedQRAmount(int fixedQRAmount) {
        this.fixedQRAmount = fixedQRAmount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public double getConsumptionPercent() {
        return consumptionPercent;
    }

    public void setConsumptionPercent(double consumptionPercent) {
        this.consumptionPercent = consumptionPercent;
    }

    public String getWhiteUsernames() {
        return whiteUsernames;
    }

    public void setWhiteUsernames(String whiteUsernames) {
        this.whiteUsernames = whiteUsernames;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getAddMoneyType() {
        return addMoneyType;
    }

    public void setAddMoneyType(int addMoneyType) {
        this.addMoneyType = addMoneyType;
    }

    public String getApiPayBankChannelCode() {
        return apiPayBankChannelCode;
    }

    public void setApiPayBankChannelCode(String apiPayBankChannelCode) {
        this.apiPayBankChannelCode = apiPayBankChannelCode;
    }

    public int getApiPayStatus() {
        return apiPayStatus;
    }

    public void setApiPayStatus(int apiPayStatus) {
        this.apiPayStatus = apiPayStatus;
    }
}
