package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface UserActionLogService {
	
	PageList search(String username, String ip, String keyword, String date, int start, int limit);

}