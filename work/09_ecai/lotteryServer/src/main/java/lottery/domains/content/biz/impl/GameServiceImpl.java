package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.encrypt.DESUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.GameService;
import lottery.domains.content.dao.GameDao;
import lottery.domains.content.entity.Game;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.jobs.MailJob;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.payment.huanst.util.Base64;
import lottery.web.WSC;
import lottery.web.WebJSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Nick on 2017-05-03.
 */
@Service
public class GameServiceImpl implements GameService {
    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    private static final DESUtil DES_UTIL = DESUtil.getInstance();
    private static final int URL_EXPIRE_SECONDS = 120;

    @Autowired
    private DataFactory dataFactory;
    @Value("${game.redirect.url}")
    private String gameRedirectUrl;
    @Value("${game.redirect.des}")
    private String gameRedirectDes;
    @Value("${game.redirect.md5}")
    private String gameRedirectMd5;

    @Autowired
    private GameDao gameDao;
    @Autowired
    private JedisTemplate jedisTemplate;
    @Autowired
    private MailJob mailJob;

    @Override
    public String getPTRedirectUrl(int userId) {
        UserGameAccount gameAccount = dataFactory.getGameAccount(userId, 11, 1);
        if (gameAccount == null) {
            return null;
        }

        String beforeUrl = jedisTemplate.get(WSC.USER_GAME_BEFORE_URL_KEY+userId);
        if (StringUtils.isNotEmpty(beforeUrl)) {
            return beforeUrl;
        }

        String accessKey = ObjectId.get().toString() + RandomStringUtils.random(8, true, true);
        String expireTime = new Moment().add(URL_EXPIRE_SECONDS, "seconds").format("yyMMddHHmmss");
        String data = accessKey + "|" + expireTime;
        String sign = DigestUtils.md5Hex(data + "|" + gameRedirectMd5);
        data += "|" + sign;

        String encryptParam = DES_UTIL.encryptStr(data, gameRedirectDes);

        try {
            encryptParam = Base64.encode(encryptParam.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("跳转第三方游戏时，获取游戏跳转地址失败", e);
            return null;
        }
        encryptParam = encryptParam.replaceAll("\r\n", "");

        String url = gameRedirectUrl;
        if (gameRedirectUrl.indexOf("?") > -1) {
            url = url + "&param=" + encryptParam;
        }
        else {
            url = url + "?param=" + encryptParam;
        }

        jedisTemplate.setex(WSC.USER_GAME_TOKEN_KEY+accessKey, userId+"", URL_EXPIRE_SECONDS);
        jedisTemplate.setex(WSC.USER_GAME_BEFORE_URL_KEY+userId, url, URL_EXPIRE_SECONDS);

        return url;
    }

    @Override
    public UserGameAccount decryptPTParam(WebJSON json, String ip, String base64EncryptParam) {
        if (StringUtils.isEmpty(base64EncryptParam)) {
            return null;
        }

        String encryptParam;
        try {
            encryptParam = new String(Base64.decode(base64EncryptParam), "UTF-8");
        } catch (IOException e) {
            log.error("跳转PT时失败，base64解密失败", e);
            json.set(1, "1-1");
            return null;
        }

        String decryptParam = DES_UTIL.decryptStr(encryptParam, gameRedirectDes);
        if (StringUtils.isEmpty(decryptParam)) {
            return null;
        }

        String[] params = decryptParam.split("\\|");
        if (params == null || params.length != 3) {
            return null;
        }

        String accessKey = params[0];
        Moment expireTime = new Moment().fromTime(params[1], "yyMMddHHmmss");
        String requestSign = params[2];

        Moment now = new Moment();
        if (now.gt(expireTime)) {
            log.warn("第三方游戏跳转失败，URL已过期");
            json.set(2, "2-7012");
            return null;
        }

        String serverSign = DigestUtils.md5Hex(params[0] + "|" + params[1] + "|" + gameRedirectMd5);
        if (!serverSign.equals(requestSign)) {
            String warnMsg = "第三方游戏跳转时服务器验证签名失败，疑似有人在非法套用用户第三方游戏账号信息，用户IP:"+ip+",encryptParam:" + encryptParam;
            warningIllegal(warnMsg);
            json.set(2, "2-12");
            return null;
        }

        String userId = jedisTemplate.get(WSC.USER_GAME_TOKEN_KEY + accessKey);
        if (StringUtils.isEmpty(userId)) {
            log.warn("第三方游戏跳转失败，URL已过期");
            json.set(2, "2-7012");
            return null;
        }

        UserGameAccount gameAccount = dataFactory.getGameAccount(Integer.valueOf(userId), 11, 1);
        if (gameAccount == null) {
            json.set(2, "2-7001");
            return null;
        }

        try {
            // 删除key
            jedisTemplate.del(WSC.USER_GAME_TOKEN_KEY+accessKey, WSC.USER_GAME_BEFORE_URL_KEY+userId);
        } catch (Exception e) {
            log.error("跳转第三方游戏时删除redis key出错", e);
        }

        return gameAccount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> listAll() {
        return gameDao.listAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Game getById(int id) {
        return gameDao.getById(id);
    }

    private void warningIllegal(String msg) {
        log.warn(msg);
        mailJob.addWarning(msg);
    }
}