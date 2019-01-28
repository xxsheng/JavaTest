package activity.domains.content.dao;

import activity.domains.content.entity.ActivitySignRecord;

public interface ActivitySignRecordDao {
	boolean add(ActivitySignRecord entity);

	boolean update(ActivitySignRecord entity);

	boolean updateLastCollectTime(int userId, String time);

	boolean delete(int userId);

	ActivitySignRecord getByUserId(int userId);
}