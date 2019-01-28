package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface ActivityCostService {
	
	PageList searchBill(String username, String date, int start, int limit);
}
