package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;

import java.util.List;

public interface UserReadService {
	/**
	 * 通过id获取用户，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	User getByIdFromRead(int id);

	/**
	 * 通过用户名获取用户，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	User getByUsernameFromRead(String username);

	/**
	 * 获取所有用户，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> listAllFromRead();

	/**
	 * 查询某个用户的自己的注单
	 */
	PageList searchAll(Double minMoney, Double maxMoney,  int start, int limit);

	/**
	 * 查询某个用户的自己
	 */
	PageList searchByUserId(int userId);

	/**
	 * 查询某些用户
	 */
	PageList searchByUserIds(Integer[] userIds, Double minMoney, Double maxMoney,  int start, int limit);

	/**
	 * 查询某个用户的团队数据，包含用户自己
	 */
	PageList searchByTeam(int userId, Double minMoney, Double maxMoney,  int start, int limit);

	/**
	 * 查询某些用户的团队数据，包含用户自己
	 */
	PageList searchByTeam(int[] userIds, Double minMoney, Double maxMoney,  int start, int limit);


	/**
	 * 查询某个用户的直属团队数据，包含用户自己
	 */
	PageList searchByDirectTeam(int userId, Double minMoney, Double maxMoney,  int start, int limit);

	/**
	 * 团队在线搜索
	 */
	PageList teamOnlineList(int userId, int start, int limit);

	/**
	 * 团队在线搜索
	 */
	PageList teamOnlineList(Integer[] userIds, int start, int limit);

	/**
	 * 获取直属下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserDirectLowerFromRead(int id);

	/**
	 * 获取直属指定等级下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserDirectLowerFromRead(int id, int code);

	List<User> getUserDirectLowerFromRead(int id, int[] codes);

	UserTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

	List<TeamStatisticsDailyVO> statisticsByTeamDaily(int userId, String sDate, String eDate);

	UserTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

	List<TeamStatisticsDailyVO> statisticsByTeamDaily(int[] userIds, String sDate, String eDate);

	UserTeamStatisticsVO statisticsAll(String sDate, String eDate);

	List<TeamStatisticsDailyVO> statisticsAllDaily(String sDate, String eDate);

	/**
	 * 查找所有主管号
	 */
	List<User> findZhuGuans();

	/**
	 * 查找所有招商号
	 */
	List<User> findZhaoShangs(List<User> zhuGuans);

	/**
	 * 查找所有直属号
	 */
	List<User> findZhiShus(List<User> zhaoShang);
}