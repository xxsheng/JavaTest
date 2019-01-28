package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.ActivityRewardBill;

public interface ActivityRewardBillDao {
	
	boolean add(ActivityRewardBill entity);
	
	ActivityRewardBill getById(int id);
	
	List<ActivityRewardBill> getUntreated(String date);
	
	List<ActivityRewardBill> getLatest(int toUser, int status, int count);
	
	boolean hasRecord(int toUser, int fromUser, int type, String date);
	
	boolean update(ActivityRewardBill entity);
	
	boolean delete(int id);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 统计总金额
	 */
	double total(String sTime, String eTime, int type);

}