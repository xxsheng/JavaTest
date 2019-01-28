package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityRedPacketRainConfig;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainConfigService {
    ActivityRedPacketRainConfig getConfig();

    boolean updateConfig(int id, String rules, String hours, int durationMinutes);

    boolean updateStatus(int id, int status);
}
