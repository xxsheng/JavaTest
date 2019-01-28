package lottery.domains.content.payment.lepay.request;

import java.util.Map;

public class FCSOpenApiTradeRecordRequest extends FCSOpenApiRequest
{
  private String tradeId;
  private String outTradeNo;

  public String getTradeId()
  {
    return this.tradeId;
  }

  public void setTradeId(String tradeId) {
    this.tradeId = tradeId;
  }

  public String getOutTradeNo() {
    return this.outTradeNo;
  }

  public void setOutTradeNo(String outTradeNo) {
    this.outTradeNo = outTradeNo;
  }

  public Map<String, String> getTextParams()
  {
    Map map = getBaseTextParams();
    map.put("trade_id", this.tradeId);
    map.put("out_trade_no", this.outTradeNo);
    return map;
  }
}