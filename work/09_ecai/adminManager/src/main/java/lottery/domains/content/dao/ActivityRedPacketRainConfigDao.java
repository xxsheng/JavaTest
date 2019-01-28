package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRedPacketRainConfig;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainConfigDao {
    ActivityRedPacketRainConfig getConfig();

    boolean updateConfig(int id, String rules, String hours, int durationMinutes);

    boolean updateStatus(int id, int status);
}
