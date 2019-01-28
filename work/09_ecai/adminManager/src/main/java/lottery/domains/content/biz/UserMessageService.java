package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.user.UserMessageVO;

public interface UserMessageService {
	
	PageList search(String toUser, String fromUser, String sTime, String eTime, int sortColoum, int start, int limit);
	
	UserMessageVO getById(int id);

	boolean delete(int id);
	
	boolean save(int id, String content);
	
}