package lottery.domains.content.biz.read;

import lottery.domains.content.entity.User;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.user.UserGameReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserLotteryReportTeamStatisticsVO;
import lottery.web.helper.session.SessionUser;

import java.util.List;

/**
 * Created by Nick on 2016/12/28.
 */
public interface UserGameReportReadService {
    /**
     * 根据用户ID汇总团队报表，以时间为维度
     */
    List<UserGameReportVO> reportByTime(User user, String sDate, String eDate);

    /**
     * 根据用户ID汇总团队报表，以用户为维度
     */
    List<UserGameReportVO> reportByUser(User user, String sTime, String eTime);

    /**
     * 统计用户团队时间段内数据
     */
    UserGameReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

    /**
     * 统计用户团队时间段内数据
     */
    UserGameReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

    /**
     * 统计系统时间段内数据
     */
    UserGameReportTeamStatisticsVO statisticsAll(String sDate, String eDate);
}
