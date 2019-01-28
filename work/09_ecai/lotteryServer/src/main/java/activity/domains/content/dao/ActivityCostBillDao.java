package activity.domains.content.dao;



import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import activity.domains.content.entity.ActivityCostBill;
import javautils.jdbc.PageList;

public interface ActivityCostBillDao {
	
	ActivityCostBill getDrawInfo(int user,String statrTime,String endTme);
	
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	boolean add(ActivityCostBill bill);
	
}
