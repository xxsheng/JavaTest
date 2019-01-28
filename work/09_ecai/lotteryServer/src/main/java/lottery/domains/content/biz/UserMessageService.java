package lottery.domains.content.biz;

public interface UserMessageService {
	
	boolean send(int type, int toUid, int fromUid, String subject, String content);

	boolean updateInboxMessage(int userId, int[] ids, int status);

	boolean updateOutboxMessage(int userId, int[] ids, int status);
	
}