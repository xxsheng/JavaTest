package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserCard;

public interface UserCardDao {
	
	boolean add(UserCard entity);
	
	UserCard getById(int id, int userId);
	
	UserCard getByCardId(String cardId);

	List<UserCard> listByUserId(int userId);
	
	boolean delete(int id, int userId);
	
	boolean setDefault(int id, int userId);
	
}