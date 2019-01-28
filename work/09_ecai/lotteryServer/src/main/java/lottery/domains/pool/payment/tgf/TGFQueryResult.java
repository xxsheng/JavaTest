package lottery.domains.pool.payment.tgf;

/**
 * Created by Nick on 2017/11/23.
 */
public class TGFQueryResult {
    private String amount; // 交易金额
    private String createdTime; // 订单创建时间
    private String feeAmount; // 手续费以元为单位
    private String outTradeNo; // 商户订单号
    private String status; // 订单状态：固定值，wait：等待支付，completed：支付成功，failed：支付失
    private String subject; // 商品名称
    private String timestamp; // 时间格式 yyyy-MM-dd HH:mm:ss
    private String tradeDate; // 交易日期
    private String tradeNo; // 我司交易号
    private String tradeType; // 交易类型
    private String respCode; // 错误代码， S0001：请求已成功执行； F9999：服务器系统繁忙；F2001：查不到此订单；
    private String respMessage; // 错误消息

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }
}
