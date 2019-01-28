package lottery.domains.content.payment.af;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class AFDaifuQueryResult {

	//code	消息编码	0000：成功，1：失败
	@JSONField(name = "result_code")
    private String result_code;
    @JSONField(name = "result_msg")
    private String result_msg;
    @JSONField(name = "order_no")
    private String order_no;
    @JSONField(name = "merchant_no")
    private String merchant_no;
    @JSONField(name = "result")
    private String result;
    @JSONField(name = "amount")
    private String amount;
    @JSONField(name = "withdraw_fee")
    private String withdraw_fee;
    
    @JSONField(name = "sign")
    private String sign;

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getResult_msg() {
		return result_msg;
	}

	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getMerchant_no() {
		return merchant_no;
	}

	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getWithdraw_fee() {
		return withdraw_fee;
	}

	public void setWithdraw_fee(String withdraw_fee) {
		this.withdraw_fee = withdraw_fee;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


    
}
