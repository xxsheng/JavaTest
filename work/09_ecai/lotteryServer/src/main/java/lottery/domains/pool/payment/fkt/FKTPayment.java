package lottery.domains.pool.payment.fkt;

import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.http.HttpClientUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.cfg.MoneyFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class FKTPayment extends AbstractPayment {
    private static final String CUS_EXT1 = "FasdfdFasdffcdg,S$QL:H[tk0dlq8o{2xyT";
    private static final Logger log = LoggerFactory.getLogger(FKTPayment.class);

    private static final String PAY_TYPE_WY = "1"; // 网银
    private static final String PAY_TYPE_WECHAT = "2"; // 微信
    private static final String PAY_TYPE_ALIPAY = "3"; // 支付宝
    private static final String PAY_TYPE_QQ = "5"; // QQ钱包
    private static final String PAY_TYPE_JD = "6"; // 京东钱包

    private static final String OUTPUT_SUCCESS = "success";
    private static final String OUTPUT_FAILED = "failed";

    public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                        PaymentChannel channel, HttpServletRequest request) {
        try {
            switch (channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_FKT:
                    return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
            }

            return null;
        } catch (Exception e) {
            log.error(channel.getName() + "封装支付参数异常", e);
            return retPrepareFailed("支付通道异常，请重试！");
        }
    }

    private static PrepareResult prepareWangYing(String billno, double amount, String bankco, String notifyUrl, String resultUrl, PaymentChannel channel, HttpServletRequest request) throws Exception {
        return prepareInternal(billno, amount, bankco, notifyUrl, resultUrl, channel, request, PAY_TYPE_WY);
    }

    private static PrepareResult prepareInternal(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                                 PaymentChannel channel, HttpServletRequest request, String payType) throws Exception {
        String money = MoneyFormat.moneyToYuanForPositive(amount + "");
        String time = new Moment().toSimpleTime();
        HashMap<String, String> params = new HashMap<>();

        String inputCharset = "UTF-8";
        params.put("input_charset", inputCharset); // 参数字符集编码
        params.put("inform_url", notifyUrl); // 服务器异步通知地址
        params.put("return_url", resultUrl); // 页面同步跳转通知地址
        params.put("pay_type", payType); // 支付方式
        params.put("bank_code", bankco); // 银行编码
        params.put("merchant_code", channel.getMerCode()); // 商户号
        params.put("order_no", billno); // 商户订单号


    //    money = Arrays.toString(AESUtil.encrypt(money.getBytes(), channel.getMd5Key().getBytes()));
        money = FKTAES.encrypt(money, channel.getMd5Key());

        params.put("order_amount", money); // 商户订单总金额
        params.put("order_time", time); // yyyy-MM-dd HH:mm:ss
        params.put("req_referer", HttpUtil.getReferer(request)); // 来路域名 建议空值
        params.put("customer_ip", HttpUtil.getRealIp(null, request)); // 消费者IP建议空值

        String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), billno);
        params.put("return_params", cusSign); // 回传参数

        String sign = getSign(params, channel.getMd5Key());
        params.put("sign", sign); // 签名

        String formUrl = channel.getPayUrl() + "/gateway/pay.html";
        return retPrepareWangYing(formUrl, params);
    }


    private static String getCusSign(String merCode, String ext1, String billno) {
        String _ext1 = ext1;
        if (StringUtils.isEmpty(_ext1)) {
            _ext1 = CUS_EXT1;
        }

        return DigestUtils.md5Hex(merCode + _ext1 + billno).toUpperCase();
    }


    private static String getSign(Map<String, String> paramsMap, String md5Key) {
        TreeMap<String, String> signMap = new TreeMap<>(paramsMap);

        StringBuffer signStr = new StringBuffer();

        Iterator<Map.Entry<String, String>> it = signMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            if ("sign".equalsIgnoreCase(key) || "reqBody".equalsIgnoreCase(key)) {
                continue;
            }

            String value = entry.getValue();
            if (StringUtils.isEmpty(value)) { // 主动忽略空值并且值为空
                continue;
            }

            signStr.append(entry.getKey()).append("=").append(value);
            if (it.hasNext()) {
                signStr.append("&");
            }
        }

        signStr.append("&key=").append(md5Key);

        String sign = DigestUtils.md5Hex(signStr.toString());

        return sign;
    }

    /**
     * 验证回调
     */
    public static VerifyResult verify(PaymentChannel channel, Map<String, String> resMap) {
        try {
            // 本地验证
            if (!localVerify(channel, resMap)) {
                return retVerifyFailed("签名验证失败");
            }

            // 远程验证
            VerifyResult verifyResult = remoteVerify(channel, resMap);

            return verifyResult;
        } catch (Exception e) {
            log.error(channel.getName() + "签名验证失败", e);
            return retVerifyFailed("签名验证失败");
        }
    }

    public static boolean localVerify(PaymentChannel channel, Map<String, String> resMap) {
        try {
            String merchantCode = resMap.get("merchant_code");
            String orderNo = resMap.get("order_no");
            String orderAmount = resMap.get("order_amount");
            String orderTime = resMap.get("order_time");
            String returnParams = resMap.get("return_params");
            String tradeNo = resMap.get("trade_no");
            String tradeStatus = resMap.get("trade_status");
            String sign = resMap.get("sign");

            if ("success".equalsIgnoreCase(tradeStatus)) {
                String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), orderNo);

                if (cusSign.equalsIgnoreCase(returnParams)) {

                    String serverSign = getSign(resMap, channel.getMd5Key());
                    if (serverSign.equalsIgnoreCase(sign)) {
                        return true;
                    } else {
                        log.error(channel.getName() + "回调本地验证失败，参数签名与服务器不一致，服务器验证：" + serverSign + ",回调：" + JSON.toJSONString(resMap));
                        return false;
                    }
                } else {
                    log.warn(channel.getName() + "回调本地验证失败，自定义验签失败，服务器验证：" + cusSign + "，回调：" + JSON.toJSONString(resMap));
                    return false;
                }
            } else {
                log.warn(channel.getName() + "回调本地验证失败，参数状态表示不成功，回调：" + JSON.toJSONString(resMap));
                return false;
            }

        } catch (Exception e) {
            log.error(channel.getName() + "回调本地验证发生异常", e);
        }
        return false;
    }

    private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap) {
        Map<String, String> params = new HashMap<>();

        String orderNo = resMap.get("order_no");
        String tradeNo = resMap.get("trade_no");

        params.put("input_charset","UTF-8");
        params.put("merchant_code", channel.getMerCode());
        params.put("order_no", orderNo);
        params.put("trade_no", tradeNo);

        String sign = getSign(params, channel.getMd5Key());
        params.put("sign", sign);

        try {
//            String url = "http://pay.fktpay.vip/gateway/query.html";
            String url = channel.getPayUrl() + "/gateway/query.html";

            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(resMap));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            FKTPayQueryResult queryResult = JSON.parseObject(retStr, FKTPayQueryResult.class);
            if (queryResult == null) {
                log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(resMap));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            if (!"true".equalsIgnoreCase(queryResult.getIsSuccess())) {
                log.error(channel.getName() + "自动上分远程验证失败，返回状态表示执行不成功：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

            if (!"success".equals(queryResult.getTradeStatus())) {
                log.error(channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

            VerifyResult verifyResult = new VerifyResult();
            verifyResult.setSelfBillno(queryResult.getOrderNo());
            verifyResult.setChannelBillno(queryResult.getTradeNo());
            double money = Double.parseDouble(queryResult.getOrderAmount());
            verifyResult.setRequestMoney(money);
            verifyResult.setReceiveMoney(money);
            verifyResult.setPayTime(new Date());
            verifyResult.setSuccess(true);
            verifyResult.setOutput(OUTPUT_SUCCESS);
            verifyResult.setSuccessOutput(OUTPUT_SUCCESS);
            verifyResult.setFailedOutput(OUTPUT_FAILED);
            return verifyResult;
        } catch (Exception e) {
            log.error(channel.getName() + "自动上分远程验证异常", e);
            return retVerifyFailed("远程验证失败，获取订单失败");
        }
    }


    private static String getSignForVerfiy(Map<String, String> resMap, String md5Key) {
        try {
            HashMap<String, String> signMap = new HashMap<>();

            signMap.put("trade_no", resMap.get("trade_no"));
            signMap.put("order_amount", resMap.get("order_amount"));
            String orderTime = URLDecoder.decode(resMap.get("order_time"), "UTF-8");
            signMap.put("order_time", orderTime);
            signMap.put("notify_type", resMap.get("notify_type"));
            String tradeTime = URLDecoder.decode(resMap.get("trade_time"), "UTF-8");
            signMap.put("trade_time", tradeTime);
            signMap.put("return_params", resMap.get("return_params"));
            signMap.put("merchant_code", resMap.get("merchant_code"));
            signMap.put("trade_status", resMap.get("trade_status"));
            signMap.put("order_no", resMap.get("order_no"));

            return getSign(signMap, md5Key);
        } catch (UnsupportedEncodingException e) {
            log.error("汇通编码异常", e);
        }
        return null;
    }
}
