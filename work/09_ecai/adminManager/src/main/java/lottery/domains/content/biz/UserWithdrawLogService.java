package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserWithdrawLog;

public interface UserWithdrawLogService {
	
	boolean add(UserWithdrawLog entity);
	
	PageList search(String billno,String username, int start, int limit);
}