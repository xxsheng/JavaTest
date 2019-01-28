package lottery.domains.content.dao;

import lottery.domains.content.entity.UserActionLog;

public interface UserActionLogDao {

	boolean add(UserActionLog entity);
	
}