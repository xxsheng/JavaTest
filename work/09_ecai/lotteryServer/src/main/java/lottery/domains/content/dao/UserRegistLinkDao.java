package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserRegistLink;

public interface UserRegistLinkDao {
	
	boolean add(UserRegistLink entity);
	
	UserRegistLink get(String code);

	List<UserRegistLink> getByUserId(int userId);

	boolean updateAmount(int id, int amount);
	
	boolean delete(int id, int userId);

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}