package lottery.domains.content.payment.lepay.request;

import java.util.Map;

public class FCSOpenApiSmsVerifyRequest extends FCSOpenApiRequest
{
  private String tradeId;
  private String veriCode;
  private String bankMobileNo;

  public String getTradeId()
  {
    return this.tradeId;
  }

  public void setTradeId(String tradeId) {
    this.tradeId = tradeId;
  }

  public String getVeriCode() {
    return this.veriCode;
  }

  public void setVeriCode(String veriCode) {
    this.veriCode = veriCode;
  }

  public String getBankMobileNo() {
    return this.bankMobileNo;
  }

  public void setBankMobileNo(String bankMobileNo) {
    this.bankMobileNo = bankMobileNo;
  }

  public Map<String, String> getTextParams()
  {
    Map params = getBaseTextParams();
    params.put("trade_id", this.tradeId);
    params.put("veri_code", this.veriCode);
    params.put("bank_mobile_no", this.bankMobileNo);
    return params;
  }
}