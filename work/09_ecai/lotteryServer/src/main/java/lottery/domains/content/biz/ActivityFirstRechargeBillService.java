package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityFirstRechargeBill;
import lottery.web.WebJSON;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityFirstRechargeBillService {
    boolean add(ActivityFirstRechargeBill bill);

    ActivityFirstRechargeBill getByUserIdAndDate(int userId, String date);

    double tryCollect(int userId, double rechargeAmount, String ip);
}
