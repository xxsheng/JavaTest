package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface VipIntegralExchangeService {
	
	PageList search(String username, String date, int start, int limit);
	
}