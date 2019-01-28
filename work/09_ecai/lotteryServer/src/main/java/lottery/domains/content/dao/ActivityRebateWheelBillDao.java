package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRebateWheelBill;

/**
 * Created by Nick on 2017/11/27.
 */
public interface ActivityRebateWheelBillDao {
    boolean add(ActivityRebateWheelBill bill);

    ActivityRebateWheelBill getByUserIdAndDate(int userId, String date);
}
