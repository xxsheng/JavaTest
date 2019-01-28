// package lottery.domains.pool.payment.hh;
//
// import com.alibaba.fastjson.JSON;
// import com.alibaba.fastjson.JSONObject;
//
// import javautils.date.Moment;
// import javautils.http.UrlParamUtils;
// import javautils.image.ImageUtil;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.hh.util.HttpClientUtil;
// import lottery.domains.pool.payment.hh.util.HuihepaySubmit;
//
// import org.apache.commons.codec.digest.DigestUtils;
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.io.UnsupportedEncodingException;
// import java.net.URLDecoder;
// import java.util.*;
//
// /**
//  * Created by Nick on 2017-08-21.
//  */
// public class HHPayment {
//     private static final String VERITY_MD5_KEY = "JEweSm,6]CN@awweedsfae,LSJAj$;aFSaade(15):+&";
//     private static final Logger log = LoggerFactory.getLogger(HHPayment.class);
//     private static final String PAY_TYPE_WY = "1";//网银
//     private static final String PAY_TYPE_WX = "2";//微信扫码
//     private static final String PAY_TYPE_QQ = "3";//qq钱包
//     private static final String PAY_TYPE_ALIPAY = "6";//支付宝
//
//     public static void prepare(RechargeResult result, String payType, String billno,
//                                     String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                     String ip) {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         String time = new Moment().toSimpleTime();
//         HashMap<String, String> params = new HashMap<>();
//         params.put("AppId", thridBean.getMerCode()); //系统分配给开发者的应用ID（等同于商户号）
//         params.put("Method", "trade.page.pay"); //接口名称
//         params.put("Format", "JSON"); //仅支持JSON
//         params.put("Charset", "UTF-8"); //请求使用的编码格式，仅支持UTF-8
//         params.put("Timestamp", "1.0"); //调用的接口版本，固定为：1.0
//         params.put("BankCode", bankco); //银行代码，当PayType=1时，此值必填
//         params.put("OutTradeNo", billno); //商户订单号，64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
//         params.put("TotalAmount", money); //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
//         params.put("Subject", "cz"); //订单标题
//         params.put("Body", "b2c"); //订单描述
//         params.put("NotifyUrl", notifyUrl); //服务器主动通知商户服务器里指定的页面，http/https路径。
//         params.put("PayType", PAY_TYPE_WY); // 支付类型
//         params.put("Timestamp", time); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//         String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         params.put("PassbackParams", cusSign); // 回传参数
//         Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
//         result.setPayUrl(PayConfig.HH.GATEWAY);
//         result.setParamsMap(new HashMap<String, String>(map));
//     }
//
//     public static void prepareQQ(RechargeResult result, String payType, String billno,
//                                     String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                     String ip) throws Exception {
//     	String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         String time = new Moment().toSimpleTime();
//         HashMap<String, String> params = new HashMap<>();
//         params.put("AppId", thridBean.getMerCode()); //系统分配给开发者的应用ID（等同于商户号）
//         params.put("Method", "trade.page.pay"); //接口名称
//         params.put("Format", "JSON"); //仅支持JSON
//         params.put("Charset", "UTF-8"); //请求使用的编码格式，仅支持UTF-8
//         params.put("Timestamp", "1.0"); //调用的接口版本，固定为：1.0
//         params.put("BankCode", ""); //银行代码，当PayType=1时，此值必填
//         params.put("OutTradeNo", billno); //商户订单号，64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
//         params.put("TotalAmount", money); //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
//         params.put("Subject", "cz"); //订单标题
//         params.put("Body", "b2c"); //订单描述
//         params.put("NotifyUrl", notifyUrl); //服务器主动通知商户服务器里指定的页面，http/https路径。
//         params.put("PayType", PAY_TYPE_QQ); // 支付类型
//         params.put("Timestamp", time); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//         String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         params.put("PassbackParams", cusSign); // 回传参数
//         Map<String,String>  map = HuihepaySubmit.buildRequestPara(params);
//         String jsonString =HttpClientUtil.post(PayConfig.HH.GATEWAY, map);
//         if (StringUtils.isEmpty(jsonString)) {
//             log.error("汇合QQ钱包封装数据时返回空,订单号：" + billno);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//             return;
//         }
//         JSONObject jsonObject=new JSONObject();
//         jsonObject=JSONObject.parseObject(jsonString);
//         if (!jsonObject.getString("Code").equals("0")) {
//         	log.error("汇合QQ钱包封装数据时返回数据Code不是0：" + JSON.toJSONString(jsonObject));
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//             return;
// 		}
//         log.info(jsonObject.toJSONString());
//         result.setReturnValue(ImageUtil.encodeQR(jsonObject.getString("QrCode"), 200, 200));
//         result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//     }
//
//     public static void prepareWeChat(RechargeResult result, String payType, String billno,
//             String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//             String ip) throws Exception {
// 		String money = MoneyFormat.moneyToYuanForPositive(Amount);
// 		String time = new Moment().toSimpleTime();
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("AppId", thridBean.getMerCode()); //系统分配给开发者的应用ID（等同于商户号）
// 		params.put("Method", "trade.page.pay"); //接口名称
// 		params.put("Format", "JSON"); //仅支持JSON
// 		params.put("Charset", "UTF-8"); //请求使用的编码格式，仅支持UTF-8
// 		params.put("Timestamp", "1.0"); //调用的接口版本，固定为：1.0
// 		params.put("BankCode", ""); //银行代码，当PayType=1时，此值必填
// 		params.put("OutTradeNo", billno); //商户订单号，64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
// 		params.put("TotalAmount", money); //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
// 		params.put("Subject", "cz"); //订单标题
// 		params.put("Body", "b2c"); //订单描述
// 		params.put("NotifyUrl", notifyUrl); //服务器主动通知商户服务器里指定的页面，http/https路径。
// 		params.put("PayType", PAY_TYPE_WX); // 支付类型
// 		params.put("Timestamp", time); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//         String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         params.put("PassbackParams", cusSign); // 回传参数
// 		Map<String,String>  map = HuihepaySubmit.buildRequestPara(params);
// 		String jsonString =HttpClientUtil.post(PayConfig.HH.GATEWAY, map);
// 		if (StringUtils.isEmpty(jsonString)) {
// 		log.error("汇合微信封装数据时返回空,订单号：" + billno);
// 		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		return;
// 		}
// 		JSONObject jsonObject=new JSONObject();
// 		jsonObject=JSONObject.parseObject(jsonString);
// 		if (!jsonObject.getString("Code").equals("0")) {
// 		log.error("汇合微信封装数据时返回数据Code不是0：" + JSON.toJSONString(jsonObject));
// 		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		return;
// 		}
// 		log.info(jsonObject.toJSONString());
// 		result.setReturnValue(ImageUtil.encodeQR(jsonObject.getString("QrCode"), 200, 200));
// 		result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// }
//
//     public static void prepareAlipay(RechargeResult result, String payType, String billno,
//             String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//             String ip) throws Exception {
// 		String money = MoneyFormat.moneyToYuanForPositive(Amount);
// 		String time = new Moment().toSimpleTime();
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("AppId", thridBean.getMerCode()); //系统分配给开发者的应用ID（等同于商户号）
// 		params.put("Method", "trade.page.pay"); //接口名称
// 		params.put("Format", "JSON"); //仅支持JSON
// 		params.put("Charset", "UTF-8"); //请求使用的编码格式，仅支持UTF-8
// 		params.put("Timestamp", "1.0"); //调用的接口版本，固定为：1.0
// 		params.put("BankCode", ""); //银行代码，当PayType=1时，此值必填
// 		params.put("OutTradeNo", billno); //商户订单号，64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
// 		params.put("TotalAmount", money); //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
// 		params.put("Subject", "cz"); //订单标题
// 		params.put("Body", "b2c"); //订单描述
// 		params.put("NotifyUrl", notifyUrl); //服务器主动通知商户服务器里指定的页面，http/https路径。
// 		params.put("PayType", PAY_TYPE_ALIPAY); // 支付类型
// 		params.put("Timestamp", time); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//         String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         params.put("PassbackParams", cusSign); // 回传参数
//
// 		Map<String,String>  map = HuihepaySubmit.buildRequestPara(params);
// 		String jsonString =HttpClientUtil.post(PayConfig.HH.GATEWAY, map);
// 		if (StringUtils.isEmpty(jsonString)) {
// 		log.error("汇合支付宝封装数据时返回空,订单号：" + billno);
// 		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		return;
// 		}
// 		JSONObject jsonObject=new JSONObject();
// 		jsonObject=JSONObject.parseObject(jsonString);
// 		if (!jsonObject.getString("Code").equals("0")) {
// 		log.error("汇合支付宝封装数据时返回数据Code不是0：" + JSON.toJSONString(jsonObject));
// 		result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		return;
// 		}
// 		log.info(jsonObject.toJSONString());
// 		result.setReturnValue(ImageUtil.encodeQR(jsonObject.getString("QrCode"), 200, 200));
// 		result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// }
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String Code = resMap.get("Code");//结果代码,返回0表示成功
//             String Message = resMap.get("Message");//代码描述，Code=0时，此值为空
//             String SignType = resMap.get("SignType");//商户生成签名字符串所使用的签名算法类型，目前仅支持MD5，后续将支持RSA2
//             String Sign = resMap.get("Sign");//商户请求参数的签名串，详见签名
//             String AppId = resMap.get("AppId");//系统分配给开发者的应用ID（等同于商户号）
//             String TradeNo = resMap.get("TradeNo");//该交易在我司系统中的交易号
//             String OutTradeNo = resMap.get("OutTradeNo");//商户订单号，原样返回
//             String TotalAmount = resMap.get("TotalAmount");//订单支付金额
//             String PassbackParams = resMap.get("PassbackParams");//扩展参数，支付成功后原样返回，如果未传此值，则不返回此参数
//
//             String cusSign = getCusSign(AppId, thridBean.getMerKey(), OutTradeNo);
//             if (cusSign.equalsIgnoreCase(PassbackParams)) {
//             	Map<String, String> map=HuihepaySubmit.buildRequestPara(resMap);
//                 if (Sign.equalsIgnoreCase(map.get("Sign"))){
//                     if (Code.equals("0")) {
//                         RechargePay pay = new RechargePay();
//                         pay.setAmount(Double.parseDouble(TotalAmount));//金额
//                         pay.setBillno(OutTradeNo);//订单编号
//                         pay.setTradeNo(TradeNo);//支付订单
//                         pay.setPayTime(new Date());//订单通知时间
//                         pay.setSuccess(true);
//                         return pay;
//                     }else {
//                         log.warn("汇合回调状态表示充值不成功，回调状态码：" + Code + ",请求信息：" + JSON.toJSONString(resMap));
//                     }
//                 }else {
//                     log.error("汇合回调验证失败，服务器验证：" + map.get("Sign") + ",请求信息：" + JSON.toJSONString(resMap));
//                     return null;
//                 }
//             } else {
//                 log.error("汇合回调自定义签名验证失败，服务器验证：" + cusSign + ",请求信息：" + JSON.toJSONString(resMap));
//                 return null;
//             }
//         } catch (Exception e) {
//             log.error("汇合回调发生异常", e);
//         }
//         return null;
//     }
//
//     private static String getCusSign(String merCode, String merKey, String billno) {
//         return DigestUtils.md5Hex(merCode + merKey + billno + VERITY_MD5_KEY).toUpperCase();
//     }
// }
