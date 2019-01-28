package lottery.domains.content.biz;

import javautils.jdbc.PageList;

/**
 * Created by Nick on 2017/3/17.
 */
public interface ActivityFirstRechargeBillService {
    PageList find(String username, String sDate, String eDate, String ip, int start, int limit);

    double sumAmount(String username, String sDate, String eDate, String ip);
    
    double tryCollect(int userId, double rechargeAmount, String ip);
    
}
