package lottery.domains.content.dao;

import lottery.domains.content.entity.HistoryUserLotteryReport;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.vo.bill.HistoryUserLotteryReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserProfitRankingVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserLotteryReportDao {
	
	boolean add(UserLotteryReport entity);
	
	UserLotteryReport get(int userId, String time);
	
	List<UserLotteryReport> list(int userId, String sTime, String eTime);
	
	boolean update(UserLotteryReport entity);
	
	List<UserLotteryReport> find(List<Criterion> criterions, List<Order> orders);
	
	List<HistoryUserLotteryReport> findHistory(List<Criterion> criterions, List<Order> orders);
	
	List<UserLotteryReport> getDayList(int[] ids, String sDate, String eDate);

	/**
	 * 统计自己及下级
	 */
	UserLotteryReportVO sumLowersAndSelf(int userId, String sTime, String eTime);
	
	/**
	 * 统计自己及下级
	 */
	HistoryUserLotteryReportVO historySumLowersAndSelf(int userId, String sTime, String eTime);

	/**
	 * 单独统计用户
	 */
	UserLotteryReportVO sum(int userId, String sTime, String eTime);

	List<UserProfitRankingVO> listUserProfitRanking(String sTime, String eTime, int start, int limit);

	List<UserProfitRankingVO> listUserProfitRankingByDate(int userId, String sTime, String eTime, int start, int limit);
}