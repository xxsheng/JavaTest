package lottery.domains.content.biz;

import javautils.jdbc.PageList;

public interface UserBetsPlanService {

	PageList search(String keyword, String username, Integer lotteryId, String expect, Integer ruleId, String minTime,
			String maxTime, Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple, Integer status,
			int start, int limit);

}