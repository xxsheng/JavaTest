package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDailySettleBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 契约日结账单DAO
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleBillReadDao {
    UserDailySettleBill getById(int id);

    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit);

    PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit);

    PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit);

    double getTotalUnIssue(int userId);

    // PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit);
}
