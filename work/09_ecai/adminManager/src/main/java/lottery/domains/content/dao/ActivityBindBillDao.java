package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.ActivityBindBill;

public interface ActivityBindBillDao {
	
	ActivityBindBill getById(int id);
	
	int getWaitTodo();
	
	List<ActivityBindBill> get(String ip, String bindName, String bindCard);
	
	boolean update(ActivityBindBill entity);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 统计总金额
	 */
	double total(String sTime, String eTime);

}