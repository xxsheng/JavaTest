package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.ActivityRedPacketRainConfigService;
import lottery.domains.content.dao.ActivityRedPacketRainConfigDao;
import lottery.domains.content.entity.ActivityRedPacketRainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityRedPacketRainConfigServiceImpl implements ActivityRedPacketRainConfigService {
    @Autowired
    private ActivityRedPacketRainConfigDao configDao;

    @Override
    @Transactional(readOnly = true)
    public ActivityRedPacketRainConfig getConfig() {
        return configDao.getConfig();
    }
}
