package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBets;

import java.util.List;

public interface UserBetsReadDao {
	/**
	 * 获取订单详情，带号码
	 */
	UserBets getByIdWithCodes(int id);

	/**
	 * 获取订单详情，不带号码
	 */
	UserBets getByIdWithoutCodes(int id);

	/**
	 * 根据用户ID和ID获取订单详情，带号码
	 */
	UserBets getByIdWithCodes(int id, int userId);

	/**
	 * 根据用户ID和ID获取订单详情，不带号码
	 */
	UserBets getByIdWithoutCodes(int id, int userId);

	/**
	 * 获取用户时间段内的消费金额
	 */
	double getBillingOrder(int userId, String startTime, String endTime);

	/**
	 * 查询未结算金额
	 */
	double getUnSettleMoney(int userId, String startTime, String endTime);

	/**
	 * 根据ID查找，不含号码
	 */
	List<UserBets> searchByIds(List<Integer> ids);

	/**
	 * 查询某个用户的自己的注单
	 */
	PageList searchAll(Integer type, Integer lotteryId,
							String expect, Integer status, String sTime, String eTime,
							int start, int limit);

	/**
	 * 查询某个用户的自己的注单
	 */
	PageList searchByUserId(int userId, Integer type, Integer lotteryId,
							String expect, Integer status, String sTime, String eTime,
							int start, int limit);

	/**
	 * 查询某个用户的团队注单，包含用户自己
	 */
	PageList searchByTeam(int userId, Integer type, Integer lotteryId,
						  String expect, Integer status, String sTime, String eTime,
						  int start, int limit);

	/**
	 * 查询某个用户的直属下级团队注单，包含用户自己
	 */
	PageList searchByDirectTeam(int userId, Integer type, Integer lotteryId,
						  String expect, Integer status, String sTime, String eTime,
						  int start, int limit);

	/**
	 * 查询某些用户的团队注单，包含用户自己
	 */
	PageList searchByTeam(Integer[] userIds, Integer type, Integer lotteryId,
						  String expect, Integer status, String sTime, String eTime,
						  int start, int limit);
}