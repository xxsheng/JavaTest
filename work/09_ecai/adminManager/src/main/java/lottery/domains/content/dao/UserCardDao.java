package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserCard;

import lottery.domains.content.vo.user.UserCardVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface UserCardDao {
	
	boolean add(UserCard entity);
	
	UserCard getById(int id);
	
	List<UserCard> getByUserId(int userId);
	
	UserCard getByCardId(String cardId);

	UserCard getByUserAndCardId(int userId, String cardId);
	
	boolean update(UserCard entity);
	
	boolean updateCardName(int userId, String cardName);
	
	boolean delete(int userId);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}