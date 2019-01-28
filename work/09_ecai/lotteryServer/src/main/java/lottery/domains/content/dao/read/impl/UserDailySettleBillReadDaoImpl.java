package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserDailySettleBillReadDao;
import lottery.domains.content.entity.UserDailySettleBill;
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
 * 契约日结账单DAO
 * Created by Nick on 2016/10/31.
 */
@Repository
public class UserDailySettleBillReadDaoImpl implements UserDailySettleBillReadDao {
    private final String tab = UserDailySettleBill.class.getSimpleName();
    private static final String SQL_FIELDS = "udsb.id, udsb.user_id, udsb.indicate_date, udsb.settle_time, udsb.min_valid_user, udsb.valid_user, udsb.scale, udsb.billing_order, udsb.this_loss, udsb.cal_amount, udsb.user_amount, udsb.lower_total_amount, udsb.lower_paid_amount, udsb.total_received, udsb.available_amount, udsb.issue_type, udsb.`status`, udsb.remarks";

    @Autowired
    private HibernateSuperReadDao<UserDailySettleBill> superDao;

    @Override
    public UserDailySettleBill getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (UserDailySettleBill) superDao.unique(hql, values);
    }

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserDailySettleBill.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(udsb.id) from user_daily_settle_bill udsb,user u where udsb.user_id = u.id and (u.id in ("+ ArrayUtils.transInIds(userIds)+") or ");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_daily_settle_bill udsb,user u where udsb.user_id = u.id and (u.id in("+ArrayUtils.transInIds(userIds)+") or ");

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
            countSql.append(" and udsb.indicate_date>=:sTime");
            querySql.append(" and udsb.indicate_date>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and udsb.indicate_date<:eTime");
            querySql.append(" and udsb.indicate_date<:eTime");
            params.put("eTime", eTime);
        }

        countSql.append(")");
        querySql.append(") order by udsb.settle_time desc,udsb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserDailySettleBill> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }

    @Override
    public PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(udsb.id) from user_daily_settle_bill udsb, user u where udsb.user_id = u.id and (u.id = :userId or u.upids like :upid)");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_daily_settle_bill udsb, user u where udsb.user_id = u.id and (u.id = :userId or u.upids like :upid)");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("upid", "%["+userId+"]%");

        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and udsb.indicate_date>=:sTime");
            querySql.append(" and udsb.indicate_date>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and udsb.indicate_date<:eTime");
            querySql.append(" and udsb.indicate_date<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by udsb.settle_time desc,udsb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserDailySettleBill> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }


    @Override
    public PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(udsb.id) from user_daily_settle_bill udsb where udsb.user_id = :userId");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_daily_settle_bill udsb where udsb.user_id = :userId");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        if (StringUtils.isNotEmpty(sTime)) {
            countSql.append(" and udsb.indicate_date>=:sTime");
            querySql.append(" and udsb.indicate_date>=:sTime");
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            countSql.append(" and udsb.indicate_date<:eTime");
            querySql.append(" and udsb.indicate_date<:eTime");
            params.put("eTime", eTime);
        }

        querySql.append(" order by udsb.settle_time desc,udsb.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserDailySettleBill> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }

    private List<UserDailySettleBill> convertSqlResults(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<UserDailySettleBill> results = new ArrayList<>(list.size());
        for (Object o : list) {
            Object[] objs = (Object[]) o;

            int index = 0;
            int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            String _indicateDate = objs[index] != null ? objs[index].toString() : ""; index++;
            String _settleTime = objs[index] != null ? objs[index].toString() : ""; index++;
            int _minValidUser = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            int _validUser = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            double _scale = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _billingOrder = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _thisLoss = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _calAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _userAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _lowerTotalAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _lowerPaidAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _totalReceived = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            double _availableAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0; index++;
            int _issueType = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            String _remarks = objs[index] != null ? objs[index].toString() : "";

            UserDailySettleBill result = new UserDailySettleBill();
            result.setId(_id);
            result.setUserId(_userId);
            result.setIndicateDate(_indicateDate);
            result.setSettleTime(_settleTime);
            result.setMinValidUser(_minValidUser);
            result.setValidUser(_validUser);
            result.setScale(_scale);
            result.setBillingOrder(_billingOrder);
            result.setThisLoss(_thisLoss);
            result.setCalAmount(_calAmount);
            result.setUserAmount(_userAmount);
            result.setLowerTotalAmount(_lowerTotalAmount);
            result.setLowerPaidAmount(_lowerPaidAmount);
            result.setTotalReceived(_totalReceived);
            result.setAvailableAmount(_availableAmount);
            result.setIssueType(_issueType);
            result.setStatus(_status);
            result.setRemarks(_remarks);
            results.add(result);
        }
        return results;
    }

    @Override
    public double getTotalUnIssue(int userId) {
        String hql = "select sum(lowerTotalAmount)-sum(lowerPaidAmount) from " + tab + " where userId = ?0 and status in (2,3)";
        Object[] values = {userId};
        Object result = superDao.unique(hql, values);
        return result != null ? ((Number) result).doubleValue() : 0;
    }
    // @Override
    // public PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit) {
    //     StringBuffer countSql = new StringBuffer("select count(udsb.id) from user_daily_settle_bill udsb, user u where udsb.user_id = u.id and (u.id = :userId or u.upids like :upid)");
    //     StringBuffer querySql = new StringBuffer("select udsb.id, udsb.user_id, udsb.indicate_date, udsb.settle_time, udsb.min_valid_user, udsb.valid_user, udsb.scale, udsb.billing_order, udsb.user_amount, udsb.status from user_daily_settle_bill udsb, user u where udsb.user_id = u.id and (u.id = :userId or u.upids like :upid)");
    //
    //     Map<String, Object> params = new HashMap<>();
    //     params.put("userId", upUserId);
    //     params.put("upid", "%["+upUserId+"]%");
    //
    //     if (StringUtils.isNotEmpty(sTime)) {
    //         countSql.append(" and udsb.indicate_date>=:sTime");
    //         querySql.append(" and udsb.indicate_date>=:sTime");
    //         params.put("sTime", sTime);
    //     }
    //     if (StringUtils.isNotEmpty(eTime)) {
    //         countSql.append(" and udsb.indicate_date<:eTime");
    //         querySql.append(" and udsb.indicate_date<:eTime");
    //         params.put("eTime", eTime);
    //     }
    //
    //     querySql.append(" order by udsb.settle_time desc,udsb.id desc");
    //
    //     PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);
    //
    //     if (pageList == null) {
    //         return new PageList();
    //     }
    //     List<?> list = pageList.getList();
    //
    //     List<UserDailySettleBill> results = new ArrayList<>(list.size());
    //     for (Object o : list) {
    //         Object[] objs = (Object[]) o;
    //
    //         int index = 0;
    //         int _id = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
    //         int _userId = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
    //         String _indicateDate = objs[index] != null ? objs[index++].toString() : "";
    //         String _settleTime = objs[index] != null ? objs[index++].toString() : "";
    //         int _minValidUser = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
    //         int _validUser = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
    //         double _scale = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
    //         double _billingOrder = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
    //         double _userAmount = objs[index] != null ? ((BigDecimal) objs[index++]).doubleValue() : 0;
    //         int _status = objs[index] != null ? Integer.valueOf(objs[index++].toString()) : 0;
    //
    //         UserDailySettleBill result = new UserDailySettleBill();
    //         result.setId(_id);
    //         result.setUserId(_userId);
    //         result.setIndicateDate(_indicateDate);
    //         result.setSettleTime(_settleTime);
    //         result.setMinValidUser(_minValidUser);
    //         result.setValidUser(_validUser);
    //         result.setScale(_scale);
    //         result.setBillingOrder(_billingOrder);
    //         result.setUserAmount(_userAmount);
    //         result.setStatus(_status);
    //         results.add(result);
    //     }
    //     pageList.setList(results);
    //     return pageList;
    //     //
    //     // String countHql = "select count(id) from " + tab + " udsb,User u where udsb.userId = u.id and (udsb.userId = ?0 or u.upid = ?1) and u.id > 0 and udsb.id > 0";
    //     // String queryHql = "select udsb from " + tab + " udsb,User u where udsb.userId = u.id and (udsb.userId = ?0 or u.upid = ?1) and u.id > 0 and udsb.id > 0";
    //     //
    //     // List<Object> params = new LinkedList<>();
    //     // params.add(upUserId);
    //     // params.add(upUserId);
    //     //
    //     // int paramIndex = 2;
    //     // if (StringUtils.isNotEmpty(sTime)) {
    //     //     countHql += " and udsb.indicateDate>=?" + paramIndex;
    //     //     queryHql += " and udsb.indicateDate>=?" + paramIndex++;
    //     //     params.add(sTime);
    //     // }
    //     // if (StringUtils.isNotEmpty(eTime)) {
    //     //     countHql += " and udsb.indicateDate<?" + paramIndex;
    //     //     queryHql += " and udsb.indicateDate<?" + paramIndex++;
    //     //     params.add(eTime);
    //     // }
    //     //
    //     // queryHql += " order by udsb.settleTime desc";
    //     //
    //     // Object[] values = params.toArray();
    //     // return superDao.findPageList(queryHql, countHql, values, start, limit);
    // }
}
