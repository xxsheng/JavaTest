package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.LotteryPlayRules;

public interface LotteryPlayRulesDao {
	
	List<LotteryPlayRules> listAll();
	
}
