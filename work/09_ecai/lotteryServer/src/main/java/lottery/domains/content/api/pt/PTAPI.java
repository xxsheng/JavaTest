package lottery.domains.content.api.pt;

import com.alibaba.fastjson.JSON;
import lottery.web.WebJSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * Created by Nick on 2016/12/26.
 */
@Component
public class PTAPI {
    private static final Logger log = LoggerFactory.getLogger(PTAPI.class);

    @Value("${pt.entitykey}")
    private String entityKey;
    @Value("${pt.prefix}")
    private String prefix;
    @Value("${pt.kioskname}")
    private String kioskname;
    @Value("${pt.adminname}")
    private String adminname;
    @Value("${pt.url}")
    private String url;

    @Autowired
    @Qualifier("ptRestTemplate")
    private RestTemplate ptRestTemplate;

    /**
     * 创建PT用户，返回创建后的用户名，如果返回空，则在webJson里有错误代码
     * @param webJSON webJson
     * @param username 用户名，系统里的用户名，不需要拼接前缀
     * @param password 密码，在PT方的密码
     * @return
     */
    public String playerCreate(WebJSON webJSON, String username, String password) {
        // 组装参数
        String realUsername = prefix + username;
        realUsername = realUsername.toUpperCase();
        String url = String.format("/player/create/adminname/%s/kioskname/%s/custom02/WIN88ENTITY/playername/%s/password/%s", adminname, kioskname, realUsername, password);

        // 发送请求
        String result = post(url);
        if (StringUtils.isEmpty(result)) {
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return null;
        }

        // 解析结果
        HashMap<String, Object> resultMap = JSON.parseObject(result, HashMap.class);
        String errorCode = assertError(resultMap);
        // if (errorCode != null && errorCode.equals("2-7000")) {
        //     // 用户名已经存在，自动关联
        //     return realUsername;
        // }
        if (errorCode != null) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, errorCode);
            return null;
        }

        HashMap<String, Object> resultMap2 = JSON.parseObject(resultMap.get("result").toString(), HashMap.class);
        if (resultMap2 == null || !resultMap2.containsKey("playername")) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return null;
        }

        return resultMap2.get("playername").toString();
    }

    /**
     * 获取用户余额，返回当前余额，如果返回空，则在webJson里有错误代码
     * @param webJSON webJson
     * @param username 用户名
     * @return
     */
    public Double playerBalance(WebJSON webJSON, String username) {
        try {
            // 组装参数
            String realUsername = username;
            realUsername = realUsername.toUpperCase();
            String url = String.format("/player/balance/playername/%s", realUsername);

            // 发送请求
            String result = post(url);
            if (StringUtils.isEmpty(result)) {
                webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
                return null;
            }

            // 解析结果
            HashMap<String, Object> resultMap = JSON.parseObject(result, HashMap.class);
            String errorCode = assertError(resultMap);
            if (errorCode != null) {
                log.error("PT返回错误：" + result);
                webJSON.set(2, errorCode);
                return null;
            }

            HashMap<String, Object> resultMap2 = JSON.parseObject(resultMap.get("result").toString(), HashMap.class);
            if (resultMap2 == null || !resultMap2.containsKey("balance")) {
                log.error("PT返回错误：" + result);
                webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
                return null;
            }

            return Double.valueOf(resultMap2.get("balance").toString());
        } catch (Exception e) {
            log.error("查询PT余额失败", e);
            return null;
        }
    }

    /**
     * 修改用户密码，返回true|false，如果false，则在webJson里有错误代码，返回true，代表修改成功
     * @param webJSON webJson
     * @param username 用户名
     * @param password 要修改的密码，明文
     * @return
     */
    public boolean playerUpdatePassword(WebJSON webJSON, String username, String password) {
        // 组装参数
        String realUsername = username;
        realUsername = realUsername.toUpperCase();
        String url = String.format("/player/update/playername/%s/password/%s", realUsername, password);

        // 发送请求
        String result = post(url);
        if (StringUtils.isEmpty(result)) {
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return false;
        }

        // 解析结果
        HashMap<String, Object> resultMap = JSON.parseObject(result, HashMap.class);
        String errorCode = assertError(resultMap);
        if (errorCode != null) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, errorCode);
            return false;
        }

        HashMap<String, Object> resultMap2 = JSON.parseObject(resultMap.get("result").toString(), HashMap.class);
        if (resultMap2 == null || !resultMap2.containsKey("playername")) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return false;
        }

        return true;
    }

    /**
     * 用户转入PT账户金额，返回当前PT余额，如果返回null，则在webJson里有错误代码
     * @param webJSON webJson
     * @param username 用户名
     * @param amount 转账金额
     * @return
     */
    public Double playerDeposit(WebJSON webJSON, String username, double amount, String billNo) {
        // 组装参数
        String realUsername = username;
        realUsername = realUsername.toUpperCase();
        String url = String.format("/player/deposit/adminname/%s/playername/%s/amount/%s/externaltranid/%s", adminname, realUsername, amount, billNo);

        // 发送请求
        String result = post(url);
        if (StringUtils.isEmpty(result)) {
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return null;
        }

        // 解析结果
        HashMap<String, Object> resultMap = JSON.parseObject(result, HashMap.class);
        String errorCode = assertError(resultMap);
        if (errorCode != null) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, errorCode);
            return null;
        }

        if (result.indexOf("currentplayerbalance") == -1) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return null;
        }

        HashMap<String, Object> resultMap2 = JSON.parseObject(resultMap.get("result").toString(), HashMap.class);
        Object currentplayerbalance = resultMap2.get("currentplayerbalance");
        if (currentplayerbalance == null) {
            return 0d;
        }

        return Double.valueOf(resultMap2.get("currentplayerbalance").toString());
    }

    /**
     * 用户从PT账户转出金额，返回当前PT余额，如果返回null，则在webJson里有错误代码
     * @param webJSON webJson
     * @param username 用户名
     * @param amount 转账金额
     * @return
     */
    public Double playerWithdraw(WebJSON webJSON, String username, double amount, String billNo) {
        // 组装参数
        String realUsername = username;
        realUsername = realUsername.toUpperCase();
        String url = String.format("/player/withdraw/adminname/%s/playername/%s/amount/%s/externaltranid/%s/isForce/1", adminname, realUsername, amount, billNo);

        // 发送请求
        String result = post(url);
        if (StringUtils.isEmpty(result)) {
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return null;
        }

        // 解析结果
        HashMap<String, Object> resultMap = JSON.parseObject(result, HashMap.class);
        String errorCode = assertError(resultMap);
        if (errorCode != null) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, errorCode);
            return null;
        }

        if (result.indexOf("currentplayerbalance") == -1) {
            log.error("PT返回错误：" + result);
            webJSON.set(2, PTCode.DEFAULT_ERROR_CODE);
            return null;
        }

        HashMap<String, Object> resultMap2 = JSON.parseObject(resultMap.get("result").toString(), HashMap.class);
        Object currentplayerbalance = resultMap2.get("currentplayerbalance");
        if (currentplayerbalance == null) {
            return 0d;
        }

        return Double.valueOf(resultMap2.get("currentplayerbalance").toString());
    }

    /**
     * 判断返回是否有错误，null表示无错误，其它表示有错误
     */
    private String assertError(HashMap<String, Object> resultMap) {
        if (resultMap.containsKey("errorcode")) {
            Object errorcode = resultMap.get("errorcode");
            String code = PTCode.transErrorCode(errorcode.toString());
            return code;
        }

        return null;
    }

    private String post(String subUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X_ENTITY_KEY", entityKey);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> request = new HttpEntity<>("", headers);

            String result = ptRestTemplate.postForObject(url + subUrl, request, String.class);

            log.debug(result);

            return result;
        } catch (Exception e) {
            log.error("连接PT发生错误，路径：" + subUrl, e);
            return null;
        }
    }
}
