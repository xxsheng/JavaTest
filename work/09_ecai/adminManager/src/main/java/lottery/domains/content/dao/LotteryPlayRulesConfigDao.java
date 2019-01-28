package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryPlayRulesConfig;

import java.util.List;

public interface LotteryPlayRulesConfigDao {
	
	List<LotteryPlayRulesConfig> listAll();

	List<LotteryPlayRulesConfig> listByLottery(int lotteryId);

	List<LotteryPlayRulesConfig> listByLotteryAndRule(int lotteryId, List<Integer> ruleIds);

	LotteryPlayRulesConfig get(int lotteryId, int ruleId);
	
	boolean save(LotteryPlayRulesConfig entity);

	boolean update(LotteryPlayRulesConfig entity);

	boolean updateStatus(int lotteryId, int ruleId, int status);

	boolean updateStatus(int ruleId, int status);

	boolean update(int id, String minNum, String maxNum);
}
