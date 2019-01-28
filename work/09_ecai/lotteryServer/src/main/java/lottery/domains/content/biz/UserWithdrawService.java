package lottery.domains.content.biz;

import lottery.domains.content.entity.UserCard;

public interface UserWithdrawService {
	boolean apply(int userId, UserCard uCard, double amount, double recMoney, double feeMoney);

	int getDateCashCount(int userId, String date);

	double getDateCashMoney(int userId, String date);

}