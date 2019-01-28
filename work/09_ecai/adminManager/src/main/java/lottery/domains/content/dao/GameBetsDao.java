package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.GameBets;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface GameBetsDao {
    GameBets getById(int id);

    GameBets get(int userId, int platformId, String betsId, String gameCode);

    boolean save(GameBets gameBets);

    boolean update(GameBets gameBets);

    PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

    /**
     * 第一位是总投注(money)，第二位是总奖金(prizeMoney)
     */
    double[] getTotalMoney(String keyword, Integer userId, Integer platformId, String minTime,
                           String maxTime, Double minMoney, Double maxMoney, Double minPrizeMoney, Double maxPrizeMoney,
                           String gameCode, String gameType, String gameName);
    /**
     * 获取用户时间段内的消费金额
     */
    double getBillingOrder(int userId, String startTime, String endTime);
}
