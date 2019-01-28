package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryPlayRulesGroupConfigService;
import lottery.domains.content.dao.LotteryPlayRulesGroupConfigDao;
import lottery.domains.content.entity.LotteryPlayRulesGroupConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LotteryPlayRulesGroupConfigServiceImpl implements LotteryPlayRulesGroupConfigService {

	@Autowired
	private LotteryPlayRulesGroupConfigDao lotteryPlayRulesGroupConfigDao;

	@Override
	@Transactional(readOnly = true)
	public List<LotteryPlayRulesGroupConfig> listAll() {
		return lotteryPlayRulesGroupConfigDao.listAll();
	}
}