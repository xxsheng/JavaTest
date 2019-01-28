package lottery.domains.content.biz;

import lottery.domains.content.vo.bill.HistoryUserGameReportVO;
import lottery.domains.content.vo.bill.UserGameReportVO;

import java.util.List;

/**
 * Created by Nick on 2016/12/28.
 */
public interface UserGameReportService {
    boolean update(int userId, int platformId, double billingOrder, double prize, double waterReturn, double proxyReturn, String time);

    List<UserGameReportVO> report(String sTime, String eTime);

    List<UserGameReportVO> report(int userId, String sTime, String eTime);
    
    List<HistoryUserGameReportVO> historyReport(String sTime, String eTime);

    List<HistoryUserGameReportVO> historyReport(int userId, String sTime, String eTime);

    List<UserGameReportVO> reportByUser(String sTime, String eTime);
}
