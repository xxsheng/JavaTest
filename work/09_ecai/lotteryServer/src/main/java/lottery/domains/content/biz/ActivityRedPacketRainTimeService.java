package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityRedPacketRainTime;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainTimeService {
    ActivityRedPacketRainTime getByDateAndHour(String date, String hour);

    List<ActivityRedPacketRainTime> listByDate(String sDate, String eDate);
}
