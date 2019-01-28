package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityRebate;

public interface ActivityRebateService {

	boolean updateStatus(int id, int status);
	
	boolean edit(int id, String rules, String startTime, String endTime);
	
	ActivityRebate getByType(int type);
	
	ActivityRebate getById(int id);
	
}