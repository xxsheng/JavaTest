package activity.domains.content.dao;

import java.util.List;

import activity.domains.content.entity.ActivityRebate;

public interface ActivityRebateDao {
	
	boolean add(ActivityRebate entity);
	
	ActivityRebate getById(int id);
	
	ActivityRebate getByType(int type);
	
	ActivityRebate getByStatusAndType(int type,int status);
	
	List<ActivityRebate>getList(int status);
	
	boolean update(ActivityRebate entity);
	
	

}