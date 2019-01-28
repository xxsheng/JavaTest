package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryPlayRulesService;
import lottery.domains.content.dao.LotteryPlayRulesDao;
import lottery.domains.content.entity.LotteryPlayRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LotteryPlayRulesServiceImpl implements LotteryPlayRulesService {

	@Autowired
	private LotteryPlayRulesDao lotteryPlayRulesDao;

	@Override
	@Transactional(readOnly = true)
	public List<LotteryPlayRules> listAll() {
		return lotteryPlayRulesDao.listAll();
	}
}