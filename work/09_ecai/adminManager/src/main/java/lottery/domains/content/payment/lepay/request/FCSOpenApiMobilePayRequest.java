package lottery.domains.content.payment.lepay.request;

import java.util.Map;


public class FCSOpenApiMobilePayRequest extends FCSOpenApiRequest
{
  private String outTradeNo;
  private String amountStr;
  private String note;
  private String remark;
  private String bankAccountName;
  private String bankAccountNo;
  private String cerNo;
  private String bankMobileNo;
  private String bankSn;
  private String bankName;
  private String isCredit;
  private String buyerIp;
  private String bankAccountType;
  private String busType;
  private String subject;

  public String getOutTradeNo()
  {
    return this.outTradeNo;
  }

  public void setOutTradeNo(String outTradeNo) {
    this.outTradeNo = outTradeNo;
  }

  public String getAmountStr() {
    return this.amountStr;
  }

  public void setAmountStr(String amountStr) {
    this.amountStr = amountStr;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getRemark() {
    return this.remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
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

  public String getCerNo() {
    return this.cerNo;
  }

  public void setCerNo(String cerNo) {
    this.cerNo = cerNo;
  }

  public String getBankMobileNo() {
    return this.bankMobileNo;
  }

  public void setBankMobileNo(String bankMobileNo) {
    this.bankMobileNo = bankMobileNo;
  }

  public String getBankSn() {
    return this.bankSn;
  }

  public void setBankSn(String bankSn) {
    this.bankSn = bankSn;
  }

  public String getBankName() {
    return this.bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getIsCredit() {
    return this.isCredit;
  }

  public void setIsCredit(String isCredit) {
    this.isCredit = isCredit;
  }

  public String getBuyerIp() {
    return this.buyerIp;
  }

  public void setBuyerIp(String buyerIp) {
    this.buyerIp = buyerIp;
  }

  public String getBankAccountType() {
    return this.bankAccountType;
  }

  public void setBankAccountType(String bankAccountType) {
    this.bankAccountType = bankAccountType;
  }

  public String getBusType() {
    return this.busType;
  }

  public void setBusType(String busType) {
    this.busType = busType;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Map<String, String> getTextParams()
  {
    Map params = getBaseTextParams();
    params.put("out_trade_no", this.outTradeNo);
    params.put("amount_str", this.amountStr);
    params.put("note", this.note);
    params.put("remark", this.remark);
    params.put("bank_account_name", this.bankAccountName);
    params.put("bank_account_no", this.bankAccountNo);
    params.put("cer_no", this.cerNo);
    params.put("bank_mobile_no", this.bankMobileNo);
    params.put("bank_sn", this.bankSn);
    params.put("bank_name", this.bankName);
    params.put("is_credit", this.isCredit);
    params.put("subject", this.subject);
    params.put("buyer_ip", this.buyerIp);
    params.put("bank_account_type", this.bankAccountType);
    params.put("bus_type", this.busType);
    return params;
  }
}