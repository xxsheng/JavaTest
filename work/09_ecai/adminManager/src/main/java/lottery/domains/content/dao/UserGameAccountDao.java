package lottery.domains.content.dao;

import lottery.domains.content.entity.UserGameAccount;

/**
 * Created by Nick on 2016/12/24.
 */
public interface UserGameAccountDao {
    /**
     * 根据平台用户名与平台ID获取平台用户信息
     */
    UserGameAccount get(String platformName, int platformId);

    UserGameAccount get(int userId, int platformId);

    /**
     * 保存账号
     */
    UserGameAccount save(UserGameAccount account);
}
