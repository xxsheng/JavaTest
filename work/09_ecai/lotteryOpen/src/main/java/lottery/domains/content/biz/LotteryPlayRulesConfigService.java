package lottery.domains.content.biz;

import lottery.domains.content.entity.LotteryPlayRulesConfig;

import java.util.List;

public interface LotteryPlayRulesConfigService {
	List<LotteryPlayRulesConfig> listAll();
}