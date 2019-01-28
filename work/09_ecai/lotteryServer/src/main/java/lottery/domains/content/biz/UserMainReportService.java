package lottery.domains.content.biz;

public interface UserMainReportService {

	boolean update(int userId, int type, double amount, String time);

	boolean updateRecharge(int userId, double amount, String time, double receiveFeeMoney);

	boolean updateUserTrans(int userId, int type, double amount, String time, boolean abs);
}