package lottery.domains.content.api.ag;

import com.alibaba.fastjson.JSON;
import javautils.encrypt.DESUtil;
import javautils.http.HttpClientUtil;
import javautils.http.UrlParamUtils;
import lottery.web.WebJSON;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2016/12/28.
 */
@Component
public class AGAPI implements InitializingBean{
    private static final Logger log = LoggerFactory.getLogger(AGAPI.class);
    private static final String AG_KEY_SEPARATOR = "/\\\\/";
    private static final String AG_KEY_SEPARATOR_NEW = "&";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private static String VALIDATION_XML = "";

    // @Value("${ag.ag.cagent}")
    // private String agCagent; // 极速厅
    @Value("${ag.agin.cagent}")
    private String aginCagent; // 国际厅
    @Value("${ag.md5key}")
    private String md5key;
    @Value("${ag.deskey}")
    private String deskey;
    @Value("${ag.giurl}")
    private String giurl;
    @Value("${ag.gciurl}")
    private String gciurl;
    @Value("${ag.actype}")
    private String actype;
    @Value("${ag.oddtype}")
    private String oddtype;

    @Value("${ag.api_name}")
    private String api_name;
    @Value("${ag.api_pass}")
    private String api_pass;
    @Value("${ag.private_key}")
    private String private_key;

    @Override
    public void afterPropertiesSet() throws Exception {
        VALIDATION_XML = "<?xml version=\"1.0\"?>";
        VALIDATION_XML += "<response action=\"userverf\">";
        VALIDATION_XML += "<element id=\"%s\">";
        VALIDATION_XML += "<properties name=\"pcode\">%s</properties>";
        VALIDATION_XML += "<properties name=\"gcode\">%s</properties>";
        VALIDATION_XML += "<properties name=\"status\">%s</properties>";
        VALIDATION_XML += "<properties name=\"errdesc\">%s</properties>";
        VALIDATION_XML += "<properties name=\"username\">%s</properties>";
        VALIDATION_XML += "<properties name=\"userid\">%s</properties>";
        VALIDATION_XML += "<properties name=\"actype\">"+actype+"</properties>";
        VALIDATION_XML += "<properties name=\"pwd\">%s</properties>";
        VALIDATION_XML += "<properties name=\"gamelevel\"></properties>";
        VALIDATION_XML += "<properties name=\"vip\"></properties>";
        VALIDATION_XML += "<properties name=\"domain\">%s</properties>";
        VALIDATION_XML += "<properties name=\"ip\">%s</properties>";
        VALIDATION_XML += "</element>";
        VALIDATION_XML += "</response>";
    }

