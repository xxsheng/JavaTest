package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.UserWithdrawLimit;

public interface UserWithdrawLimitService {

	boolean add(int userId, double amount, String time, PaymentChannel channel);

	boolean add(int userId, double amount, String time, int type, int subType, double percent);

	UserWithdrawLimit getByUserId(int userId);
	
	List<UserWithdrawLimit> getUserWithdrawLimits(int userId);
	
}