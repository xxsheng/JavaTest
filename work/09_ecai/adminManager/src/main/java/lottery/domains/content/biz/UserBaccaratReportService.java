package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.vo.bill.UserBaccaratReportVO;

public interface UserBaccaratReportService {

	boolean update(int userId, int type, double amount, String time);
	
	List<UserBaccaratReportVO> report(String sTime, String eTime);

	List<UserBaccaratReportVO> report(int userId, String sTime, String eTime);
	
}