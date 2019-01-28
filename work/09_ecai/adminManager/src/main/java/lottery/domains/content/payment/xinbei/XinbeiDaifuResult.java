package lottery.domains.content.payment.xinbei;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-05-28.
 */
public class XinbeiDaifuResult {
    @JSONField(name = "SettleType")
    private String SettleType; // 资金结算方式 0：自助结算；1：委托结算
    @JSONField(name = "UrgentType")
    private String UrgentType; // 转账方式 0：T+0；1：T+n
    @JSONField(name = "Amount")
    private String Amount; // 金额 单位：分
    @JSONField(name = "BankConfig")
    private String BankConfig; // 转账账户信息 为结算中银行卡相关信息
    @JSONField(name = "MerchantOrder")
    private String MerchantOrder; // 商户平台订单号
    @JSONField(name = "SerialNo")
    private String SerialNo; // 新贝平台流水号
    @JSONField(name = "Status")
    private String Status; // 订单状态 0：提现失败；1：提现成功
    @JSONField(name = "Msg")
    private String Msg; // 文本提示信息
    @JSONField(name = "MsgCode")
    private String MsgCode; // 消息的编号
    @JSONField(name = "Sin")
    private String Sin; // 签名

    public String getSettleType() {
        return SettleType;
    }

    public void setSettleType(String settleType) {
        SettleType = settleType;
    }

    public String getUrgentType() {
        return UrgentType;
    }

    public void setUrgentType(String urgentType) {
        UrgentType = urgentType;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getBankConfig() {
        return BankConfig;
    }

    public void setBankConfig(String bankConfig) {
        BankConfig = bankConfig;
    }

    public String getMerchantOrder() {
        return MerchantOrder;
    }

    public void setMerchantOrder(String merchantOrder) {
        MerchantOrder = merchantOrder;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getMsgCode() {
        return MsgCode;
    }

    public void setMsgCode(String msgCode) {
        MsgCode = msgCode;
    }

    public String getSin() {
        return Sin;
    }

    public void setSin(String sin) {
        Sin = sin;
    }
}
