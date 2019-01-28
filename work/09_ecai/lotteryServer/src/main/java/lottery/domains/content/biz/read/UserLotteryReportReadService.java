package lottery.domains.content.biz.read;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserLotteryReportTeamStatisticsVO;

import java.util.List;

public interface UserLotteryReportReadService {
	/**
	 * 根据用户ID汇总团队报表，以时间为维度
	 */
	List<UserLotteryReportVO> reportByTime(User user, String sDate, String eDate);

	/**
	 * 根据用户ID汇总团队报表，以用户为维度
	 */
	List<UserLotteryReportVO> reportByUser(User user, String sTime, String eTime);

	List<?> listAmountGroupByUserIds(Integer[] ids, String sTime, String eTime);

	/**
	 * 获取用户自己的报表，以时间为维度，并不总计
	 */
	List<UserLotteryReport> getSelfReportByTime(int userId, String sDate, String eDate);

	/**
	 * 获取用户自己的报表并总计
	 */
	UserLotteryReportVO getSelfReport(int userId, String sTime, String eTime);

	/**
	 * 统计用户团队时间段内数据
	 */
	UserLotteryReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

	/**
	 * 统计用户团队时间段内数据，每天数据
	 */
	List<TeamStatisticsDailyVO> statisticsByTeamDaily(int userId, String sDate, String eDate);

	/**
	 * 统计用户团队时间段内数据
	 */
	UserLotteryReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

	/**
	 * 统计用户团队时间段内数据，每天数据
	 */
	List<TeamStatisticsDailyVO> statisticsByTeamDaily(int[] userIds, String sDate, String eDate);

	/**
	 * 统计系统时间段内数据，每天数据
	 */
	UserLotteryReportTeamStatisticsVO statisticsAll(String sDate, String eDate);

	/**
	 * 统计系统时间段内数据
	 */
	List<TeamStatisticsDailyVO> statisticsAllDaily(String sDate, String eDate);
}