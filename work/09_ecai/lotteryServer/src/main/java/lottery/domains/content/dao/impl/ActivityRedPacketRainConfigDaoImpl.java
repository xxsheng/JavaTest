package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRedPacketRainConfigDao;
import lottery.domains.content.entity.ActivityRedPacketRainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityRedPacketRainConfigDaoImpl implements ActivityRedPacketRainConfigDao {
    private final String tab = ActivityRedPacketRainConfig.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRedPacketRainConfig> superDao;

    @Override
    public ActivityRedPacketRainConfig getConfig() {
        String hql = "from " + tab;
        return superDao.list(hql, 0, 1).get(0);
    }
}
