package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserWithdrawLimit;

public interface UserWithdrawLimitDao {
	
	UserWithdrawLimit getByUserId(int userId);
	
	List<UserWithdrawLimit> getUserWithdrawLimits(int userId, String time);
	
	boolean add(UserWithdrawLimit entity);
	
	boolean delByUserId(int userId);
	
//	boolean resetTotal(int userId);
//	
//	boolean updateTotal(int userId, double totalAmount);
//	
//	boolean updateTotal(int userId, double totalAmount, String totalTime);
//	
//	
//	boolean resetLotteryMoney(int userId);
//	
//	boolean updateLotteryMoney(int userId, double totalAmount);
//	
//	boolean updateLotteryMoney(int userId, double totalAmount, String totalTime);
	

	
}