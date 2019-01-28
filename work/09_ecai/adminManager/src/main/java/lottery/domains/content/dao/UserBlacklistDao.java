package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserBlacklist;

public interface UserBlacklistDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	boolean add(UserBlacklist entity);
	
	boolean delete(int id);

	int getByIp(String ip);
}