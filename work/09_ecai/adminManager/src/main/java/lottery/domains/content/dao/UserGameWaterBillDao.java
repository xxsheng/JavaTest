package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.entity.UserGameWaterBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 游戏返水账单DAO
 * Created by Nick on 2017/02/04.
 */
public interface UserGameWaterBillDao {
    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    boolean add(UserGameWaterBill bill);
}
