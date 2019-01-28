package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.vo.bill.UserMainReportVO;

public interface UserMainReportService {

	boolean update(int userId, int type, double amount, String time);
	
	List<UserMainReportVO> report(String sTime, String eTime);

	List<UserMainReportVO> report(int userId, String sTime, String eTime);
	
}