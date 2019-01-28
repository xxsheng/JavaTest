package lottery.domains.content.payment.yr;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class YRDaifuQueryResult {

	//code	消息编码	0000：成功，1：失败
	@JSONField(name = "resultCode")
    private String resultCode;
    @JSONField(name = "outTradeNo")
    private String outTradeNo;
    @JSONField(name = "remitStatus")
    private String remitStatus;
    @JSONField(name = "settAmount")
    private String settAmount;
    @JSONField(name = "completeDate")
    private String completeDate;
    @JSONField(name = "errMsg")
    private String errMsg;
    @JSONField(name = "sign")
    private String sign;
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getRemitStatus() {
		return remitStatus;
	}
	public void setRemitStatus(String remitStatus) {
		this.remitStatus = remitStatus;
	}
	public String getSettAmount() {
		return settAmount;
	}
	public void setSettAmount(String settAmount) {
		this.settAmount = settAmount;
	}
	public String getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	

    
}
