package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserMessage;

public interface UserMessageDao {

	UserMessage getById(int id);
	
	boolean delete(int id);
	
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	boolean save(UserMessage userMessage);

	void update(UserMessage message);
	
}