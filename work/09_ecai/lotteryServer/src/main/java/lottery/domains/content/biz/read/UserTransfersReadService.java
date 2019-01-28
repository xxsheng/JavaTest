package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

public interface UserTransfersReadService {
	PageList search(int userId, String billno, Integer type, String sTime, String eTime, int start, int limit);
}