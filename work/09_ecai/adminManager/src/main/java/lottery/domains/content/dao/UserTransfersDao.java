package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserTransfers;

public interface UserTransfersDao {
	
	boolean add(UserTransfers entity);

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	double getTotalTransfers(String sTime, String eTime, int type);

}