package admin.domains.content.biz;

import javautils.jdbc.PageList;

public interface AdminUserActionLogService {
	
	PageList search(String username, String actionId, String error, String sTime, String eTime, int start, int limit);
	
}