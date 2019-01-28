package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

public interface UserRechargeReadService {
	PageList search(int userId, Integer type, String billno, String sTime, String eTime, int start, int limit);
}