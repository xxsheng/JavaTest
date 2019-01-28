package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBetsLimit;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface UserBetsLimitDao {

	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	boolean save(UserBetsLimit entity);
	
	boolean update(UserBetsLimit entity);
	
	boolean delete(int id);
	
	UserBetsLimit getByUserId(int userId);
	
	UserBetsLimit getById(int id);
}
