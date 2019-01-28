package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.GameBetsReadDao;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.UserBill;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Nick on 2016/12/28.
 */
@Repository
public class GameBetsReadDaoImpl implements GameBetsReadDao {
    private final String tab = GameBets.class.getSimpleName();

    @Autowired
    private HibernateSuperReadDao<GameBets> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(GameBets.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public PageList searchAll(Integer platformId, String sTime, String eTime, int start, int limit) {
        StringBuffer countHql = new StringBuffer("select count(gb.id) from " + tab + " gb where gb.id > 0");
        StringBuffer queryHql = new StringBuffer("select gb from " + tab + " gb where gb.id > 0");

        List<Object> params = new LinkedList<>();

        int paramIndex = 0;
        if (platformId != null) {
            countHql.append(" and gb.platformId=?").append(paramIndex);
            queryHql.append(" and gb.platformId=?").append(paramIndex++);
            params.add(platformId);
        }
        if (StringUtils.isNotEmpty(sTime)) {
            countHql.append(" and gb.time>=?").append(paramIndex);
            queryHql.append(" and gb.time>=?").append(paramIndex++);
            params.add(sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countHql.append(" and gb.time<?").append(paramIndex);
            queryHql.append(" and gb.time<?").append(paramIndex++);
            params.add(eTime);
        }

        queryHql.append(" order by gb.time desc,gb.id desc");

        Object[] values = params.toArray();
        return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
    }

    @Override
    public PageList searchByTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(gb.id) from game_bets gb,user u where gb.user_id = u.id and (u.id = :userId or u.upids like :upid)");
        StringBuffer querySql = new StringBuffer("select gb.id, gb.user_id, gb.platform_id, gb.bets_id, gb.game_code, gb.game_type, gb.game_name, gb.money, gb.prize_money, gb.progressive_money, gb.progressive_prize, gb.balance, gb.status, gb.time, gb.prize_time, gb.ext1, gb.ext2, gb.ext3 from game_bets gb,user u where gb.user_id = u.id and (u.id = :userId or u.upids like :upid)");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("upid", "%["+userId+"]%");

        if (platformId != null) {
            countSql.append(" and gb.platform_id=:platformId");
            querySql.append(" and gb.platform_id=:platformId");
            params.put("platformId", platformId);
        }
        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and gb.time>=:sTime");
            querySql.append(" and gb.time>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and gb.time<:eTime");
            querySql.append(" and gb.time<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by gb.time desc,gb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();

        List<GameBets> results = new ArrayList<>(list.size());
        for (Object o : list) {
            Object[] objs = (Object[]) o;


            int index = 0;
            int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            int _platformId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            String _betsId = objs[index] != null ? objs[index++].toString() : "";
            String _gameCode = objs[index] != null ? objs[index++].toString() : "";
            String _gameType = objs[index] != null ? objs[index++].toString() : "";
            String _gameName = objs[index] != null ? objs[index++].toString() : "";
            double _money = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _prizeMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _progressiveMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _progressivePrize = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _balance = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            int _status = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            String _time = objs[index] != null ? objs[index++].toString() : "";
            String _prizeTime = objs[index] != null ? objs[index++].toString() : "";
            String _ext1 = objs[index] != null ? objs[index++].toString() : "";
            String _ext2 = objs[index] != null ? objs[index++].toString() : "";
            String _ext3 = objs[index] != null ? objs[index++].toString() : "";

            GameBets result = new GameBets();
            result.setId(_id);
            result.setUserId(_userId);
            result.setPlatformId(_platformId);
            result.setBetsId(_betsId);
            result.setGameCode(_gameCode);
            result.setGameType(_gameType);
            result.setGameName(_gameName);
            result.setMoney(_money);
            result.setPrizeMoney(_prizeMoney);
            result.setProgressiveMoney(_progressiveMoney);
            result.setProgressivePrize(_progressivePrize);
            result.setBalance(_balance);
            result.setStatus(_status);
            result.setTime(_time);
            result.setPrizeTime(_prizeTime);
            result.setExt1(_ext1);
            result.setExt2(_ext2);
            result.setExt3(_ext3);
            results.add(result);
        }
        pageList.setList(results);
        return pageList;

        // StringBuffer countHql = new StringBuffer("select count(gb.id) from " + tab + " gb where gb.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
        // StringBuffer queryHql = new StringBuffer("select gb from " + tab + " gb where gb.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
        //
        // List<Object> params = new LinkedList<>();
        // params.add(userId);
        // params.add("%["+userId+"]%");
        //
        // int paramIndex = 2;
        // if (platformId != null) {
        //     countHql.append(" and gb.platformId=?").append(paramIndex);
        //     queryHql.append(" and gb.platformId=?").append(paramIndex++);
        //     params.add(platformId);
        // }
        // if (StringUtils.isNotEmpty(sTime)) {
        //     countHql.append(" and gb.time>=?").append(paramIndex);
        //     queryHql.append(" and gb.time>=?").append(paramIndex++);
        //     params.add(sTime);
        // }
        // if (StringUtils.isNotEmpty(eTime)) {
        //     countHql.append(" and gb.time<?").append(paramIndex);
        //     queryHql.append(" and gb.time<?").append(paramIndex++);
        //     params.add(eTime);
        // }
        //
        // queryHql.append(" order by gb.time desc,gb.id desc");
        //
        // Object[] values = params.toArray();
        // return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
    }

    @Override
    public PageList searchByDirectTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(gb.id) from game_bets gb,user u where gb.user_id = u.id and (u.id = :userId or u.upid = :upid)");
        StringBuffer querySql = new StringBuffer("select gb.id, gb.user_id, gb.platform_id, gb.bets_id, gb.game_code, gb.game_type, gb.game_name, gb.money, gb.prize_money, gb.progressive_money, gb.progressive_prize, gb.balance, gb.status, gb.time, gb.prize_time, gb.ext1, gb.ext2, gb.ext3 from game_bets gb,user u where gb.user_id = u.id and (u.id = :userId or u.upid = :upid)");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("upid", userId);

        if (platformId != null) {
            countSql.append(" and gb.platform_id=:platformId");
            querySql.append(" and gb.platform_id=:platformId");
            params.put("platformId", platformId);
        }
        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and gb.time>=:sTime");
            querySql.append(" and gb.time>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and gb.time<:eTime");
            querySql.append(" and gb.time<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by gb.time desc,gb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();

        List<GameBets> results = new ArrayList<>(list.size());
        for (Object o : list) {
            Object[] objs = (Object[]) o;


            int index = 0;
            int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            int _platformId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            String _betsId = objs[index] != null ? objs[index++].toString() : "";
            String _gameCode = objs[index] != null ? objs[index++].toString() : "";
            String _gameType = objs[index] != null ? objs[index++].toString() : "";
            String _gameName = objs[index] != null ? objs[index++].toString() : "";
            double _money = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _prizeMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _progressiveMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _progressivePrize = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _balance = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            int _status = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            String _time = objs[index] != null ? objs[index++].toString() : "";
            String _prizeTime = objs[index] != null ? objs[index++].toString() : "";
            String _ext1 = objs[index] != null ? objs[index++].toString() : "";
            String _ext2 = objs[index] != null ? objs[index++].toString() : "";
            String _ext3 = objs[index] != null ? objs[index++].toString() : "";

            GameBets result = new GameBets();
            result.setId(_id);
            result.setUserId(_userId);
            result.setPlatformId(_platformId);
            result.setBetsId(_betsId);
            result.setGameCode(_gameCode);
            result.setGameType(_gameType);
            result.setGameName(_gameName);
            result.setMoney(_money);
            result.setPrizeMoney(_prizeMoney);
            result.setProgressiveMoney(_progressiveMoney);
            result.setProgressivePrize(_progressivePrize);
            result.setBalance(_balance);
            result.setStatus(_status);
            result.setTime(_time);
            result.setPrizeTime(_prizeTime);
            result.setExt1(_ext1);
            result.setExt2(_ext2);
            result.setExt3(_ext3);
            results.add(result);
        }
        pageList.setList(results);
        return pageList;

        // StringBuffer countHql = new StringBuffer("select count(gb.id) from " + tab + " gb where gb.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
        // StringBuffer queryHql = new StringBuffer("select gb from " + tab + " gb where gb.userId in (select u.id from User u where u.id = ?0 or u.upids like ?1)");
        //
        // List<Object> params = new LinkedList<>();
        // params.add(userId);
        // params.add("%["+userId+"]%");
        //
        // int paramIndex = 2;
        // if (platformId != null) {
        //     countHql.append(" and gb.platformId=?").append(paramIndex);
        //     queryHql.append(" and gb.platformId=?").append(paramIndex++);
        //     params.add(platformId);
        // }
        // if (StringUtils.isNotEmpty(sTime)) {
        //     countHql.append(" and gb.time>=?").append(paramIndex);
        //     queryHql.append(" and gb.time>=?").append(paramIndex++);
        //     params.add(sTime);
        // }
        // if (StringUtils.isNotEmpty(eTime)) {
        //     countHql.append(" and gb.time<?").append(paramIndex);
        //     queryHql.append(" and gb.time<?").append(paramIndex++);
        //     params.add(eTime);
        // }
        //
        // queryHql.append(" order by gb.time desc,gb.id desc");
        //
        // Object[] values = params.toArray();
        // return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
    }

    @Override
    public PageList searchByTeam(Integer[] userIds, Integer platformId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(gb.id) from game_bets gb,user u where gb.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");
        StringBuffer querySql = new StringBuffer("select gb.id, gb.user_id, gb.platform_id, gb.bets_id, gb.game_code, gb.game_type, gb.game_name, gb.money, gb.prize_money, gb.progressive_money, gb.progressive_prize, gb.balance, gb.status, gb.time, gb.prize_time, gb.ext1, gb.ext2, gb.ext3 from game_bets gb,user u where gb.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");

        Map<String, Object> params = new HashMap<>();

        for (int i = 0; i < userIds.length; i++) {
            countSql.append("u.upids like :upid").append(i);
            querySql.append("u.upids like :upid").append(i);
            params.put("upid" + i, "%["+userIds[i]+"]%");

            if (i < userIds.length - 1) {
                countSql.append(" or ");
                querySql.append(" or ");
            }
        }

        countSql.append(")");
        querySql.append(")");

        if (platformId != null) {
            countSql.append(" and gb.platform_id=:platformId");
            querySql.append(" and gb.platform_id=:platformId");
            params.put("platformId", platformId);
        }
        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and gb.time>=:sTime");
            querySql.append(" and gb.time>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and gb.time<:eTime");
            querySql.append(" and gb.time<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by gb.time desc,gb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();

        List<GameBets> results = new ArrayList<>(list.size());
        for (Object o : list) {
            Object[] objs = (Object[]) o;

            int index = 0;
            int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            int _platformId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            String _betsId = objs[index] != null ? objs[index++].toString() : "";
            String _gameCode = objs[index] != null ? objs[index++].toString() : "";
            String _gameType = objs[index] != null ? objs[index++].toString() : "";
            String _gameName = objs[index] != null ? objs[index++].toString() : "";
            double _money = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _prizeMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _progressiveMoney = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _progressivePrize = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            double _balance = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
            int _status = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
            String _time = objs[index] != null ? objs[index++].toString() : "";
            String _prizeTime = objs[index] != null ? objs[index++].toString() : "";
            String _ext1 = objs[index] != null ? objs[index++].toString() : "";
            String _ext2 = objs[index] != null ? objs[index++].toString() : "";
            String _ext3 = objs[index] != null ? objs[index++].toString() : "";

            GameBets result = new GameBets();
            result.setId(_id);
            result.setUserId(_userId);
            result.setPlatformId(_platformId);
            result.setBetsId(_betsId);
            result.setGameCode(_gameCode);
            result.setGameType(_gameType);
            result.setGameName(_gameName);
            result.setMoney(_money);
            result.setPrizeMoney(_prizeMoney);
            result.setProgressiveMoney(_progressiveMoney);
            result.setProgressivePrize(_progressivePrize);
            result.setBalance(_balance);
            result.setStatus(_status);
            result.setTime(_time);
            result.setPrizeTime(_prizeTime);
            result.setExt1(_ext1);
            result.setExt2(_ext2);
            result.setExt3(_ext3);
            results.add(result);
        }
        pageList.setList(results);
        return pageList;

        // StringBuffer countHql = new StringBuffer("select count(gb.id) from " + tab + " gb where gb.userId in(select u.id from User u where u.id in("+ ArrayUtils.transInIds(userIds)+") or (");
        // StringBuffer queryHql = new StringBuffer("select gb from " + tab + " gb where gb.userId in(select u.id from User u where u.id in("+ ArrayUtils.transInIds(userIds)+") or (");
        //
        // List<Object> params = new LinkedList<>();
        //
        // int paramIndex = 0;
        // for (int i = 0; i < userIds.length; i++) {
        //     countHql.append("u.upids like ?").append(paramIndex);
        //     queryHql.append("u.upids like ?").append(paramIndex++);
        //
        //     if (i < userIds.length - 1) {
        //         countHql.append(" or ");
        //         queryHql.append(" or ");
        //     }
        //
        //     params.add("%[" + userIds[i] + "]%");
        // }
        // countHql.append(")) and gb.id > 0");
        // queryHql.append(")) and gb.id > 0");
        //
        //
        // if (platformId != null) {
        //     countHql.append(" and gb.platformId=?").append(paramIndex);
        //     queryHql.append(" and gb.platformId=?").append(paramIndex++);
        //     params.add(platformId);
        // }
        // if (StringUtils.isNotEmpty(sTime)) {
        //     countHql.append(" and gb.time>=?").append(paramIndex);
        //     queryHql.append(" and gb.time>=?").append(paramIndex++);
        //     params.add(sTime);
        // }
        // if (StringUtils.isNotEmpty(eTime)) {
        //     countHql.append(" and gb.time<?").append(paramIndex);
        //     queryHql.append(" and gb.time<?").append(paramIndex++);
        //     params.add(eTime);
        // }
        //
        // queryHql.append("order by gb.time desc,gb.id desc");
        //
        // Object[] values = params.toArray();
        // return superDao.findPageList(queryHql.toString(), countHql.toString(), values, start, limit);
    }

    @Override
    public double getBillingOrder(int userId, String startTime, String endTime) {
        String hql = "select sum(money) from " + tab + " where userId = ?0 and time >= ?1 and time < ?2";
        Object[] values = {userId, startTime, endTime};
        Object result = superDao.unique(hql, values);
        return result != null ? ((Number) result).doubleValue() : 0;
    }

    @Override
    public boolean save(GameBets gameBets) {
        return superDao.save(gameBets);
    }
}
