package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;

/**
 * Created by Nick on 2016/12/28.
 */
public interface GameBetsReadService {
    /**
     * 查询所有注单
     */
    PageList searchAll(Integer platformId, String sTime, String eTime, int start, int limit);

    PageList searchByUserId(int userId, Integer platformId, String sTime, String eTime, int start, int limit);

    PageList searchByTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit);

    PageList searchByDirectTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit);

    PageList searchByTeam(Integer[] userIds, Integer platformId, String sTime, String eTime, int start, int limit);

    double getBillingOrder(int userId, String startTime, String endTime);
}
