package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryPlayRulesConfigService;
import lottery.domains.content.dao.LotteryPlayRulesConfigDao;
import lottery.domains.content.entity.LotteryPlayRulesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LotteryPlayRulesConfigServiceImpl implements LotteryPlayRulesConfigService {

	@Autowired
	private LotteryPlayRulesConfigDao lotteryPlayRulesConfigDao;

	@Override
	@Transactional(readOnly = true)
	public List<LotteryPlayRulesConfig> listAll() {
		return lotteryPlayRulesConfigDao.listAll();
	}
}