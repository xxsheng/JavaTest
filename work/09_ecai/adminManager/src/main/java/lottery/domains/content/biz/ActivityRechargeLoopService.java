package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface ActivityRechargeLoopService {
	
	PageList search(String username, String date, Integer step, int start, int limit);

}