package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserBetsLimit;

public interface UserBetsLimitDao {

	List<UserBetsLimit> getByUserId(int userId);
	
}
