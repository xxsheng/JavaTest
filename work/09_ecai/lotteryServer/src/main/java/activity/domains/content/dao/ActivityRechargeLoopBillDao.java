package activity.domains.content.dao;

import java.util.List;

import activity.domains.content.entity.ActivityRechargeLoopBill;

public interface ActivityRechargeLoopBillDao {

	boolean add(ActivityRechargeLoopBill entity);
	
	List<ActivityRechargeLoopBill> getByDate(int userId, String date);
	
}