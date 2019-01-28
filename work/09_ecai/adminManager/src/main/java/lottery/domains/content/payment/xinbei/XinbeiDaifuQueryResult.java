package lottery.domains.content.payment.xinbei;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-05-28.
 */
public class XinbeiDaifuQueryResult {
    @JSONField(name = "Amount")
    private String Amount; // 单位分的整数
    @JSONField(name = "ErrInfo")
    private String ErrInfo; // 返回信息描述
    @JSONField(name = "MerchantCode")
    private String MerchantCode; // 商户编号
    @JSONField(name = "MerchantOrder")
    private String MerchantOrder; // 商户订单号
    @JSONField(name = "sign")
    private String sign; // 签名
    @JSONField(name = "Status")
    private String Status; // 订单状态 00成功；50进行中；99失败
    @JSONField(name = "XbeiOrderId")
    private String XbeiOrderId; // 新贝平台订单号

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getErrInfo() {
        return ErrInfo;
    }

    public void setErrInfo(String errInfo) {
        ErrInfo = errInfo;
    }

    public String getMerchantCode() {
        return MerchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        MerchantCode = merchantCode;
    }

    public String getMerchantOrder() {
        return MerchantOrder;
    }

    public void setMerchantOrder(String merchantOrder) {
        MerchantOrder = merchantOrder;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getXbeiOrderId() {
        return XbeiOrderId;
    }

    public void setXbeiOrderId(String xbeiOrderId) {
            XbeiOrderId = xbeiOrderId;
        }
}
