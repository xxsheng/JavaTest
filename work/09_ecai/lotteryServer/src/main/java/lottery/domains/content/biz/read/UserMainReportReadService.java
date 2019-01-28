package lottery.domains.content.biz.read;

import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.vo.bill.UserMainReportVO;
import lottery.domains.content.vo.user.TeamStatisticsDailyVO;
import lottery.domains.content.vo.user.UserMainReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;
import lottery.domains.content.vo.user.UserVO;

import java.util.List;

public interface UserMainReportReadService {
	List<UserMainReportVO> report(User user, String sTime, String eTime);

	/**
	 * 获取用户自己的报表，以时间为维度，并不总计
	 */
	List<UserMainReport> getSelfReportByTime(int userId, String sDate, String eDate);

	UserMainReportVO getSelfReport(int userId, String sTime, String eTime);

	UserMainReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

	List<TeamStatisticsDailyVO> statisticsByTeamDaily(int userId, String sDate, String eDate);

	UserMainReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

	List<TeamStatisticsDailyVO> statisticsByTeamDaily(int[] userIds, String sDate, String eDate);

	UserMainReportTeamStatisticsVO statisticsAll(String sDate, String eDate);

	List<TeamStatisticsDailyVO> statisticsAllDaily(String sDate, String eDate);
}