package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

public interface UserSysMessageReadService {
	PageList search(int userId, int start, int limit);

	int getUnreadCount(int userId);
}