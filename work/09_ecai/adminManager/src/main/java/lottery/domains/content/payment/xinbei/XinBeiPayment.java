package lottery.domains.content.payment.xinbei;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import javautils.http.HttpClientUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.payment.mkt.URLUtils;
import lottery.domains.content.payment.utils.MoneyFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 新贝
 * Created by Nick on 2017-05-28.
 */
@Component
public class XinBeiPayment implements InitializingBean{
    private static final Logger log = LoggerFactory.getLogger(XinBeiPayment.class);
    private static final String ENCRYPT_KEY = "1"; // 1：MD5加密
    private static final String ENCRYPT = "1"; // 1：MD5加密
    private static final String SETTLE_TYPE = "1"; // 0：自助结算；1：委托结算；
    private static final String URGENT_TYPE = "1"; // 1:T+0；0:T+N；
    private static Map<Integer, String> BRANCH_NAMES = new HashMap<>();

    @Value("${xinbei.daifu.url}")
    private String daifuUrl;

    @Value("${xinbei.daifu.query.url}")
    private String daifuQueryUrl;

    @Value("${xinbei.daifu.wangyin.paypasswd}")
    private String daifuWangyingPasswd;

    @Value("${xinbei.daifu.wangyin.tokenkey}")
    private String daifuWangyingTokenKey;

    @Value("${xinbei.daifu.wechat.paypasswd}")
    private String daifuWeChatPasswd;

    @Value("${xinbei.daifu.wechat.tokenkey}")
    private String daifuWeChatTokenKey;

    @Value("${xinbei.daifu.alipay.paypasswd}")
    private String daifuAlipayPasswd;

