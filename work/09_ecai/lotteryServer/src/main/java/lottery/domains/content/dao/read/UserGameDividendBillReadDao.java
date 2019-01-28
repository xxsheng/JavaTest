package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;

import java.util.List;

public interface UserGameDividendBillReadDao {
	PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit);

	PageList searchByLowers(int upUserId, String sTime, String eTime, int start, int limit);
}