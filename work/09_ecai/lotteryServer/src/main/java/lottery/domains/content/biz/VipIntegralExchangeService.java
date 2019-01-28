package lottery.domains.content.biz;

import lottery.web.WebJSON;

public interface VipIntegralExchangeService {
	
	boolean doExchange(WebJSON json, int userId, int integral);
	
}