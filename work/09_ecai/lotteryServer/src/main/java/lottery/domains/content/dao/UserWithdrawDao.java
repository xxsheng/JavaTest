package lottery.domains.content.dao;

import lottery.domains.content.entity.UserWithdraw;

public interface UserWithdrawDao {
	boolean add(UserWithdraw entity);

	int getDateCashCount(int userId, String date);

	double getDateCashMoney(int userId, String date);
}