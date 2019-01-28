package lottery.domains.pool.payment.yr;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import javautils.http.HttpClientUtil;
import javautils.http.HttpUtil;
import javautils.http.UrlParamUtils;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.cf.CFScanResult;
import lottery.domains.pool.payment.cfg.MoneyFormat;
import net.sf.json.JSONObject;

/**
 * 易云支付
 * Created by Nick on 2017-12-30.
 */
public class YRPayment extends AbstractPayment {
    private static final Logger log = LoggerFactory.getLogger(YRPayment.class);

    private static final String P_ISSMART = "0";

    private static final String CHANNEL_ID_WY = "1"; // 网银支付
    private static final String CHANNEL_ID_ALIPAY = "2"; // 支付宝扫码
    private static final String CHANNEL_ID_WECHAT = "3"; // 微信扫码
    private static final String CHANNEL_ID_QQ = "89"; // QQ 扫码
    private static final String CHANNEL_ID_JDPAY = "91"; // 京东钱包

    private static final String OUTPUT_SUCCESS = "SUCCESS";
    private static final String OUTPUT_FAILED = "FAILED";

    public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                                PaymentChannel channel, HttpServletRequest request) {
        try {
            switch(channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_YR:
                    return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_YRQQ:
                    return prepareQQ(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_YRWECHAT:
                    return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_YRALIPAY:
                    return prepareAlipay(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
               
            }

            return null;
        } catch (Exception e) {
            log.error(channel.getName() + "封装支付参数异常", e);
            return retPrepareFailed("支付通道异常，请重试！");
        }
    }

    /**
     * 网银
     */
    private static PrepareResult prepareWangYing(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                               PaymentChannel channel, HttpServletRequest request) {
    	String ip = HttpUtil.getRealIp(null, request);
        Map<String, String> params = new TreeMap<>();
        params.put("payKey", channel.getMerCode());
        params.put("outTradeNo", billno);
        Date d = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
        params.put("orderTime", sdf.format(d));
        params.put("productType", "50000103");
        params.put("productName", "qmt");
        params.put("orderIp", ip);
        params.put("notifyUrl", notifyUrl);
        params.put("returnUrl", resultUrl);
        params.put("orderPrice", amount+""); 
        params.put("bankCode", bankco); 
        String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&",true) + "&paySecret="+channel.getMd5Key();
        String sign  =  DigestUtils.md5Hex(signStr).toUpperCase();
        params.put("sign", sign); // 签名
        try {
            String url = channel.getPayUrl() ;
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取请求地址为空,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }

            YRScanResult scanResult = JSON.parseObject(retStr, YRScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取请求地址时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }

            if (!"0000".equals(scanResult.getResultCode())) {
                log.error(channel.getName() + "获取请求地址返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取请求地址失败，请重试！" + scanResult.getResultCode() + "，" + scanResult.getErrMsg());
            }
            
            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
            prepareResult.setSuccess(true);
            String formUrl = scanResult.getPayMessage().replaceAll("<script>location.href='","");
            formUrl = formUrl.replaceAll("'</script>", "");
            prepareResult.setFormUrl(formUrl);
            return prepareResult;
        } catch (Exception e) {
            log.error(channel.getName() + "获取请求地址异常", e);
            return retPrepareFailed("获取请求地址失败，请重试！");
        }
    
    	
    	
    	//return prepareCommon(billno, amount, bankco, notifyUrl, resultUrl, channel, CHANNEL_ID_WY);
    }

    /**
     * 微信
     */
    private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                     PaymentChannel channel, HttpServletRequest request) {
        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_WECHAT);
    }



    /**
     * 支付宝
     */
    private static PrepareResult prepareAlipay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                              PaymentChannel channel, HttpServletRequest request) {
        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_ALIPAY);
    }

    /**
     * QQ
     */
    private static PrepareResult prepareQQ(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                 PaymentChannel channel, HttpServletRequest request) {
        Map<String, String> params = new TreeMap<>();
        params.put("payKey", channel.getMerCode());
        params.put("orderPrice", amount+""); 
        params.put("outTradeNo", billno);
        params.put("productType", "70000303");
     	String ip = HttpUtil.getRealIp(null, request);
        Date d = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
        params.put("orderTime", sdf.format(d));
        params.put("productName", "qmt");
        params.put("orderIp", ip);
        params.put("notifyUrl", notifyUrl);
        params.put("returnUrl", resultUrl);
        String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&",true) + "&paySecret="+channel.getMd5Key();
        String sign  =  DigestUtils.md5Hex(signStr).toUpperCase();
        params.put("sign", sign); // 签名
        try {
            String url = "http://gateway.gzwwjy.com/cnpPay/initPay";
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            YRScanResult scanResult = JSON.parseObject(retStr, YRScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            if (!"0000".equals(scanResult.getResultCode())) {
                log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getErrMsg() + "，" + scanResult.getPayMessage());
            }
            
/*            Map<String,String> data = scanResult.getRespType();
            if (data.isEmpty()) {
                log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }
             String pay_QR= data.get("pay_QR");
            if (StringUtils.isEmpty(pay_QR)) {
                log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }*/

            //String qrCode = ImageUtil.encodeQR(pay_QR, 200, 200);

            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setJumpType(PrepareResult.JUMP_TYPE_QR);
            prepareResult.setSuccess(true);
            prepareResult.setQrCode("11");

            return prepareResult;
        } catch (Exception e) {
            log.error(channel.getName() + "获取二维码异常", e);
            return retPrepareFailed("获取二维码失败，请重试！");
        }
    }

    /**
     * 京东
     */
    private static PrepareResult prepareJDPay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                    PaymentChannel channel, HttpServletRequest request) {

        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_JDPAY);
    }

    private static PrepareResult prepareCommon(String billno, double amount, String bankco, String notifyUrl, String resultUrl, PaymentChannel channel, String pChannelID) {
        String money = MoneyFormat.moneyToYuanForPositive(amount+"");
        Map<String, String> params = new TreeMap<>();

        String subject = RandomStringUtils.random(8, true, true);
        String price = RandomUtils.nextInt(1, 200) + "";
        String quantity = RandomUtils.nextInt(1, 5) + "";

        params.put("payKey", channel.getMerCode());
        params.put("outTradeNo", billno);
        Date d = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
        params.put("orderTime", sdf.format(d));
        params.put("productType", "50000103");
        params.put("productName", "qmt");
        params.put("notifyUrl", notifyUrl);
        params.put("returnUrl", resultUrl);
        params.put("orderPrice", amount+""); 
        params.put("bankCode", bankco); 


        String formURL = channel.getPayUrl() + "/Pay/KDBank.aspx";
        return retPrepareWangYing(formURL, params);
    }

    /**
     * 验证回调
     */
    public static VerifyResult verify(PaymentChannel channel, Map<String, String> resMap){
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

    private static boolean localVerify(PaymentChannel channel, Map<String, String> resMap) {
        try {
        	/* {sign=EDE36854978D6C36D4C3E6B2FE69629C, 
               	trxNo=77772018032810283877, tradeStatus=SUCCESS,
        			field2=1=88882017121910001372, payKey=0b9dbcd0327a431d9e8da3941525ce50, 
        			successTime=20180328134018, outTradeNo=180328133758u8pGjIdt, orderPrice=3.00, 
        			orderTime=20180328133810, reqBody=, 
        			productName=qmt, 
        			{sign=EDE36854978D6C36D4C3E6B2FE69629C, 
        			trxNo=77772018032810283877, tradeStatus=SUCCESS,
        			 field2=1=88882017121910001372, payKey=0b9dbcd0327a431d9e8da3941525ce50, 
        			 successTime=20180328134018, 
        			 outTradeNo=180328133758u8pGjIdt, 
        			 orderPrice=3.00, orderTime=20180328133810, 
        			 reqBody=, productName=qmt, 
        			 productType=50000103}
        			productType=50000103}*/
            Map<String, String> params = new TreeMap<>();
            params.put("payKey", resMap.get("payKey"));
            params.put("orderPrice", resMap.get("orderPrice"));
            params.put("outTradeNo", resMap.get("outTradeNo"));
            params.put("productType", resMap.get("productType"));
            params.put("orderTime", resMap.get("orderTime"));
            params.put("tradeStatus", resMap.get("tradeStatus"));
            params.put("successTime", resMap.get("successTime"));
            params.put("trxNo", resMap.get("trxNo"));
            String sign = resMap.get("sign");
            String tradeStatus = resMap.get("tradeStatus");
            resMap.remove("sign");
            if ("SUCCESS".equals(tradeStatus)) {
                String signStr = UrlParamUtils.toUrlParamWithoutEmpty(resMap, "&",true) +"&paySecret=" +channel.getMd5Key();
                String serverSign =  DigestUtils.md5Hex(signStr).toUpperCase();
              
                if (serverSign.equalsIgnoreCase(sign)){
                    return true;
                }
                else {
                    log.error(channel.getName() + "回调本地验证失败，参数签名与服务器不一致，服务器验证：" + serverSign + ",回调：" + JSON.toJSONString(resMap));
                    return false;
                }
            }
            else {
                log.warn(channel.getName() + "回调本地验证失败，参数状态表示不成功，回调：" + JSON.toJSONString(resMap));
                return false;
            }
        } catch (Exception e) {
            log.error(channel.getName() + "回调本地验证发生异常", e);
        }
        return false;
    }

    private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap){
        Map<String, String> params = new HashMap<>();
        params.put("payKey", channel.getMerCode());
        params.put("outTradeNo", resMap.get("outTradeNo"));
        String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&",true) + "&paySecret="+channel.getMd5Key();
        String sign  =  DigestUtils.md5Hex(signStr).toUpperCase();
        params.put("sign", sign); // 签名
        try {
            String url ="http://gateway.gzwwjy.com/query/singleOrder";

            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(resMap));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            YRQueryResult queryResult = transferResult(retStr);
            if (queryResult == null) {
                log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(resMap));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            if (!"SUCCESS".equals(queryResult.getOrderStatus())) {
                log.error(channel.getName() + "自动上分远程验证失败，返回状态表示执行不成功：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

/*            if (!"1".equals(queryResult.getP_status())) {
                log.error(channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }*/

            VerifyResult verifyResult = new VerifyResult();
            verifyResult.setSelfBillno(queryResult.getOutTradeNo());
            verifyResult.setChannelBillno(queryResult.getOutTradeNo());
            double money = Double.parseDouble(queryResult.getOrderPrice());
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

    private static YRQueryResult transferResult(String retStr) {
        JSONObject jsonObject=JSONObject.fromObject(retStr);
        YRQueryResult queryResult=(YRQueryResult)JSONObject.toBean(jsonObject, YRQueryResult.class);
        return queryResult;
    }
}
