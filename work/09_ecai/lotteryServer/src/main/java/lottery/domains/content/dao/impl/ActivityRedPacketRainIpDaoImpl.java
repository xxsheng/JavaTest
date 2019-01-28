package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRedPacketRainIpDao;
import lottery.domains.content.entity.ActivityRedPacketRainIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/18.
 */
@Repository
public class ActivityRedPacketRainIpDaoImpl implements ActivityRedPacketRainIpDao {
    private final String tab = ActivityRedPacketRainIp.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRedPacketRainIp> superDao;

    @Override
    public boolean add(ActivityRedPacketRainIp rainIp) {
        return superDao.save(rainIp);
    }

    @Override
    public ActivityRedPacketRainIp getByIp(String ip) {
        String hql = "from " + tab + " where ip = ?0";
        Object[] values = {ip};
        return (ActivityRedPacketRainIp) superDao.unique(hql, values);
    }
}
