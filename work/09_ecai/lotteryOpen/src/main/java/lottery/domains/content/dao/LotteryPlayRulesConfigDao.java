package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryPlayRulesConfig;

import java.util.List;

public interface LotteryPlayRulesConfigDao {
	
	List<LotteryPlayRulesConfig> listAll();
	
}
