package lottery.domains.content.dao;

import lottery.domains.content.entity.UserGameAccount;

/**
 * Created by Nick on 2016/12/24.
 */
public interface UserGameAccountDao {
    /**
     * 保存账号
     */
    UserGameAccount save(UserGameAccount account);
}
