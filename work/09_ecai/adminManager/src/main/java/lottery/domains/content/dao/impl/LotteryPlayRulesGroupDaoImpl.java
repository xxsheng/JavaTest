package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupDao;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryPlayRulesGroupDaoImpl implements LotteryPlayRulesGroupDao {

	private final String tab = LotteryPlayRulesGroup.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<LotteryPlayRulesGroup> superDao;
	
	@Override
	public List<LotteryPlayRulesGroup> listAll() {
		String hql = "from " + tab + " order by typeId,sort";
		return superDao.list(hql);
	}

	@Override
	public List<LotteryPlayRulesGroup> listByType(int typeId) {
		String hql = "from " + tab + " where typeId = ?0 order by typeId,sort";
		Object[] values = {typeId};
		return superDao.list(hql, values);
	}

	@Override
	public LotteryPlayRulesGroup getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (LotteryPlayRulesGroup) superDao.unique(hql, values);
	}

	@Override
	public boolean update(LotteryPlayRulesGroup entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update from " + tab + " set status = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
}