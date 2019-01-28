package lottery.domains.content.dao.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.GameBetsDao;
import lottery.domains.content.entity.GameBets;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2016/12/24.
 */
@Repository
public class GameBetsDaoImpl implements GameBetsDao {
    private final String tab = GameBets.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<GameBets> superDao;

    @Override
    public GameBets getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (GameBets) superDao.unique(hql, values);
    }

    @Override
    public GameBets get(int userId, int platformId, String betsId, String gameCode) {
        String hql = "from " + tab + " where userId = ?0 and platformId=?1 and betsId=?2 and gameCode=?3";
        Object[] values = {userId, platformId, betsId, gameCode};
        return (GameBets) superDao.unique(hql, values);
    }

    @Override
    public boolean save(GameBets gameBets) {
        return superDao.save(gameBets);
    }

    @Override
    public boolean update(GameBets gameBets) {
        return superDao.update(gameBets);
    }

    @Override
    public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(GameBets.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public double[] getTotalMoney(String keyword, Integer userId, Integer platformId, String minTime, String maxTime,
                                  Double minMoney, Double maxMoney, Double minPrizeMoney, Double maxPrizeMoney,
                                  String gameCode, String gameType, String gameName) {
        String hql = "select sum(money+progressiveMoney), sum(prizeMoney+progressivePrize) from " + tab + " where 1=1";

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotNull(keyword)) {
            if (StringUtil.isInteger(keyword)) {
                hql += " and (id = :id or betsId like :betsId)";
                params.put("id", Integer.valueOf(keyword));
                params.put("betsId", "%" + keyword + "%");
            }
            else {
                hql += " and (betsId like :betsId)";
                params.put("betsId", "%" + keyword + "%");
            }
        }
        if (userId != null) {
            hql += " and userId = :userId";
            params.put("userId", userId);
        }
        if(platformId != null) {
            hql += " and platformId = :platformId";
            params.put("platformId", platformId);
        }
        if(StringUtil.isNotNull(minTime)) {
            hql += " and time > :minTime";
            params.put("minTime", minTime);
        }
        if(StringUtil.isNotNull(maxTime)) {
            hql += " and time < :maxTime";
            params.put("maxTime", maxTime);
        }
        if(minMoney != null) {
            hql += " and money >= :minMoney";
            params.put("minMoney", minMoney);
        }
        if(maxMoney != null) {
            hql += " and money <= :maxMoney";
            params.put("maxMoney", maxMoney);
        }
        if(minPrizeMoney != null) {
            hql += " and prizeMoney >= :minPrizeMoney";
            params.put("minPrizeMoney", minPrizeMoney);
        }
        if(maxPrizeMoney != null) {
            hql += " and prizeMoney <= :maxPrizeMoney";
            params.put("maxPrizeMoney", maxPrizeMoney);
        }
        if(StringUtils.isNotEmpty(gameCode)) {
            hql += " and gameCode like :gameCode";
            params.put("gameCode", "%" + gameCode + "%");
        }
        if(StringUtils.isNotEmpty(gameType)) {
            hql += " and gameType like :gameType";
            params.put("gameType", "%" + gameType + "%");
        }
        if(StringUtils.isNotEmpty(gameName)) {
            hql += " and gameName like :gameName";
            params.put("gameName", "%" + gameName + "%");
        }
        Object result = superDao.uniqueWithParams(hql, params);
        if (result == null) {
            return new double[]{0, 0};
        }
        Object[] results = (Object[]) result;
        double totalMoney = results[0] == null ? 0 : (Double) results[0];
        double totalPrizeMoney = results[1] == null ? 0 : (Double) results[1];
        return new double[]{totalMoney, totalPrizeMoney};
    }
    
    @Override
    public double getBillingOrder(int userId, String startTime, String endTime) {
        String hql = "select sum(money) from " + tab + " where userId = ?0 and time >= ?1 and time < ?2";
        Object[] values = {userId, startTime, endTime};
        Object result = superDao.unique(hql, values);
        return result != null ? ((Number) result).doubleValue() : 0;
    }
}