    /**
     * 生成进入游戏中心的链接
     *
     * @param loginname AG用户名
     * @param password  AG密码
     * @param website   返回主页的链接，获取request.getHeader("Host)
     */
    public String forwardGame(String loginname, String password, String website, String gameType, String actype) {
        // 请求参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("cagent", aginCagent); // 代理编码
        paramsMap.put("loginname", loginname); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        paramsMap.put("password", password); // AG用户密码
        paramsMap.put("dm", website); // ‘dm’ 代表返回的网站域名
        String sid = aginCagent + System.currentTimeMillis();
        paramsMap.put("sid", sid); // sid = (cagent+序列), 序列是唯一的 13~16 位数
        paramsMap.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        paramsMap.put("lang", "1"); // zh-cn (简体中文) 1
        paramsMap.put("gameType", gameType); // 游戏类型 (AGIN 平台, 为空将导入整合页面)  0 大厅；1 旗舰厅； 5 竞咪厅 6 捕鱼厅；11 HTML5 大厅 (AGIN 移动网页版游戏平台大厅)
        paramsMap.put("oddtype", oddtype); // 下注范围，A：20-50000。B：50-5000。C：20-10000。
        paramsMap.put("cur", "CNY"); // 货币种类
        // paramsMap.put("mh5", "y"); // 代表 AGIN 移动网页版
        // paramsMap.put("flashid", ""); HB 平台(Habanero 平台)
        // paramsMap.put("session_token", ""); 生成方式：当用戶登陆网站，网站保存Session Token在内存，用于驗證用户合法性 接入Iframe及手机Html5时必须带入session_token

        String paramsStr = UrlParamUtils.toUrlParam(paramsMap, AG_KEY_SEPARATOR, false);

        String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
        targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");

        String key = DigestUtils.md5Hex(targetParams + md5key);

        String url = gciurl + "/forwardGame.do?params=" + targetParams + "&key=" + key;
        return url;
    }
    //登录游戏
    public String getGameUrlForword(WebJSON webJSON, String username, String actype) {
        // 请求参数
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("username", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        paramsMap.put("api_name", api_name);
        paramsMap.put("api_pass", api_pass);
        paramsMap.put("private_key", private_key);
        paramsMap.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        paramsMap.put("oddtype", oddtype); // 下注范围，A：20-50000。B：50-5000。C：20-10000。
        String url = "Login";
        AGResult result = postAg(paramsMap,url);
        String info = result.getInfo();
        return info;
    }

    /**
     * 检测并创建游戏账号，返回创建后的用户名，如果返回空，则在webJson里有错误代码
     *
     * @param username AG用户名
     * @param password AG密码
     * @return
     */
    public String checkOrCreateGameAccount(WebJSON webJSON, String username, String password, String actype) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        params.put("api_name", api_name);
        params.put("api_pass", api_pass);
        params.put("private_key", private_key);
        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        params.put("oddtype", oddtype); // 下注范围，A：20-50000。B：50-5000。C：20-10000。
//        params.put("cagent", aginCagent); // 代理编码
//        params.put("loginname", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
//        params.put("method", "lg"); // 数值 = “lg” 代表 ”检测并创建游戏账号
//        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
//        params.put("password", password); // 游戏账号密码
//        params.put("oddtype", oddtype); // 下注范围，A：20-50000。B：50-5000。C：20-10000。
//        params.put("cur", "CNY"); // 货币,CNY：人民币

        // 请求
        AGResult result = post(params);
        if (result == null) {
            webJSON.set(2, "2-8001");
            return null;
        }

        // 返回是否正确
        if (!"0".equals(result.getInfo())) {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                if (StringUtils.isEmpty(result.getMsg())) {
                    webJSON.set(2, "2-8000");
                }
                else {
                    webJSON.set(2, errorCode, result.getMsg());
                }
            } else {
                webJSON.set(2, errorCode);
            }
            return null;
        }

