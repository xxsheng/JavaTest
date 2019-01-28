package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserSecurity;

public interface UserSecurityDao {
	
	boolean add(UserSecurity entity);
	
	List<UserSecurity> listByUserId(int userId);
	
	UserSecurity getById(int id, int userId);
}
