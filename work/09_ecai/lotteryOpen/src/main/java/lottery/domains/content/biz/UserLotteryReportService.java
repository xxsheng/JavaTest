package lottery.domains.content.biz;

public interface UserLotteryReportService {
	boolean update(int userId, int type, double amount, String time);
}