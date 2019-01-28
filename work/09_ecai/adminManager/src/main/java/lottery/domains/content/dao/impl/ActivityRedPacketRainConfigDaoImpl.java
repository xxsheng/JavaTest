package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRedPacketRainConfigDao;
import lottery.domains.content.entity.ActivityRedPacketRainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        List<ActivityRedPacketRainConfig> list = superDao.list(hql, 0, 1);
        return list == null || list.size() <= 0 ? null : list.get(0);
    }

    @Override
    public boolean updateConfig(int id, String rules, String hours, int durationMinutes) {
        String hql = "update " + tab + " set rules = ?0, hours = ?1, durationMinutes = ?2 where id = ?3";
        Object[] values = {rules, hours, durationMinutes, id};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateStatus(int id, int status) {
        String hql = "update " + tab + " set status = ?0 where id = ?1";
        Object[] values = {status, id};
        return superDao.update(hql, values);
    }
}
