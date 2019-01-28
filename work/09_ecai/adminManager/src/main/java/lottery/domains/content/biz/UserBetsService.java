package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.vo.user.HistoryUserBetsVO;
import lottery.domains.content.vo.user.UserBetsVO;

import java.util.Date;
import java.util.List;

public interface UserBetsService {
	
	PageList search(String keyword, String username, Integer uype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
					String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
					Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked, String ip, int start, int limit);
	
	
	UserBetsVO getById(int id);
	
	HistoryUserBetsVO getHistoryById(int id);
	
	UserBets getBetsById(int id);
	
	PageList searchHistory(String keyword, String username, Integer uype,Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
			String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
			Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked, int start, int limit);

	List<UserBets> notOpened(int lotteryId, Integer ruleId, String expect, String match);
	
	boolean cancel(int lotteryId, Integer ruleId, String expect, String match);
	
	boolean cancel(int id);
	
	List<UserBetsVO> getSuspiciousOrder(int userId, int multiple);
	
	int countUserOnline(Date time);
	

	double[] getTotalMoney(String keyword, String username,Integer uype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
						   String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
						   Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked, String ip);
	
	double[] getHistoryTotalMoney(String keyword, String username,Integer uype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
			   String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
			   Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked);
	
	double getBillingOrder(int userId, String startTime, String endTime);

}