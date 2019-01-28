package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityFirstRechargeConfigDao;
import lottery.domains.content.entity.ActivityFirstRechargeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityFirstRechargeConfigDaoImpl implements ActivityFirstRechargeConfigDao {
    private final String tab = ActivityFirstRechargeConfig.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityFirstRechargeConfig> superDao;

    @Override
    public ActivityFirstRechargeConfig getConfig() {
        String hql = "from " + tab;
        return superDao.list(hql, 0, 1).get(0);
    }

    @Override
    public boolean updateConfig(int id, String rules) {
        String hql = "update " + tab + " set rules = ?0 where id = ?1";
        Object[] values = {rules, id};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateStatus(int id, int status) {
        String hql = "update " + tab + " set status = ?0 where id = ?1";
        Object[] values = {status, id};
        return superDao.update(hql, values);
    }
}
