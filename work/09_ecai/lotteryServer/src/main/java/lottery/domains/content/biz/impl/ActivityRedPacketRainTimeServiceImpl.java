package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.ActivityRedPacketRainTimeService;
import lottery.domains.content.dao.ActivityRedPacketRainTimeDao;
import lottery.domains.content.entity.ActivityRedPacketRainTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
@Service
public class ActivityRedPacketRainTimeServiceImpl implements ActivityRedPacketRainTimeService{
    @Autowired
    private ActivityRedPacketRainTimeDao timeDao;

    @Override
    @Transactional(readOnly = true)
    public ActivityRedPacketRainTime getByDateAndHour(String date, String hour) {
        return timeDao.getByDateAndHour(date, hour);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityRedPacketRainTime> listByDate(String sDate, String eDate) {
        return timeDao.listByDate(sDate, eDate);
    }
}
