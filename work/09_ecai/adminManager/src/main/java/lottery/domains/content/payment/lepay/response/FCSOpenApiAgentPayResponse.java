package lottery.domains.content.payment.lepay.response;

public class FCSOpenApiAgentPayResponse extends FCSOpenApiResponse
{
  private boolean success;
  private String errorCode;
  private String errorMsg;
  private String tradeId;

  public String getTradeId()
  {
    return this.tradeId;
  }

  public void setTradeId(String tradeId) {
    this.tradeId = tradeId;
  }

  public String getErrorMsg() {
    return this.errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}