package lottery.domains.content.dao.read;

import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.vo.bill.UserGameReportRichVO;
import lottery.domains.content.vo.user.UserGameReportTeamStatisticsVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface UserGameReportReadDao {
    List<UserGameReport> find(List<Criterion> criterions, List<Order> orders);

    /**
     * 搜索时间段内所有人的游戏总计报表，以时间为维度
     */
    List<UserGameReport> getDayListAll(String sDate, String eDate);

    /**
     * 搜索时间段内某个用户团队的游戏总计报表，以时间为维度
     */
    List<UserGameReport> getDayListByTeam(int userId, String sDate, String eDate);

    /**
     * 搜索时间段内某些用户团队的游戏总计报表，以时间为维度
     */
    List<UserGameReport> getDayListByTeam(int[] userIds, String sDate, String eDate);


    /**
     * 搜索时间段内所有人的游戏报表，以用户为维度
     */
    List<UserGameReportRichVO> searchAll(String sTime, String eTime);

    /**
     * 搜索时间段内某个用户团队的游戏报表，以用户为维度
     */
    List<UserGameReportRichVO> searchByTeam(int userId, String sTime, String eTime);

    /**
     * 搜索时间段内某些用户团队的游戏报表，以用户为维度
     */
    List<UserGameReportRichVO> searchByTeam(int[] userIds, String sTime, String eTime);

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
