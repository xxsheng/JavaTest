package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.ActivityRebateWheelBillDao;
import lottery.domains.content.entity.ActivityRebateWheelBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/11/27.
 */
@Repository
public class ActivityRebateWheelBillDaoImpl implements ActivityRebateWheelBillDao {
    private final String tab = ActivityRebateWheelBill.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<ActivityRebateWheelBill> superDao;

    @Override
    public boolean add(ActivityRebateWheelBill bill) {
        return superDao.save(bill);
    }

    @Override
    public ActivityRebateWheelBill getByUserIdAndDate(int userId, String date) {
        String hql = "from " + tab + " where userId = ?0 and date = ?1";
        Object[] values = {userId, date};
        return (ActivityRebateWheelBill) superDao.unique(hql, values);
    }
}
