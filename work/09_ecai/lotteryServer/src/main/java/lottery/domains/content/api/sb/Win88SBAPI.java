package lottery.domains.content.api.sb;

import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.http.HttpClientUtil;
import javautils.http.UrlParamUtils;
import lottery.web.WebJSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nick on 2017-05-27.
 */
@Component
public class Win88SBAPI implements InitializingBean{
    private static final Logger log = LoggerFactory.getLogger(Win88SBAPI.class);

    @Value("${sb.opcode}")
    private String opCode;
    @Value("${sb.md5key}")
    private String md5key;
    @Value("${sb.apiurl}")
    private String apiUrl;
    @Value("${sb.pcgameurl}")
    private String pcGameUrl;
    @Value("${sb.mobilegameurl}")
    private String mobileGameUrl;
    @Value("${sb.unlogingameurl}")
    private String unLoginGameUrl;
    @Value("${sb.oddstype}")
    private String oddsType;
    @Value("${sb.mintransfer}")
    private String minTransfer;
    @Value("${sb.maxtransfer}")
    private String maxTransfer;

    private static final String CREATEMEMBER_URL = "api/CreateMember";
    private static final String LOGIN_URL = "api/Login";
    private static final String CHECKUSERBALANCE_URL = "api/CheckUserBalance";
    private static final String FUNDTRANSFER_URL = "api/FundTransfer";
    private static final String CHECKFUNDTRANSFER_URL = "api/CheckFundTransfer";
    private static final String HELLO_URL = "api/Hello";

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotEmpty(apiUrl)) {
            if (!apiUrl.endsWith("/")) {
                apiUrl = apiUrl + "/";
            }
        }
    }

    public boolean hello() {
        try {
            String url = new StringBuffer(apiUrl).append(HELLO_URL).append("?OpCode=").append(opCode).toString();

            String json = HttpClientUtil.get(url, null, 5000);

            if (StringUtils.isEmpty(json)) {
                log.error("访问SB接口时返回空，访问地址：" + url);
                return false;
            }

            Win88SBCommonResult result = JSON.parseObject(json, Win88SBCommonResult.class);
            if (result == null) {
                log.error("连接SB发生解析错误，请求地址："+url+",返回：" + json);
                return false;
            }
            if (!"0".equals(result.getErrorCode())) {
                log.error("连接SB发生错误" + ",返回：" + json);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("连接SB发生错误", e);
            return false;
        }
    }

    public boolean createMember(WebJSON json, String playerName) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("OpCode", opCode);
        params.put("PlayerName", playerName);
        params.put("FirstName", playerName);
        params.put("LastName", playerName);
        params.put("OddsType", oddsType);
        params.put("MaxTransfer", maxTransfer);
        params.put("MinTransfer", minTransfer);

        Object postResult = post(json, CREATEMEMBER_URL, params, Win88SBCommonResult.class);
        if (postResult == null) {
            return false;
        }

        Win88SBCommonResult result = (Win88SBCommonResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            log.error("创建沙巴会员出错，返回错误空的错误代码，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                json.set(2, "2-10000");
            }
            else {
                // 将错误信息直接提示给用户
                json.set(2, "2-10002", result.getMessage(), errorCode);
            }
            return false;
        }

        return true;
    }

    public String pcLoginUrl(WebJSON json, String playerName) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("OpCode", opCode);
        params.put("PlayerName", playerName);

        Object postResult = post(json, LOGIN_URL, params, Win88SBLoginResult.class);
        if (postResult == null) {
            return null;
        }

        Win88SBLoginResult result = (Win88SBLoginResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                log.error("获取沙巴PC游戏地址出错，返回错误空的错误代码，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
                json.set(2, "2-10000");
            }
            else {
                // 将错误信息直接提示给用户
                json.set(2, "2-10002", result.getMessage(), errorCode);
            }
            return null;
        }

        return String.format(pcGameUrl, result.getSessionToken());
    }

    public String mobileLoginUrl(WebJSON json, String playerName) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("OpCode", opCode);
        params.put("PlayerName", playerName);

        Object postResult = post(json, LOGIN_URL, params, Win88SBLoginResult.class);
        if (postResult == null) {
            return null;
        }

        Win88SBLoginResult result = (Win88SBLoginResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                log.error("获取沙巴手机游戏地址出错，返回错误空的错误代码，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
                json.set(2, "2-10000");
            }
            else {
                // 将错误信息直接提示给用户
                json.set(2, "2-10002", result.getMessage(), errorCode);
            }
            return null;
        }

        return String.format(mobileGameUrl, result.getSessionToken());
    }

    public String unLoginUrl() {
        return unLoginGameUrl;
    }

    public Double checkUserBalance(WebJSON json, String playerName) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("OpCode", opCode);
        params.put("PlayerName", playerName);

        Object postResult = post(json, CHECKUSERBALANCE_URL, params, Win88SBCheckUserBalanceResult.class);
        if (postResult == null) {
            return null;
        }

        Win88SBCheckUserBalanceResult result = (Win88SBCheckUserBalanceResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            log.error("获取沙巴会员余额失败，返回错误空的错误代码，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                json.set(2, "2-10000");
            }
            else {
                // 将错误信息直接提示给用户
                json.set(2, "2-10002", result.getMessage(), errorCode);
            }
            return null;
        }
        if (CollectionUtils.isEmpty(result.getData())) {
            json.set(2, "2-10000");
            return null;
        }

        return result.getData().get(0).getBalance();
    }

    public Win88SBFundTransferResult deposit(WebJSON json, String playerName, int amount) {
        return fundTransfer(json, playerName, amount, "1");
    }

    public Win88SBFundTransferResult withdraw(WebJSON json, String playerName, int amount) {
        return fundTransfer(json, playerName, amount, "0");
    }

    private Win88SBFundTransferResult fundTransfer(WebJSON json, String playerName, int amount, String direction) {
        if (amount < 0) {
            json.set(2, "2-1017");
            return null;
        }

        String opTransId = generateOpTransId();
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("Opcode", opCode); // confused me
        // params.put("OpCode", opCode);
        params.put("PlayerName", playerName);
        params.put("OpTransId", opTransId);
        params.put("amount", BigDecimal.valueOf(amount).intValue()+"");
        params.put("direction", direction);

        Object postResult = post(json, FUNDTRANSFER_URL, params, Win88SBFundTransferResult.class);
        if (postResult == null) {
            return reCheckFundTransferIsSucceed(json, playerName, opTransId);
        }

        Win88SBFundTransferResult result = (Win88SBFundTransferResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            log.error("沙巴转账返回表示失败，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                json.set(2, "2-10000");
            }
            else {
                // 将错误信息直接提示给用户
                json.set(2, "2-10002", result.getMessage(), errorCode);
            }
            return null;
        }

        if (result.getData() == null || result.getData().getStatus() == null ) {
            log.error("沙巴转账返回Data为空，转账失败，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
            json.set(2, "2-10000");
            return null;
        }

        // 0:成功执行;1:系统错误;2:挂起
        if (result.getData().getStatus() == 0 || result.getData().getStatus() == 2) {
            result.setOpTransId(opTransId);
            return result;
        }

        // 转账失败
        log.error("沙巴转账返回状态表示失败，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
        json.set(2, "2-8008");
        return null;
    }

    private Win88SBFundTransferResult reCheckFundTransferIsSucceed(WebJSON json, String playerName, String opTransId) {
        // 因为网络原因错误了，再查一次状态
        Win88SBFundTransferResult checkResult = checkFundTransfer(json, playerName, opTransId);

        // 如果再次因为网络错误，这里就返回空，表示转账失败,json里会有错误代码
        if (checkResult == null) {
            return null;
        }

        if (checkResult.getData() == null || checkResult.getData().getStatus() == null ) {
            log.error("沙巴重检查转账时返回Data为空，转账失败,返回：" + JSON.toJSONString(checkResult) + ",订单号：" + opTransId + ",用户名：" + playerName);
            json.set(2, "2-10000");
            return null;
        }

        // 0:成功执行;1:系统错误;2:挂起
        if (checkResult.getData().getStatus() == 0 || checkResult.getData().getStatus() == 2) {
            // 成功
            checkResult.setOpTransId(opTransId);
            return checkResult;
        }

        // 转账失败
        log.error("沙巴转账重检查返回状态表示失败,返回：" + JSON.toJSONString(checkResult) + ",订单号：" + opTransId + ",用户名：" + playerName);
        json.set(2, "2-8008");
        return null;
    }

    private Win88SBFundTransferResult checkFundTransfer(WebJSON json, String playerName, String opTransId) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("OpCode", opCode);
        params.put("PlayerName", playerName);
        params.put("OpTransId", opTransId);

        Object postResult = post(json, CHECKFUNDTRANSFER_URL, params, Win88SBFundTransferResult.class);
        if (postResult == null) {
            return null;
        }

        Win88SBFundTransferResult result = (Win88SBFundTransferResult) postResult;
        if (!"0".equals(result.getErrorCode())) {
            log.error("检查沙巴转账出错，返回错误空的错误代码，请求参数：" + JSON.toJSONString(params) + ",返回：" + JSON.toJSONString(result));
            String errorCode = result.getErrorCode();
            if (StringUtils.isEmpty(errorCode)) {
                json.set(2, "2-10000");
            }
            else {
                // 将错误信息直接提示给用户
                json.set(2, "2-10002", result.getMessage(), errorCode);
            }
            return null;
        }

        result.setOpTransId(opTransId);

        return result;
    }

    private String generateOpTransId() {
        return "SB" + new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(5, true, true);
    }

    /**
     * post只是将返回的结果转换并返回，如果发生网络错误，返回空
     */
    private Object post(WebJSON webJSON, String subUrl, Map<String, String> params, Class<? extends Win88SBResult> resultClass) {
        try {
            // // 请求头
            // HttpHeaders headers = new HttpHeaders();
            // headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // HttpEntity<String> request = new HttpEntity<>(headers);

            String paramsStr = UrlParamUtils.toUrlParam(params);

            String securityToken = new StringBuffer(md5key).append("/").append(subUrl).append("?").append(paramsStr).toString();
            securityToken = DigestUtils.md5Hex(securityToken).toUpperCase();

            String paramsEncode =  new StringBuffer("SecurityToken=").append(securityToken).append("&").append(paramsStr).toString();

            String url = new StringBuffer(apiUrl).append(subUrl).append("?").append(paramsEncode).toString();

            log.debug("开始请求沙巴：{}", url);

            String json = HttpClientUtil.get(url, null, 5000);

            if (StringUtils.isEmpty(json)) {
                log.error("访问SB时返回空，访问地址：" + url);
                webJSON.set(2, "2-10001");
                return null;
            }

            Win88SBResult result = null;
            try {
                result = JSON.parseObject(json, resultClass);
            } catch (Exception e) {
                log.error("解析沙巴返回信息错误:" + json, e);
            }
            if (result == null) {
                log.error("连接SB发生解析错误，请求地址："+subUrl+",请求参数：" + JSON.toJSONString(params) + ",返回：" + json);
                webJSON.set(2, "2-10001");
                return null;
            }

            return result;
        } catch (Exception e) {
            log.error("连接SB发生错误，请求地址："+subUrl+",请求参数：" + JSON.toJSONString(params), e);
            webJSON.set(2, "2-10001");
            return null;
        }
    }
}
