package lottery.domains.content.payment.lepay.response;


public class FCSOpenApiMobilePayResponse extends FCSOpenApiResponse
{
  private String tradeId;

  public String getTradeId()
  {
    return this.tradeId;
  }

  public void setTradeId(String tradeId) {
    this.tradeId = tradeId;
  }
}