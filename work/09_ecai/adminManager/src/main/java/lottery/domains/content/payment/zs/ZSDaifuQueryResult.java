package lottery.domains.content.payment.zs;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class ZSDaifuQueryResult {
    @JSONField(name = "merchantCode")
    private String merchantCode;
    @JSONField(name = "outOrderId")
    private String outOrderId;
    @JSONField(name = "orderId")
    private String orderId;
    @JSONField(name = "state")
    private String state;
    @JSONField(name = "transTime")
    private String transTime;
    @JSONField(name = "totalAmount")
    private Long totalAmount;
    @JSONField(name = "fee")
    private Long fee;
    @JSONField(name = "errorMsg")
    private String errorMsg;
    @JSONField(name = "sign")
    private String sign;
    
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public String getOutOrderId() {
		return outOrderId;
	}
	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public Long getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Long getFee() {
		return fee;
	}
	public void setFee(Long fee) {
		this.fee = fee;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
}
