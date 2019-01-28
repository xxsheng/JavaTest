package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

public interface UserBillReadService {
	/**
	 * 查询所有账单
	 */
	PageList searchAll(Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某个用户的自己的账单
	 */
	PageList searchByUserId(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某个用户的团队账单，包含用户自己
	 */
	PageList searchByTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某个用户的团队账单，包含用户自己
	 */
	PageList searchByDirectTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某些用户的团队账单，包含用户自己
	 */
	PageList searchByTeam(Integer[] userIds, Integer account, Integer type, String sTime, String eTime, int start, int limit);
}