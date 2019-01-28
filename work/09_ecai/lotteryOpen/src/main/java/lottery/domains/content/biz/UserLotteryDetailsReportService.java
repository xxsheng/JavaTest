package lottery.domains.content.biz;

import lottery.domains.content.entity.UserLotteryDetailsReport;

import java.util.List;

public interface UserLotteryDetailsReportService {
	/**
	 * 批量新增或修改报表详情
	 */
	void addDetailsReports(List<UserLotteryDetailsReport> detailsReports);
	
	
	boolean update(int userId, int lotteryId, int ruleId, int type, double amount, String time);

}