package lottery.domains.content.biz;


import lottery.domains.content.entity.UserBlacklist;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserBlacklistService {
	
	/**
	 * 加入黑名单
	 */
	boolean add(String username, int bankId, String cardName, String cardId, String ip);

	List<UserBlacklist> getByUsername(String username);

	List<UserBlacklist> getByCard(String cardName, String cardId);

	List<UserBlacklist> getByCardName(String cardName);

	List<UserBlacklist> list(List<Criterion> criterions, List<Order> orders);

	int getByIp(String ip);
	
}