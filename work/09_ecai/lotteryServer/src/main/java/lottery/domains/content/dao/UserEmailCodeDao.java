package lottery.domains.content.dao;

import lottery.domains.content.entity.UserEmailCode;

public interface UserEmailCodeDao {

	boolean save(UserEmailCode entity);
	
	UserEmailCode get(int type, String username, String code);
	
	boolean update(UserEmailCode entity);
	
	boolean updateStatus(int id, int status);
	
}