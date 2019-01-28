package activity.domains.content.dao;

import activity.domains.content.entity.ActivitySalaryBill;

public interface ActivitySalaryBillDao {
	
	boolean add(ActivitySalaryBill entity);
	
	boolean hasRecord(int userId, int type, String date);

}