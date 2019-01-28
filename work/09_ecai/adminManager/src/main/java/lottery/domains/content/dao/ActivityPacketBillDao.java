package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.ActivityPacketBill;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface ActivityPacketBillDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 统计总金额
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	double total(String sTime, String eTime);
	
	boolean save(ActivityPacketBill bill);
	
}