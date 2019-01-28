package lottery.domains.content.dao.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.GameBets;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2016/12/28.
 */
public interface GameBetsReadDao {
    /**
     * 按条件搜索，除非使用的条件性能非常高(如唯一索引)，否则请自已另起方法乖乖写sql
     */
    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    /**
     * 查询所有注单
     */
    PageList searchAll(Integer platformId, String sTime, String eTime, int start, int limit);

    /**
     * 查询某个用户的团队注单，包含用户自己
     */
    PageList searchByTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit);

    /**
     * 查询某个用户的直属下级团队注单，包含用户自己
     */
    PageList searchByDirectTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit);

    /**
     * 查询某些用户的团队注单，包含用户自己
     */
    PageList searchByTeam(Integer[] userIds, Integer platformId, String sTime, String eTime, int start, int limit);

    /**
     * 获取用户时间段内的消费金额
     */
    double getBillingOrder(int userId, String startTime, String endTime);

    boolean save(GameBets gameBets);
}
