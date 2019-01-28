package lottery.domains.pool.payment.cfg;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;


import net.sf.json.JSONObject;

public class RechargeResult {
	private String tradeId;
	private String redirectUrl;
	private String keyword;
	private String payUserName;
	private String message;
	private String signature;
	private String payMethod;
	private String payUrl;
	private int returnCode;//（-1：请求失败，0：验签失败，1：成功）
	private String errorMsg;
	private Date createTime;
	private JSONObject jsonValue;
	private String returnValue;//请求成功返回值
	private List<BasicNameValuePair> weChatParam;
	private HashMap<String, String> paramsMap;
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getPayUserName() {
		return payUserName;
	}

	public void setPayUserName(String payUserName) {
		this.payUserName = payUserName;
	}

	public JSONObject getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(JSONObject jsonValue) {
		this.jsonValue = jsonValue;
	}
	
	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public List<BasicNameValuePair> getWeChatParam() {
		return weChatParam;
	}

	public void setWeChatParam(List<BasicNameValuePair> weChatParam) {
		this.weChatParam = weChatParam;
	}

	public HashMap<String, String> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(HashMap<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}
	
}