package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.encrypt.DESUtil;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.pt.PTAPI;
import lottery.domains.content.api.sb.Win88SBAPI;
import lottery.domains.content.biz.UserGameAccountService;
import lottery.domains.content.dao.UserGameAccountDao;
import lottery.domains.content.dao.read.UserGameAccountReadDao;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Nick on 2016/12/27.
 */
@Service
public class UserGameAccountServiceImpl implements UserGameAccountService {
    private static final Logger log = LoggerFactory.getLogger(UserGameAccountServiceImpl.class);
    private static final String PWD_KEY = "$*JUjidjn&0jjfikla1knnkalii097hfk1lf8nGi8el";

    @Autowired
    private PTAPI ptAPI;
    @Autowired
    private AGAPI agAPI;
    @Autowired
    private Win88SBAPI win88SBAPI;
    @Autowired
    private UserGameAccountDao uGameAccountDao;
    @Autowired
    private UserGameAccountReadDao uGameAccountReadDao;
    @Autowired
    private DataFactory dataFactory;

    @Override
    public UserGameAccount createIfNoAccount(WebJSON webJSON, int userId, String username, int platformId, int model) {
        UserGameAccount account = dataFactory.getGameAccount(userId, platformId, model);
        if (account != null) {
            return account;
        }

        // 生成随机密码
        String password = RandomStringUtils.random(6, true, false);
        // 加密随机密码
        String encryptStr = DESUtil.getInstance().encryptStr(password, PWD_KEY);

        String platformUsername;
        if (platformId == Global.BILL_ACCOUNT_PT) { // 11：PT
            platformUsername = createByPT(webJSON, username, password);
        }
        else if (platformId == Global.BILL_ACCOUNT_AG) { // 4：AG
            platformUsername = username +"_"+ RandomStringUtils.random(4, false, true);
            platformUsername = createByAG(webJSON, platformUsername, password, model);
        }
        else if (platformId == Global.BILL_ACCOUNT_IM) { // 12：IM
            platformUsername = username; // 直接创建账号
        }
        else if (platformId == Global.BILL_ACCOUNT_SB) { // 13：SB
            platformUsername = username + RandomStringUtils.random(4, false, true);
            platformUsername = createBySB(webJSON, platformUsername);
        }
        else {
            webJSON.set(2, "2-7007");
            return null;
        }

        // 用户名为空，返回
        if (platformUsername == null) {
            if (webJSON.getError() == 0) {
                webJSON.set(1, "1-1");
            }
            return null;
        }

        // 创建成功，保存账号
        account = new UserGameAccount();
        account.setUserId(userId);
        account.setPlatformId(platformId);
        account.setUsername(platformUsername);
        account.setPassword(encryptStr);
        if (model != 1 && model != 0) {
            account.setModel(1);
        }
        else {
            account.setModel(model);
        }
        account.setTime(new Moment().toSimpleTime());
        uGameAccountDao.save(account);
        return account;
    }

    @Override
    public String getGameUrlForword(WebJSON webJSON, String username, int model) {
        String actype = model == 1 ? "1" : "0";
        String gameUrlForword = agAPI.getGameUrlForword(webJSON, username, actype);
        return gameUrlForword;
    }

    @Override
    @Transactional(readOnly = true)
    public UserGameAccount getByUsername(String username, int platformId, int model) {
        return uGameAccountReadDao.getByUsername(username, platformId, model);
    }

    @Override
    public boolean modPwd(WebJSON webJSON, int userId, int platformId, String password) {
        UserGameAccount gameAccount = dataFactory.getGameAccount(userId, platformId, 1); // 只有PT有修改密码功能，所以默认是真钱
        if (gameAccount == null) {
            webJSON.set(2, "2-8010"); // 需首次登录游戏
            return false;
        }

        boolean succeed = false;
        switch (platformId) {
            case 11: // PT
                succeed = ptAPI.playerUpdatePassword(webJSON, gameAccount.getUsername(), password);
                break;
            default:
                webJSON.set(2, "2-7007"); // 暂不支持的平台
                break;
        }

        if (succeed) {
            String encryptPwd = encryptPwd(password);
            gameAccount.setPassword(encryptPwd);
        }

        return succeed;
    }

    @Override
    public String decryptPwd(String password) {
        return DESUtil.getInstance().decryptStr(password, PWD_KEY);
    }

    @Override
    public String encryptPwd(String password) {
        return DESUtil.getInstance().encryptStr(password, PWD_KEY);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGameAccount get(int userId, int platformId, int model) {
        return uGameAccountReadDao.get(userId, platformId, model);
    }

    @Override
    public UserGameAccount save(UserGameAccount account) {
        return uGameAccountDao.save(account);
    }

    private String createByPT(WebJSON webJSON, String username, String password) {
        try {
            String platformUsername = ptAPI.playerCreate(webJSON, username, password);

            // 用户名重复，尝试重新创建，在账号后加a
            if (platformUsername == null) {
                String errorCode = webJSON.getCode();
                if ("2-7000".equals(errorCode)) {
                    username += "a";
                    platformUsername = ptAPI.playerCreate(webJSON, username, password);
                }
            }

            return platformUsername;
        } catch (Exception e) {
            log.error("创建PT账号失败", e);
            return null;
        }
    }

    private String createByAG(WebJSON webJSON, String username, String password, int model) {
        try {
            String actype = model == 1 ? "1" : "0";
            String platformUsername = agAPI.checkOrCreateGameAccount(webJSON, username, password, actype);

            // 用户名重复，尝试重新创建，在账号后加a
            if (platformUsername == null) {
                String errorCode = webJSON.getCode();
                if ("2-8002".equals(errorCode)) {
                    username += "a";
                    platformUsername = agAPI.checkOrCreateGameAccount(webJSON, username, password, actype);
                }
            }

            return platformUsername;
        } catch (Exception e) {
            log.error("创建AG账号失败", e);
            return null;
        }
    }

    private String createBySB(WebJSON webJSON, String username) {
        try {
            boolean created = win88SBAPI.createMember(webJSON, username);

            if (!created) {
                return null;
            }

            return username;
        } catch (Exception e) {
            log.error("创建SB账号失败", e);
            return null;
        }
    }
}
