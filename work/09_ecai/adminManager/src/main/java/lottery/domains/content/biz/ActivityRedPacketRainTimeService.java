package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityRedPacketRainTime;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainTimeService {
    boolean add(ActivityRedPacketRainTime time);

    ActivityRedPacketRainTime getByDateAndHour(String date, String hour);

    boolean initTimes(int days);
}
