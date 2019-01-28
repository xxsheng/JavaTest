package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserMessage;

public interface UserMessageDao {

	boolean add(UserMessage entity);
	
	int getUnreadCount(int userId);
	
	boolean updateInboxMessage(int userId, int[] ids, int status);
	
	boolean updateOutboxMessage(int userId, int[] ids, int status);
	
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}