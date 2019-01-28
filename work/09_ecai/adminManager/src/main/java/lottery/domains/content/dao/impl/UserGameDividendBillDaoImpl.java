package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserGameDividendBillDao;
import lottery.domains.content.entity.UserGameDividendBill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2017/1/23.
 */
@Repository
public class UserGameDividendBillDaoImpl implements UserGameDividendBillDao {
    private final String tab = UserGameDividendBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserGameDividendBill> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        return superDao.findPageList(UserGameDividendBill.class, "id", criterions, orders, start, limit);
    }

    @Override
    public double sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount, Integer status) {
        String hql = "select sum(userAmount) from " + tab + " where 1=1";

        Map<String, Object> params = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            hql += " and userId in :userId";
            params.put("userId", userIds);
        }
        if (StringUtils.isNotEmpty(sTime)) {
            hql += " and indicateStartDate >= :sTime";
            params.put("sTime", sTime);
        }
        if (StringUtils.isNotEmpty(eTime)) {
            hql += " and indicateEndDate <= :eTime";
            params.put("eTime", eTime);
        }
        if (minUserAmount != null) {
            hql += " and userAmount >= :minUserAmount";
            params.put("minUserAmount", minUserAmount);
        }
        if (maxUserAmount != null) {
            hql += " and userAmount <= :maxUserAmount";
            params.put("maxUserAmount", maxUserAmount);
        }
        if (status != null) {
            hql += " and status = :status";
            params.put("status", status);
        }
        Object result = superDao.uniqueWithParams(hql, params);
        if (result == null) {
            return 0;
        }
        return (Double) result;
    }

    @Override
    public List<UserGameDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
        return superDao.findByCriteria(UserGameDividendBill.class, criterions, orders);
    }

    @Override
    public UserGameDividendBill getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (UserGameDividendBill) superDao.unique(hql, values);
    }

    @Override
    public boolean add(UserGameDividendBill dividendBill) {
        return superDao.save(dividendBill);
    }

    @Override
    public boolean update(int id, int status, double userAmount, String remarks) {
        String hql = "update " + tab + " set status = ?1, userAmount = ?2, remarks = ?3 where id = ?0";
        Object[] values = {id, status, userAmount, remarks};
        return superDao.update(hql, values);
    }

    @Override
    public boolean del(int id) {
        String hql = "delete from " + tab + " where id = ?0";
        Object[] values = {id};
        return superDao.delete(hql, values);
    }
}
