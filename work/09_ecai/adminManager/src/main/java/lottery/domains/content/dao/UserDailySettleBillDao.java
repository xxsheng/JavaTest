package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDailySettleBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 契约日结账单DAO
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleBillDao {
    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    List<UserDailySettleBill> findByCriteria(List<Criterion> criterions, List<Order> orders);

    boolean add(UserDailySettleBill settleBill);

    UserDailySettleBill getById(int id);

    boolean update(UserDailySettleBill settleBill);
}