    @Value("${xinbei.daifu.alipay.tokenkey}")
    private String daifuAlipayTokenKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        BRANCH_NAMES.put(1, "中国工商银行股份有限公司上海市龙江路支行");
        BRANCH_NAMES.put(2, "中国建设银行北京市分行营业部");
        BRANCH_NAMES.put(3, "中国农业银行股份有限公司忻州和平分理处");
        BRANCH_NAMES.put(4, "招商银行股份有限公司厦门金湖支行");
        BRANCH_NAMES.put(5, "中国银行股份有限公司赣州市客家大道支行");
        BRANCH_NAMES.put(6, "交通银行北京安翔里支行");
        BRANCH_NAMES.put(7, "上海浦东发展银行安亭支行");
        BRANCH_NAMES.put(8, "兴业银行北京安华支行");
        BRANCH_NAMES.put(9, "中信银行北京安贞支行");
        BRANCH_NAMES.put(10, "宁波银行股份有限公司北京东城支行");
        BRANCH_NAMES.put(11, "上海银行股份有限公司北京安贞支行");
        BRANCH_NAMES.put(12, "杭州银行股份有限公司上海北新泾支行");
        BRANCH_NAMES.put(13, "渤海银行股份有限公司北京朝阳门支行");
        BRANCH_NAMES.put(14, "浙商银行股份有限公司杭州滨江支行");
        BRANCH_NAMES.put(15, "广发银行股份有限公司北京朝阳北路支行");
        BRANCH_NAMES.put(16, "中国邮政储蓄银行股份有限公司北京昌平区北七家支行");
        BRANCH_NAMES.put(17, "深圳发展银行");
        BRANCH_NAMES.put(18, "中国民生银行股份有限公北京西大望路支行");
        BRANCH_NAMES.put(19, "中国光大银行股份有限公司北京安定门支行");
        BRANCH_NAMES.put(20, "华夏银行北京德外支行");
        BRANCH_NAMES.put(21, "北京银行安定门支行");
        BRANCH_NAMES.put(22, "南京银行股份有限公司北京车公庄支行");
        BRANCH_NAMES.put(23, "平安银行股份有限公司北京北苑支行");
        BRANCH_NAMES.put(24, "北京农村商业银行股份有限公司漷县支行");
    }

    /**
     * 网银代付接口，返回第三方的注单ID
     */
    public String daifu(WebJSONObject json, PaymentChannel channel, double money, String billno,
                         String name, int bankId, String card, String branchName, String returnUrl) {
        try {
            log.debug("开始新贝网银代付,注单ID:{},姓名:{},卡号:{},分行:{},商户号：{}", billno, name, card, branchName, channel.getMerCode());
            return daifuInternel(json, channel.getMerCode(), daifuWangyingPasswd, daifuWangyingTokenKey, money, billno, name, bankId, card, branchName, returnUrl);
        } catch (Exception e) {
            log.error("新贝网银代付发生异常", e);
            json.set(2, "2-4000");
            return null;
        }
    }

    /**
     * 微信代付接口，返回第三方的注单ID
     */
    public String daifuWeChat(WebJSONObject json, PaymentChannel channel, double money, String billno,
                         String name, int bankId, String card, String branchName, String returnUrl) {
        try {
            log.debug("开始新贝微信代付,注单ID:{},姓名:{},卡号:{},分行:{},商户号：{}", billno, name, card, branchName, channel.getMerCode());
            return daifuInternel(json, channel.getMerCode(), daifuWeChatPasswd, daifuWeChatTokenKey, money, billno, name, bankId, card, branchName, returnUrl);
        } catch (Exception e) {
            log.error("新贝微信代付发生异常", e);
            json.set(2, "2-4000");
            return null;
        }
    }

    /**
     * 支付宝代付接口，返回第三方的注单ID
     */
    public String daifuAlipay(WebJSONObject json, PaymentChannel channel, double money, String billno,
                         String name, int bankId, String card, String branchName, String returnUrl) {
        try {
            log.debug("开始新贝支付宝代付,注单ID:{},姓名:{},卡号:{},分行:{},商户号：{}", billno, name, card, branchName, channel.getMerCode());
            return daifuInternel(json, channel.getMerCode(), daifuAlipayPasswd, daifuAlipayTokenKey, money, billno, name, bankId, card, branchName, returnUrl);
        } catch (Exception e) {
            log.error("新贝支付宝代付发生异常", e);
            json.set(2, "2-4000");
            return null;
        }
    }



    private String daifuInternel(WebJSONObject json, String merCode, String passwd, String tokenKey, double money, String billno,
                                 String name, int bankId, String card, String branchName, String returnUrl){
        try {
            if (bankId == 23) {
                // 平安银行不能出款
                json.set(2, "2-4012");
                return null;
            }

            String realBranchName = BRANCH_NAMES.get(bankId);
            if (realBranchName == null) {
                realBranchName = branchName;
            }

            long amount = MoneyFormat.yuanToFenMoney(money+"");
            String bankConfig = realBranchName + "|" + card + "|" + name + "|" + amount + "|1"; // [开户行全称或清算行号|银行卡号|开户人|结算金额|1：对私，2：对公]

            String sign = amount + bankConfig + ENCRYPT + returnUrl + billno + passwd + SETTLE_TYPE + URGENT_TYPE + tokenKey;

            sign = DigestUtils.md5Hex(sign).toUpperCase();

            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("amount", amount+""); // 单位：分
            dataMap.put("bank_config", bankConfig); // [开户行全称或清算行号|银行卡号|开户人|结算金额|1：对私，2：对公]
            dataMap.put("encrypt", ENCRYPT); // 1：MD5加密
            dataMap.put("PayPassWord", passwd);
            dataMap.put("settle_type", SETTLE_TYPE); // 0：自助结算；1：委托结算；
            dataMap.put("sign", sign); // 报文签名
            dataMap.put("UrgentType", URGENT_TYPE); // 1:T+0；0:T+N；
            dataMap.put("MerchantOrder", billno); // 商户平台订单号，商户平台内唯一
            dataMap.put("MerchantNotifyUrl", returnUrl); // 代付成功后，接受回传通知的地址

            String dataString = JSON.toJSONString(dataMap);
            dataString = URLUtils.encode(dataString, "UTF-8");

            Map<String, String> paramsMap = new LinkedHashMap<>();
            paramsMap.put("data", dataString);
            paramsMap.put("Merchantaccount", merCode);
            paramsMap.put("Encryptkey", ENCRYPT_KEY);

            String retStr;
            try {
                // retStr = WebUtil.doPost(daifuUrl, paramsMap, "UTF-8", 3000, 15000);
                // retStr = new HttpsClientUtil().doPost(daifuUrl, paramsMap, "UTF-8");
                retStr = HttpClientUtil.post(daifuUrl, paramsMap, null, 5000);
            } catch (Exception e) {
                log.error("新贝代付发生连接异常", e);
                retStr = null;
            }
            if (StringUtils.isEmpty(retStr)) {
                log.error("新贝代付发生连接异常，开始查询订单状态：" + billno);
                String xbOrderId = query(merCode, tokenKey, billno);
                if (StringUtils.isNotEmpty(xbOrderId)) {
                    log.debug("新贝代付查询返回表示成功,我方订单号：{},新贝订单号：{}", billno, xbOrderId);
                    return xbOrderId;
                }
                else {
                    log.error("新贝代付查询返回表示不成功：我方订单号：" + billno);
                    json.set(2, "2-4001");
                    return null;
                }
            }

            XinbeiDaifuResult result;
            try {
                retStr = URLUtils.decode(retStr, "UTF-8");
                result = JSON.parseObject(retStr, XinbeiDaifuResult.class);
            } catch (Exception e) {
                log.error("新贝代付请求失败，解析返回数据失败，返回数据为：" + retStr, e);
                json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                return null;
            }

            if (result == null) {
                log.error("新贝代付请求失败，解析返回数据失败，返回数据为：" + retStr);
                json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                return null;
            }

            if ("1".equals(result.getStatus())) {

                String serverSign = result.getAmount() + result.getBankConfig() + result.getMsg() + result.getMsgCode() + result.getSerialNo() + result.getSettleType() + result.getStatus()+tokenKey;
                serverSign = DigestUtils.md5Hex(serverSign).toUpperCase();

                if (StringUtils.equalsIgnoreCase(serverSign, result.getSin())) {
                    if (StringUtils.isNotEmpty(result.getSerialNo())) {
                        log.debug("新贝代付请求返回订单号：{}", result.getSerialNo());
                        return result.getSerialNo();
                    }
                    else {
                        log.error("新贝代付返回订单ID为空,我方订单ID:" + billno);
                        json.setWithParams(2, "2-4014");
                        return null;
                    }
                }
                else{
                    log.error("新贝代付请求成功,但返回数据签名与服务器签名不匹配,返回数据为：" + retStr + ",服务器签名:" + serverSign);
                    json.setWithParams(2, "2-4008");
                    return result.getSerialNo();
                }
            }
            else {
                // 返回失败，查询订单状态
                if ("申请提现失败".equals(result.getMsg())) {
                    log.debug("新贝代付请求返回提示：{}，开始查询订单状态：{}", result.getMsg(), billno);
                    String xbOrderId = query(merCode, tokenKey, billno);
                    if (StringUtils.isNotEmpty(xbOrderId)) {
                        log.debug("新贝代付查询返回表示成功,我方订单号：{},新贝订单号：{}", billno, xbOrderId);
                        return xbOrderId;
                    }
                    else {
                        log.error("新贝代付请求失败,返回数据为：" + retStr + ",我方订单ID:" + billno);
                        json.setWithParams(2, "2-4002", result.getMsg());
                        return null;
                    }
                }
                else {
                    log.error("新贝代付请求失败,返回数据为：" + retStr + ",我方订单ID:" + billno);
                    json.setWithParams(2, "2-4002", result.getMsg());
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("新贝代付失败,发生异常", e);
            json.set(2, "2-4000");
            return null;
        }
    }


    private String query(String merCode, String tokenKey, String billno){
        try {
            String sign = ENCRYPT + merCode + billno + tokenKey;
            sign = DigestUtils.md5Hex(sign).toUpperCase();

            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("Encrypt", ENCRYPT); // 1表示MD5加密
            dataMap.put("MerchantCode", merCode); // 新贝平台账号编码
            dataMap.put("OrderId", billno); // 商户自己业务逻辑的订单号
            dataMap.put("sign", sign);

            String dataString = JSON.toJSONString(dataMap);
            dataString = URLUtils.encode(dataString, "UTF-8");

            Map<String, String> paramsMap = new LinkedHashMap<>();
            paramsMap.put("data", dataString);
            paramsMap.put("Merchantaccount", merCode);
            paramsMap.put("Encryptkey", ENCRYPT_KEY);

            String retStr;
            try {
                // retStr = new HttpsClientUtil().doPost(daifuQueryUrl, paramsMap, "UTF-8");
                retStr = HttpClientUtil.post(daifuQueryUrl, paramsMap, null, 5000);
            } catch (Exception e) {
                log.error("新贝代付查询请求失败，发生连接异常", e);
                return null;
            }
            if (StringUtils.isEmpty(retStr)) {
                log.error("新贝代付查询请求失败，发送请求后返回空数据");
                return null;
            }

            XinbeiDaifuQueryResult result;
            try {
                retStr = URLUtils.decode(retStr, "UTF-8");

                log.debug("新贝查询返回数据：{}", retStr);

                retStr = retStr.substring(9, retStr.length()-2);

                result = JSON.parseObject(retStr, XinbeiDaifuQueryResult.class);
            } catch (Exception e) {
                log.error("新贝代付查询请求失败，解析返回数据失败，返回数据为：" + retStr, e);
                return null;
            }

            if (result == null) {
                log.error("新贝代付查询请求失败，解析返回数据失败，返回数据为：" + retStr);
                return null;
            }

            if ("00".equals(result.getStatus()) || "50".equals(result.getStatus()) && StringUtils.isNotEmpty(result.getXbeiOrderId())) { // 00成功；50进行中；99失败。-50订单不存在；
                log.debug("新贝代付查询请求成功，返回状态表示成功");
                return result.getXbeiOrderId();
            }
            else {
                return null;
            }
        } catch (Exception e) {
            log.error("新贝代付查询请求失败,发生异常", e);
            return null;
        }
    }
}
