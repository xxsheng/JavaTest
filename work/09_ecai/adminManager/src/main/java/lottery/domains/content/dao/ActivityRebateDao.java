package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRebate;

public interface ActivityRebateDao {
	
	boolean add(ActivityRebate entity);
	
	ActivityRebate getById(int id);
	
	ActivityRebate getByType(int type);
	
	boolean update(ActivityRebate entity);

}