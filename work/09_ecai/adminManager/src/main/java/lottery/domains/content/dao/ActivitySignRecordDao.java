package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface ActivitySignRecordDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}