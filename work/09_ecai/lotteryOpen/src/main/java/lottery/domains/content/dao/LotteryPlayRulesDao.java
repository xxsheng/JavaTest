package lottery.domains.content.dao;

import lottery.domains.content.entity.LotteryPlayRules;

import java.util.List;

public interface LotteryPlayRulesDao {
	
	List<LotteryPlayRules> listAll();
	
}
