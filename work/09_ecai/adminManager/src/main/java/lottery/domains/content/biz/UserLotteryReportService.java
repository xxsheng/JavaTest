package lottery.domains.content.biz;

import lottery.domains.content.vo.bill.HistoryUserLotteryReportVO;
import lottery.domains.content.vo.bill.UserBetsReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserProfitRankingVO;

import java.util.List;

public interface UserLotteryReportService {

	boolean update(int userId, int type, double amount, String time);

	boolean updateRechargeFee(int userId, double amount, String time);

	List<UserLotteryReportVO> report(String sTime, String eTime);

	List<UserLotteryReportVO> report(int userId, String sTime, String eTime);
	List<UserLotteryReportVO> reportByType(Integer type, String sTime, String eTime);
	
	
	List<HistoryUserLotteryReportVO> historyReport(String sTime, String eTime);
	
	List<HistoryUserLotteryReportVO> historyReport(int userId, String sTime, String eTime);
	
	List<UserBetsReportVO> bReport(Integer type, Integer lottery, Integer ruleId, String sTime, String eTime);

	List<UserProfitRankingVO> listUserProfitRanking(Integer userId, String sTime, String eTime, int start, int limit);
}