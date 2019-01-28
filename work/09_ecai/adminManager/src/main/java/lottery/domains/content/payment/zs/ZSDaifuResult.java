package lottery.domains.content.payment.zs;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class ZSDaifuResult {
    @JSONField(name = "merchantCode")
    private String merchantCode;
    @JSONField(name = "outOrderId")
    private String outOrderId;
    @JSONField(name = "orderId")
    private String orderId;
    @JSONField(name = "totalAmount")
    private Long totalAmount;
    @JSONField(name = "fee")
    private Long fee;
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
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
}
