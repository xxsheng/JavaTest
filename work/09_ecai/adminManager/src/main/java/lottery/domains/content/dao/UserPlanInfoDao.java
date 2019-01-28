package lottery.domains.content.dao;

import lottery.domains.content.entity.UserPlanInfo;

public interface UserPlanInfoDao {

	boolean add(UserPlanInfo entity);
	
	UserPlanInfo get(int userId);
	
	boolean update(UserPlanInfo entity);
	
	boolean update(int userId, int planCount, int prizeCount, double totalMoney, double totalPrize);
	
	boolean updateLevel(int userId, int level);
	
}