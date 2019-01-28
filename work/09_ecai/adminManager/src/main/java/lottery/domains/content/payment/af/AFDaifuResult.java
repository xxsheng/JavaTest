package lottery.domains.content.payment.af;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-09-04.
 */
public class AFDaifuResult {

	@JSONField(name = "result_code")
	private String result_code;

	@JSONField(name = "result_msg")
	private String result_msg;

	@JSONField(name = "merchant_no")
	private String merchant_no;
	
	@JSONField(name = "order_no")
	private String order_no;
	
	@JSONField(name = "mer_order_no")
	private String mer_order_no;
	
	@JSONField(name = "result")
	private String result;

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

	public String getMerchant_no() {
		return merchant_no;
	}

	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getMer_order_no() {
		return mer_order_no;
	}

	public void setMer_order_no(String mer_order_no) {
		this.mer_order_no = mer_order_no;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	

}
