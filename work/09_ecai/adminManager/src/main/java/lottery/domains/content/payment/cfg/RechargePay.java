package lottery.domains.content.payment.cfg;


import java.util.Date;

/**
 *支付结果返回数据
 */
public class RechargePay {
    private String billno;//订单编号
    private String tradeNo;//支付订单
    private double amount;//订单金额
    private String recvCardNo;//商户ID
    private String recvName;//商户名
    private Integer payBankId;//支付银行ID
    private String payBankName;//支付银行
    private Date payTime;//支付时间
    private int userId;//用户ID
    private String notifyType;//通知方式
    private String tradeStatus;//支付状态
	private String requestHost; // 请求地址
    
	public String getBillno() {
		return billno;
	}
	public void setBillno(String billno) {
		this.billno = billno;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getRecvCardNo() {
		return recvCardNo;
	}
	public void setRecvCardNo(String recvCardNo) {
		this.recvCardNo = recvCardNo;
	}
	public String getRecvName() {
		return recvName;
	}
	public void setRecvName(String recvName) {
		this.recvName = recvName;
	}
	public Integer getPayBankId() {
		return payBankId;
	}
	public void setPayBankId(Integer payBankId) {
		this.payBankId = payBankId;
	}
	public String getPayBankName() {
		return payBankName;
	}
	public void setPayBankName(String payBankName) {
		this.payBankName = payBankName;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	public String getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public String getRequestHost() {
		return requestHost;
	}
	public void setRequestHost(String requestHost) {
		this.requestHost = requestHost;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	
}
