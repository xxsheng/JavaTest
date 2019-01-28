package lottery.domains.pool.payment.cf;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import javautils.http.HttpClientUtil;
import javautils.http.UrlParamUtils;
import javautils.image.ImageUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.cfg.MoneyFormat;

/**
 * 创富支付
 * Created by Nick on 2017-11-21.
 */
public class CFPayment extends AbstractPayment {
    private static final Logger log = LoggerFactory.getLogger(CFPayment.class);
    private static final String CHARSET = "UTF-8";
    private static final String ISAPP_WEB = "web";
    private static final String ISAPP_APP = "app";
    private static final String PAYMENTTYPE = "1";
    private static final String PAYMETHOD_DIRECTPAY = "directPay";
    private static final String PAYMETHOD_BANKPAY = "bankPay";
    private static final String SERVICE_ONLINE_PAY = "online_pay";
    private static final String SIGNTYPE_SHA = "SHA";
    private static final String SELLER_EMAIL = "bebraveman@163.com";

    private static final String OUTPUT_SUCCESS = "OK";
    private static final String OUTPUT_FAILED = "failed";

    public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                                PaymentChannel channel, HttpServletRequest request) {
        try {
            switch(channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_CF:
                    return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_CFWECHAT:
                    return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_CFALIPAY:
                    return prepareAlipay(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_CFQQ:
                    return prepareQQ(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_CFJDPAY:
                    return prepareJDPay(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
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
        Map<String, String> params = new TreeMap<>();
        params.put("pay_memberid", channel.getMerCode());
        params.put("pay_orderid", billno);
        Date d = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
        params.put("pay_applydate", sdf.format(d));
        params.put("pay_bankcode", "KUAIJIE");
        params.put("pay_notifyurl", notifyUrl);
        params.put("pay_callbackurl", resultUrl);
        params.put("pay_amount", amount+""); 
        String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&",true) + "&key="+channel.getMd5Key();
        String sign  =  DigestUtils.md5Hex(signStr).toUpperCase();
        params.put("sign", sign); // 签名
        params.put("pay_tongdao", "Kuaijie"); // 签名
        try {
            String url = channel.getPayUrl() ;
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取请求地址为空,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }

            CFScanResult scanResult = JSON.parseObject(retStr, CFScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取请求地址时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }

            if (!"100001".equals(scanResult.getSuccessno())) {
                log.error(channel.getName() + "获取请求地址返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取请求地址失败，请重试！" + scanResult.getRespCode() + "，" + scanResult.getRespMessage());
            }
            
            Map<String,String> data = scanResult.getData();
            if (data.isEmpty()) {
                log.error(channel.getName() + "获取请求地址返回空的,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }
             String pay_QR= data.get("pay_QR");
             String payurl = data.get("pay_url");
            if (StringUtils.isEmpty(pay_QR)) {
                log.error(channel.getName() + "获取请求地址返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }
            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setJumpType(PrepareResult.QUICK_TYPE_FORM);
            prepareResult.setSuccess(true);
            prepareResult.setQrCode(pay_QR);
            prepareResult.setFormUrl(payurl);
            return prepareResult;
        } catch (Exception e) {
            log.error(channel.getName() + "获取请求地址异常", e);
            return retPrepareFailed("获取请求地址失败，请重试！");
        }
    
     
    }

    /**
     * 微信
     */
    private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                     PaymentChannel channel, HttpServletRequest request) {
        return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "WXPAY");
    }



    /**
     * 支付宝
     */
    private static PrepareResult prepareAlipay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                              PaymentChannel channel, HttpServletRequest request) {
        return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "ALIPAY");
    }

    /**
     * QQ
     */
    private static PrepareResult prepareQQ(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                 PaymentChannel channel, HttpServletRequest request) {
        return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "QQPAY");
    }

    /**
     * 京东
     */
    private static PrepareResult prepareJDPay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                    PaymentChannel channel, HttpServletRequest request) {

        return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "JDPAY");
    }

    /**
     * 获取二维码
     */
    private static PrepareResult prepareWithQRCode(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                                   PaymentChannel channel, HttpServletRequest request, String defaultbank) {
        String money = MoneyFormat.moneyToYuanForPositive(amount+"");
        Map<String, String> params = new TreeMap<>();
        params.put("pay_memberid", channel.getMerCode());
        params.put("pay_orderid", billno);
        Date d = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
        params.put("pay_applydate", sdf.format(d));
        params.put("pay_bankcode", "QQ_NATIVE");
        params.put("pay_notifyurl", notifyUrl);
        params.put("pay_callbackurl", resultUrl);
        params.put("pay_amount", money); // 成功跳转URL

        String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&",true) + "&key="+channel.getMd5Key();
        String sign  =  DigestUtils.md5Hex(signStr).toUpperCase();
        params.put("sign", sign); // 签名
        params.put("pay_tongdao","QQwallet");
        try {
            String url = channel.getPayUrl() ;
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            CFScanResult scanResult = JSON.parseObject(retStr, CFScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            if (!"100001".equals(scanResult.getSuccessno())) {
                log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getRespCode() + "，" + scanResult.getRespMessage());
            }
            
            Map<String,String> data = scanResult.getData();
            if (data.isEmpty()) {
                log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }
             String pay_QR= data.get("pay_QR");
            if (StringUtils.isEmpty(pay_QR)) {
                log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            //String qrCode = ImageUtil.encodeQR(pay_QR, 200, 200);

            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setJumpType(PrepareResult.JUMP_TYPE_QR);
            prepareResult.setSuccess(true);
            prepareResult.setQrCode(pay_QR);

            return prepareResult;
        } catch (Exception e) {
            log.error(channel.getName() + "获取二维码异常", e);
            return retPrepareFailed("获取二维码失败，请重试！");
        }
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
        //服务器验证：19A1C1A096A919DF1019C16E485EEF31,回调：{"sign":"8579F0C6186384951ED87A2DF5572CB1","amount":"1.000","memberid":"10033","returncode":"00","orderid":"180203213058DxVTzkvr","reserved2":"","datetime":"20180203213239"}
        	//                                                                                                                                                      amount=1.000&datetime=20180203213937&memberid=10033&orderid=180203213058DxVTzkvr&returncode=00key=3krdv6ey37yii5nwftkgs5zduiidpt
            Map<String, String> params = new TreeMap<>();
            params.put("memberid", resMap.get("memberid"));
            params.put("orderid", resMap.get("orderid"));
            params.put("amount", resMap.get("amount"));
            params.put("datetime", resMap.get("datetime"));
            params.put("returncode", resMap.get("returncode"));
            String sign = resMap.get("sign");
            String tradeStatus = resMap.get("returncode");

            if ("00".equals(tradeStatus)) {
                String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&",true) +"&key=" +channel.getMd5Key();
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

    private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap) {
        String outTradeNo = resMap.get("orderid");
        
     //   params.put("charset", CHARSET); // 参数编码字符集
     //   params.put("merchantId", channel.getMerCode()); // 支付平台分配的商户ID
        //String signStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&") + channel.getMd5Key();

        //String sign = sign(signStr, CHARSET);
        //params.put("signType", SIGNTYPE_SHA); // 签名方式 ：SHA
       // params.put("sign", sign); // 签名

        try {
        /*    String paramsStr = UrlParamUtils.toUrlParamWithoutEmpty(params, "&");
            String url = channel.getPayUrl() + "payment/v1/order/" + channel.getMerCode() + "-" + outTradeNo + "?" + paramsStr;*/

            VerifyResult verifyResult = new VerifyResult();
            verifyResult.setSelfBillno(outTradeNo);
            verifyResult.setChannelBillno(outTradeNo);
            double money = Double.parseDouble(resMap.get("amount"));
            verifyResult.setRequestMoney(money);
            verifyResult.setReceiveMoney(money);
            verifyResult.setPayTime(new Date());
            verifyResult.setSuccess(true);
            verifyResult.setOutput(OUTPUT_SUCCESS);
            verifyResult.setSuccessOutput(OUTPUT_SUCCESS);
            return verifyResult;
        } catch (Exception e) {
            log.error(channel.getName() + "自动上分远程验证异常", e);
            return retVerifyFailed("远程验证失败，获取订单失败");
        }
    }

    private static final String SIGN_ALGORITHMS = "SHA-1";

    
    
    /**
     * SHA1 安全加密算法
     */
    private static String sign(String content,String inputCharset)  {
        //获取信息摘要 - 参数字典排序后字符串
        try {
            //指定sha1算法
            MessageDigest digest = MessageDigest.getInstance(SIGN_ALGORITHMS);
            digest.update(content.getBytes(inputCharset));
            //获取字节数组
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
