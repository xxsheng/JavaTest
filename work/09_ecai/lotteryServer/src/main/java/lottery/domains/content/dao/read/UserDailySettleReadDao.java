package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDailySettle;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 契约日结DAO
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleReadDao {
    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    PageList searchByTeam(int[] userIds, int start, int limit);

    PageList searchByTeam(int userId, int start, int limit);

    PageList searchByUserId(int userId, int start, int limit);

    List<UserDailySettle> findByUserIds(List<Integer> userIds);
    
    Long getCountUser(int userId);

    // PageList searchByDirectLowers(int upUserId, int start, int limit);
    //
    // List<UserDailySettle> findByDirectLowers(int upUserId);
}
