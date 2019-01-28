package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettleBill;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 契约日结账单Service
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleBillService {
    /**
     * 查找契约分页数据
     */
    PageList search(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount,
                    Integer status, int start, int limit);

    boolean add(UserDailySettleBill settleBill);

    boolean update(UserDailySettleBill settleBill);

    List<UserDailySettleBill> findByCriteria(List<Criterion> criterions, List<Order> orders);

    List<UserDailySettleBill> getDirectLowerBills(int userId, String indicateDate, Integer[] status, Integer issueType);

    /**
     * 扣发日结，无所谓层级，但状态必须是余额不足，会从用户账上扣发给直属下级的账单，只往下走一层
     * @param id 日结ID
     * @return
     */
    UserDailySettleBill issue(int id);
}
