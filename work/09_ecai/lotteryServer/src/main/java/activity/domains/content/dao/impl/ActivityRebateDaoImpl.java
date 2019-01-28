package activity.domains.content.dao.impl;

import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.entity.ActivityRebate;
import javautils.jdbc.hibernate.HibernateSuperDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

	@Override
	public ActivityRebate getByStatusAndType(int type, int status) {
		String hql = "from " + tab + " where type = ?0 and status =?1";
		Object[] values = { type ,status};
		return (ActivityRebate) superDao.unique(hql, values);
	}

	@Override
	public List<ActivityRebate> getList(int status) {
		String hql = "from " + tab + " where status = ?0 ";
		Object[] values = {status};
		return superDao.list(hql, values);
	}

}