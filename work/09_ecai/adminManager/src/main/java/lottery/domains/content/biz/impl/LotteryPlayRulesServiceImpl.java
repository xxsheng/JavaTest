package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryPlayRulesService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.LotteryPlayRulesConfigDao;
import lottery.domains.content.dao.LotteryPlayRulesDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesConfig;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.lottery.LotteryPlayRulesSimpleVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LotteryPlayRulesServiceImpl implements LotteryPlayRulesService {
	@Autowired
	private LotteryPlayRulesDao rulesDao;

	@Autowired
	private LotteryPlayRulesConfigDao configDao;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private DbServerSyncDao dbServerSyncDao;


	@Override
	public List<LotteryPlayRulesVO> list(int lotteryId, Integer groupId) {
		// lottery_play_rules和lottery_play_rules_config组合数据，lottery_play_rules_config优先级高于lottery_play_rules
		Lottery lottery = lotteryDataFactory.getLottery(lotteryId);
		if (lottery == null) {
			return new ArrayList<>();
		}

		// 分别查找数据
		List<LotteryPlayRules> rules;
		List<LotteryPlayRulesConfig> configs;
		if (groupId == null) {
			rules = rulesDao.listByType(lottery.getType());
			configs = configDao.listByLottery(lotteryId);
		}
		else {
			rules = rulesDao.listByTypeAndGroup(lottery.getType(), groupId);
			List<Integer> ruleIds = new ArrayList<>();
			for (LotteryPlayRules rule : rules) {
				ruleIds.add(rule.getId());
			}
			configs = configDao.listByLotteryAndRule(lotteryId, ruleIds);
		}

		List<LotteryPlayRulesVO> results = new ArrayList<>();
		// 合并数据
		for (LotteryPlayRules rule : rules) {
			if (groupId != null && rule.getGroupId() != groupId) {
				continue;
			}

			LotteryPlayRulesConfig config = null;

			for (LotteryPlayRulesConfig rulesConfig : configs) {
				if (rulesConfig.getRuleId() == rule.getId()) {
					config = rulesConfig;
					break;
				}
			}

			LotteryPlayRulesVO vo = new LotteryPlayRulesVO(rule, config, lotteryDataFactory);
			if (config == null) {
				vo.setLotteryName(lottery.getShowName());
				vo.setLotteryId(lotteryId);
			}
			results.add(vo);
		}

		return results;
	}

	@Override
	public List<LotteryPlayRulesSimpleVO> listSimple(int typeId, Integer groupId) {
		// lottery_play_rules和lottery_play_rules_config组合数据，lottery_play_rules_config优先级高于lottery_play_rules
		LotteryType lotteryType = lotteryDataFactory.getLotteryType(typeId);
		if (lotteryType == null) {
			return new ArrayList<>();
		}

		// 分别查找数据
		List<LotteryPlayRules> rules;
		if (groupId == null) {
			rules = rulesDao.listByType(typeId);
		}
		else {
			rules = rulesDao.listByTypeAndGroup(typeId, groupId);
		}

		List<LotteryPlayRulesSimpleVO> results = new ArrayList<>();
		// 合并数据
		for (LotteryPlayRules rule : rules) {
			if (groupId != null && rule.getGroupId() != groupId) {
				continue;
			}

			LotteryPlayRulesSimpleVO vo = new LotteryPlayRulesSimpleVO(rule, lotteryDataFactory);
			results.add(vo);
		}

		return results;
	}

	@Override
	public LotteryPlayRulesVO get(int lotteryId, int ruleId) {
		Lottery lottery = lotteryDataFactory.getLottery(lotteryId);
		if (lottery == null) {
			return null;
		}

		// 分别查找数据
		LotteryPlayRules rule = rulesDao.getById(ruleId);
		if (rule == null) {
			return null;
		}

		LotteryPlayRulesConfig config = configDao.get(lotteryId, ruleId);

		LotteryPlayRulesVO result = new LotteryPlayRulesVO(rule, config, lotteryDataFactory);
		if (config == null) {
			result.setLotteryName(lottery.getShowName());
			result.setLotteryId(lotteryId);
		}
		return result;
	}

	@Override
	public boolean edit(int ruleId, Integer lotteryId, String minNum, String maxNum) {
		if (!checkEditParams(ruleId, minNum, maxNum)) {
			return false;
		}

		if (lotteryId != null) {
			LotteryPlayRulesConfig config = configDao.get(lotteryId, ruleId);
			if (config == null) {
				LotteryPlayRules rule = rulesDao.getById(ruleId);
				if (rule == null) {
					return false;
				}

				config = new LotteryPlayRulesConfig();
				config.setRuleId(ruleId);
				config.setLotteryId(lotteryId);
				config.setMinNum(minNum);
				config.setMaxNum(maxNum);
				config.setStatus(rule.getStatus());
				config.setPrize(rule.getPrize());
				configDao.save(config);
			}
			else {
				// 指定了彩票ID，则更新彩票配置表
				config.setMinNum(minNum);
				config.setMaxNum(maxNum);
				configDao.update(config);
			}
		}
		else {
			// 未指定彩票ID，玩法组和配置表都更新
			rulesDao.update(ruleId, minNum, maxNum);
			configDao.update(ruleId, minNum, maxNum);
		}

		dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_PLAY_RULES);

		return true;
	}

	private boolean checkEditParams(int ruleId, String minNum, String maxNum) {
		if (StringUtils.isEmpty(minNum) || StringUtils.isEmpty(maxNum)) {
			return false;
		}

		LotteryPlayRules originalRule = rulesDao.getById(ruleId);
		if (originalRule == null) {
			return false;
		}

		String[] minNums = minNum.split(",");
		String[] maxNums = maxNum.split(",");

		for (String num : minNums) {
			if (!NumberUtils.isDigits(num) || (Integer.valueOf(num) < 0)) {
				return false;
			}
		}

		for (String num : maxNums) {
			if (!NumberUtils.isDigits(num) || (Integer.valueOf(num) < 0)) {
				return false;
			}
		}

		String[] originalMinNums = originalRule.getMinNum().split(",");
		String[] originalMaxNums = originalRule.getMaxNum().split(",");

		if (minNums.length != originalMinNums.length) {
			return false;
		}
		if (maxNums.length != originalMaxNums.length) {
			return false;
		}

		return true;
	}

	@Override
	public boolean updateStatus(int ruleId, Integer lotteryId, boolean enable) {
		int _status = enable ? 0 : -1;

		if (lotteryId != null) {
			LotteryPlayRulesConfig config = configDao.get(lotteryId, ruleId);
			if (config == null) {
				LotteryPlayRules rule = rulesDao.getById(ruleId);
				if (rule == null) {
					return false;
				}

				config = new LotteryPlayRulesConfig();
				config.setRuleId(ruleId);
				config.setLotteryId(lotteryId);
				config.setMinNum(rule.getMinNum());
				config.setMaxNum(rule.getMaxNum());
				config.setStatus(_status);
				config.setPrize(rule.getPrize());
				configDao.save(config);
			}
			else {
				// 指定了彩票ID，则更新彩票配置表
				configDao.updateStatus(lotteryId, ruleId, _status);
			}
		}
		else {
			// 未指定彩票ID，玩法组和配置表都更新
			rulesDao.updateStatus(ruleId, _status);
			configDao.updateStatus(ruleId, _status);
		}

		dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_PLAY_RULES);

		return true;
	}
}