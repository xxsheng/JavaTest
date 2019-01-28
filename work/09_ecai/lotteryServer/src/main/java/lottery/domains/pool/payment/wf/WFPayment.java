package lottery.domains.pool.payment.wf;

import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.http.HttpClientUtil;
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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WFPayment extends AbstractPayment {
    private static final String CUS_EXT1 = "FasdfdFasdffcdg,S$QL:H[tk0dlq8o{2xyT";
    private static final Logger log = LoggerFactory.getLogger(WFPayment.class);

    private static final String PAY_TYPE_ALIPAY_NATIVE = "ALIPAY_NATIVE"; // 支付宝扫码
    private static final String PAY_TYPE_ALIPAY_H5 = "ALIPAY_H5"; // 支付宝 H5
    private static final String PAY_TYPE_WEIXIN_NATIVE = "WEIXIN_NATIVE"; // 微信扫码
    private static final String PAY_TYPE_WEIXIN_H5 = "WEIXIN_H5"; // 微信 H5
    private static final String PAY_TYPE_QQ_NATIVE = "QQ_NATIVE"; // QQ 扫码
    private static final String PAY_TYPE_QQ_H5 = "QQ_H5"; // QQ_H5
    private static final String PAY_TYPE_UNIONPAY_NATIVE = "UNIONPAY_NATIVE"; // 银联扫码
    private static final String PAY_TYPE_JD_NATIVE = "JD_NATIVE"; // 京东扫码
    private static final String PAY_TYPE_JD_H5 = "JD_H5"; // JD 扫码


    private static final String OUTPUT_SUCCESS = "SUCCESS";
    private static final String OUTPUT_FAILED = "failed";

    public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                        PaymentChannel channel, HttpServletRequest request) {
        try {
            switch (channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_WF:
                    return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
            }

            return null;
        } catch (Exception e) {
            log.error(channel.getName() + "封装支付参数异常", e);
            return retPrepareFailed("支付通道异常，请重试！");
        }
    }

    private static PrepareResult prepareWangYing(String billno, double amount, String bankco, String notifyUrl, String resultUrl, PaymentChannel channel, HttpServletRequest request) throws Exception {
        return prepareInternal(billno, amount, bankco, notifyUrl, resultUrl, channel, request, PAY_TYPE_UNIONPAY_NATIVE);
    }

    private static PrepareResult prepareInternal(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                                 PaymentChannel channel, HttpServletRequest request, String payType) throws Exception {
        String money = MoneyFormat.moneyToYuanForPositive(amount + "");
        String time = new Moment().toSimpleTime();
        HashMap<String, String> params = new HashMap<>();

        params.put("merId", channel.getMerCode());
        params.put("svcName", "gatewayPay");
        params.put("tranType", bankco);
        params.put("merchOrderId", billno);
        params.put("pName", "zhifu");

        BigDecimal bigDeciMoney = new BigDecimal(money);
        bigDeciMoney = bigDeciMoney.multiply(new BigDecimal("100"));
        params.put("amt", bigDeciMoney.toString());

        params.put("showCashier", "1");
        params.put("notifyUrl", notifyUrl);
        params.put("retUrl", "retUrl\":\"" + resultUrl);


        String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), billno);
        params.put("merData", cusSign); // 回传参数


        //接口签名
        String md5Value = getSign(params, channel.getMd5Key());

        params.put("md5value", md5Value); // 签名

        String formUrl = channel.getPayUrl();
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
        //对签名参数进行排序
        String[] keys = paramsMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if ("".equals(paramsMap.get(key)) == false && key.equals("md5value") == false  ) {
                sb.append(paramsMap.get(key));
            }
        }

        //------------------------------

        //-------------------------------------------

        sb.append(md5Key);

        //接口签名
        String md5Value = DigestUtils.md5Hex(sb.toString()).toUpperCase();
        return md5Value;
    }


    /**
     * 验证回调
     */
    public static VerifyResult verify(PaymentChannel channel, Map<String, String> resMap) throws UnsupportedEncodingException {

        log.info("verify");

        //打印参数
        String[] keys = resMap.keySet().toArray(new String[0]);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key);
            sb.append("=");
            resMap.put(key,URLDecoder.decode(resMap.get(key),"utf-8"));

            if(key.equals("orderStatusMsg"))
            {
                resMap.put(key,"交易成功");
            }

            sb.append(URLDecoder.decode(resMap.get(key), "utf-8") );
            sb.append("\n");

        }

        log.info(sb.toString());

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

    public static void main(String[] args) throws UnsupportedEncodingException {
        PaymentChannel channel = new PaymentChannel();

        channel.setMerCode("WF69417");
        channel.setExt1("ext1");
        channel.setMd5Key("TLg/0cRnHSTiTD/edOuVd+iXQp8i1OkzMkr6ogz7B70t9RRJAolA+A==");


        Map<String, String> resMap =  new HashMap<String , String>();

//        {"tranTime":"20171227 20:05:22",
//                "amt":"10200.00",
//                "merData":"5F900B4CBC192CD167A59AD771C80132",
//                "md5value":"AD9AB9428FB39C486659EE2717596198",
//                "status":"0",
//                "merchOrderId":"171227200413U8CgvQmo",
//                "reqBody":"",
//                "orderId":"I201712270001165242",
//                "orderStatusMsg":"äº¤æ\u0098\u0093æ\u0088\u0090å\u008A\u009F"};

        resMap.put("tranTime","20171227 20:05:22");
        resMap.put("amt","10200.00");
        resMap.put("merData","5F900B4CBC192CD167A59AD771C80132");
        resMap.put("md5value","AD9AB9428FB39C486659EE2717596198");
        resMap.put("status","0");
        resMap.put("merchOrderId","171227200413U8CgvQmo");
        resMap.put("reqBody","");
        resMap.put("orderId","I201712270001165242");
        resMap.put("orderStatusMsg","äº¤æ\u0098\u0093æ\u0088\u0090å\u008A\u009F");


        VerifyResult vr = verify( channel,  resMap);


    }

    public static boolean localVerify(PaymentChannel channel, Map<String, String> resMap) {
        try {

            //		paramMap.put("status","WF68898");
//		paramMap.put("orderStatusMsg","WF68898");
//		paramMap.put("merchOrderId","WF68898");
//		paramMap.put("orderId","WF68898");
//		paramMap.put("amt","WF68898");
//		paramMap.put("tranTime","WF68898");
//		paramMap.put("merData","WF68898");

            String merchantCode = resMap.get("merchant_code");
            String merchOrderId = resMap.get("merchOrderId");
            String orderNo = resMap.get("orderId");
            String orderAmount = resMap.get("order_amount");
            String orderTime = resMap.get("order_time");
            String returnParams = resMap.get("merData");
            String tradeNo = resMap.get("trade_no");
            String tradeStatus = resMap.get("status");
            String sign = resMap.get("md5value");

            //======================================\

//            Map<String , Object> paramMap = new HashMap<String , Object>();
//            String status = getString_UrlDecode_UTF8("status");
//            paramMap.put("status", status);
//            String orderStatusMsg = getString_UrlDecode_UTF8("orderStatusMsg");
//            paramMap.put("orderStatusMsg", orderStatusMsg);
//            String merchOrderId = getString_UrlDecode_UTF8("merchOrderId");
//            paramMap.put("merchOrderId", merchOrderId);
//            String orderId = getString_UrlDecode_UTF8("orderId");
//            paramMap.put("orderId", orderId);
//            String amt = getString_UrlDecode_UTF8("amt");
//            paramMap.put("amt", amt);
//            String tranTime = getString_UrlDecode_UTF8("tranTime");
//            paramMap.put("tranTime", tranTime);
//            String merData = getString_UrlDecode_UTF8("merData");
//            paramMap.put("merData", merData);
//            String req_md5value = getString_UrlDecode_UTF8("md5value");
//
//            //验签
//            String[] keys = paramMap.keySet().toArray(new String[0]);
//            Arrays.sort(keys);
//            StringBuilder sb = new StringBuilder();
//            for (String key : keys) {
//                if (!"".equals(paramMap.get(key))) {
//                    sb.append(paramMap.get(key));
//                }
//            }
//
//            sb.append(MERCHANT_KEY);
//
////            if (logger.isInfoEnabled()) {
////                logger.info("notify(HttpServletRequest, HttpServletResponse, ModelMap) - StringBuilder sb=" + sb); //$NON-NLS-1$
////            }
//
//
//            String md5Value = DigestUtils.md5Hex(sb.toString()).toUpperCase();
//


            //=======================================



            if ("0".equalsIgnoreCase(tradeStatus)) {
                String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), merchOrderId);

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

        //		paramMap.put("status","WF68898");
//		paramMap.put("orderStatusMsg","WF68898");
//		paramMap.put("merchOrderId","WF68898");
//		paramMap.put("orderId","WF68898");
//		paramMap.put("amt","WF68898");
//		paramMap.put("tranTime","WF68898");
//		paramMap.put("merData","WF68898");


        String merId = resMap.get("merId");
        params.put("merId", channel.getMerCode());

        String svcName = resMap.get("svcName");
        params.put("svcName", "payOrderQry");
//        params.put("tranType", "1000044");
//        params.put("pName","zhifu");
//        params.put("amt",resMap.get("amt"));
        String merchOrderId = resMap.get("merchOrderId");
        params.put("merchOrderId", merchOrderId);


        //接口签名
        String md5Value = getSign(params, channel.getMd5Key()).toUpperCase();
        params.put("md5value", md5Value);

//=========================================================

        //打印参数
        String[] keys = params.keySet().toArray(new String[0]);

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key);
            sb.append("=");
            sb.append(params.get(key));
            sb.append("\n");

        }

        log.info("打印远程验证");
        log.info(sb.toString());



        //================================================================

        try {
            String url = "https://pay.llsyqm.com/payApi" ;

            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(params));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            WFPayQueryResult queryResult = JSON.parseObject(retStr, WFPayQueryResult.class);
            if (queryResult == null) {
                log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(params));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            if (!"000000".equalsIgnoreCase(queryResult.getIsSuccess())) {
                log.error(channel.getName() + "自动上分远程验证失败，返回状态表示执行不成功：请求：" + JSON.toJSONString(params) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

            if (!"0".equals(queryResult.getTradeStatus())) {
                log.error(channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(params) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

            VerifyResult verifyResult = new VerifyResult();
            verifyResult.setSelfBillno(queryResult.getTradeNo());
            verifyResult.setChannelBillno(queryResult.getOrderNo());
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

            //		paramMap.put("status","WF68898");
//		paramMap.put("orderStatusMsg","WF68898");
//		paramMap.put("merchOrderId","WF68898");
//		paramMap.put("orderId","WF68898");
//		paramMap.put("amt","WF68898");
//		paramMap.put("tranTime","WF68898");
//		paramMap.put("merData","WF68898");

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
            log.error("五福编码异常", e);
        }
        return null;
    }
}
