package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.entity.ActivityRebate;

@Repository
public class ActivityRebateDaoImpl implements ActivityRebateDao {

	private final String tab = ActivityRebate.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<ActivityRebate> superDao;

	@Override
	public boolean add(ActivityRebate entity) {
		return superDao.save(entity);
	}

	@Override
	public ActivityRebate getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = { id };
		return (ActivityRebate) superDao.unique(hql, values);
	}

	@Override
	public ActivityRebate getByType(int type) {
		String hql = "from " + tab + " where type = ?0";
		Object[] values = { type };
		return (ActivityRebate) superDao.unique(hql, values);
	}

	@Override
	public boolean update(ActivityRebate entity) {
		return superDao.update(entity);
	}

}