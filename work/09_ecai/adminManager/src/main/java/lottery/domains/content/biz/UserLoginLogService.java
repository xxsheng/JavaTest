package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface UserLoginLogService {
	
	PageList search(String username, String ip, String date,String loginLine, int start, int limit);
	
	PageList searchHistory(String username, String ip, String date,String loginLine, int start, int limit);
	
	PageList searchSameIp(String username, String ip, int start, int limit);

}