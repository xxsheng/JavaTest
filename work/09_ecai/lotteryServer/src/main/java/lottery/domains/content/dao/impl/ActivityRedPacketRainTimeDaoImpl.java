package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRedPacketRainTimeDao;
import lottery.domains.content.entity.ActivityRedPacketRainTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityRedPacketRainTimeDaoImpl implements ActivityRedPacketRainTimeDao {
    private final String tab = ActivityRedPacketRainTime.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRedPacketRainTime> superDao;

    @Override
    public ActivityRedPacketRainTime getByDateAndHour(String date, String hour) {
        String hql = "from " + tab + " where date = ?0 and hour = ?1";
        Object[] values = {date, hour};
        return (ActivityRedPacketRainTime) superDao.unique(hql, values);
    }

    @Override
    public List<ActivityRedPacketRainTime> listByDate(String sDate, String eDate) {
        String hql = "from " + tab + " where date >= ?0 and date <= ?1 order by startTime asc";
        Object[] values = {sDate, eDate};
        return superDao.list(hql, values);
    }
}
