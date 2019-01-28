package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserReadDao {
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
	 * 获取直属下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserDirectLowerFromRead(int id);

	/**
	 * 获取直属指定等级下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserDirectLowerFromRead(int id, int code);

	/**
	 * 获取直属指定等级下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserDirectLowerFromRead(int id, int[] codes);

	/**
	 * 获取所有下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserLowerFromRead(int id);

	/**
	 * 获取所有下级，注意这是读方法，有可能会跟主库数据不一致，切不可用作判断余额的依据
	 */
	List<User> getUserLowerFromRead(List<Integer> userIds);

	/**
	 * 列出用户
	 */
	List<User> listFromRead(List<Criterion> criterions, List<Order> orders);

	/**
	 * 分页查询
	 */
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	UserTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

	UserTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

	UserTeamStatisticsVO statisticsAll(String sDate, String eDate);

	/**
	 * 搜索时间段内总日注册人数，以时间为维度
	 */
	List<?> getDayRegistAll(String sTime, String eTime);

	/**
	 * 搜索时间段内某个用户团队总日注册人数，以时间为维度
	 */
	List<?> getDayRegistByTeam(int userId, String sTime, String eTime);

	/**
	 * 搜索时间段内某些用户团队总日注册人数，以时间为维度
	 */
	List<?> getDayRegistByTeam(int[] userIds, String sTime, String eTime);
}