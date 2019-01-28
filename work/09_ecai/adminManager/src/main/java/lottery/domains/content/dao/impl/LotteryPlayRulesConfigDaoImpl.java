package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesConfigDao;
import lottery.domains.content.entity.LotteryPlayRulesConfig;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesConfigDaoImpl implements LotteryPlayRulesConfigDao {

	private final String tab = LotteryPlayRulesConfig.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRulesConfig> superDao;
	
	@Override
	public List<LotteryPlayRulesConfig> listAll() {
		String hql = "from " + tab + " order by id";
		return superDao.list(hql);
	}

	@Override
	public List<LotteryPlayRulesConfig> listByLottery(int lotteryId) {
		String hql = "from " + tab + " where lotteryId = ?0 order by id";
		Object[] values = {lotteryId};
		return superDao.list(hql, values);
	}

	@Override
	public List<LotteryPlayRulesConfig> listByLotteryAndRule(int lotteryId, List<Integer> ruleIds) {
		if (CollectionUtils.isEmpty(ruleIds)) {
			String hql = "from " + tab + " where lotteryId = ?0 order by id";
			Object[] values = {lotteryId};
			return superDao.list(hql, values);
		}
		else {
			String hql = "from " + tab + " where lotteryId = ?0 and ruleId in ("+ArrayUtils.transInIds(ruleIds)+") order by id";
			Object[] values = {lotteryId};
			return superDao.list(hql, values);
		}
	}

	@Override
	public LotteryPlayRulesConfig get(int lotteryId, int ruleId) {
		String hql = "from " + tab + " where lotteryId = ?0 and ruleId = ?1";
		Object[] values = {lotteryId, ruleId};
		return (LotteryPlayRulesConfig) superDao.unique(hql, values);
	}

	@Override
	public boolean save(LotteryPlayRulesConfig entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(LotteryPlayRulesConfig entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean updateStatus(int lotteryId, int ruleId, int status) {
		String hql = "update from " + tab + " set status = ?0 where lotteryId = ?1 and ruleId = ?2";
		Object[] values = {status, lotteryId, ruleId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateStatus(int ruleId, int status) {
		String hql = "update from " + tab + " set status = ?0 where ruleId = ?1";
		Object[] values = {status, ruleId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean update(int id, String minNum, String maxNum) {
		String hql = "update from " + tab + " set minNum = ?0, maxNum = ?1 where id = ?2";
		Object[] values = {minNum, maxNum, id};
		return superDao.update(hql, values);
	}
}