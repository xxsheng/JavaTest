package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRedPacketRainIp;

/**
 * Created by Nick on 2017/3/18.
 */
public interface ActivityRedPacketRainIpDao {
    boolean add(ActivityRedPacketRainIp rainIp);

    ActivityRedPacketRainIp getByIp(String ip);
}
