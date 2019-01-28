package lottery.domains.content.payment.lepay.request;

import java.util.Map;


public class FCSOpenApiAgentDeductRequest extends FCSOpenApiRequest {
	private String bankAccountName;
	private String cerNo;
	private String bankSn;
	private String bankMbileNo;
	private String bankAccountNo;
	private String amountStr;
	private String remark;
	private String outTradeNo;
	private String openSource;

	public String getBankAccountName() {
		return this.bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getCerNo() {
		return this.cerNo;
	}

	public void setCerNo(String cerNo) {
		this.cerNo = cerNo;
	}

	public String getBankSn() {
		return this.bankSn;
	}

	public void setBankSn(String bankSn) {
		this.bankSn = bankSn;
	}

	public String getBankMbileNo() {
		return this.bankMbileNo;
	}

	public void setBankMbileNo(String bankMbileNo) {
		this.bankMbileNo = bankMbileNo;
	}

	public String getBankAccountNo() {
		return this.bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getAmountStr() {
		return this.amountStr;
	}

	public void setAmountStr(String amountStr) {
		this.amountStr = amountStr;
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

	public String getOpenSource() {
		return this.openSource;
	}

	public void setOpenSource(String openSource) {
		this.openSource = openSource;
	}

	public Map<String, String> getTextParams() {
		Map map = getBaseTextParams();
		map.put("bank_account_name", this.bankAccountName);
		map.put("cer_no", this.cerNo);
		map.put("bank_sn", this.bankSn);
		map.put("bank_account_no", this.bankAccountNo);
		map.put("bank_mobile_no", this.bankMbileNo);
		map.put("remark", this.remark);
		map.put("out_trade_no", this.outTradeNo);
		map.put("amount_str", this.amountStr);
		map.put("open_source", this.openSource);
		return map;
	}
}