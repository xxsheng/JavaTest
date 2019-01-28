package lottery.domains.content.payment.lepay.request;

import java.util.Map;


public class FCSOpenApiAgentPayRequest extends FCSOpenApiRequest {
	private String outTradeNo;
	private String bankSn;
	private String bankSiteName;
	private String bankAccountName;
	private String bankAccountNo;
	private String bankMobileNo;
	private String bankProvince;
	private String bankCity;
	private String amountStr;
	private String remark;
	private String busType;
	private String userAgreement;

	public String getOutTradeNo() {
		return this.outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getBankSn() {
		return this.bankSn;
	}

	public void setBankSn(String bankSn) {
		this.bankSn = bankSn;
	}

	public String getBankSiteName() {
		return this.bankSiteName;
	}

	public void setBankSiteName(String bankSiteName) {
		this.bankSiteName = bankSiteName;
	}

	public String getBankAccountName() {
		return this.bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccountNo() {
		return this.bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getBankProvince() {
		return this.bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}

	public String getBankCity() {
		return this.bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
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

	public String getBusType() {
		return this.busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getUserAgreement() {
		return this.userAgreement;
	}

	public void setUserAgreement(String userAgreement) {
		this.userAgreement = userAgreement;
	}

	public String getBankMobileNo() {
		return this.bankMobileNo;
	}

	public void setBankMobileNo(String bankMobileNo) {
		this.bankMobileNo = bankMobileNo;
	}

	public Map<String, String> getTextParams() {
		Map map = getBaseTextParams();
		map.put("out_trade_no", this.outTradeNo);
		map.put("bank_sn", this.bankSn);
		map.put("bank_site_name", this.bankSiteName);
		map.put("bank_account_name", this.bankAccountName);
		map.put("bank_account_no", this.bankAccountNo);
		map.put("bank_province", this.bankProvince);
		map.put("bank_city", this.bankCity);
		map.put("amount_str", this.amountStr);
		map.put("remark", this.remark);
		map.put("bus_type", this.busType);
		map.put("user_agreement", this.userAgreement);
		map.put("bank_mobile_no", this.bankMobileNo);
		return map;
	}
}