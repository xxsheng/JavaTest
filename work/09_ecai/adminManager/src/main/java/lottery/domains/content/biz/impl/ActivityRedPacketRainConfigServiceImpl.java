package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.ActivityRedPacketRainConfigService;
import lottery.domains.content.biz.ActivityRedPacketRainTimeService;
import lottery.domains.content.dao.ActivityRedPacketRainConfigDao;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.entity.ActivityRedPacketRainConfig;
import lottery.domains.content.global.DbServerSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityRedPacketRainConfigServiceImpl implements ActivityRedPacketRainConfigService {
    @Autowired
    private ActivityRedPacketRainConfigDao configDao;
    @Autowired
    private ActivityRedPacketRainTimeService timeService;
    @Autowired
    private DbServerSyncDao dbServerSyncDao;

    @Override
    public ActivityRedPacketRainConfig getConfig() {
        return configDao.getConfig();
    }

    @Override
    public boolean updateConfig(int id, String rules, String hours, int durationMinutes) {
        boolean updated = configDao.updateConfig(id, rules, hours, durationMinutes);
        return updated;
    }

    @Override
    public boolean updateStatus(int id, int status) {
        boolean updated = configDao.updateStatus(id, status);
        if (updated) {
            timeService.initTimes(2);
        }
        dbServerSyncDao.update(DbServerSyncEnum.ACTIVITY);
        return updated;
    }
}
