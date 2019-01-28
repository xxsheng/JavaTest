package lottery.domains.content.payment.lepay.request;

import java.util.Map;

public class FCSOpenApiBankPayRequest extends FCSOpenApiRequest {
	private String amount;
	private String remark;
	private String outTradeNo;
	private String tran_ip;
	private String good_name;
	private String goods_detail;
	private String input_charset;
	private String sign_type;

	public String getAmount() {
		return this.amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOutTradeNo() {
		return this.outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTran_ip() {
		return tran_ip;
	}

	public void setTran_ip(String tran_ip) {
		this.tran_ip = tran_ip;
	}

	public String getGood_name() {
		return good_name;
	}

	public void setGood_name(String good_name) {
		this.good_name = good_name;
	}

	public String getGoods_detail() {
		return goods_detail;
	}

	public void setGoods_detail(String goods_detail) {
		this.goods_detail = goods_detail;
	}
	
	public String getInput_charset() {
		return input_charset;
	}

	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public Map<String, String> getTextParams() {
		Map map = getBaseTextParams();
		map.put("remark", this.remark);
		map.put("out_trade_no", this.outTradeNo);
		map.put("tran_ip", this.tran_ip);
		map.put("good_name", this.good_name);
		map.put("goods_detail", this.goods_detail);
		map.put("input_charset", this.input_charset);
		map.put("sign_type", this.sign_type);
		map.put("amount_str", this.amount.toString());
		return map;
	}
}
