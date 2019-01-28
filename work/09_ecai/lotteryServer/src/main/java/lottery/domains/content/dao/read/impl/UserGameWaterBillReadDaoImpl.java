package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserGameWaterBillReadDao;
import lottery.domains.content.entity.UserGameWaterBill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 老虎机真人体育返水账单DAO
 * Created by Nick on 2017/02/04
 */
@Repository
public class UserGameWaterBillReadDaoImpl implements UserGameWaterBillReadDao {
    private final String tab = UserGameWaterBill.class.getSimpleName();
    private static final String SQL_FIELDS = "ugwb.id, ugwb.user_id, ugwb.indicate_date, ugwb.from_user, ugwb.settle_time, ugwb.scale, ugwb.billing_order, ugwb.user_amount, ugwb.`type`, ugwb.`status`, ugwb.remark";

    @Autowired
    private HibernateSuperReadDao<UserGameWaterBill> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserGameWaterBill.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(ugwb.id) from user_game_water_bill ugwb,user u where ugwb.user_id = u.id and (u.id in ("+ ArrayUtils.transInIds(userIds)+") or ");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_game_water_bill ugwb,user u where ugwb.user_id = u.id and (u.id in("+ArrayUtils.transInIds(userIds)+") or ");

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

        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and ugwb.indicate_date>=:sTime");
            querySql.append(" and ugwb.indicate_date>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and ugwb.indicate_date<:eTime");
            querySql.append(" and ugwb.indicate_date<:eTime");
            params.put("eTime", eTime);
        }

        countSql.append(")");
        querySql.append(") order by ugwb.settle_time desc,ugwb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserGameWaterBill> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }

    @Override
    public PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(ugwb.id) from user_game_water_bill ugwb, user u where ugwb.user_id = u.id and (u.id = :userId or u.upids like :upid)");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_game_water_bill ugwb, user u where ugwb.user_id = u.id and (u.id = :userId or u.upids like :upid)");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("upid", "%["+userId+"]%");

        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and ugwb.indicate_date>=:sTime");
            querySql.append(" and ugwb.indicate_date>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and ugwb.indicate_date<:eTime");
            querySql.append(" and ugwb.indicate_date<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by ugwb.settle_time desc,ugwb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserGameWaterBill> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }

    @Override
    public PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(ugwb.id) from user_game_water_bill ugwb where ugwb.user_id = :userId");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_game_water_bill ugwb where ugwb.user_id = :userId");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and ugwb.indicate_date>=:sTime");
            querySql.append(" and ugwb.indicate_date>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and ugwb.indicate_date<:eTime");
            querySql.append(" and ugwb.indicate_date<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by ugwb.settle_time desc,ugwb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserGameWaterBill> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }

    private List<UserGameWaterBill> convertSqlResults(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<UserGameWaterBill> results = new ArrayList<>(list.size());
        for (Object o : list) {
            Object[] objs = (Object[]) o;

            int index = 0;
            int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            String _indicateDate = objs[index] != null ? objs[index].toString() : ""; index++;
            int _fromUser = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            String _settleTime = objs[index] != null ? objs[index].toString() : ""; index++;
            double _scale = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _billingOrder = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _userAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            int _type = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            String _remarks = objs[index] != null ? objs[index].toString() : "";

            UserGameWaterBill result = new UserGameWaterBill();
            result.setId(_id);
            result.setUserId(_userId);
            result.setIndicateDate(_indicateDate);
            result.setFromUser(_fromUser);
            result.setSettleTime(_settleTime);
            result.setScale(_scale);
            result.setBillingOrder(_billingOrder);
            result.setUserAmount(_userAmount);
            result.setType(_type);
            result.setStatus(_status);
            result.setRemark(_remarks);
            results.add(result);
        }
        return results;
    }

    //
    // @Override
    // public PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit) {
    //     String countHql = "select count(*) from " + tab + " ugwb,User u where ugwb.userId = u.id and (ugwb.userId = ?0 or u.upid = ?1) and u.id > 0";
    //     String queryHql = "select ugwb from " + tab + " ugwb,User u where ugwb.userId = u.id and (ugwb.userId = ?0 or u.upid = ?1) and u.id > 0";
    //
    //     List<Object> params = new LinkedList<>();
    //     params.add(upUserId);
    //     params.add(upUserId);
    //
    //     int paramIndex = 2;
    //     if (StringUtils.isNotEmpty(sTime)) {
    //         countHql += " and ugwb.indicateDate>=?" + paramIndex;
    //         queryHql += " and ugwb.indicateDate>=?" + paramIndex++;
    //         params.add(sTime);
    //     }
    //     if (StringUtils.isNotEmpty(eTime)) {
    //         countHql += " and ugwb.indicateDate<?" + paramIndex;
    //         queryHql += " and ugwb.indicateDate<?" + paramIndex++;
    //         params.add(eTime);
    //     }
    //
    //     queryHql += " order by ugwb.settleTime desc";
    //
    //     Object[] values = params.toArray();
    //     return superDao.findPageList(queryHql, countHql, values, start, limit);
    // }
}