        return username;
    }

    /**
     * 获取玩家余额，返回余额，如果返回空，则在webJson里有错误代码
     *
     * @param username AG用户名
     * @param password AG密码
     * @return
     */
    public Double getBalance(WebJSON webJSON, String username, String password) {
        try {
            // 组装参数
            HashMap<String, String> params = new HashMap<>();
            params.put("api_name", api_name);
            params.put("api_pass", api_pass);
            params.put("private_key", private_key);
            params.put("username", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
            params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能


//            params.put("loginname", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
//            params.put("cagent", aginCagent); // 代理编码
//            params.put("method", "gb"); // 数值 = “gb” 代表 查询余额
//            params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
//            params.put("password", password); // 游戏账号密码
//            params.put("cur", "CNY"); // 货币,CNY：人民币

            // 请求
            AGResult result = post(params);
            if (result == null) {
                webJSON.set(2, "2-8001");
                return null;
            }

            // 返回是否正确
            if (!NumberUtils.isNumber(result.getInfo())) {
                log.error("AG返回错误：" + JSON.toJSONString(result));
                String errorCode = AGCode.transErrorCode(result.getInfo());
                if ("2-8006".equals(errorCode)) {
                    if (StringUtils.isEmpty(result.getMsg())) {
                        webJSON.set(2, "2-8000");
                    }
                    else {
                        webJSON.set(2, errorCode, result.getMsg());
                    }
                } else {
                    webJSON.set(2, errorCode);
                }
                return null;
            }

            return Double.valueOf(result.getInfo());
        } catch (Exception e) {
            log.error("查询AG余额失败", e);
            return null;
        }
    }
    /**
    　* @Description: 查询余额
    　* @param [webJSON, username, password]
    　* @return java.lang.Double
    　* @throws
    　* @author SunJiang
    　* @date 2018/08/24,024 15:56:48
    　*/
    public Double getBalanceNew(WebJSON webJSON, String username) {
        try {
            // 组装参数
            HashMap<String, String> params = new HashMap<>();
            params.put("api_name", api_name);
            params.put("api_pass", api_pass);
            params.put("private_key", private_key);
            params.put("username", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
            params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能

            AGResult result = postAg(params, "GetBalance");
            if (result == null) {
                webJSON.set(2, "2-8001");
                return null;
            }
            String info = result.getInfo();
            JSONObject jsonObject = JSONObject.fromObject(info);

            JSONObject.fromObject(jsonObject.get("data").toString()).get("balance");
            //1成功  0失败
            if("1".equals(jsonObject.get("result").toString())){
                return Double.valueOf(JSONObject.fromObject(jsonObject.get("data").toString()).get("balance").toString());
            }else{
                webJSON.set(2, jsonObject.get("msg").toString());
                log.error("AG返回错误：" + jsonObject.get("msg").toString());
                return null;
            }
        } catch (Exception e) {
            log.error("查询AG余额失败", e);
            return null;
        }
    }

    /**
     * 转入，返回转账单号
     *
     * @param username AG用户名
     * @param password AG密码
     * @param amount   转账金额，保留2位小数
     * @return
     */
    public String transferIn(WebJSON webJSON, String username, String password, int amount) {
        return transfer(webJSON, username, password, amount, true);
    }

    /**
     * 转入，返回转账单号
     *
     * @param username AG用户名
     * @param password AG密码
     * @param amount   转账金额，保留2位小数
     * @return
     */
    public String transferOut(WebJSON webJSON, String username, String password, int amount) {
        return transfer(webJSON, username, password, amount, false);
    }

    public AGValidation transValidationFromString(String body) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(body)));

            AGValidation validation = new AGValidation();
            Node node_request = doc.getFirstChild();
            String action = node_request.getAttributes().getNamedItem("action").getNodeValue();
            validation.setAction(action);

            Node node_element = node_request.getFirstChild();
            String id = node_element.getAttributes().getNamedItem("id").getNodeValue();
            validation.setId(id);

            NodeList childNodes = node_element.getChildNodes();

            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                Node item = childNodes.item(i);
                String name = item.getAttributes().getNamedItem("name").getNodeValue();
                String value = item.getTextContent();

                if ("pcode".equals(name)) {
                    validation.setPcode(value);
                }
                else if ("gcode".equals(name)) {
                    validation.setGcode(value);
                }
                else if ("userid".equals(name)) {
                    validation.setUserid(value);
                }
                else if ("password".equals(name)) {
                    validation.setPassword(value);
                    String realPassword = StringUtils.substring(value, 4, value.length() - 6);
                    validation.setRealPassword(realPassword);
                }
                else if ("token".equals(name)) {
                    validation.setToken(value);
                }
                else if ("cagent".equals(name)) {
                    validation.setCagent(value);
                }
            }

            return validation;
        } catch (Exception e) {
            log.error("转换AG登录验证错误:" + body, e);
        }

        return null;
    }

    public String transValidationToString(AGValidation validation, String status, String errdesc, String domain, String ip) {
        Object[] values = new Object[]{validation.getId(), validation.getPcode(), validation.getGcode(), status, errdesc,
                validation.getUserid(), validation.getUserid(), validation.getRealPassword(), domain, ip};
        String xml = String.format(VALIDATION_XML, values);
        return xml;
    }

    public boolean verifyAGValidation(AGValidation validation) {
        String serverToken = validation.getPcode() + validation.getGcode() + validation.getUserid() + validation.getPassword() + md5key;
        serverToken = DigestUtils.md5Hex(serverToken);
        return serverToken.equals(validation.getToken());
    }

    private String transfer(WebJSON webJSON, String username, String password, int amount, boolean in) {
        String billno = null;
        try {
            // 第一步：预备转账
            billno = prepareTransferCredit(webJSON, username, password, amount, in);
            if (StringUtils.isEmpty(billno)) {
                return null;
            }

            // 第二步：确认转账
            boolean confirm = transferCreditConfirm(webJSON, username, password, amount, billno, in);
            if (confirm == false) {
                return null;
            }

            // 第三步：查询状态
            // boolean result = queryOrderStatus(webJSON, billno);
            // return !result ? null : billno;
            return billno;
        } catch (Exception e) {
            log.error("AG转账发生异常，订单号：" + billno + "用户名：" + username + "，金额：" + amount + "，是否转入：" + in, e);
            if (billno != null) {
                boolean result = queryOrderStatus(webJSON, billno);
                return !result ? null : billno;
            }
            else {
                return null;
            }
        }
    }

    /**
     * 预备转账，返回流水号表示成功，否则失败，则在webJson里有错误代码
     * 依次使用prepareTransferCredit -> transferCreditConfirm -> queryOrderStatus
     *
     * @param username 系统内的用户名
     * @return
     */
    private String prepareTransferCredit(WebJSON webJSON, String username, String password, int amount, boolean in) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("cagent", aginCagent); // 代理编码
        params.put("loginname", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        params.put("method", "tc"); // 数值 = “tc” 代表 预备转账
        String billno = aginCagent + System.nanoTime() + "" + RandomUtils.nextInt(99);
        params.put("billno", billno); // 注单号
        params.put("type", in ? "IN" : "OUT"); // in：转入。out：转出
        params.put("credit", amount + ""); // 转款额度(如 000.00), 只保留小数点后两个位
        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        params.put("password", password); // 密码
        params.put("cur", "CNY"); // 货币,CNY：人民币

        // 请求
        AGResult result = post(params);
        if (result == null) {
            webJSON.set(2, "2-8001");
            return null;
        }

        // 返回是否正确
        if (!"0".equals(result.getInfo())) {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                if (StringUtils.isEmpty(result.getMsg())) {
                    webJSON.set(2, "2-8000");
                }
                else {
                    webJSON.set(2, errorCode, result.getMsg());
                }
            } else {
                webJSON.set(2, errorCode);
            }
            return null;
        }

        return billno;
    }

    /**
     * 确认转账，返回true成功，返回false失败，则在webJson里有错误代码
     * 使用完后，再调用确认转账方法，再调用查询状态方法
     *
     * @param username 系统内的用户名
     * @return
     */
    private boolean transferCreditConfirm(WebJSON webJSON, String username, String password, int amount, String billno, boolean in) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("cagent", aginCagent); // 代理编码
        params.put("loginname", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        params.put("method", "tcc"); // 数值 = “tc” 代表 确认转账
        params.put("billno", billno); // 注单号
        params.put("type", in ? "IN" : "OUT"); // in：转入。out：转出
        params.put("credit", amount + ""); // 转款额度(如 000.00), 只保留小数点后两个位
        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        params.put("flag", "1"); // 1：确认；0：取消
        params.put("password", password); // 密码
        params.put("cur", "CNY"); // 货币,CNY：人民币

        // 请求
        AGResult result = post(params);
        if (result == null) {
            webJSON.set(2, "2-8001");
            return false;
        }

        if ("0".equals(result.getInfo())) {
            // 确认成功
            return true;
        }
        else if ("1".equals(result.getInfo())) {
            // 确认失败
            log.error("AG转账确认失败：" + JSON.toJSONString(result));
            webJSON.set(2, "2-8008");
            return false;
        }
        else if ("2".equals(result.getInfo())) {
            // 转账失败，无效金额
            webJSON.set(2, "2-8007");
            return false;
        }
        // 网络错误，查订单状态
        else if ("network_error".equalsIgnoreCase(result.getInfo())) {
            boolean status = queryOrderStatus(webJSON, billno);
            return status;
        }
        else {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                if (StringUtils.isEmpty(result.getMsg())) {
                    webJSON.set(2, "2-8000");
                }
                else {
                    webJSON.set(2, errorCode, result.getMsg());
                }
            } else {
                webJSON.set(2, errorCode);
            }
            return false;
        }
    }

    /**
    　* @Description: AG转出 或转入 金额
    　* @param [webJSON, username, password, amount, billno, in]
    　* @return boolean
    　* @throws
    　* @author SunJiang
    　* @date 2018/08/24,024 15:24:34
    　*/
    public boolean transferCreditNew(WebJSON webJSON, String username, int amount, boolean in) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        params.put("api_name", api_name);
        params.put("api_pass", api_pass);
        params.put("private_key", private_key);
        params.put("action", in ? "IN" : "OUT"); // in：转入。out：转出
        params.put("remit", amount + ""); // 转款额度(如 000.00), 只保留小数点后两个位
        // 请求
        AGResult result = postAg(params, "Transfer");

        if (result == null) {
            webJSON.set(2, "2-8001");
            return false;
        }
        String info = result.getInfo();
        JSONObject jsonObject = JSONObject.fromObject(info);
        //0成功 1失败
        result.setInfo(jsonObject.get("result").toString());


        if ("0".equals(result.getInfo())) {
            // 确认失败
            log.error("AG转账确认失败：" + jsonObject.get("msg").toString());
            webJSON.set(2, "2-8008");
            return false;
        }
        else if ("1".equals(result.getInfo())) {
            // 确认成功
            return true;
        }
