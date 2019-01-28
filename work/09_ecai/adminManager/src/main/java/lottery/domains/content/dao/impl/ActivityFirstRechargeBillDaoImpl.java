package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityFirstRechargeBillDao;
import lottery.domains.content.entity.ActivityFirstRechargeBill;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityFirstRechargeBillDaoImpl implements ActivityFirstRechargeBillDao {
    private final String tab = ActivityFirstRechargeBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityFirstRechargeBill> superDao;

    @Override
    public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(ActivityFirstRechargeBill.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public double sumAmount(Integer userId, String sDate, String eDate, String ip) {
        String hql = "select sum(amount) from " + tab + " where 1=1";

        Map<String, Object> params = new HashMap<>();
        if (userId != null) {
            hql += " and userId = :userId";
            params.put("userId", userId);
        }
        if(StringUtil.isNotNull(sDate)) {
            hql += " and date >= :sDate";
            params.put("sDate", sDate);
        }
        if(StringUtil.isNotNull(eDate)) {
            hql += " and date < :eDate";
            params.put("eDate", eDate);
        }
        if (StringUtil.isNotNull(ip)) {
            hql += " and ip = :ip";
            params.put("ip", ip);
        }
        Object result = superDao.uniqueWithParams(hql, params);
        if (result == null) {
            return 0;
        }
        return (Double) result;
    }

    @Override
    public ActivityFirstRechargeBill getByDateAndIp(String date, String ip) {
        String hql = "from " + tab + " where date = ?0 and ip = ?1";
        Object[] values = {date, ip};
        return (ActivityFirstRechargeBill) superDao.unique(hql, values);
    }

    @Override
    public ActivityFirstRechargeBill getByUserIdAndDate(int userId, String date) {
        String hql = "from " + tab + " where userId = ?0 and date = ?1";
        Object[] values = {userId, date};
        return (ActivityFirstRechargeBill) superDao.unique(hql, values);
    }

    @Override
    public boolean add(ActivityFirstRechargeBill bill) {
        return superDao.save(bill);
    }
}
