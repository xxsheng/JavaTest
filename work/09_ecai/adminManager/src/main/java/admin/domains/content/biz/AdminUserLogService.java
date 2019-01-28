package admin.domains.content.biz;

import javautils.jdbc.PageList;

public interface AdminUserLogService {

	PageList search(String username, String ip, String keyword, String sDate, String eDate, int start, int limit);
	
}