package lottery.domains.content.biz;

public interface UserLotteryDetailsReportService {
	
	boolean update(int userId, int lotteryId, int ruleId, int type, double amount, String time);

}