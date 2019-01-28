package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserBlacklist;

public interface UserBlacklistDao {
	
	boolean add(UserBlacklist entity);
	
	List<UserBlacklist> getByUsername(String username);
	
	List<UserBlacklist> getByCard(String cardName, String cardId);
	
	List<UserBlacklist> getByCardName(String cardName);
	
	List<UserBlacklist> list(List<Criterion> criterions, List<Order> orders);
	
	int getByIp(String ip);

}