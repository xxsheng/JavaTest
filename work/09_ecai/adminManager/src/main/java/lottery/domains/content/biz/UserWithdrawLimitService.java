package lottery.domains.content.biz;

import lottery.domains.content.entity.UserWithdrawLimit;

import java.util.Map;

public interface UserWithdrawLimitService {
	
	boolean delByUserId(int userId);
	
	boolean add(int userId, double amount, String time, int type, int subType, double percent);

	UserWithdrawLimit getByUserId(int userId);
	
	Map<String, Object> getUserWithdrawLimits(int userId, String time);
	
}