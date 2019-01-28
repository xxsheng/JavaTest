package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBalanceSnapshot;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2017-07-03.
 */
public interface UserBalanceSnapshotDao {
    PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

    boolean add(UserBalanceSnapshot entity);
}
