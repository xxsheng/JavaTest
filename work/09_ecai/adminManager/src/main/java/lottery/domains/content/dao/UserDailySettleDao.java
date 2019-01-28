package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDailySettle;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 契约日结DAO
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleDao {

    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    List<UserDailySettle> list(List<Criterion> criterions, List<Order> orders);

    List<UserDailySettle> findByUserIds(List<Integer> userIds);

    UserDailySettle getByUserId(int userId);

    UserDailySettle getById(int id);

    /**
     * 新增日结
     */
    void add(UserDailySettle entity);

    /**
     * 根据用户ID删除日结
     */
    void deleteByUser(int userId);

    /**
     * 删除团队日结
     */
    void deleteByTeam(int upUserId);

    /**
     * 删除团队日结，但不包含自己
     */
    void deleteLowers(int upUserId);

    boolean updateStatus(int id, int status);

    boolean updateSomeFields(int id, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel);

    boolean updateSomeFields(int id, double scale, int minValidUser, int status, int fixed, double minScale, double maxScale);

    boolean updateTotalAmount(int userId, double amount);

}
