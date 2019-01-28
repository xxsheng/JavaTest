package lottery.domains.pool.payment.rx;

public class RXResult {
	
	private String message;//扫码 请求响应信息 
    private String respInfo; // 请求响应信息
    private String amount; // 支付金额
    private String orderId; // 支付订单号
    private String url; // 二维码支付地址
    private String respCode;//支付成功：0000
    private String type;//H5快捷网银填写：kkwwo  扫码微信：kowx 支付宝：koali  QQ：koqq
    private String userid; // 请求商户号
    private String state;//扫码 请求成功：59  
    
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRespInfo() {
		return respInfo;
	}
	public void setRespInfo(String respInfo) {
		this.respInfo = respInfo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
    
    
}
