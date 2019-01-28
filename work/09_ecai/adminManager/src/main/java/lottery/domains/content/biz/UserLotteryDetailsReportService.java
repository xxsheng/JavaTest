package lottery.domains.content.biz;

import lottery.domains.content.vo.bill.HistoryUserLotteryDetailsReportVO;
import lottery.domains.content.vo.bill.UserBetsReportVO;
import lottery.domains.content.vo.bill.UserLotteryDetailsReportVO;

import java.util.List;

public interface UserLotteryDetailsReportService {
	
	boolean update(int userId, int lotteryId, int ruleId, int type, double amount, String time);
	
	List<UserLotteryDetailsReportVO> reportLowersAndSelf(int userId, Integer lotteryId, String sTime, String eTime);

	List<HistoryUserLotteryDetailsReportVO> historyReportLowersAndSelf(int userId, Integer lotteryId, String sTime, String eTime);
	
	List<UserLotteryDetailsReportVO> reportSelf(int userId, Integer lotteryId, String sTime, String eTime);
	
	List<HistoryUserLotteryDetailsReportVO> historyReportSelf(int userId, Integer lotteryId, String sTime, String eTime);

	List<UserBetsReportVO> sumUserBets(Integer type, Integer lottery, Integer ruleId, String sTime, String eTime);

}