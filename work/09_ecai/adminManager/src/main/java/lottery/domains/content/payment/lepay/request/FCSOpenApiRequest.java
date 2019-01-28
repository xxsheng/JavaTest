package lottery.domains.content.payment.lepay.request;

import java.util.HashMap;
import java.util.Map;

public abstract class FCSOpenApiRequest
{
  protected String partner;
  protected String returnUrl;
  protected String service;
  protected String requestTime;

  public String getPartner()
  {
    return this.partner;
  }

  public void setPartner(String partner) {
    this.partner = partner;
  }

  public String getReturnUrl() {
    return this.returnUrl;
  }

  public void setReturnUrl(String returnUrl) {
    this.returnUrl = returnUrl;
  }

  public String getService() {
    return this.service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getRequestTime() {
    return this.requestTime;
  }

  public void setRequestTime(String requestTime) {
    this.requestTime = requestTime;
  }

  protected Map<String, String> getBaseTextParams() {
    Map map = new HashMap();
    map.put("partner", this.partner);
    map.put("return_url", this.returnUrl);
    map.put("service", this.service);
    map.put("request_time", this.requestTime);
    return map;
  }

  public abstract Map<String, String> getTextParams();
}