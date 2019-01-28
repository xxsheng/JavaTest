package lottery.domains.content.payment.lepay.request;

import java.util.Map;


public class FCSOpenApiMobilePayResendRequest extends FCSOpenApiRequest
{
  private String tradeId;

  public String getTradeId()
  {
    return this.tradeId;
  }

  public void setTradeId(String tradeId) {
    this.tradeId = tradeId;
  }

  public Map<String, String> getTextParams()
  {
    Map params = getBaseTextParams();
    params.put("trade_id", this.tradeId);
    return params;
  }
}