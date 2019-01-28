package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.ActivityRedPacketRainTimeService;
import lottery.domains.content.dao.ActivityRedPacketRainConfigDao;
import lottery.domains.content.dao.ActivityRedPacketRainTimeDao;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.entity.ActivityRedPacketRainConfig;
import lottery.domains.content.entity.ActivityRedPacketRainTime;
import lottery.domains.content.global.DbServerSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityRedPacketRainTimeServiceImpl implements ActivityRedPacketRainTimeService{
    @Autowired
    private ActivityRedPacketRainTimeDao timeDao;
    @Autowired
    private ActivityRedPacketRainConfigDao configDao;
    @Autowired
    private DbServerSyncDao dbServerSyncDao;

    @Override
    public boolean add(ActivityRedPacketRainTime time) {
        return timeDao.add(time);
    }

    @Override
    public ActivityRedPacketRainTime getByDateAndHour(String date, String hour) {
        return timeDao.getByDateAndHour(date, hour);
    }

    @Override
    public synchronized boolean initTimes(int days) {
        ActivityRedPacketRainConfig config = configDao.getConfig();
        if (config == null || config.getStatus() == 0) {
            return false;
        }

        int durationMinutes = config.getDurationMinutes();
        int maxEndMinute = 60 - durationMinutes;
        if (maxEndMinute <= 0) {
            maxEndMinute = 50;
        }

        int addedCount = 0;
        for (int i = 0; i < days; i++) {
            String date = new Moment().add(i, "days").format("yyyy-MM-dd");
            String hours = config.getHours();
            String[] hoursArr = hours.split(",");
            for (String hour : hoursArr) {
                String _hour = String.format("%02d", Integer.valueOf(hour));
                ActivityRedPacketRainTime rainTime = timeDao.getByDateAndHour(date, _hour);
                if (rainTime == null) {
                    rainTime = new ActivityRedPacketRainTime();

                    Random random = new Random();
                    int minute = random.nextInt(maxEndMinute);
                    if (minute <= 0) {
                        minute = 1;
                    }
                    if (minute >= 60) {
                        minute = 10;
                    }

                    String _minute = String.format("%02d", minute);
                    String _second = "00";

                    String _startTime = date + " " + _hour + ":" + _minute + ":" + _second;
                    String _endTime = new Moment().fromTime(_startTime).add(durationMinutes, "minutes").toSimpleTime();

                    rainTime.setDate(date);
                    rainTime.setHour(_hour);
                    rainTime.setStartTime(_startTime);
                    rainTime.setEndTime(_endTime);
                    timeDao.add(rainTime);
                    addedCount++;
                }
            }
        }

        if (addedCount > 0) {
            dbServerSyncDao.update(DbServerSyncEnum.ACTIVITY);
        }

        return true;
    }
}
