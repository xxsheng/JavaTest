package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserWithdrawLimit;

public interface UserWithdrawLimitDao {
	
	UserWithdrawLimit getByUserId(int userId);
	
	List<UserWithdrawLimit> getUserWithdrawLimits(int userId);
	
	boolean add(UserWithdrawLimit entity);
	
	boolean resetLimit(int userId);
//	
//	boolean updateTotal(int userId, double totalAmount);
//	
//	boolean updateTotal(int userId, double totalAmount, String totalTime);
//	
//	
//	boolean resetLotteryAccount(int userId);
//	
//	boolean updateLotteryAccount(int userId, double totalAmount);
//	
//	boolean updateLotteryAccount(int userId, double totalAmount, String totalTime);
	
}