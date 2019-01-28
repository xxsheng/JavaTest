package lottery.domains.content.dao;

import lottery.domains.content.entity.HistoryUserGameReport;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.vo.bill.HistoryUserGameReportVO;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface UserGameReportDao {
    boolean save(UserGameReport entity);

    UserGameReport get(int userId, int platformId, String time);

    boolean update(UserGameReport entity);

    List<UserGameReport> list(int userId, String sTime, String eTime);

    List<UserGameReport> find(List<Criterion> criterions, List<Order> orders);
    
    List<HistoryUserGameReport> findHistory(List<Criterion> criterions, List<Order> orders);

    List<UserGameReportVO> reportByUser(String sTime, String eTime);

    /**
     * 统计自己及下级
     */
    UserGameReportVO sumLowersAndSelf(int userId, String sTime, String eTime);
    
    /**
     * （历史）统计自己及下级
     */
    HistoryUserGameReportVO historySumLowersAndSelf(int userId, String sTime, String eTime);

    /**
     * 单独统计用户
     */
    UserGameReportVO sum(int userId, String sTime, String eTime);
}
