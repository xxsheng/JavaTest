package lottery.domains.content.biz;

import lottery.domains.content.entity.UserGameAccount;
import lottery.web.WebJSON;

/**
 * Created by Nick on 2016/12/26.
 */
public interface UserGameAccountService {
    /**
     * 获取游戏账号，没有则创建
     */
    UserGameAccount createIfNoAccount(WebJSON webJSON, int userId, String username, int platformId, int model);

    String getGameUrlForword(WebJSON webJSON, String username, int model);

    UserGameAccount getByUsername(String username, int platformId, int model);

    /**
     * 修改密码
     */
    boolean modPwd(WebJSON webJSON, int userId, int platformId, String password);
    /**
     * 解密密码
     */
    String decryptPwd(String password);
    /**
     * 加密密码
     */
    String encryptPwd(String password);

    /**
     * 根据用户ID和平台ID获取用户第三方账号信息
     */
    UserGameAccount get(int userId, int platformId, int model);

    /**
     * 保存账号
     */
    UserGameAccount save(UserGameAccount account);
}
