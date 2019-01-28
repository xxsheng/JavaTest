package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserDailySettleReadDao;
import lottery.domains.content.entity.UserDailySettle;
import org.apache.commons.collections.CollectionUtils;
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
 * 契约日结DAO
 * Created by Nick on 2016/10/31.
 */
@Repository
public class UserDailySettleReadDaoImpl implements UserDailySettleReadDao {
    private final String tab = UserDailySettle.class.getSimpleName();
    private static final String SQL_FIELDS = "uds.id, uds.user_id, uds.scale_level, uds.loss_level, uds.sales_level, uds.user_level, uds.min_valid_user, uds.create_time, uds.agree_time, uds.start_date, uds.end_date, uds.total_amount, uds.`status`, uds.`fixed`, uds.`min_scale`, uds.`max_scale`";

    @Autowired
    private HibernateSuperReadDao<UserDailySettle> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserDailySettle.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public Long getCountUser(int userId) {
        String hql = "select count(id) from " + tab + " udsb where userId = ?0";
        Object[] values = {userId};
        return (Long) superDao.unique(hql, values);
    }
    
    @Override
    public PageList searchByTeam(int[] userIds, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(uds.id) from user_daily_settle uds,user u where uds.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_daily_settle uds,user u where uds.user_id = u.id and (u.id in("+ArrayUtils.transInIds(userIds)+") or ");

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
        querySql.append(") order by uds.create_time desc,uds.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();
        List<UserDailySettle> results = convertSqlResults(list);
        pageList.setList(results);
        return pageList;
    }

    @Override
    public PageList searchByTeam(int userId, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(uds.id) from user_daily_settle uds, user u where uds.user_id = u.id and (u.id = :userId or u.upids like :upid)");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_daily_settle uds, user u where uds.user_id = u.id and (u.id = :userId or u.upids like :upid)");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("upid", "%["+userId+"]%");

        querySql.append(" order by uds.create_time desc,uds.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();

        List<UserDailySettle> results = convertSqlResults(list);

        pageList.setList(results);
        return pageList;
    }

    @Override
    public PageList searchByUserId(int userId, int start, int limit) {
        StringBuffer countSql = new StringBuffer("select count(uds.id) from user_daily_settle uds where uds.user_id = :userId");
        StringBuffer querySql = new StringBuffer("select ").append(SQL_FIELDS).append(" from user_daily_settle uds where uds.user_id = :userId");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        querySql.append(" order by uds.create_time desc,uds.id desc");

        PageList pageList = superDao.findPageList(countSql.toString(), querySql.toString(), params, start, limit);

        if (pageList == null) {
            return new PageList();
        }
        List<?> list = pageList.getList();

        List<UserDailySettle> results = convertSqlResults(list);

        pageList.setList(results);
        return pageList;
    }

    private List<UserDailySettle> convertSqlResults(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<UserDailySettle> results = new ArrayList<>(list.size());
        for (Object o : list) {
            Object[] objs = (Object[]) o;

            int index = 0;

            int _id = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0; index++;
            int _userId = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;  index++;
            String _scale = objs[index] != null ? objs[index].toString() : ""; index++;
			String _loss = objs[index] != null ? objs[index].toString() : ""; index++;
			String _sales = objs[index] != null ? objs[index].toString() : ""; index++;
			String _user = objs[index] != null ? objs[index].toString() : ""; index++;
            int _minValidUser = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;  index++;
            String _createTime = objs[index] != null ? objs[index].toString() : "";  index++;
            String _agreeTime = objs[index] != null ? objs[index].toString() : "";  index++;
            String _startDate = objs[index] != null ? objs[index].toString() : "";  index++;
            String _endDate = objs[index] != null ? objs[index].toString() : "";  index++;
            double _totalAmount = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;  index++;
            int _status = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;  index++;
            int _fixed = objs[index] != null ? Integer.valueOf(objs[index].toString()) : 0;  index++;
            double _minScale = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;  index++;
            double _maxScale = objs[index] != null ? ((BigDecimal) objs[index]).doubleValue() : 0;  index++;

            UserDailySettle result = new UserDailySettle();
            result.setId(_id);
            result.setUserId(_userId);
            result.setScaleLevel(_scale);
			result.setSalesLevel(_sales); 
			result.setLossLevel(_loss); 
			result.setUserLevel(_user);
            result.setMinValidUser(_minValidUser);
            result.setCreateTime(_createTime);
            result.setAgreeTime(_agreeTime);
            result.setStartDate(_startDate);
            result.setEndDate(_endDate);
            result.setTotalAmount(_totalAmount);
            result.setStatus(_status);
            result.setFixed(_fixed);
            result.setMinScale(_minScale);
            result.setMaxScale(_maxScale);

            results.add(result);
        }
        return results;
    }

    @Override
    public List<UserDailySettle> findByUserIds(List<Integer> userIds) {
        String hql = "from " + tab + " where userId in( " +  ArrayUtils.transInIds(userIds) + ")";
        return superDao.list(hql);
    }


    // @Override
    // public PageList searchByDirectLowers(int upUserId, int start, int limit) {
    //     String countHql = "select count(*) from " + tab + " uds,User u where uds.userId = u.id and (uds.userId = ?0 or u.upid = ?1) and u.id >0";
    //     String queryHql = "select uds from " + tab + " uds,User u where uds.userId = u.id and (uds.userId = ?0 or u.upid = ?1) and u.id > 0 order by uds.createTime desc";
    //     Object[] values = {upUserId, upUserId};
    //     return superDao.findPageList(queryHql, countHql, values, start, limit);
    // }
    //
    // @Override
    // public List<UserDailySettle> findByDirectLowers(int upUserId) {
    //     String queryHql = "select uds from " + tab + " uds,User u where uds.userId = u.id and u.upid = ?0 and u.id > 0";
    //     Object[] values = {upUserId};
    //     return superDao.list(queryHql, values);
    // }
}
