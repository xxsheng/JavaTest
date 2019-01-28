package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.user.UserBetsOriginalVO;

public interface UserBetsOriginalService {
	
	PageList search(String keyword, String username,  Integer uype,Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
					String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
					Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, int start, int limit);

	UserBetsOriginalVO getById(int id);

	double[] getTotalMoney(String keyword, String username, Integer uype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
						   String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
						   Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status);
}