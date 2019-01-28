package lottery.domains.content.dao;

import lottery.domains.content.entity.UserGameDividendBill;

public interface UserGameDividendBillDao {
	UserGameDividendBill getById(int id);

	boolean updateStatus(int id, int status, int beforeStatus);
}