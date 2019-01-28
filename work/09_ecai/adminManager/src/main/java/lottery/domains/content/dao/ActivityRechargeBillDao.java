package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.ActivityRechargeBill;

public interface ActivityRechargeBillDao {
	
	ActivityRechargeBill getById(int id);
	
	int getWaitTodo();
	
	boolean update(ActivityRechargeBill entity);
	
	boolean add(ActivityRechargeBill entity);
	
	boolean hasDateRecord(int userId, String date);
	
	List<ActivityRechargeBill> get(String ip, String date);

	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

	double total(String sTime, String eTime);

}