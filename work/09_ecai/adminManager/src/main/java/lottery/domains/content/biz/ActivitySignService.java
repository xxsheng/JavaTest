package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface ActivitySignService {
	
	PageList searchBill(String username, String date, int start, int limit);
	
	PageList searchRecord(String username, int start, int limit);
	
}