package lottery.domains.content.payment.lepay.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FCSOpenApiWxPayResponse extends FCSOpenApiResponse {

	@JsonProperty("wx_pay_sm_url")
	private String payWxSmUrl;

	@JsonProperty("wx_pay_hx_url")
	private String payWxHxUrl;

	public String getPayWxSmUrl() {
		return this.payWxSmUrl;
	}

	public void setPayWxSmUrl(String payWxSmUrl) {
		this.payWxSmUrl = payWxSmUrl;
	}

	public String getPayWxHxUrl() {
		return this.payWxHxUrl;
	}

	public void setPayWxHxUrl(String payWxHxUrl) {
		this.payWxHxUrl = payWxHxUrl;
	}
}