package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 老虎机真人体育返水账单DAO
 * Created by Nick on 2017/02/04
 */
public interface UserGameWaterBillReadDao {
    // PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
    //
    // PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit);

    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit);

    PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit);

    PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit);
}
