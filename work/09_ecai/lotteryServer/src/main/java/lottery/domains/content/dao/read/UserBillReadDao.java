package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBill;

public interface UserBillReadDao {
	UserBill getById(int id);

	/**
	 * 查询某个用户的自己的账单
	 */
	PageList searchByUserId(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询所有账单
	 */
	PageList searchAll(Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某个用户的团队账单，包含用户自己
	 */
	PageList searchByTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某个用户的直属下级团队账单，包含用户自己
	 */
	PageList searchByDirectTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit);

	/**
	 * 查询某些用户的团队账单，包含用户自己
	 */
	PageList searchByTeam(Integer[] userIds, Integer account, Integer type, String sTime, String eTime, int start, int limit);

}