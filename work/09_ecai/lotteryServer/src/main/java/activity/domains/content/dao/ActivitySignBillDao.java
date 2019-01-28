package activity.domains.content.dao;

import activity.domains.content.entity.ActivitySignBill;

public interface ActivitySignBillDao {

	boolean add(ActivitySignBill entity);
	
	ActivitySignBill get(int userId, String time);
	
}