package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.ActivityFirstRechargeConfigService;
import lottery.domains.content.dao.ActivityFirstRechargeConfigDao;
import lottery.domains.content.entity.ActivityFirstRechargeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityFirstRechargeConfigServiceImpl implements ActivityFirstRechargeConfigService {
    @Autowired
    private ActivityFirstRechargeConfigDao configDao;

    @Override
    @Transactional(readOnly = true)
    public ActivityFirstRechargeConfig getConfig() {
        return configDao.getConfig();
    }
}
