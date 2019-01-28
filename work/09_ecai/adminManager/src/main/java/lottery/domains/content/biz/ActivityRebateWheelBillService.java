package lottery.domains.content.biz;

import javautils.jdbc.PageList;

/**
 * Created by Nick on 2017/11/27.
 */
public interface ActivityRebateWheelBillService {
    PageList find(String username, String minTime, String maxTime, String ip, int start, int limit);

    double sumAmount(String username, String minTime, String maxTime, String ip);
}
