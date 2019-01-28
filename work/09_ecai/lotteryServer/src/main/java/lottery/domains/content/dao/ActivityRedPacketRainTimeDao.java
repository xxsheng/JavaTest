package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRedPacketRainTime;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainTimeDao {
    ActivityRedPacketRainTime getByDateAndHour(String date, String hour);

    List<ActivityRedPacketRainTime> listByDate(String sDate, String eDate);
}
