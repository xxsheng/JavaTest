package lottery.domains.content.payment.lepay.request;

import java.util.Map;


public class FCSOpenApiRefundRequest extends FCSOpenApiRequest {
	private String tradeId;
	private String amountStr;

	public String getAmountStr() {
		return this.amountStr;
	}

	public void setAmountStr(String amountStr) {
		this.amountStr = amountStr;
	}

	public String getTradeId() {
		return this.tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public Map<String, String> getTextParams() {
		Map params = getBaseTextParams();
		params.put("trade_id", this.tradeId);
		params.put("amount_str", this.amountStr);
		return params;
	}
}