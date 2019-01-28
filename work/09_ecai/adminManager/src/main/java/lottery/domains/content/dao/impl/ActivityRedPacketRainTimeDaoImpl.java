package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRedPacketRainTimeDao;
import lottery.domains.content.entity.ActivityRedPacketRainTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityRedPacketRainTimeDaoImpl implements ActivityRedPacketRainTimeDao {
    private final String tab = ActivityRedPacketRainTime.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRedPacketRainTime> superDao;

    @Override
    public boolean add(ActivityRedPacketRainTime time) {
        return superDao.save(time);
    }

    @Override
    public ActivityRedPacketRainTime getByDateAndHour(String date, String hour) {
        String hql = "from " + tab + " where date = ?0 and hour = ?1";
        Object[] values = {date, hour};
        return (ActivityRedPacketRainTime) superDao.unique(hql, values);
    }
}