//        else if ("2".equals(result.getInfo())) {
//            // 转账失败，无效金额
//            webJSON.set(2, "2-8007");
//            return false;
//        }
//        // 网络错误，查订单状态
//        else if ("network_error".equalsIgnoreCase(result.getInfo())) {
//            boolean status = queryOrderStatus(webJSON, billno);
//            return status;
//        }
//        else {
//            log.error("AG返回错误：" + jsonObject.get("msg").toString());
//            String errorCode = AGCode.transErrorCode(result.getInfo());
//            if ("2-8006".equals(errorCode)) {
//                if (StringUtils.isEmpty(result.getMsg())) {
//                    webJSON.set(2, "2-8000");
//                }
//                else {
//                    webJSON.set(2, errorCode, result.getMsg());
//                }
//            } else {
//                webJSON.set(2, errorCode);
//            }
//            return false;
//        }
        return false;
    }

    /**
     * 查询订单状态，返回true成功，返回false失败，则在webJson里有错误代码
     */
    private boolean queryOrderStatus(WebJSON webJSON, String billno) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("cagent", aginCagent); // 代理编码
        params.put("billno", billno); // 注单号
        params.put("method", "qos"); // 数值 = “tc” 代表 查询转账状态
        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        params.put("cur", "CNY"); // 货币,CNY：人民币

        // 请求
        AGResult result = post(params);
        if (result == null) {
            webJSON.set(2, "2-8001");
            return false;
        }

        if ("0".equals(result.getInfo())) {
            // 转账成功
            return true;
        }
        else if ("1".equals(result.getInfo())) {
            // 转账失败
            webJSON.set(2, "2-8008");
            return false;
        }
        else if ("2".equals(result.getInfo())) {
            // 转账失败，无效金额
            webJSON.set(2, "2-8007");
            return false;
        } else {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                if (StringUtils.isEmpty(result.getMsg())) {
                    webJSON.set(2, "2-8000");
                }
                else {
                    webJSON.set(2, errorCode, result.getMsg());
                }
            } else {
                webJSON.set(2, errorCode);
            }
            return false;
        }
    }

    private AGResult postAg(HashMap<String, String> params,String paramUrl) {
        try {
            // 请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "WEB_LIB_GI_" + aginCagent);
            // headers.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.put("Content-Type", "text/xml; utf-8=;charset=UTF-8");

            // 请求数据
            String paramsStr = UrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR_NEW, false);
            // 请求
            String url = giurl + "/"+paramUrl+"?"+ paramsStr;
            String xml = HttpClientUtil.doPost(url);

            AGResult result = new AGResult();
            result.setInfo(xml);
            return result;
        } catch (Exception e) {
            log.error("连接AG发生异常，请求参数：" + JSON.toJSONString(params), e);
            return null;
        }
    }
    private AGResult post(HashMap<String, String> params) {
        try {
            // 请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "WEB_LIB_GI_" + aginCagent);
            // headers.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.put("Content-Type", "text/xml; utf-8=;charset=UTF-8");

            // 请求数据
            String paramsStr = UrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR, false);
            String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
            targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");

            // MD5签名
            String key = DigestUtils.md5Hex(targetParams + md5key);

            // 请求
            String url = giurl + "/doBusiness.do?params=" + targetParams + "&key=" + key;
            String xml = HttpClientUtil.post(url, null, headers, 5000);
            AGResult result = toResult(xml);
            return result;
        } catch (Exception e) {
            log.error("连接AG发生异常，请求参数：" + JSON.toJSONString(params), e);
            return null;
        }

        // try {
        //     // 请求头
        //     Map<String, String> headers = new HashMap<>();
        //     headers.put("User-Agent", "WEB_LIB_GI_" + aginCagent);
        //     headers.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        //
        //     // 请求数据
        //     String paramsStr = UrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR, false);
        //     String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
        //     targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");
        //
        //     // MD5签名
        //     String key = DigestUtils.md5Hex(targetParams + md5key);
        //
        //     // 请求
        //     String url = giurl + "/doBusiness.do?params=" + targetParams + "&key=" + key;
        //     String xml = HttpClientUtil.post(url, null, headers, 5000);
        //     AGResult result = toResult(xml);
        //     return result;
        // } catch (Exception e) {
        //     log.error("连接AG发生错误，请求参数：" + JSON.toJSONString(params), e);
        //     return null;
        // }
        // try {
        //     // 请求头
        //     HttpHeaders headers = new HttpHeaders();
        //     headers.add("User-Agent", "WEB_LIB_GI_" + aginCagent);
        //     headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //     HttpEntity<String> request = new HttpEntity<>(headers);
        //
        //     // 请求数据
        //     String paramsStr = UrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR, false);
        //     String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
        //     targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");
        //
        //     // MD5签名
        //     String key = DigestUtils.md5Hex(targetParams + md5key);
        //
        //     // 请求
        //     String url = giurl + "/doBusiness.do?params=" + targetParams + "&key=" + key;
        //     String xml = restTemplate.postForObject(url, request, String.class);
        //     AGResult result = toResult(xml);
        //     return result;
        // } catch (Exception e) {
        //     log.error("连接AG发生错误，请求参数：" + JSON.toJSONString(params), e);
        //     return null;
        // }
    }

    private AGResult toResult(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            String info = doc.getFirstChild().getAttributes().getNamedItem("info").getNodeValue();
            String msg = doc.getFirstChild().getAttributes().getNamedItem("msg").getNodeValue();

            AGResult result = new AGResult();
            result.setInfo(info);
            result.setMsg(msg);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
