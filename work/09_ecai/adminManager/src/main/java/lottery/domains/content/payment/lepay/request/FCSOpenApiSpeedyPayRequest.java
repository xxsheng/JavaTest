package lottery.domains.content.payment.lepay.request;

import java.util.Map;

public class FCSOpenApiSpeedyPayRequest extends FCSOpenApiRequest
{
  private String amount;
  private String remark;
  private String outTradeNo;
  private String subject;
  private String subBody;
  private String speedyPayType;

  public String getAmount()
  {
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

  public String getSpeedyPayType() {
    return this.speedyPayType;
  }

  public void setSpeedyPayType(String speedyPayType) {
    this.speedyPayType = speedyPayType;
  }

  public Map<String, String> getTextParams()
  {
    Map map = getBaseTextParams();
    map.put("remark", this.remark);
    map.put("out_trade_no", this.outTradeNo);
    map.put("subject", this.subject);
    map.put("sub_body", this.subBody);
    map.put("amount_str", this.amount.toString());
    map.put("speedy_pay_type", this.speedyPayType);
    return map;
  }
}