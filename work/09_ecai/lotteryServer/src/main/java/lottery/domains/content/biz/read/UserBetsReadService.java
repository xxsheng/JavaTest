package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.bets.UserBetsVO;

import java.util.List;

public interface UserBetsReadService {
	/**
	 * 获取订单详情，带号码
	 */
	UserBetsVO getByIdWithCodes(int id);

	/**
	 * 获取订单详情，不带号码
	 */
	UserBetsVO getByIdWithoutCodes(int id);

	/**
	 * 根据用户ID和ID获取订单详情，带号码
	 */
	UserBetsVO getByIdWithCodes(int id, int userId);

	/**
	 * 根据用户ID和ID获取订单详情，不带号码
	 */
	UserBetsVO getByIdWithoutCodes(int id, int userId);

	/**
	 * 快速获取用户最近的5条投注记录
	 */
	List<UserBetsVO> getRecentUserBets(int userId);

	/**
	 * 快速获取用户最近的5条追号投注记录
	 */
	List<UserBetsVO> getRecentChaseUserBets(int userId);

	/**
	 * 快速获取用户最近未开奖的5条投注记录
	 */
	List<UserBetsVO> getRecentUserBetsUnOpen(int userId);

	/**
	 * 快速获取用户最近的5条非未开奖的投注记录
	 */
	List<UserBetsVO> getRecentUserBetsOpened(int userId);

	/**
	 * 查询所有注单
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

	double getBillingOrder(int userId, String startTime, String endTime);

	/**
	 * 查询未结算金额
	 */
	double getUnSettleMoney(int userId, String startTime, String endTime);
}