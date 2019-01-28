package activity.domains.content.dao.impl;

import activity.domains.content.dao.ActivitySignRecordDao;
import activity.domains.content.entity.ActivitySignRecord;
import javautils.jdbc.hibernate.HibernateSuperDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ActivitySignRecordDaoImpl implements ActivitySignRecordDao {
	
	private final String tab = ActivitySignRecord.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivitySignRecord> superDao;
	
	@Override
	public boolean add(ActivitySignRecord entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(ActivitySignRecord entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean updateLastCollectTime(int userId, String time) {
		String hql = "update " + tab + " set lastCollectTime = ?0 where userId = ?1";
		Object[] values = { time, userId };
		return superDao.update(hql, values);
	}

	@Override
	public ActivitySignRecord getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return (ActivitySignRecord) superDao.unique(hql, values);
	}
	
	@Override
	public boolean delete(int userId) {
		String hql = "deleteAllByUpUserId from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}

}