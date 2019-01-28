package lottery.domains.content.biz;

import lottery.domains.content.entity.LotteryPlayRules;

import java.util.List;

public interface LotteryPlayRulesService {
	List<LotteryPlayRules> listAll();
}