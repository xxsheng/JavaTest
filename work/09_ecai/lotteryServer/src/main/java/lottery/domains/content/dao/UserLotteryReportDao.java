package lottery.domains.content.dao;

import lottery.domains.content.entity.UserLotteryReport;

public interface UserLotteryReportDao {
	
	boolean add(UserLotteryReport entity);
	
	UserLotteryReport get(int userId, String time);
	
	boolean update(UserLotteryReport entity);
}