// package lottery.domains.pool.payment.lepay;
//
// import com.alibaba.fastjson.JSON;
// import javautils.date.Moment;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.lepay.response.FCSOpenApiResponse;
// import lottery.domains.pool.payment.lepay.utils.*;
// import net.sf.json.JSONObject;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.math.BigDecimal;
// import java.net.URLEncoder;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.LinkedHashMap;
// import java.util.Map;
//
// /**
//  * Created by Nick on 2017/2/13.
//  */
// public class LepayPayment {
//     private static final Logger log = LoggerFactory.getLogger(LepayPayment.class);
//
//     public static void prepareAlipay(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip){
//         try {
//             Map<String, String> paramsMap = new LinkedHashMap<>();
//             paramsMap.put("amount_str", Amount);
//             paramsMap.put("out_trade_no", billno);
//             paramsMap.put("partner", thridBean.getMerCode());
//             paramsMap.put("remark", "cz");
//             paramsMap.put("service", "ali_pay");
//             paramsMap.put("sub_body", "cz");
//             paramsMap.put("subject", "cz");
//             paramsMap.put("ali_pay_type", "ali_sm");
//             paramsMap.put("return_url", notifyUrl);
//             String paramsString = WebUtil.getURL(paramsMap);
//             //以上为加密字符串
//
//             String sign = URLEncoder.encode(RSACoderUtil.sign(paramsString.getBytes("UTF-8"), PayConfig.LEPAY.PRIVATE_KEY), "UTF-8");
//             Map<String, String> contents = new HashMap<>();
//             contents.put("partner", thridBean.getMerCode());
//             contents.put("content", RSACoderUtil.getParamsWithDecodeByPublicKey(paramsString, "UTF-8", PayConfig.LEPAY.FCS_PUBLIC_KEY));
//             contents.put("input_charset", "UTF-8");
//             contents.put("sign_type", "SHA1WithRSA");
//             contents.put("sign", sign);
//             String strResult = WebUtil.doPost(PayConfig.LEPAY.GATEWAY, contents, "UTF-8", 3000, 15000);
//             FCSOpenApiResponse openApiResponse = JsonUtil.parseObject(strResult, FCSOpenApiResponse.class);
//
//             if (openApiResponse.getIs_succ().equals("T")) {
//                 String responseCharset = openApiResponse.getCharset();
//                 byte[] byte64 = CoderUtil.decryptBASE64(openApiResponse.getResponse());
//                 String responseResult = new String(RSACoderUtil.decryptByPrivateKey(byte64, PayConfig.LEPAY.PRIVATE_KEY), responseCharset);
//                 if (StringUtils.isNotEmpty(responseResult)) {
//                     JSONObject jsonObject = JSONObject.fromObject(responseResult);
//                     String base64QRCode = jsonObject.getString("base64QRCode");
//                     if (StringUtils.isNotEmpty(base64QRCode)) {
//                         result.setReturnValue(base64QRCode);
//                         result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//                     }
//                 }
//             }
//             else {
//                 log.error("乐付支付宝封装异常:" + openApiResponse.getFault_code() + "," + openApiResponse.getFault_reason());
//                 if ("E1010001".equals(openApiResponse.getFault_code())) {
//                     // E1010001,商户单号:20170426614146352484系统已存在
//                     result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 }
//                 else {
//                     result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 }
//             }
//         } catch (Exception e) {
//             log.error("Lepay支付宝封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static void prepareWechat(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip){
//         try {
//             Map<String, String> paramsMap = new LinkedHashMap<>();
//             paramsMap.put("amount_str", Amount);
//             paramsMap.put("out_trade_no", billno);
//             paramsMap.put("partner", thridBean.getMerCode());
//             paramsMap.put("remark", "cz");
//             paramsMap.put("service", "wx_pay");
//             paramsMap.put("sub_body", "cz");
//             paramsMap.put("subject", "cz");
//             paramsMap.put("wx_pay_type", "wx_sm");
//             paramsMap.put("return_url", notifyUrl);
//             String paramsString = WebUtil.getURL(paramsMap);
//             //以上为加密字符串
//
//             String sign = URLEncoder.encode(RSACoderUtil.sign(paramsString.getBytes("UTF-8"), PayConfig.LEPAY.PRIVATE_KEY), "UTF-8");
//             Map<String, String> contents = new HashMap<>();
//             contents.put("partner", thridBean.getMerCode());
//             contents.put("content", RSACoderUtil.getParamsWithDecodeByPublicKey(paramsString, "UTF-8", PayConfig.LEPAY.FCS_PUBLIC_KEY));
//             contents.put("input_charset", "UTF-8");
//             contents.put("sign_type", "SHA1WithRSA");
//             contents.put("sign", sign);
//             String strResult = WebUtil.doPost(PayConfig.LEPAY.GATEWAY, contents, "UTF-8", 3000, 15000);
//             FCSOpenApiResponse openApiResponse = JsonUtil.parseObject(strResult, FCSOpenApiResponse.class);
//
//             if (openApiResponse.getIs_succ().equals("T")) {
//                 String responseCharset = openApiResponse.getCharset();
//                 byte[] byte64 = CoderUtil.decryptBASE64(openApiResponse.getResponse());
//                 String responseResult = new String(RSACoderUtil.decryptByPrivateKey(byte64, PayConfig.LEPAY.PRIVATE_KEY), responseCharset);
//                 if (StringUtils.isNotEmpty(responseResult)) {
//                     JSONObject jsonObject = JSONObject.fromObject(responseResult);
//                     String base64QRCode = jsonObject.getString("base64QRCode");
//                     if (StringUtils.isNotEmpty(base64QRCode)) {
//                         result.setReturnValue(base64QRCode);
//                         result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//                     }
//                 }
//             }
//             else {
//                 log.error("乐付微信封装异常:" + openApiResponse.getFault_code() + "," + openApiResponse.getFault_reason());
//                 if ("E1010001".equals(openApiResponse.getFault_code())) {
//                     // E1010001,商户单号:20170426614146352484系统已存在
//                     result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 }
//                 else {
//                     result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 }
//             }
//         } catch (Exception e) {
//             log.error("Lepay微信封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static void prepare(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip){
//         try {
//             String requestTime = new Moment().format("yyMMddHHmmss");
//             Map<String, String> md5Map = new LinkedHashMap<>();
//             md5Map.put("partner", thridBean.getMerCode());
//             md5Map.put("service", "gateway_pay");
//             md5Map.put("out_trade_no", billno);
//             md5Map.put("amount_str", Amount);
//             md5Map.put("tran_ip", ip);
//             md5Map.put("buyer_name", "cz");
//             md5Map.put("buyer_contact", "cz");
//             md5Map.put("good_name", "cz");
//             md5Map.put("request_time", requestTime);
//             md5Map.put("return_url", notifyUrl);
//             md5Map.put("verfication_code", thridBean.getMerKey());
//
//             HashMap<String, String> paramsMap = new HashMap<>();
//
//             paramsMap.put("service", "gateway_pay");
//             paramsMap.put("partner", thridBean.getMerCode());
//             paramsMap.put("input_charset", "UTF-8");
//             paramsMap.put("sign_type", "MD5");
//             String md5ParamsString = WebUtil.getURL(md5Map);
//             paramsMap.put("sign", DigestUtils.md5Hex(md5ParamsString));
//             paramsMap.put("request_time", requestTime);
//             paramsMap.put("return_url", notifyUrl);
//             paramsMap.put("out_trade_no", billno);
//             paramsMap.put("amount_str", Amount);
//             paramsMap.put("tran_time", requestTime);
//             paramsMap.put("tran_ip", ip);
//             paramsMap.put("buyer_name", "cz");
//             paramsMap.put("buyer_contact", "cz");
//             paramsMap.put("good_name", "cz");
//             paramsMap.put("goods_detail", "cz");
//             paramsMap.put("bank_code", bankco);
//             paramsMap.put("receiver_address", "cz");
//             paramsMap.put("redirect_url", resultUrl);
//             result.setPayUrl(thridBean.getLink());
//             result.setParamsMap(paramsMap);
//         } catch (Exception e) {
//             log.error("Lepay微信封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static void prepareSpeed(RechargeResult result, String billno,
//                                     String Amount, PaymentThrid thridBean, String notifyUrl, String resultUrl, String ip){
//         try {
//             String requestTime = new Moment().format("yyMMddHHmmss");
//             Map<String, String> md5Map = new LinkedHashMap<>();
//             md5Map.put("partner", thridBean.getMerCode());
//             md5Map.put("service", "speedy_pay");
//             md5Map.put("out_trade_no", billno);
//             md5Map.put("amount_str", Amount);
//             md5Map.put("tran_ip", ip);
//             md5Map.put("buyer_name", "cz");
//             md5Map.put("buyer_contact", "cz");
//             md5Map.put("good_name", "cz");
//             md5Map.put("request_time", requestTime);
//             md5Map.put("return_url", notifyUrl);
//             md5Map.put("verfication_code", thridBean.getMerKey());
//
//             HashMap<String, String> paramsMap = new HashMap<>();
//
//             paramsMap.put("service", "speedy_pay");
//             paramsMap.put("partner", thridBean.getMerCode());
//             paramsMap.put("input_charset", "UTF-8");
//             paramsMap.put("sign_type", "MD5");
//             String md5ParamsString = WebUtil.getURL(md5Map);
//             paramsMap.put("sign", DigestUtils.md5Hex(md5ParamsString));
//             paramsMap.put("request_time", requestTime);
//             paramsMap.put("return_url", notifyUrl);
//             paramsMap.put("out_trade_no", billno);
//             paramsMap.put("amount_str", Amount);
//             paramsMap.put("tran_time", requestTime);
//             paramsMap.put("tran_ip", ip);
//             paramsMap.put("buyer_name", "cz");
//             paramsMap.put("buyer_contact", "cz");
//             paramsMap.put("good_name", "cz");
//             paramsMap.put("goods_detail", "cz");
//             paramsMap.put("receiver_address", "cz");
//             paramsMap.put("advice_url", resultUrl);
//             result.setPayUrl(thridBean.getLink());
//             result.setParamsMap(paramsMap);
//         } catch (Exception e) {
//             log.error("Lepay微信封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     /**
//      * 支付成功通知
//      * @param thridBean
//      * @param resMap
//      */
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String content = resMap.get("content"); //第三方传过来的加密rsa内容
//             String sign = resMap.get("sign"); //第三方传过来的加密内容
//             String sign_type = resMap.get("sign_type"); //加密类型
//             String input_charset = resMap.get("input_charset");
//             String request_time = resMap.get("request_time");
//             String out_trade_no = resMap.get("out_trade_no");//商户平台订单号
//             String status = resMap.get("status"); //状态 1为成功  2为失败
//
//             String paramsStr = RSACoderUtil.rsaDecrypt(content, PayConfig.LEPAY.PRIVATE_KEY); //获取加密的明文 解出来
//
//             Map<String, Object> paramsMap = JSON.parseObject(paramsStr, HashMap.class);
//
//             boolean flag = RSACoderUtil.verifySign2(paramsStr, sign, PayConfig.LEPAY.FCS_PUBLIC_KEY); //让解出来的明文跟第三方传过来的值进行比较 如果为true 说明成功
//             if (flag) {
//                 if (StringUtils.equals(status, "1")) {
//                     RechargePay pay = new RechargePay();
//                     pay.setRecvCardNo(thridBean.getMerCode()); // 商户号
//                     // pay.setAmount(Double.parseDouble(transactionFee)/100);//金额
//                     pay.setBillno(out_trade_no);//订单编号
//                     pay.setTradeNo(paramsMap.get("trade_id").toString());//支付订单
//                     BigDecimal amountStr = (BigDecimal) paramsMap.get("amount_str");
//                     pay.setAmount(amountStr.doubleValue());
//                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                     pay.setPayTime(sdf.parse(sdf.format(new Date())));//订单通知时间
//                     pay.setTradeStatus(status);//支付状态(1为成功  2为失败)
//                     return pay;
//                 }
//             }
//         } catch (Exception e) {
//             log.error("解析乐付回调时出错", e);
//         }
//         return null;
//     }
//
//     /**
//      * 支付成功通知
//      * @param thridBean
//      * @param resMap
//      */
//     public static RechargePay notifySpeed(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String content = resMap.get("content"); //第三方传过来的加密rsa内容
//             String sign = resMap.get("sign"); //第三方传过来的加密内容
//             String sign_type = resMap.get("sign_type"); //加密类型
//             String input_charset = resMap.get("input_charset");
//             String request_time = resMap.get("request_time");
//             String out_trade_no = resMap.get("out_trade_no");//商户平台订单号
//             String status = resMap.get("status"); //状态 1为成功  2为失败
//
//             String paramsStr = RSACoderUtil.rsaDecrypt(content, PayConfig.LEPAY.SPEED_PAY_PRIVATE_KEY); //获取加密的明文 解出来
//
//             Map<String, Object> paramsMap = JSON.parseObject(paramsStr, HashMap.class);
//
//             boolean flag = RSACoderUtil.verifySign2(paramsStr, sign, PayConfig.LEPAY.FCS_PUBLIC_KEY); //让解出来的明文跟第三方传过来的值进行比较 如果为true 说明成功
//             if (flag) {
//                 if (StringUtils.equals(status, "1")) {
//                     RechargePay pay = new RechargePay();
//                     pay.setRecvCardNo(thridBean.getMerCode()); // 商户号
//                     // pay.setAmount(Double.parseDouble(transactionFee)/100);//金额
//                     pay.setBillno(out_trade_no);//订单编号
//                     pay.setTradeNo(out_trade_no);//支付订单
//                     BigDecimal amountStr = (BigDecimal) paramsMap.get("amount_str");
//                     pay.setAmount(amountStr.doubleValue());
//                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                     pay.setPayTime(sdf.parse(sdf.format(new Date())));//订单通知时间
//                     pay.setTradeStatus(status);//支付状态(1为成功  2为失败)
//                     return pay;
//                 }
//             }
//         } catch (Exception e) {
//             log.error("解析乐付快捷支付回调时出错", e);
//         }
//         return null;
//     }
// }
