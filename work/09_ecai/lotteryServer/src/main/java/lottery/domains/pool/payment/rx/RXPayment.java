// package lottery.domains.pool.payment.rx;
//
// import com.alibaba.fastjson.JSON;
// import com.alibaba.fastjson.JSONObject;
//
// import javautils.http.HttpClientUtil;
// import javautils.image.ImageUtil;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.rx.util.Base64;
// import lottery.domains.pool.payment.rx.util.Base64Utils;
// import lottery.domains.pool.payment.rx.util.RSAEncrypt;
//
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.net.URLDecoder;
// import java.net.URLEncoder;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * 荣讯支付
//  * @author cavan
//  *
//  */
// public class RXPayment {
//     private static final Logger log = LoggerFactory.getLogger(RXPayment.class);
//
//     public static void prepare(RechargeResult result, String payType, String billno,
//                                String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                String ip) throws Exception {
//         long fenMoney = MoneyFormat.yuanToFenMoney(Amount);
//         String type="kozl";
//         String data = "{\"account\":\"" + thridBean.getMerCode() + "\"," +
//                 "\"amount\":\"" + fenMoney + "\"," + "\"banktype\":\"" + bankco + "\"," + "\"callback_url\":\"" + URLEncoder.encode(resultUrl, "UTF-8") + "\"," +
//                 "\"notify_url\":\"" + URLEncoder.encode(notifyUrl, "UTF-8") + "\"," + "\"orderId\":\"" + billno + "\"," +
//                 "\"type\":\"" + type + "\"" + "}";
//
//         //公钥加密过程
//         byte[] cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PLATFORM_PUBLIC_KEY),data.getBytes());
//         String cipher=Base64Utils.encode(cipherData);
//         //私钥加密过程
//         cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY),data.getBytes());
//         String signature=Base64Utils.encode(cipherData);
//
//         HashMap<String, String> params = new HashMap<>();
//         params.put("data", cipher); // 商户号、交易金额、同步跳转地址、银行编码、 异步通知地址、交易订单号、  请求类型等参数组成json串，   使用通道公钥生成公钥加密串  后Base64转码
//         params.put("signature", signature); //  商户号、交易金额、同步跳转地址、异步通知地址、交易订单号、   请求类型、组成json串，   使用商户私钥生成签名数据  后Base64转码签名算法MD5withRSA
//         result.setPayUrl(PayConfig.RX.ZL_URL);
//         result.setParamsMap(params);
//     }
//
//     public static void prepareAlipay(RechargeResult result,String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip) throws Exception {
//         long fenMoney = MoneyFormat.yuanToFenMoney(Amount);
//         String type="koali";
//         String data = "{\"account\":\"" + thridBean.getMerCode() + "\"," +
//                 "\"amount\":\"" + fenMoney + "\"," +
//                 "\"notify_url\":\"" + URLEncoder.encode(notifyUrl, "UTF-8") + "\"," + "\"orderId\":\"" + billno + "\"," +
//                 "\"type\":\"" + type + "\"" + "}";
//         //公钥加密过程
//         byte[] cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PLATFORM_PUBLIC_KEY),data.getBytes());
//         String cipher= Base64Utils.encode(cipherData);
//         //私钥加密过程
//         byte[] privateCipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY),data.getBytes());
//         String signature=Base64Utils.encode(privateCipherData);
//
//         JSONObject params = new JSONObject();
//         params.put("data", cipher); // 商户号、交易金额、异步通知地址、交易订单号、 请求类型等参数组成json串，使用通道公钥生成公钥加密串 后Base64转码
//         params.put("signature", signature); //  商户号、交易金额、同步跳转地址、异步通知地址、交易订单号、   请求类型、组成json串，   使用商户私钥生成签名数据  后Base64转码签名算法MD5withRSA
//         try {
//         	String json = HttpClientUtil.postAsStream(PayConfig.RX.SCAN_GATEWAY, JSON.toJSONString(params), null, 10000);
//         	if (StringUtils.isEmpty(json)) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时返回空,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//
//         	JSONObject jObject=JSONObject.parseObject(json);
//         	String resultData=jObject.getString("data");
//         	String resultSignature= jObject.getString("signature");
//         	if ((resultSignature == null) || (resultSignature.trim().isEmpty())) {
//                 log.error("荣讯扫码支付"+bankco+"封装数据时signature返回空不进行解析,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//               }
//             if ((resultData == null) || (resultData.trim().isEmpty())) {
//             	log.error("荣讯扫码支付"+bankco+"封装数据时data返回空不进行解析,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//               }
//         	byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64Utils.decode(resultData));
//         	String restr=new String(res);
//
//         	RXResult zsResult = JSON.parseObject(restr, RXResult.class);
//         	if (zsResult == null || restr == null) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时解析空：" + restr + ",我方订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//         	if (StringUtils.isEmpty(zsResult.getUrl())) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时返回二维码为空：" + restr + ",我方订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//
//         	result.setReturnValue(ImageUtil.encodeQR(URLDecoder.decode( zsResult.getUrl(),"UTF-8"), 200, 200));
//         	result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//         } catch (Exception e) {
//         	log.error("荣讯扫码支付"+bankco+"封装数据异常" + ",我方订单号：" + billno, e);
//         	result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
// //
// //
//     public static void prepareWeChat(RechargeResult result,String payType, String billno,
//                                  String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                  String ip) throws Exception {
//         long fenMoney = MoneyFormat.yuanToFenMoney(Amount);
//         String type="kowx";
//         String data = "{\"account\":\"" + thridBean.getMerCode() + "\"," +
//                 "\"amount\":\"" + fenMoney + "\"," +
//                 "\"notify_url\":\"" + URLEncoder.encode(notifyUrl, "UTF-8") + "\"," + "\"orderId\":\"" + billno + "\"," +
//                 "\"type\":\"" + type + "\"" + "}";
//         //公钥加密过程
//         byte[] cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PLATFORM_PUBLIC_KEY),data.getBytes());
//         String cipher= Base64Utils.encode(cipherData);
//         //私钥加密过程
//         byte[] privateCipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY),data.getBytes());
//         String signature=Base64Utils.encode(privateCipherData);
//
//         JSONObject params = new JSONObject();
//         params.put("data", cipher); // 商户号、交易金额、异步通知地址、交易订单号、 请求类型等参数组成json串，使用通道公钥生成公钥加密串 后Base64转码
//         params.put("signature", signature); //  商户号、交易金额、同步跳转地址、异步通知地址、交易订单号、   请求类型、组成json串，   使用商户私钥生成签名数据  后Base64转码签名算法MD5withRSA
//         try {
//         	String json = HttpClientUtil.postAsStream(PayConfig.RX.SCAN_GATEWAY, JSON.toJSONString(params), null, 10000);
//         	if (StringUtils.isEmpty(json)) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时返回空,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//
//         	JSONObject jObject=JSONObject.parseObject(json);
//         	String resultData=jObject.getString("data");
//         	String resultSignature= jObject.getString("signature");
//         	if ((resultSignature == null) || (resultSignature.trim().isEmpty())) {
//                 log.error("荣讯扫码支付"+bankco+"封装数据时signature返回空不进行解析,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//               }
//             if ((resultData == null) || (resultData.trim().isEmpty())) {
//             	log.error("荣讯扫码支付"+bankco+"封装数据时data返回空不进行解析,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//               }
//         	byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64Utils.decode(resultData));
//         	String restr=new String(res);
//
//         	RXResult zsResult = JSON.parseObject(restr, RXResult.class);
//         	if (zsResult == null || restr == null) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时解析空：" + restr + ",我方订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//         	if (StringUtils.isEmpty(zsResult.getUrl())) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时返回二维码为空：" + restr + ",我方订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//
//         	result.setReturnValue(ImageUtil.encodeQR(URLDecoder.decode( zsResult.getUrl(),"UTF-8"), 200, 200));
//         	result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//         } catch (Exception e) {
//         	log.error("荣讯扫码支付"+bankco+"封装数据异常" + ",我方订单号：" + billno, e);
//         	result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
// //
//     public static void prepareQQ(RechargeResult result,String payType, String billno,
//                                  String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                  String ip) throws Exception {
//         long fenMoney = MoneyFormat.yuanToFenMoney(Amount);
//         String type="koqq";
//         String data = "{\"account\":\"" + thridBean.getMerCode() + "\"," +
//                 "\"amount\":\"" + fenMoney + "\"," +
//                 "\"notify_url\":\"" + URLEncoder.encode(notifyUrl, "UTF-8") + "\"," + "\"orderId\":\"" + billno + "\"," +
//                 "\"type\":\"" + type + "\"" + "}";
//         //公钥加密过程
//         byte[] cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PLATFORM_PUBLIC_KEY),data.getBytes());
//         String cipher= Base64Utils.encode(cipherData);
//         //私钥加密过程
//         byte[] privateCipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY),data.getBytes());
//         String signature=Base64Utils.encode(privateCipherData);
//
//         JSONObject params = new JSONObject();
//         params.put("data", cipher); // 商户号、交易金额、异步通知地址、交易订单号、 请求类型等参数组成json串，使用通道公钥生成公钥加密串 后Base64转码
//         params.put("signature", signature); //  商户号、交易金额、同步跳转地址、异步通知地址、交易订单号、   请求类型、组成json串，   使用商户私钥生成签名数据  后Base64转码签名算法MD5withRSA
//         try {
//         	String json = HttpClientUtil.postAsStream(PayConfig.RX.SCAN_GATEWAY, JSON.toJSONString(params), null, 10000);
//         	if (StringUtils.isEmpty(json)) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时返回空,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//
//         	JSONObject jObject=JSONObject.parseObject(json);
//         	String resultData=jObject.getString("data");
//         	String resultSignature= jObject.getString("signature");
//         	if ((resultSignature == null) || (resultSignature.trim().isEmpty())) {
//                 log.error("荣讯扫码支付"+bankco+"封装数据时signature返回空不进行解析,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//               }
//             if ((resultData == null) || (resultData.trim().isEmpty())) {
//             	log.error("荣讯扫码支付"+bankco+"封装数据时data返回空不进行解析,订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//               }
//         	byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64Utils.decode(resultData));
//         	String restr=new String(res);
//
//         	RXResult zsResult = JSON.parseObject(restr, RXResult.class);
//         	if (zsResult == null || restr == null) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时解析空：" + restr + ",我方订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//         	if (StringUtils.isEmpty(zsResult.getUrl())) {
//         		log.error("荣讯扫码支付"+bankco+"封装数据时返回二维码为空：" + restr + ",我方订单号：" + billno);
//         		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         		return;
//         	}
//
//         	result.setReturnValue(ImageUtil.encodeQR(URLDecoder.decode( zsResult.getUrl(),"UTF-8"), 200, 200));
//         	result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//         } catch (Exception e) {
//         	log.error("荣讯扫码支付"+bankco+"封装数据异常" + ",我方订单号：" + billno, e);
//         	result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//     /**
//      * 支付成功通知
//      * @param thridBean
//      * @param resMap
//      */
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String data = resMap.get("data"); // 加密参数
//             String signature = resMap.get("signature"); // 签名数据
//
//             if ((signature == null) || (signature.trim().isEmpty())) {
//             	log.error("荣讯回调未获取到signature，不进行解密以及验签,请求信息：" + JSON.toJSONString(resMap));
//                 return null;
//               }
//             if ((data == null) || (data.trim().isEmpty())) {
//             	log.error("荣讯回调未获取到data，不进行解密以及验签,请求信息：" + JSON.toJSONString(resMap));
//                 return null;
//               }
//             //私钥解密过程
// //            byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64Utils.decode(data));
// //            String restr=new String(res);
//
//             String restr=Base64.decodeToString(data);
//
//             RXResult zsResult = JSON.parseObject(restr, RXResult.class);
//             Boolean bool=RSAEncrypt.publicsign(restr,  Base64.decode(signature), RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PLATFORM_PUBLIC_KEY));
//             if (bool){
//                     RechargePay pay = new RechargePay();
//                     pay.setAmount((Double.parseDouble(zsResult.getAmount())/100));//金额
//                     pay.setBillno(zsResult.getOrderId());//订单编号
//                     pay.setTradeNo(zsResult.getOrderId());//支付订单
//                     pay.setPayTime(new Date());//订单通知时间
//                     pay.setTradeStatus(zsResult.getRespCode());
//                     pay.setSuccess(true);
//                     return pay;
//             }else {
//                 log.error("荣讯回调验证失败，服务器验证：" + restr + ",请求信息：" + JSON.toJSONString(resMap));
//                 return null;
//             }
//         } catch (Exception e) {
//             log.error("荣讯回调发生异常,请求信息：" + JSON.toJSONString(resMap), e);
//         }
//         return null;
//     }
// }
