package lottery.domains.pool.payment.wf;

import com.alibaba.fastjson.annotation.JSONField;

public class WFPayQueryResult {
    @JSONField(name = "retCode")
    private String isSuccess;

    @JSONField(name = "sign")
    private String sign;

    @JSONField(name = "merchant_code")
    private String merchantCode;

    @JSONField(name = "orderId")
    private String orderNo;

    @JSONField(name = "amt")
    private String orderAmount;

    @JSONField(name = "order_time")
    private String orderTime;

    @JSONField(name = "merchOrderId")
    private String tradeNo;

    @JSONField(name = "trade_time")
    private String tradeTime;

    @JSONField(name = "status")
    private String tradeStatus;

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }
}
