package lottery.domains.content.dao.read;

import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.vo.bill.UserLotteryReportRichVO;
import lottery.domains.content.vo.user.UserLotteryReportTeamStatisticsVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 *
 */
public interface UserLotteryReportReadDao {
	List<?> listAmount(int[] ids, String sTime, String eTime);

	List<?> listAmountGroupByUserIds(Integer[] ids, String sTime, String eTime);

	List<UserLotteryReport> find(List<Criterion> criterions, List<Order> orders);

	/**
	 * 搜索时间段内所有人的彩票报表，以时间为维度
	 */
	List<UserLotteryReport> getDayListAll(String sDate, String eDate);

	/**
	 * 搜索时间段内某个用户团队的彩票报表，以时间为维度
	 */
	List<UserLotteryReport> getDayListByTeam(int userId, String sDate, String eDate);

	/**
	 * 搜索时间段内某些用户团队的彩票报表，以时间为维度
	 */
	List<UserLotteryReport> getDayListByTeam(int[] userIds, String sDate, String eDate);

	/**
	 * 搜索时间段内所有人的彩票报表，以用户为维度
	 */
	List<UserLotteryReportRichVO> searchAll(String sTime, String eTime);

	/**
	 * 搜索时间段内某个用户团队的游戏报表，以用户为维度
	 */
	List<UserLotteryReportRichVO> searchByTeam(int userId, String sTime, String eTime);

	/**
	 * 搜索时间段内某些用户团队的游戏报表，以用户为维度
	 */
	List<UserLotteryReportRichVO> searchByTeam(int[] userIds, String sTime, String eTime);

	/**
	 * 统计用户团队时间段内数据
	 */
	UserLotteryReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

	/**
	 * 统计用户团队时间段内数据
	 */
	UserLotteryReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

	/**
	 * 统计系统时间段内数据
	 */
	UserLotteryReportTeamStatisticsVO statisticsAll(String sDate, String eDate);

}