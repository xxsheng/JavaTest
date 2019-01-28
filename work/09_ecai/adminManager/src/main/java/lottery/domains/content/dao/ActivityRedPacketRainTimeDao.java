package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRedPacketRainTime;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainTimeDao {
    boolean add(ActivityRedPacketRainTime time);

    ActivityRedPacketRainTime getByDateAndHour(String date, String hour);
}
