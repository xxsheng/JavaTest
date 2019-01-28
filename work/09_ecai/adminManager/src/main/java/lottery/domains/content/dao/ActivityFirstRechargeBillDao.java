package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.ActivityFirstRechargeBill;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityFirstRechargeBillDao {
    PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

    double sumAmount(Integer userId, String sDate, String eDate, String ip);
    
    ActivityFirstRechargeBill getByDateAndIp(String date, String ip);
    
    ActivityFirstRechargeBill getByUserIdAndDate(int userId, String date);
    
    boolean add(ActivityFirstRechargeBill bill);
}
