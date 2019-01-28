package lottery.domains.content.biz;

import lottery.domains.content.entity.ActivityRedPacketRainBill;
import lottery.web.WebJSON;

import java.util.List;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityRedPacketRainBillService {
    boolean add(ActivityRedPacketRainBill bill);

    ActivityRedPacketRainBill getByUserIdAndDateAndHour(int userId, String date, String hour);

    Double collect(WebJSON json, int userId, String ip);
}
