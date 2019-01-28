package lottery.domains.content.biz;

public interface UserLotteryReportService {

	boolean update(int userId, int type, double amount, String time);

	boolean updateRechargeFee(int userId, double amount, String time);
}