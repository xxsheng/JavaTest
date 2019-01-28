package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityFirstRechargeConfig;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityFirstRechargeConfigService {
    ActivityFirstRechargeConfig getConfig();

    boolean updateConfig(int id, String rules);

    boolean updateStatus(int id, int status);
}
