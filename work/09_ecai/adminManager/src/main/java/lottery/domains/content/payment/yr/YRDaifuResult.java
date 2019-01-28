package lottery.domains.content.payment.yr;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class YRDaifuResult {
	
    @JSONField(name = "resultCode")
    private String resultCode;
    
    @JSONField(name = "outTradeNo")
    private String outOrderId;
    
    @JSONField(name = "remitStatus")
    private String remitStatus;
    @JSONField(name = "orderPrice")
    private Double orderPrice;
    @JSONField(name = "sign")
    private String sign;
    @JSONField(name = "errMsg")
    private String errMsg;
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getOutOrderId() {
		return outOrderId;
	}
	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}
	public String getRemitStatus() {
		return remitStatus;
	}
	public void setRemitStatus(String remitStatus) {
		this.remitStatus = remitStatus;
	}

	public Double getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
    
	
}
