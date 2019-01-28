package lottery.domains.content.payment.lepay.request;

import java.util.Map;

public class FCSOpenApiAilpayPayRequest extends FCSOpenApiRequest {
	private String amount;
	private String remark;
	private String outTradeNo;
	private String aliPayType;
	private String subject;
	private String subBody;

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

	public String getAliPayType() {
		return aliPayType;
	}

	public void setAliPayType(String aliPayType) {
		this.aliPayType = aliPayType;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubBody() {
		return this.subBody;
	}

	public void setSubBody(String subBody) {
		this.subBody = subBody;
	}

	public Map<String, String> getTextParams() {
		Map map = getBaseTextParams();
		map.put("remark", this.remark);
		map.put("out_trade_no", this.outTradeNo);
		map.put("ali_pay_type", this.aliPayType);
		map.put("subject", this.subject);
		map.put("sub_body", this.subBody);
		map.put("amount_str", this.amount.toString());
		return map;
	}
}