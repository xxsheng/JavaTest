package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserWhitelist;

public interface UserWhitelistDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	boolean add(UserWhitelist entity);
	
	boolean delete(int id);

}