package lottery.domains.content.payment.ht;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class HTPayResult {
    @JSONField(name = "is_success")
    private String isSuccess;
    @JSONField(name = "sign")
    private String sign;
    @JSONField(name = "errror_msg")
    private String errrorMsg;
    @JSONField(name = "trans_id")
    private String transId;
    @JSONField(name = "order_id")
    private String orderId;
    @JSONField(name = "bank_status")
    private String bankStatus;

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

    public String getErrrorMsg() {
        return errrorMsg;
    }

    public void setErrrorMsg(String errrorMsg) {
        this.errrorMsg = errrorMsg;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBankStatus() {
        return bankStatus;
    }

    public void setBankStatus(String bankStatus) {
        this.bankStatus = bankStatus;
    }
}
