package lottery.domains.content.payment.RX;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author cavan
 *
 */
public class RXDaifuQueryResult {
	//商户号
    @JSONField(name = "userid")
    private String userid;
    //订单号
    @JSONField(name = "orderId")
    private String orderId;
    //查询成功：61
    @JSONField(name = "state")
    private String state;
    //返回1：代付失败 返回2：代付受理中 返回3：代付失败退回 返回4：代付成功
    @JSONField(name = "orderId_state")
    private String orderId_state;
    //订单金额
    @JSONField(name = "money")
    private String money;
    //订单手续费
    @JSONField(name = "fee")
    private String fee;
    //响应信息
    @JSONField(name = "message")
    private String message;
    //代付返回:ToQuery
    @JSONField(name = "type")
    private	String type;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
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

	public String getOrderId_state() {
		return orderId_state;
	}

	public void setOrderId_state(String orderId_state) {
		this.orderId_state = orderId_state;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
