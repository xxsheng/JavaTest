package lottery.domains.content.dao;

import lottery.domains.content.entity.UserLotteryDetailsReport;
import lottery.domains.content.vo.bill.HistoryUserLotteryDetailsReportVO;
import lottery.domains.content.vo.bill.UserBetsReportVO;
import lottery.domains.content.vo.bill.UserLotteryDetailsReportVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserLotteryDetailsReportDao {

	boolean add(UserLotteryDetailsReport entity);
	
	UserLotteryDetailsReport get(int userId, int lotteryId, int ruleId, String time);
	
	boolean update(UserLotteryDetailsReport entity);

	List<UserLotteryDetailsReport> find(List<Criterion> criterions, List<Order> orders);

	/**
	 * 统计自己及下级，以彩票为维度
	 */
	List<UserLotteryDetailsReportVO> sumLowersAndSelfByLottery(int userId, String sTime, String eTime);
	
	/**
	 * 统计自己及下级，以彩票为维度
	 */
	List<HistoryUserLotteryDetailsReportVO> historySumLowersAndSelfByLottery(int userId, String sTime, String eTime);

	/**
	 * 统计自己及下级，以玩法为维度
	 */
	List<UserLotteryDetailsReportVO> sumLowersAndSelfByRule(int userId, int lotteryId, String sTime, String eTime);
	
	/**
	 * 历史统计自己及下级，以玩法为维度
	 */
	List<HistoryUserLotteryDetailsReportVO> historySumLowersAndSelfByRule(int userId, int lotteryId, String sTime, String eTime);

	/**
	 * 统计自己，以彩票为维度
	 */
	List<UserLotteryDetailsReportVO> sumSelfByLottery(int userId, String sTime, String eTime);
	
	/**
	 * 统计自己，以彩票为维度
	 */
	List<HistoryUserLotteryDetailsReportVO> historySumSelfByLottery(int userId, String sTime, String eTime);

	/**
	 * 统计自己，以玩法为维度
	 */
	List<UserLotteryDetailsReportVO> sumSelfByRule(int userId, int lotteryId, String sTime, String eTime);
	
	/**
	 * 统计自己，以玩法为维度
	 */
	List<HistoryUserLotteryDetailsReportVO> historySumSelfByRule(int userId, int lotteryId, String sTime, String eTime);

	/**
	 * 彩票盈亏报表
	 */
	List<UserBetsReportVO> sumUserBets(List<Integer> lotteryIds, Integer ruleId, String sTime, String eTime);
}