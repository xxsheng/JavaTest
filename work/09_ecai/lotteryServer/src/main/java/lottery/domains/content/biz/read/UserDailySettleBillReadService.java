package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDailySettleBill;

/**
 * 契约日结账单Service
 * Created by Nick on 2016/10/31.
 */
public interface UserDailySettleBillReadService {

    UserDailySettleBill getById(int id);

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

    double getTotalUnIssue(int userId);

    // /**
    //  * 查找契约分页数据
    //  */
    // PageList search(List<Integer> userIds, String sTime, String eTime, int start, int limit);
    //
    // PageList searchByDirectLowers(int upUserId, String sTime, String eTime, int start, int limit);
}
