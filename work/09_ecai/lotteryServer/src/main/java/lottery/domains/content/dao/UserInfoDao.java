package lottery.domains.content.dao;

import lottery.domains.content.entity.UserInfo;

public interface UserInfoDao {
	
	UserInfo get(int userId);
	
	boolean add(UserInfo entity);
	
	boolean update(UserInfo entity);

}