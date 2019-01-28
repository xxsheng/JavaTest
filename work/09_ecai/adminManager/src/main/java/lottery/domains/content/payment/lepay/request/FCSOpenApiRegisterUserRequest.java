package lottery.domains.content.payment.lepay.request;

import java.util.Map;


public class FCSOpenApiRegisterUserRequest extends FCSOpenApiRequest
{
  private String bankAccountName;
  private String cerNo;
  private String bankSn;
  private String bankAccountNo;
  private String bankMobileNo;
  private String remark;

  public String getBankAccountName()
  {
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

  public String getBankAccountNo() {
    return this.bankAccountNo;
  }

  public void setBankAccountNo(String bankAccountNo) {
    this.bankAccountNo = bankAccountNo;
  }

  public String getBankMobileNo() {
    return this.bankMobileNo;
  }

  public void setBankMobileNo(String bankMobileNo) {
    this.bankMobileNo = bankMobileNo;
  }

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Map<String, String> getTextParams()
  {
    Map map = getBaseTextParams();
    map.put("bank_account_name", this.bankAccountName);
    map.put("cer_no", this.cerNo);
    map.put("bank_sn", this.bankSn);
    map.put("bank_account_no", this.bankAccountNo);
    map.put("bank_mobile_no", this.bankMobileNo);
    map.put("remark", this.remark);
    return map;
  }
}