package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.ActivityFirstRechargeConfigService;
import lottery.domains.content.dao.ActivityFirstRechargeConfigDao;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.entity.ActivityFirstRechargeConfig;
import lottery.domains.content.global.DbServerSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityFirstRechargeConfigServiceImpl implements ActivityFirstRechargeConfigService {
    @Autowired
    private ActivityFirstRechargeConfigDao configDao;
    @Autowired
    private DbServerSyncDao dbServerSyncDao;

    @Override
    public ActivityFirstRechargeConfig getConfig() {
        return configDao.getConfig();
    }

    @Override
    public boolean updateConfig(int id, String rules) {
        boolean updated = configDao.updateConfig(id, rules);
        dbServerSyncDao.update(DbServerSyncEnum.ACTIVITY);
        return updated;
    }

    @Override
    public boolean updateStatus(int id, int status) {
        boolean updated = configDao.updateStatus(id, status);
        dbServerSyncDao.update(DbServerSyncEnum.ACTIVITY);
        return updated;
    }
}
