package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserInfo;

public interface UserInfoDao {
	
	UserInfo get(int userId);
	
	List<UserInfo> listByBirthday(String date);
	
	boolean add(UserInfo entity);
	
	boolean update(UserInfo entity);

}