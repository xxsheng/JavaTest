package lottery.domains.content.dao.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRebateWheelBillDao;
import lottery.domains.content.entity.ActivityRebateWheelBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2017/11/27.
 */
@Repository
public class ActivityRebateWheelBillDaoImpl implements ActivityRebateWheelBillDao {
    private final String tab = ActivityRebateWheelBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRebateWheelBill> superDao;

    @Override
    public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(ActivityRebateWheelBill.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public double sumAmount(Integer userId, String minTime, String maxTime, String ip) {
        String hql = "select sum(amount) from " + tab + " where 1=1";

        Map<String, Object> params = new HashMap<>();
        if (userId != null) {
            hql += " and userId = :userId";
            params.put("userId", userId);
        }
        if(StringUtil.isNotNull(minTime)) {
            hql += " and time >= :minTime";
            params.put("minTime", minTime);
        }
        if(StringUtil.isNotNull(maxTime)) {
            hql += " and time <= :maxTime";
            params.put("maxTime", maxTime);
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
}
