package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRebateWheelIpDao;
import lottery.domains.content.entity.ActivityRebateWheelIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/11/27.
 */
@Repository
public class ActivityRebateWheelIpDaoImpl implements ActivityRebateWheelIpDao {
    private final String tab = ActivityRebateWheelIp.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRebateWheelIp> superDao;

    @Override
    public boolean add(ActivityRebateWheelIp entity) {
        return superDao.save(entity);
    }

    @Override
    public ActivityRebateWheelIp getByIp(String ip) {
        String hql = "from " + tab + " where ip = ?0";
        Object[] values = {ip};
        return (ActivityRebateWheelIp) superDao.unique(hql, values);
    }
}
