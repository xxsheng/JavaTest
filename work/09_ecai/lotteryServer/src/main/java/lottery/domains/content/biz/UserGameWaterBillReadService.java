package lottery.domains.content.biz;

import javautils.jdbc.PageList;

/**
 * 老虎机真人体育返水账单Service
 * Created by Nick on 2017/02/04
 */
public interface UserGameWaterBillReadService {
    // PageList search(List<Integer> userIds, String sTime, String eTime, int start, int limit);
    //
    // PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit);

    /**
     * 查询所有数据，按创建时间倒序排序
     */
    PageList searchAll(String sTime, String eTime, int start, int limit);

    /**
     * 查询指定用户的团队数据，按创建时间倒序排序
     */
    PageList searchByTeam(int[] userIds, String sTime, String eTime, int start, int limit);

    /**
     * 查询指定用户的团队数据，按创建时间倒序排序
     */
    PageList searchByTeam(int userId, String sTime, String eTime, int start, int limit);

    /**
     * 查询某个用户的自己的数据
     */
    PageList searchByUserId(int userId, String sTime, String eTime, int start, int limit);
}
