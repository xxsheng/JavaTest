package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesDao;
import lottery.domains.content.entity.LotteryPlayRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesDaoImpl implements LotteryPlayRulesDao {

	private final String tab = LotteryPlayRules.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRules> superDao;
	
	@Override
	public List<LotteryPlayRules> listAll() {
		String hql = "from " + tab + " order by groupId";
		return superDao.list(hql);
	}

	@Override
	public List<LotteryPlayRules> listByType(int typeId) {
		String hql = "from " + tab + " where typeId = ?0 order by groupId";
		Object[] values = {typeId};
		return superDao.list(hql, values);
	}

	@Override
	public List<LotteryPlayRules> listByTypeAndGroup(int typeId, int groupId) {
		String hql = "from " + tab + " where typeId = ?0 and groupId = ?1 order by groupId";
		Object[] values = {typeId, groupId};
		return superDao.list(hql, values);
	}

	@Override
	public LotteryPlayRules getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (LotteryPlayRules) superDao.unique(hql, values);
	}

	@Override
	public boolean update(LotteryPlayRules entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update from " + tab + " set status = ?0 where id = ?1";
		Object[] values = {status, id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean update(int id, String minNum, String maxNum) {
		String hql = "update from " + tab + " set minNum = ?0, maxNum = ?1 where id = ?2";
		Object[] values = {minNum, maxNum, id};
		return superDao.update(hql, values);
	}
}