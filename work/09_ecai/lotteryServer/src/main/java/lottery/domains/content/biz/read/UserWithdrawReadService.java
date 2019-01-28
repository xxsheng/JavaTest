package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

public interface UserWithdrawReadService {
	PageList search(int userId, String billno, String sTime, String eTime, Integer status, int start, int limit);
}