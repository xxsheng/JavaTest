package lottery.domains.content.api.ag;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.encrypt.DESUtil;
import javautils.ftp.FTPServer;
import javautils.http.HttpClientUtil;
import javautils.http.ToUrlParamUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2016/12/28.
 */
@Component
public class AGAPI {
    private static final Logger log = LoggerFactory.getLogger(AGAPI.class);
    private static final String AG_KEY_SEPARATOR = "/\\\\/";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

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
    @Value("${ag.ftpusername}")
    private String ftpUsername;
    @Value("${ag.ftppassword}")
    private String ftpPassword;
    @Value("${ag.ftpurl}")
    private String ftpUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 生成进入游戏中心的链接
     *
     * @param loginname AG用户名
     * @param password  AG密码
     * @param website   返回主页的链接，获取request.getHeader("Host)
     */
    public String forwardGame(String loginname, String password, String website) {
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
        paramsMap.put("method", "lg"); // 检测并创建账号
        paramsMap.put("gameType", "1"); // 游戏类型 (AGIN 平台, 为空将导入整合页面)  0 大厅；1 旗舰厅 11 HTML5 大厅 (AGIN 移动网页版游戏平台大厅)
        paramsMap.put("oddtype", oddtype); // 下注范围，A：20-50000。B：50-5000。C：20-10000。
        paramsMap.put("cur", "CNY"); // 货币种类
        // paramsMap.put("mh5", "y"); // 代表 AGIN 移动网页版
        // paramsMap.put("flashid", ""); HB 平台(Habanero 平台)
        // paramsMap.put("session_token", ""); 生成方式：当用戶登陆网站，网站保存Session Token在内存，用于驗證用户合法性 接入Iframe及手机Html5时必须带入session_token

        String paramsStr = ToUrlParamUtils.toUrlParam(paramsMap, AG_KEY_SEPARATOR, false);

        String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
        targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");

        String key = DigestUtils.md5Hex(targetParams + md5key);

        String url = gciurl + "/forwardGame.do?params=" + targetParams + "&key=" + key;
        return url;
    }

    /**
     * 检测并创建游戏账号，返回创建后的用户名，如果返回空，则在webJson里有错误代码
     *
     * @param username AG用户名
     * @param password AG密码
     * @return
     */
    public String checkOrCreateGameAccount(WebJSONObject webJSON, String username, String password) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("cagent", aginCagent); // 代理编码
        params.put("loginname", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        params.put("method", "lg"); // 数值 = “lg” 代表 ”检测并创建游戏账号
        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        params.put("password", password); // 游戏账号密码
        params.put("oddtype", oddtype); // 下注范围，A：20-50000。B：50-5000。C：20-10000。
        params.put("cur", "CNY"); // 货币,CNY：人民币

        // 请求
        AGResult result = post(params);
        if (result == null) {
            log.error("AG返回内容解析为空");
            webJSON.set(2, AGCode.DEFAULT_ERROR_CODE);
            return null;
        }

        // 返回是否正确
        if (!"0".equals(result.getInfo())) {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                webJSON.set(2, errorCode);
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
    public Double getBalance(WebJSONObject webJSON, String username, String password) {
        // 组装参数
        HashMap<String, String> params = new HashMap<>();
        params.put("cagent", aginCagent); // 代理编码
        params.put("loginname", username); // 游戏账号的登錄名, 必須少于 20 個字元  不可以带特殊字符，只可以数字，字母，下划线
        params.put("method", "gb"); // 数值 = “gb” 代表 查询余额
        params.put("actype", actype); // 1：真钱账号，0：试玩账号，AGIN平台的试玩账号的默认额度为2000人民币，试玩账号不提供转账功能
        params.put("password", password); // 游戏账号密码
        params.put("cur", "CNY"); // 货币,CNY：人民币

        // 请求
        AGResult result = post(params);
        if (result == null) {
            log.error("AG返回内容解析为空");
            webJSON.set(2, AGCode.DEFAULT_ERROR_CODE);
            return null;
        }

        // 返回是否正确
        if (!NumberUtils.isNumber(result.getInfo())) {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                webJSON.set(2, errorCode);
            } else {
                webJSON.set(2, errorCode);
            }
            return null;
        }

        return Double.valueOf(result.getInfo());
    }

    /**
     * 转入，返回转账单号
     *
     * @param username AG用户名
     * @param password AG密码
     * @param amount   转账金额，保留2位小数
     * @return
     */
    public String transferIn(WebJSONObject webJSON, String username, String password, int amount) {
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
    public String transferOut(WebJSONObject webJSON, String username, String password, int amount) {
        return transfer(webJSON, username, password, amount, false);
    }

    private String transfer(WebJSONObject webJSON, String username, String password, int amount, boolean in) {
        // 第一步：预备转账
        String billno = prepareTransferCredit(webJSON, username, password, amount, in);
        if (StringUtils.isEmpty(billno)) {
            return null;
        }

        try {
            // 第二步：确认转账
            boolean confirm = transferCreditConfirm(webJSON, username, password, amount, billno, in);
            if (confirm == false) {
                return null;
            }

            // 第三步：查询状态
            boolean result = queryOrderStatus(webJSON, billno);
            return !result ? null : billno;
        } catch (Exception e) {
            log.error("AG转账发生异常：" + billno, e);
            boolean result = queryOrderStatus(webJSON, billno);
            return !result ? null : billno;
        }
    }

    /**
     * 预备转账，返回流水号表示成功，否则失败，则在webJson里有错误代码
     * 依次使用prepareTransferCredit -> transferCreditConfirm -> queryOrderStatus
     *
     * @param username 系统内的用户名
     * @return
     */
    private String prepareTransferCredit(WebJSONObject webJSON, String username, String password, int amount, boolean in) {
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
            log.error("AG返回内容解析为空");
            webJSON.set(2, AGCode.DEFAULT_ERROR_CODE);
            return null;
        }

        // 返回是否正确
        if (!"0".equals(result.getInfo())) {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                webJSON.set(2, errorCode);
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
    private boolean transferCreditConfirm(WebJSONObject webJSON, String username, String password, int amount, String billno, boolean in) {
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
            log.error("AG返回内容解析为空");
            webJSON.set(2, AGCode.DEFAULT_ERROR_CODE);
            return false;
        }

        if ("0".equals(result.getInfo())) {
            // 确认成功
            return true;
        } else if ("1".equals(result.getInfo())) {
            // 确认失败
            log.error("AG转账确认失败：" + JSON.toJSONString(result));
            webJSON.set(2, "2-8008");
            return false;
        } else if ("2".equals(result.getInfo())) {
            // 转账失败，无效金额
            webJSON.set(2, "2-8007");
            return false;
        }
        // 网络错误，查订单状态
        else if ("network_error".equals(result.getInfo())) {
            boolean status = queryOrderStatus(webJSON, billno);
            return status;
        } else {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                webJSON.set(2, errorCode);
            } else {
                webJSON.set(2, errorCode);
            }
            return false;
        }
    }

    /**
     * 查询订单状态，返回true成功，返回false失败，则在webJson里有错误代码
     */
    private boolean queryOrderStatus(WebJSONObject webJSON, String billno) {
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
            log.error("AG返回内容解析为空");
            webJSON.set(2, AGCode.DEFAULT_ERROR_CODE);
            return false;
        }

        if ("0".equals(result.getInfo())) {
            // 转账成功
            return true;
        } else if ("1".equals(result.getInfo())) {
            // 转账失败
            webJSON.set(2, "2-8008");
            return false;
        } else if ("2".equals(result.getInfo())) {
            // 转账失败，无效金额
            webJSON.set(2, "2-8007");
            return false;
        } else {
            log.error("AG返回错误：" + JSON.toJSONString(result));
            String errorCode = AGCode.transErrorCode(result.getInfo());
            if ("2-8006".equals(errorCode)) {
                webJSON.set(2, errorCode);
            } else {
                webJSON.set(2, errorCode);
            }
            return false;
        }
    }

    /**
     * 获取投注内容
     */
    public List<AGBetRecord> getRecords(String startTime, String endTime) throws Exception{
        FTPServer ftpUtil = new FTPServer();
        try {
            ftpUtil.connectServer(ftpUrl, FTPClient.DEFAULT_PORT, ftpUsername, ftpPassword, null);

            Moment start = new Moment().fromTime(startTime);
            Moment end = new Moment().fromTime(endTime);

            // 开始结束日期
            String _startDate = start.format("yyyyMMdd");
            String _endDate = end.format("yyyyMMdd");
            String _startTime = start.format("yyyyMMddHHmm");
            String _endTime = end.format("yyyyMMddHHmm");

            // 要读中文件夹中的所有文件
            List<String> readFiles = new ArrayList<>();

            // 开始日期的所有文件
            // 真人
            List<String> startFiles = ftpUtil.getFileList("/AGIN/" + _startDate);
            startFiles = filterFiles(startFiles, _startDate, _startTime, _endTime, "AGIN");
            if (CollectionUtils.isNotEmpty(startFiles)) {
                readFiles.addAll(startFiles);
            }

            // 捕鱼
            List<String> hunterStartFiles = ftpUtil.getFileList("/HUNTER/" + _startDate);
            hunterStartFiles = filterFiles(hunterStartFiles, _startDate, _startTime, _endTime, "HUNTER");
            if (CollectionUtils.isNotEmpty(hunterStartFiles)) {
                readFiles.addAll(hunterStartFiles);
            }

            // XIN电子游戏
            List<String> xinStartFiles = ftpUtil.getFileList("/XIN/" + _startDate);
            xinStartFiles = filterFiles(xinStartFiles, _startDate, _startTime, _endTime, "XIN");
            if (CollectionUtils.isNotEmpty(xinStartFiles)) {
                readFiles.addAll(xinStartFiles);
            }

            // YOPLAY电子游戏
            List<String> yoplayStartFiles = ftpUtil.getFileList("/YOPLAY/" + _startDate);
            yoplayStartFiles = filterFiles(yoplayStartFiles, _startDate, _startTime, _endTime, "YOPLAY");
            if (CollectionUtils.isNotEmpty(yoplayStartFiles)) {
                readFiles.addAll(yoplayStartFiles);
            }

            // 结束日期的所有文件
            if (!_startDate.equals(_endDate)) {
                // 真人
                List<String> endFiles = ftpUtil.getFileList("/AGIN/" + _endDate);
                endFiles = filterFiles(endFiles, _endDate, _startTime, _endTime, "AGIN");
                if (CollectionUtils.isNotEmpty(endFiles)) {
                    readFiles.addAll(endFiles);
                }

                // 捕鱼
                List<String> hunterEndFiles = ftpUtil.getFileList("/HUNTER/" + _endDate);
                hunterEndFiles = filterFiles(hunterEndFiles, _endDate, _startTime, _endTime, "HUNTER");
                if (CollectionUtils.isNotEmpty(hunterEndFiles)) {
                    readFiles.addAll(hunterEndFiles);
                }

                // XIN电子游戏
                List<String> xinEndFiles = ftpUtil.getFileList("/XIN/" + _endDate);
                xinEndFiles = filterFiles(xinEndFiles, _endDate, _startTime, _endTime, "XIN");
                if (CollectionUtils.isNotEmpty(xinEndFiles)) {
                    readFiles.addAll(xinEndFiles);
                }

                // YOPLAY电子游戏
                List<String> yoplayFiles = ftpUtil.getFileList("/YOPLAY/" + _endDate);
                yoplayFiles = filterFiles(yoplayFiles, _endDate, _startTime, _endTime, "YOPLAY");
                if (CollectionUtils.isNotEmpty(yoplayFiles)) {
                    readFiles.addAll(yoplayFiles);
                }
            }

            if (CollectionUtils.isEmpty(readFiles)) {
                return null;
            }

            List<AGBetRecord> records = new ArrayList<>();
            for (String readFile : readFiles) {
                String xml = ftpUtil.readFile(readFile);
                if (StringUtils.isEmpty(xml)) {
                    continue;
                }
                List<AGBetRecord> betRecords = toRecords(xml);
                if (CollectionUtils.isNotEmpty(betRecords)) {
                    records.addAll(betRecords);
                }
            }

            return records;
        } catch (Exception e) {
            log.error("获取AG投注记录时出错", e);
            throw e;
        } finally {
            try {
                ftpUtil.closeServer();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private List<String> filterFiles(List<String> files, String date, String startTime, String endTime, String folder) {
        if (CollectionUtils.isEmpty(files)) {
            return null;
        }

        List<String> filterFiles = new ArrayList<>();
        for (String file : files) {
            String fileTime = file.split("\\.")[0];
            if (fileTime.compareTo(startTime) >= 0 && fileTime.compareTo(endTime) <= 0) {
                filterFiles.add("/"+folder+"/" + date + "/" + file);
            }
        }

        return filterFiles;
    }

    private AGResult post(HashMap<String, String> params) {
        try {
            // 请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "WEB_LIB_GI_" + aginCagent);
            headers.put("Content-Type", "text/xml; utf-8=;charset=UTF-8");

            // 请求数据
            String paramsStr = ToUrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR, false);
            String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
            targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");

            // MD5签名
            String key = DigestUtils.md5Hex(targetParams + md5key);

            // 请求
            String url = giurl + "/doBusiness.do?params=" + targetParams + "&key=" + key;

            log.debug("AG操作参数URL：{}，操作参数：{}", url, JSON.toJSONString(params));

            String xml = HttpClientUtil.post(url, null, headers, 100000);

            AGResult result = toResult(xml);
            return result;
        } catch (Exception e) {
            log.error("连接AG发生错误，请求参数：" + JSON.toJSONString(params), e);
            return null;
        }
        // try {
        //     // 请求头
        //     HttpHeaders headers = new HttpHeaders();
        //     headers.add("User-Agent", "WEB_LIB_GI_" + aginCagent);
        //     headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //     HttpEntity<String> request = new HttpEntity<>(headers);
        //
        //     // 请求数据
        //     String paramsStr = ToUrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR, false);
        //     String targetParams = DESUtil.getInstance().encryptStr(paramsStr, deskey);
        //     targetParams = targetParams.replaceAll(LINE_SEPARATOR, "");
        //
        //     // MD5签名
        //     String key = DigestUtils.md5Hex(targetParams + md5key);
        //
        //     // 请求
        //     String url = giurl + "/doBusiness.do?params=" + targetParams + "&key=" + key;
        //
        //     // log.debug("AG操作参数：{}", JSON.toJSONString(params));
        //     // log.debug("AG操作参数URL：{}", url);
        //
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
            log.error("转换AG结果出现异常：" + xml, e);
            return null;
        }
    }

    private List<AGBetRecord> toRecords(String xml) {
        List<AGBetRecord> records = new ArrayList<>();
        try {

            String[] splits = xml.split(LINE_SEPARATOR);
            for (String split : splits) {
                if (StringUtils.isEmpty(split)) {
                    continue;
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(split)));

                Node firstChild = doc.getFirstChild();

                NamedNodeMap attributes = firstChild.getAttributes();

                String dataType = attributes.getNamedItem("dataType").getNodeValue();
                if ("BR".equals(dataType) || "EBR".equals(dataType)) {
                    String billNo = attributes.getNamedItem("billNo").getNodeValue();
                    String playerName = attributes.getNamedItem("playerName").getNodeValue();
                    String agentCode = attributes.getNamedItem("agentCode").getNodeValue();
                    String gameCode = attributes.getNamedItem("gameCode").getNodeValue();
                    String netAmount = attributes.getNamedItem("netAmount").getNodeValue();
                    String betTime = attributes.getNamedItem("betTime").getNodeValue();
                    String gameType = attributes.getNamedItem("gameType").getNodeValue();
                    String betAmount = attributes.getNamedItem("betAmount").getNodeValue();
                    String validBetAmount = attributes.getNamedItem("validBetAmount").getNodeValue();
                    String flag = attributes.getNamedItem("flag").getNodeValue();
                    String playType = attributes.getNamedItem("playType").getNodeValue();
                    String currency = attributes.getNamedItem("currency").getNodeValue();
                    String tableCode = attributes.getNamedItem("tableCode").getNodeValue();
                    String recalcuTime = attributes.getNamedItem("recalcuTime").getNodeValue();
                    String platformType = attributes.getNamedItem("platformType").getNodeValue();
                    String remark = attributes.getNamedItem("remark").getNodeValue();
                    String round = attributes.getNamedItem("round").getNodeValue();
                    String result = attributes.getNamedItem("result").getNodeValue();
                    String beforeCredit = attributes.getNamedItem("beforeCredit").getNodeValue();
                    String deviceType = attributes.getNamedItem("deviceType").getNodeValue();

                    if ("1".equals(flag)) { // 1:已结算；0:未结算
                        AGBetRecord record = new AGBetRecord();
                        record.setDataType(dataType);
                        record.setBillNo(billNo);
                        record.setPlayerName(playerName);
                        record.setAgentCode(agentCode);
                        record.setGameCode(gameCode);
                        record.setNetAmount(netAmount);
                        record.setBetTime(recalcuTime);
                        record.setGameType(gameType);
                        record.setBetAmount(betAmount);
                        record.setValidBetAmount(validBetAmount);
                        record.setFlag(flag);
                        record.setPlayType(playType);
                        record.setCurrency(currency);
                        record.setTableCode(tableCode);
                        record.setRecalcuTime(recalcuTime);
                        record.setPlatformType(platformType);
                        record.setRemark(remark);
                        record.setRound(round);
                        record.setResult(result);
                        record.setBeforeCredit(beforeCredit);
                        record.setDeviceType(deviceType);
                        records.add(record);
                    }
                }
                else if ("HSR".equals(dataType)) {
                    String billNo = attributes.getNamedItem("tradeNo").getNodeValue();
                    String playerName = attributes.getNamedItem("playerName").getNodeValue();
                    // String agentCode = attributes.getNamedItem("agentCode").getNodeValue();
                    String type = attributes.getNamedItem("type").getNodeValue();
                    String Earn = attributes.getNamedItem("Earn").getNodeValue(); // 输赢
                    String creationTime = attributes.getNamedItem("creationTime").getNodeValue();
                    String Cost = attributes.getNamedItem("Cost").getNodeValue(); // 投注
                    String Roombet = attributes.getNamedItem("Roombet").getNodeValue(); // 房间赔率
                    String flag = attributes.getNamedItem("flag").getNodeValue(); // 0成功
                    String platformType = attributes.getNamedItem("platformType").getNodeValue(); // 0成功
                    String previousAmount = attributes.getNamedItem("previousAmount").getNodeValue(); // 0成功

                    if ("0".equals(flag)) {
                        AGBetRecord record = new AGBetRecord();
                        record.setDataType(dataType);
                        record.setBillNo(billNo);
                        record.setPlayerName(playerName);
                        record.setRound(platformType);
                        record.setGameCode("捕鱼");
                        record.setNetAmount(Earn);
                        record.setBetTime(creationTime);
                        record.setGameType(Roombet);
                        record.setBetAmount(Cost);
                        record.setValidBetAmount(Cost);
                        record.setBeforeCredit(previousAmount);
                        record.setFlag(flag);
                        records.add(record);
                    }
                }

            }
        } catch (Exception e) {
            log.error("转换AG投注时出现异常：" + xml, e);
        }
        return records;
    }

    public static String transGameType(String gameType) {
        switch (gameType) {
            case "BAC":
                return "百家乐";
            case "CBAC":
                return "包桌百家乐";
            case "LINK":
                return "连环百家乐";
            case "DT":
                return "龙虎";
            case "SHB":
                return "骰宝";
            case "ROU":
                return "轮盘";
            case "FT":
                return "番摊";
            case "LBAC":
                return "竞咪百家乐";
            case "ULPK":
                return "终极德州扑克";
            case "SBAC":
                return "保險百家樂";
            case "NN":
                return "牛牛";
            case "BJ":
                return "21點";
            case "ZJH":
                return "炸金花";
            case "0.10":
                return "0.1倍场";
            case "1":
            case "1.0":
            case "1.00":
                return "1倍场";
            case "2":
            case "2.0":
            case "2.00":
                return "2倍场";
            case "10":
            case "10.0":
            case "10.00":
                return "10倍场";
            case "50":
            case "50.0":
            case "50.00":
                return "50倍场";
            case "100":
            case "100.0":
            case "100.00":
                return "100倍场";
            case "200":
            case "200.0":
            case "200.00":
                return "200倍场";
            case "300":
            case "300.0":
            case "300.00":
                return "300倍场";
            case "500":
            case "500.0":
            case "500.00":
                return "500倍场";
            default:
                return gameType;
        }
    }

    public static String transRound(String round) {
        switch (round) {
            case "DSP":
                return "国际厅";
            case "AGQ":
                return "旗舰厅";
            case "VIP":
                return "包桌厅";
            case "LED":
                return "竞咪厅";
            case "LOTTO":
                return "彩票";
            case "AGHH":
                return "豪华厅";
            case "HUNTER":
                return "捕鱼厅";
            default:
                return round;
        }
    }

    public static String transDeviceType(String deviceType) {
        switch (deviceType) {
            case "0":
                return "电脑";
            case "1":
                return "手机";
            default:
                return "未知";
        }
    }

    // public static String transPlayType(String playType) {
    //     switch (playType) {
    //         case "1":
    //             return "庄";
    //         case "2":
    //             return "闲";
    //         case "3":
    //             return "和";
    //         case "4":
    //             return "庄对";
    //         case "5":
    //             return "闲对";
    //         case "6":
    //             return "大";
    //         case "7":
    //             return "小";
    //         case "8":
    //             return "莊保險";
    //         case "9":
    //             return "閑保險";
    //         case "11":
    //             return "庄免佣";
    //         case "12":
    //             return "庄龙宝";
    //         case "13":
    //             return "闲龙宝";
    //         case "21":
    //             return "龙";
    //         case "22":
    //             return "虎";
    //         case "23":
    //             return "和(龙虎)";
    //         case "41":
    //             return "大";
    //         case "42":
    //             return "小";
    //         case "43":
    //             return "单";
    //         case "44":
    //             return "双";
    //         case "45":
    //             return "全围";
    //         case "46":
    //             return "围1";
    //         case "47":
    //             return "围2";
    //         case "48":
    //             return "围3";
    //         case "49":
    //             return "围4";
    //         case "50":
    //             return "围5";
    //         case "51":
    //             return "围6";
    //         case "52":
    //             return "单点1";
    //         case "53":
    //             return "单点2";
    //         case "54":
    //             return "单点3";
    //         case "55":
    //             return "单点4";
    //         case "56":
    //             return "单点5";
    //         case "57":
    //             return "单点6";
    //         case "58":
    //             return "对子1";
    //         case "59":
    //             return "对子2";
    //         case "60":
    //             return "对子3";
    //         case "61":
    //             return "对子4";
    //         case "62":
    //             return "对子5";
    //         case "63":
    //             return "对子6";
    //         case "64":
    //             return "组合12";
    //         case "65":
    //             return "组合13";
    //         case "66":
    //             return "组合14";
    //         case "67":
    //             return "组合15";
    //         default:
    //             return "未知";
    //     }
    // }

    public static void main(String[] args) {
        try {
            String xml = "<row dataType=\"BR\"  billNo=\"161231136782592\" playerName=\"qqq123_5667\" agentCode=\"A8P001001001001\" gameCode=\"GB00216C310OB\" netAmount=\"-20\" betTime=\"2016-12-31 13:37:00\" gameType=\"BAC\" betAmount=\"20\" validBetAmount=\"20\" flag=\"1\" playType=\"1\" currency=\"CNY\" tableCode=\"B20R\" loginIP=\"203.177.178.242\" recalcuTime=\"2016-12-31 13:37:18\" platformType=\"AGIN\" remark=\"\" round=\"AGQ\" result=\"\" beforeCredit=\"20\" deviceType=\"0\" />";
            xml += LINE_SEPARATOR + xml;
            System.out.println(xml);

            String[] split = xml.split(LINE_SEPARATOR);
            for (String xmlSingle : split) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(xmlSingle)));

                Node firstChild = doc.getFirstChild();
                NamedNodeMap attributes = firstChild.getAttributes();
                String dataType = attributes.getNamedItem("dataType").getNodeValue();
                System.out.println(dataType);
            }



        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
