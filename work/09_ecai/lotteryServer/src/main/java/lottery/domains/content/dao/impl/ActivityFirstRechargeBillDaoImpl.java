package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityFirstRechargeBillDao;
import lottery.domains.content.entity.ActivityFirstRechargeBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityFirstRechargeBillDaoImpl implements ActivityFirstRechargeBillDao {
    private final String tab = ActivityFirstRechargeBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityFirstRechargeBill> superDao;

    @Override
    public boolean add(ActivityFirstRechargeBill bill) {
        return superDao.save(bill);
    }

    @Override
    public ActivityFirstRechargeBill getByUserIdAndDate(int userId, String date) {
        String hql = "from " + tab + " where userId = ?0 and date = ?1";
        Object[] values = {userId, date};
        return (ActivityFirstRechargeBill) superDao.unique(hql, values);
    }

    @Override
    public ActivityFirstRechargeBill getByDateAndIp(String date, String ip) {
        String hql = "from " + tab + " where date = ?0 and ip = ?1";
        Object[] values = {date, ip};
        return (ActivityFirstRechargeBill) superDao.unique(hql, values);
    }
}
