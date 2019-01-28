package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityFirstRechargeBill;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityFirstRechargeBillDao {
    boolean add(ActivityFirstRechargeBill bill);

    ActivityFirstRechargeBill getByUserIdAndDate(int userId, String date);

    ActivityFirstRechargeBill getByDateAndIp(String date, String ip);
}
