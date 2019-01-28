package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;

public interface UserActionLogDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

}