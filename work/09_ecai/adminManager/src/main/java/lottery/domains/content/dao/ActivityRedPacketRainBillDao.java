package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainBillDao {
    PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

    double sumAmount(Integer userId, String minTime, String maxTime, String ip);
}
