package lottery.domains.content.api.im;

import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.http.UrlParamUtils;
import lottery.web.WebJSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2017-05-22.
 */
@Component
public class IMAPI implements InitializingBean{
    private static final char[] SALT_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final String LANGUAGE = "zh-CN";
    public static final String CURRENCY_CODE = "RMB";

    private static final Logger log = LoggerFactory.getLogger(IMAPI.class);
    @Value("${im.api_account}")
    private String apiAccount;
    @Value("${im.key}")
    private String key;
    @Value("${im.url}")
    private String url;

    private String loginUrl;
    private String balanceUrl;
    private String depositUrl;
    private String withdrawUrl;
    private String checkTransferUrl;
    private String mobileLoginUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        loginUrl = url + "/api/imsb/login.ashx";
        balanceUrl = url + "/api/imsb/balance.ashx";
        depositUrl = url + "/api/imsb/deposit.ashx";
        withdrawUrl = url + "/api/imsb/withdrawal.ashx";
        checkTransferUrl = url + "/api/imsb/checktransfer.ashx";
        mobileLoginUrl = url + "/api/imsb/mobilelogin.ashx";
    }

    /**
     * 生成进入游戏链接，用户名密码不存在将创建新的账号密码，返回空则表示请求失败webJOSN里会有错误代码
     * @param userName 用户名
     * @param password  密码
     */
    public String loginUrl(WebJSON webJSON, String userName, String password) {
        // 请求参数
        Map<String, String> params = new HashMap<>();
        params.put("apiAccount", apiAccount); // 介接廠商帳號
        params.put("userName", userName); // 遊戲帳號，6 ~ 12 個英文與數字組合
        params.put("password", password); // 遊戲密碼，6 ~ 12 個英文與數字組合
        params.put("language", LANGUAGE); // zh-CN(簡中)、zh-TW(繁中)、en-US(英文)

        String salt = RandomStringUtils.random(5, SALT_CHARS);
        String codeStr = key + apiAccount + userName + password + LANGUAGE + salt;
        String code = salt + DigestUtils.md5Hex(codeStr).toLowerCase();

        params.put("code", code); // 驗證碼

        Object postResult = post(webJSON, loginUrl, params, IMLoginResult.class);
        if (postResult == null) {
            return null;
        }

        IMLoginResult result = (IMLoginResult) postResult;
        if (!result.isSuccess()) {
            webJSON.set(2, "2-8006", result.getMessage());
        }

        return result.getData().getGameUrl();
    }

    /**
     * 查询余额，返回空则表示请求失败webJOSN里会有错误代码
     * @param webJSON
     * @param userName
     * @return
     */
    public Double balance(WebJSON webJSON, String userName) {
        try {
            // 请求参数
            Map<String, String> params = new HashMap<>();
            params.put("apiAccount", apiAccount); // 介接廠商帳號
            params.put("userName", userName); // 遊戲帳號，6 ~ 12 個英文與數字組合

            String salt = RandomStringUtils.random(5, SALT_CHARS);
            String codeStr = key + apiAccount + userName + salt;
            String code = salt + DigestUtils.md5Hex(codeStr).toLowerCase();

            params.put("code", code); // 驗證碼

            Object postResult = post(webJSON, balanceUrl, params, IMBalanceResult.class);
            if (postResult == null) {
                return null;
            }

            IMBalanceResult result = (IMBalanceResult) postResult;
            if (!result.isSuccess()) {
                webJSON.set(2, "2-8006", result.getMessage());
            }

            return result.getData().getBalance();
        } catch (Exception e) {
            log.error("查询IM余额失败", e);
            return 0d;
        }
    }

    /**
     * 转入,返回交易編號，返回空则表示请求失败webJOSN里会有错误代码
     * @param webJSON
     * @param userName
     * @param password
     * @param amount
     * @return
     */
    public String deposit(WebJSON webJSON, String userName, String password, String amount) {
        // 请求参数
        String transSN = generateTransSN();
        Map<String, String> params = new HashMap<>();
        params.put("apiAccount", apiAccount); // 介接廠商帳號
        params.put("userName", userName); // 遊戲帳號，6 ~ 12 個英文與數字組合
        params.put("password", password); // 遊戲密碼，6 ~ 12 個英文與數字組合
        params.put("transSN", transSN); // 交易編號(唯一)，10~19 個數字
        params.put("amount", amount); // 金額
        params.put("currencyCode", CURRENCY_CODE); // 幣別 (選填) (參考附錄 => 幣別)

        String salt = RandomStringUtils.random(5, SALT_CHARS);
        String codeStr = key + apiAccount + userName + password + transSN + amount + CURRENCY_CODE + salt;
        String code = salt + DigestUtils.md5Hex(codeStr).toLowerCase();

        params.put("code", code); // 驗證碼

        Object postResult = post(webJSON, depositUrl, params, IMTransferResult.class);
        if (postResult == null) {
            return null;
        }

        IMTransferResult result = (IMTransferResult) postResult;
        if (!result.isSuccess()) {
            webJSON.set(2, "2-8006", result.getMessage());
        }

        return result.getData().getTransferSN();
    }

    /**
     * 转出,返回交易編號，返回空则表示请求失败webJOSN里会有错误代码
     * @param webJSON
     * @param userName
     * @param password
     * @param amount
     * @return
     */
    public String withdraw(WebJSON webJSON, String userName, String password, String amount) {
        // 请求参数
        String transSN = generateTransSN();
        Map<String, String> params = new HashMap<>();
        params.put("apiAccount", apiAccount); // 介接廠商帳號
        params.put("userName", userName); // 遊戲帳號，6 ~ 12 個英文與數字組合
        params.put("password", password); // 遊戲密碼，6 ~ 12 個英文與數字組合
        params.put("transSN", transSN); // 交易編號(唯一)，10~19 個數字
        params.put("amount", amount); // 金額
        params.put("currencyCode", CURRENCY_CODE); // 幣別 (選填) (參考附錄 => 幣別)

        String salt = RandomStringUtils.random(5, SALT_CHARS);
        String codeStr = key + apiAccount + userName + password + transSN + amount + CURRENCY_CODE + salt;
        String code = salt + DigestUtils.md5Hex(codeStr).toLowerCase();

        params.put("code", code); // 驗證碼

        Object postResult = post(webJSON, withdrawUrl, params, IMTransferResult.class);
        if (postResult == null) {
            return null;
        }

        IMTransferResult result = (IMTransferResult) postResult;
        if (!result.isSuccess()) {
            webJSON.set(2, "2-8006", result.getMessage());
        }

        return result.getData().getTransferSN();
    }

    private String generateTransSN() {
        return "IM" + new Moment().format("yyyyMMddHHmmss") + RandomStringUtils.random(3, true, true);
    }

    /**
     * 查询转账单状态，返回true表示成功，返回false则表示请求失败webJOSN里会有错误代码
     * @param webJSON
     * @return
     */
    public boolean checkTransfer(WebJSON webJSON, String transferSN) {
        // 请求参数
        Map<String, String> params = new HashMap<>();
        params.put("apiAccount", apiAccount); // 介接廠商帳號
        params.put("transferSN", transferSN); // 交易編號(唯一)，10~19 個數字

        String salt = RandomStringUtils.random(5, SALT_CHARS);
        String codeStr = key + apiAccount + transferSN + salt;
        String code = salt + DigestUtils.md5Hex(codeStr).toLowerCase();

        params.put("code", code); // 驗證碼

        Object result = post(webJSON, checkTransferUrl, params, IMCommonResult.class);
        if (result == null) {
            return false;
        }

        return true;
    }

    private Object post(WebJSON webJSON, String postUrl, Map<String, String> params, Class<? extends IMResult> resultClass) {
        try {
            // 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> request = new HttpEntity<>(headers);

            String paramsStr = UrlParamUtils.toUrlParam(params);

            // 请求
            String url = postUrl + "?" + paramsStr + "&_=" + System.currentTimeMillis();
            String json = restTemplate.postForObject(url, request, String.class);

            if (StringUtils.isEmpty(json)) {
                log.error("访问IM时返回空，访问地址：" + postUrl);
                webJSON.set(2, "2-9000");
                return null;
            }

            IMResult result = JSON.parseObject(json, resultClass);
            if (IMCode.SUCCESS != result.getCode() || !result.isSuccess()) {
                String errorCode = IMCode.transErrorCode(result.getCode());
                if (StringUtils.isEmpty(errorCode)) {
                    if (StringUtils.isEmpty(result.getMessage())) {
                        webJSON.set(2, "2-8006", result.getMessage());
                    }
                    else {
                        webJSON.set(2, "2-9000");
                    }
                }
                else {
                    webJSON.set(2, errorCode);
                }
                return null;
            }

            return result;
        } catch (Exception e) {
            log.error("连接IM发生错误，请求地址："+postUrl+",请求参数：" + JSON.toJSONString(params), e);
            webJSON.set(2, "2-9001");
            return null;
        }
    }
}
