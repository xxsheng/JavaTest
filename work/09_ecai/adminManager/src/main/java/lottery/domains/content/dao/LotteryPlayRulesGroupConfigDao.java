package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryPlayRulesGroupConfig;

import java.util.List;

public interface LotteryPlayRulesGroupConfigDao {
	
	List<LotteryPlayRulesGroupConfig> listAll();

	List<LotteryPlayRulesGroupConfig> listByLottery(int lotteryId);

	LotteryPlayRulesGroupConfig get(int lotteryId, int groupId);
	
	boolean save(LotteryPlayRulesGroupConfig entity);

	boolean update(LotteryPlayRulesGroupConfig entity);

	boolean updateStatus(int lotteryId, int groupId, int status);

	boolean updateStatus(int groupId, int status);
}
