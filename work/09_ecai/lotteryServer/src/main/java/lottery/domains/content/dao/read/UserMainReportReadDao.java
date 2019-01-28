package lottery.domains.content.dao.read;

import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.vo.bill.UserMainReportRichVO;
import lottery.domains.content.vo.user.UserMainReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserTeamStatisticsVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserMainReportReadDao {
	List<UserMainReport> find(List<Criterion> criterions, List<Order> orders);

	double getTotalRecharge(int userId);

	/**
	 * 统计系统时间段内总计数据，每天数据
	 */
	List<UserMainReport> getDayListAll(String sDate, String eDate);

	/**
	 * 统计用户团队时间段内总计数据，每天数据
	 */
	List<UserMainReport> getDayListByTeam(int userId, String sDate, String eDate);

	/**
	 * 统计用户团队时间段内总计数据，每天数据
	 */
	List<UserMainReport> getDayListByTeam(int[] userIds, String sDate, String eDate);

	/**
	 * 统计系统时间段内总计数据，每个下级数据
	 */
	List<UserMainReportRichVO> searchAll(String sTime, String eTime);

	/**
	 * 统计用户团队时间段内总计数据，每个下级数据
	 */
	List<UserMainReportRichVO> searchByTeam(int userId, String sTime, String eTime);

	/**
	 * 统计用户团队时间段内总计数据，每个下级数据
	 */
	List<UserMainReportRichVO> searchByTeam(int[] userIds, String sTime, String eTime);

	/**
	 * 统计用户团队时间段内总计数据
	 */
	UserMainReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate);

	/**
	 * 统计用户团队时间段内总计数据
	 */
	UserMainReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate);

	/**
	 * 统计系统时间段内总计数据
	 */
	UserMainReportTeamStatisticsVO statisticsAll(String sDate, String eDate);
}