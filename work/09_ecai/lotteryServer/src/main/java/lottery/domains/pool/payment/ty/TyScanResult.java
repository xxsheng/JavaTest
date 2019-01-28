package lottery.domains.pool.payment.ty;


public class TyScanResult {
    private String Status; // 状态 0失败 1 成功
    private String Msg; // 返回消息
    private String PayWay;//通道 101支付宝 102
    private String QRImg;//	二维码图片
    private String QRTxt;//二维码解析路径
    private String OrderID;//千应平台订单号
    private String UOrderID;//商户订单号
    private String Money;//支付金额
    private String RealMoney;//实际支付金额
    private String GoFalse;//支付失败跳转
    private String GoTrue;//支付成功跳转
    private String TimeOut;//超时秒 已ctime开始
    private String CTime;//订单创建时间

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	public String getPayWay() {
		return PayWay;
	}

	public void setPayWay(String payWay) {
		PayWay = payWay;
	}

	public String getQRImg() {
		return QRImg;
	}

	public void setQRImg(String QRImg) {
		this.QRImg = QRImg;
	}

	public String getQRTxt() {
		return QRTxt;
	}

	public void setQRTxt(String QRTxt) {
		this.QRTxt = QRTxt;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getUOrderID() {
		return UOrderID;
	}

	public void setUOrderID(String UOrderID) {
		this.UOrderID = UOrderID;
	}

	public String getMoney() {
		return Money;
	}

	public void setMoney(String money) {
		Money = money;
	}

	public String getRealMoney() {
		return RealMoney;
	}

	public void setRealMoney(String realMoney) {
		RealMoney = realMoney;
	}

	public String getGoFalse() {
		return GoFalse;
	}

	public void setGoFalse(String goFalse) {
		GoFalse = goFalse;
	}

	public String getGoTrue() {
		return GoTrue;
	}

	public void setGoTrue(String goTrue) {
		GoTrue = goTrue;
	}

	public String getTimeOut() {
		return TimeOut;
	}

	public void setTimeOut(String timeOut) {
		TimeOut = timeOut;
	}

	public String getCTime() {
		return CTime;
	}

	public void setCTime(String CTime) {
		this.CTime = CTime;
	}
}
