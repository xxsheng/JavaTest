package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryPlayRulesGroupService;
import lottery.domains.content.dao.LotteryPlayRulesGroupDao;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LotteryPlayRulesGroupServiceImpl implements LotteryPlayRulesGroupService {

	@Autowired
	private LotteryPlayRulesGroupDao lotteryPlayRulesGroupDao;

	@Override
	@Transactional(readOnly = true)
	public List<LotteryPlayRulesGroup> listAll() {
		return lotteryPlayRulesGroupDao.listAll();
	}
}