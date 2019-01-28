package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRedPacketRainBillDao;
import lottery.domains.content.entity.ActivityRedPacketRainBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/17.
 */
@Repository
public class ActivityRedPacketRainBillDaoImpl implements ActivityRedPacketRainBillDao {
    private final String tab = ActivityRedPacketRainBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRedPacketRainBill> superDao;

    @Override
    public boolean add(ActivityRedPacketRainBill bill) {
        return superDao.save(bill);
    }

    @Override
    public ActivityRedPacketRainBill getByUserIdAndDateAndHour(int userId, String date, String hour) {
        String hql = "from " + tab + " where userId = ?0 and date = ?1 and hour = ?2";
        Object[] values = {userId, date, hour};
        return (ActivityRedPacketRainBill) superDao.unique(hql, values);
    }
}
