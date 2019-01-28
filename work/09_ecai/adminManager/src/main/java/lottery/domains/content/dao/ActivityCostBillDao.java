package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;

public interface ActivityCostBillDao {
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 统计总金额
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	double total(String sTime, String eTime);
}
