package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.LotteryOpenTime;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
public interface LotteryOpenTimeService {
    List<LotteryOpenTime> listAll();

    PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
}
