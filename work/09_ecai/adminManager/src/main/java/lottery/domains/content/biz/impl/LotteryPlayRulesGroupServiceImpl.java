package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryPlayRulesGroupService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupConfigDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.LotteryPlayRulesGroupConfig;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupSimpleVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩法组
 */
@Service
public class LotteryPlayRulesGroupServiceImpl implements LotteryPlayRulesGroupService {
	@Autowired
	private LotteryPlayRulesGroupDao groupDao;

	@Autowired
	private LotteryPlayRulesGroupConfigDao configDao;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private DbServerSyncDao dbServerSyncDao;

	@Override
	public List<LotteryPlayRulesGroupSimpleVO> listSimpleByType(int typeId) {
		List<LotteryPlayRulesGroup> groups = groupDao.listByType(typeId);
		if (CollectionUtils.isEmpty(groups)) {
			return new ArrayList<>();
		}

		List<LotteryPlayRulesGroupSimpleVO> simpleVOS = new ArrayList<>();
		for (LotteryPlayRulesGroup group : groups) {
			simpleVOS.add(new LotteryPlayRulesGroupSimpleVO(group, lotteryDataFactory));
		}

		return simpleVOS;
	}

	@Override
	public List<LotteryPlayRulesGroupVO> list(int lotteryId) {
		// lottery_play_rules_group和lottery_play_rules_group_config组合数据，lottery_play_rules_group_config优先级高于lottery_play_rules_group
		Lottery lottery = lotteryDataFactory.getLottery(lotteryId);
		if (lottery == null) {
			return new ArrayList<>();
		}

		// 分别查找数据
		List<LotteryPlayRulesGroup> groups = groupDao.listByType(lottery.getType());
		List<LotteryPlayRulesGroupConfig> configs = configDao.listByLottery(lotteryId);

		List<LotteryPlayRulesGroupVO> results = new ArrayList<>();
		// 合并数据
		for (LotteryPlayRulesGroup group : groups) {

			LotteryPlayRulesGroupConfig config = null;

			for (LotteryPlayRulesGroupConfig groupConfig : configs) {
				if (groupConfig.getGroupId() == group.getId()) {
					config = groupConfig;
					break;
				}
			}

			LotteryPlayRulesGroupVO vo = new LotteryPlayRulesGroupVO(group, config, lotteryDataFactory);
			if (config == null) {
				vo.setLotteryName(lottery.getShowName());
				vo.setLotteryId(lotteryId);
			}
			results.add(vo);
		}

		return results;
	}

	@Override
	public boolean updateStatus(int groupId, Integer lotteryId, boolean enable) {
		int _status = enable ? 0 : -1;

		if (lotteryId != null) {
			LotteryPlayRulesGroupConfig config = configDao.get(lotteryId, groupId);
			if (config == null) {
				config = new LotteryPlayRulesGroupConfig();
				config.setGroupId(groupId);
				config.setLotteryId(lotteryId);
				config.setStatus(_status);
				configDao.save(config);
			}
			else {
				// 指定了彩票ID，则更新彩票配置表
				configDao.updateStatus(lotteryId, groupId, _status);
			}
		}
		else {
			// 未指定彩票ID，玩法组和配置表都更新
			groupDao.updateStatus(groupId, _status);
			configDao.updateStatus(groupId, _status);
		}

		dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_PLAY_RULES);

		return true;
	}
}