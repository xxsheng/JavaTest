package lottery.domains.content.dao.read;

import lottery.domains.content.entity.UserGameAccount;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface UserGameAccountReadDao {
    /**
     * 根据用户ID和平台ID获取用户第三方账号信息
     */
    UserGameAccount get(int userId, int platformId, int model);

    UserGameAccount getByUsername(String username, int platformId, int model);

    /**
     * 搜索时间段内总日注册人数，以时间为维度
     */
    List<?> getDayRegistAll(String sTime, String eTime);

    /**
     * 搜索时间段内某个用户团队总日注册人数，以时间为维度
     */
    List<?> getDayRegistByTeam(int userId, String sTime, String eTime);

    /**
     * 搜索时间段内某些用户团队总日注册人数，以时间为维度
     */
    List<?> getDayRegistByTeam(int[] userIds, String sTime, String eTime);
}
