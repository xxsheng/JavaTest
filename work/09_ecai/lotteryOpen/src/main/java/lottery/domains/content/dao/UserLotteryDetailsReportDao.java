package lottery.domains.content.dao;

import lottery.domains.content.entity.UserLotteryDetailsReport;

import java.util.List;

public interface UserLotteryDetailsReportDao {

	boolean add(UserLotteryDetailsReport entity);
	
	UserLotteryDetailsReport get(int userId, int lotteryId, int ruleId, String time);
	
	boolean update(UserLotteryDetailsReport entity);

	void addDetailsReports(List<UserLotteryDetailsReport> detailsReports);
}