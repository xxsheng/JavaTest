package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserSysMessage;

public interface UserSysMessageDao {

	boolean add(UserSysMessage entity);
	
	List<UserSysMessage> listUnread(int userId);

	boolean updateUnread(int userId, int[] ids);

}