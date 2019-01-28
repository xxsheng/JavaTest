package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

public interface UserMessageReadService {
	PageList getInboxMessage(int userId, int start, int limit);
	
	PageList getOutboxMessage(int userId, int start, int limit);

	int getUnreadCount(int userId);
}