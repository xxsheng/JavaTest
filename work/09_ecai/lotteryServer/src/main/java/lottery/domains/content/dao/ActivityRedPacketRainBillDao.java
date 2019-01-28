package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRedPacketRainBill;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainBillDao {
    boolean add(ActivityRedPacketRainBill bill);

    ActivityRedPacketRainBill getByUserIdAndDateAndHour(int userId, String date, String hour);
}
