package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupConfigDao;
import lottery.domains.content.entity.LotteryPlayRulesGroupConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesGroupConfigDaoImpl implements LotteryPlayRulesGroupConfigDao {

	private final String tab = LotteryPlayRulesGroupConfig.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRulesGroupConfig> superDao;
	
	@Override
	public List<LotteryPlayRulesGroupConfig> listAll() {
		String hql = "from " + tab + " order by id";
		return superDao.list(hql);
	}

	@Override
	public List<LotteryPlayRulesGroupConfig> listByLottery(int lotteryId) {
		String hql = "from " + tab + " where lotteryId = ?0 order by id";
		Object[] values = {lotteryId};
		return superDao.list(hql, values);
	}

	@Override
	public LotteryPlayRulesGroupConfig get(int lotteryId, int groupId) {
		String hql = "from " + tab + " where lotteryId = ?0 and groupId = ?1";
		Object[] values = {lotteryId, groupId};
		return (LotteryPlayRulesGroupConfig) superDao.unique(hql, values);
	}

	@Override
	public boolean save(LotteryPlayRulesGroupConfig entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(LotteryPlayRulesGroupConfig entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean updateStatus(int lotteryId, int groupId, int status) {
		String hql = "update from " + tab + " set status = ?0 where lotteryId = ?1 and groupId = ?2";
		Object[] values = {status, lotteryId, groupId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateStatus(int groupId, int status) {
		String hql = "update from " + tab + " set status = ?0 where groupId = ?1";
		Object[] values = {status, groupId};
		return superDao.update(hql, values);
	}
}