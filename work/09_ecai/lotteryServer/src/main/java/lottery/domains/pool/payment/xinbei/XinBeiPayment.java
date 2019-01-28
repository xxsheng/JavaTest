// package lottery.domains.pool.payment.xinbei;
//
// import com.alibaba.fastjson.JSON;
// import javautils.date.Moment;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.xunbao.XUNBAOPayment;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * Created by Nick on 2017-05-28.
//  */
// public class XinBeiPayment {
//     private static final String VERITY_MD5_KEY = "JESm,6]CN@,LSJAj$;e(15):+&";
//     private static final String VERSION = "V1.0";
//     private static final String PAYCODE_WECHAT = "100040";
//     private static final String PAYCODE_ALIPAY = "100067";
//     private static final String PAYCODE_QQPAY = "100068";
//
//     private static final Logger log = LoggerFactory.getLogger(XinBeiPayment.class);
//
//     public static void prepare(RechargeResult result, String payType, String billno,
//                                String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                String ip) {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         String orderDate = new Moment().format("yyyyMMddHHmmss");
//
//         HashMap<String, String> params = new HashMap<>();
//         params.put("Version", VERSION); // 使用网关的版本号
//         params.put("MerchantCode", thridBean.getMerCode()); // 新贝平台商户编码
//         params.put("OrderId", billno); // 商户自己业务逻辑的订单号
//         params.put("Amount", money); // 交易流程中发生的金额
//         params.put("AsyNotifyUrl", notifyUrl); // 业务完成时异步回发通知的地址
//         params.put("SynNotifyUrl", resultUrl); // 业务完成时间同步会发通知的地址
//         params.put("OrderDate", orderDate); // 商户产生订单时的交易时间,格式如：20130102030405
//         params.put("TradeIp", ip); // 发起交易的客户IP地址
//         params.put("PayCode", bankco); // 接入平台时对应的接口编码
//
//         String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.XINBEI.TOKEN_KEY, billno, orderDate, ip, money);
//         params.put("Remark2", cusSign); // 商户自定义备注字段
//
//         String md5Sign = "Version=[%s]MerchantCode=[%s]OrderId=[%s]Amount=[%s]AsyNotifyUrl=[%s]SynNotifyUrl=[%s]OrderDate=[%s]TradeIp=[%s]PayCode=[%s]TokenKey=[%s]";
//         md5Sign = String.format(md5Sign, VERSION, thridBean.getMerCode(), billno, money, notifyUrl, resultUrl, orderDate, ip, bankco, PayConfig.XINBEI.TOKEN_KEY);
//         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();
//         params.put("SignValue", md5Sign); // 根据接口文档组合参数加密后的字段，加密方式为MD5大写
//
//         result.setPayUrl(PayConfig.XINBEI.GATEWAY);
//         result.setParamsMap(params);
//     }
//
//     public static void main(String[] args) {
//         String merCode = "E05339";
//         String billno = "E00000";
//         String money = "1111.00";
//         String notifyUrl = "http://hsquick2.dianny.top/Rechargenotify/36";
//         String resultUrl = "http://hsquick2.dianny.top/Rechargeresult/36";
//         String orderDate = "20170811014455";
//         String ip = "119.92.198.131";
//         String bankco = "100013";
//         String tokenKey = PayConfig.XINBEI.TOKEN_KEY;
//         // String tokenKey = "4075d884260c01007ffba0fa5de24359";
//
//         String md5Sign = "Version=[V1.0]MerchantCode=[E00000]OrderId=[MR00000000]Amount=[10]AsyNotifyUrl=[http://www.xxx.com/AsynNotify]SynNotifyUrl=[http://www.xxx.com/SynNotifyUrl]OrderDate=[20131201112211]TradeIp=[127.0.0.1]PayCode=[100001]TokenKey=[1234567890]";
//        // md5Sign = String.format(md5Sign, VERSION, merCode, billno, money, notifyUrl, resultUrl, orderDate, ip, bankco, tokenKey);
//         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();
//     }
//
//     public static void prepareAlipay(RechargeResult result,String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip) {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         String orderDate = new Moment().format("yyyyMMddHHmmss");
//
//         HashMap<String, String> params = new HashMap<>();
//         params.put("Version", VERSION); // 使用网关的版本号
//         params.put("MerchantCode", thridBean.getMerCode()); // 新贝平台商户编码
//         params.put("OrderId", billno); // 商户自己业务逻辑的订单号
//         params.put("Amount", money); // 交易流程中发生的金额
//         params.put("AsyNotifyUrl", notifyUrl); // 业务完成时异步回发通知的地址
//         params.put("SynNotifyUrl", resultUrl); // 业务完成时间同步会发通知的地址
//         params.put("OrderDate", orderDate); // 商户产生订单时的交易时间,格式如：20130102030405
//         params.put("TradeIp", ip); // 发起交易的客户IP地址
//         params.put("PayCode", PAYCODE_ALIPAY); // 接入平台时对应的接口编码
//
//         String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.XINBEI.TOKEN_KEY_ALIPAY, billno, orderDate, ip, money);
//         params.put("Remark2", cusSign); // 商户自定义备注字段
//
//         String md5Sign = "Version=[%s]MerchantCode=[%s]OrderId=[%s]Amount=[%s]AsyNotifyUrl=[%s]SynNotifyUrl=[%s]OrderDate=[%s]TradeIp=[%s]PayCode=[%s]TokenKey=[%s]";
//         md5Sign = String.format(md5Sign, VERSION, thridBean.getMerCode(), billno, money, notifyUrl, resultUrl, orderDate, ip, PAYCODE_ALIPAY, PayConfig.XINBEI.TOKEN_KEY_ALIPAY);
//         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();
//
//         params.put("SignValue", md5Sign); // 根据接口文档组合参数加密后的字段，加密方式为MD5大写
//
//         result.setPayUrl(PayConfig.XINBEI.GATEWAY);
//         result.setParamsMap(params);
//     }
//
//     public static void prepareQQpay(RechargeResult result,String payType, String billno,
//             String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//             String ip) {
// 		String money = MoneyFormat.moneyToYuanForPositive(Amount);
// 		String orderDate = new Moment().format("yyyyMMddHHmmss");
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("Version", VERSION); // 使用网关的版本号
// 		params.put("MerchantCode", thridBean.getMerCode()); // 新贝平台商户编码
// 		params.put("OrderId", billno); // 商户自己业务逻辑的订单号
// 		params.put("Amount", money); // 交易流程中发生的金额
// 		params.put("AsyNotifyUrl", notifyUrl); // 业务完成时异步回发通知的地址
// 		params.put("SynNotifyUrl", resultUrl); // 业务完成时间同步会发通知的地址
// 		params.put("OrderDate", orderDate); // 商户产生订单时的交易时间,格式如：20130102030405
// 		params.put("TradeIp", ip); // 发起交易的客户IP地址
// 		params.put("PayCode", PAYCODE_QQPAY); // 接入平台时对应的接口编码
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.XINBEI.TOKEN_KEY_QQPAY, billno, orderDate, ip, money);
// 		params.put("Remark2", cusSign); // 商户自定义备注字段
// 		String md5Sign = "Version=[%s]MerchantCode=[%s]OrderId=[%s]Amount=[%s]AsyNotifyUrl=[%s]SynNotifyUrl=[%s]OrderDate=[%s]TradeIp=[%s]PayCode=[%s]TokenKey=[%s]";
// 		md5Sign = String.format(md5Sign, VERSION, thridBean.getMerCode(), billno, money, notifyUrl, resultUrl, orderDate, ip, PAYCODE_QQPAY, PayConfig.XINBEI.TOKEN_KEY_QQPAY);
// 		md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();
// 		params.put("SignValue", md5Sign); // 根据接口文档组合参数加密后的字段，加密方式为MD5大写
//
// 		result.setPayUrl(PayConfig.XINBEI.GATEWAY);
// 		result.setParamsMap(params);
// 	}
//
//     public static void prepareWeChat(RechargeResult result,String payType, String billno,
//                                  String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                  String ip) {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         String orderDate = new Moment().format("yyyyMMddHHmmss");
//
//         HashMap<String, String> params = new HashMap<>();
//         params.put("Version", VERSION); // 使用网关的版本号
//         params.put("MerchantCode", thridBean.getMerCode()); // 新贝平台商户编码
//         params.put("OrderId", billno); // 商户自己业务逻辑的订单号
//         params.put("Amount", money); // 交易流程中发生的金额
//         params.put("AsyNotifyUrl", notifyUrl); // 业务完成时异步回发通知的地址
//         params.put("SynNotifyUrl", resultUrl); // 业务完成时间同步会发通知的地址
//         params.put("OrderDate", orderDate); // 商户产生订单时的交易时间,格式如：20130102030405
//         params.put("TradeIp", ip); // 发起交易的客户IP地址
//         params.put("PayCode", PAYCODE_WECHAT); // 接入平台时对应的接口编码
//
//         String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.XINBEI.TOKEN_KEY_WECHAT, billno, orderDate, ip, money);
//         params.put("Remark2", cusSign); // 商户自定义备注字段
//
//         String md5Sign = "Version=[%s]MerchantCode=[%s]OrderId=[%s]Amount=[%s]AsyNotifyUrl=[%s]SynNotifyUrl=[%s]OrderDate=[%s]TradeIp=[%s]PayCode=[%s]TokenKey=[%s]";
//         md5Sign = String.format(md5Sign, VERSION, thridBean.getMerCode(), billno, money, notifyUrl, resultUrl, orderDate, ip, PAYCODE_WECHAT, PayConfig.XINBEI.TOKEN_KEY_WECHAT);
//         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();
//
//         params.put("SignValue", md5Sign); // 根据接口文档组合参数加密后的字段，加密方式为MD5大写
//
//         result.setPayUrl(PayConfig.XINBEI.GATEWAY);
//         result.setParamsMap(params);
//     }
//
//     /**
//      * 支付成功通知
//      * @param thridBean
//      * @param resMap
//      */
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String Version = resMap.get("Version"); // 使用网关的版本号
//             String MerchantCode = resMap.get("MerchantCode"); // 新贝平台商户编码
//             String OrderId = resMap.get("OrderId"); // 商户自己业务逻辑的订单号
//             String OrderDate = resMap.get("OrderDate"); // 商户产生订单时的交易时间，格式如： 20130102030405
//             String TradeIp = resMap.get("TradeIp"); // 向商户发起通知的 IP 地址
//             String SerialNo = resMap.get("SerialNo"); // 新贝平台交易流水号
//             String Amount = resMap.get("Amount"); // 交易流程中真正发生的金额
//             String PayCode = resMap.get("PayCode"); // 接入平台时对应的接口编码
//             String State = resMap.get("State"); // 处理后的结果编码 -1：协议错误；0未支付；1成功支付；2查询出错；3：系统异常
//             String Message = resMap.get("Message"); // 处理后的结果信息
//             String FinishTime = resMap.get("FinishTime"); // 平台交易处理完成时间，格式如： 20130102030405
//             String Remark2 = resMap.get("Remark2"); // 商户自定义备注字段
//             String SignValue = resMap.get("SignValue"); // 根据接口文档组合参数加密后的字段
//
//             String money = MoneyFormat.moneyToYuanForPositive(Amount);
//
//             String tokenKey = "";
//             switch (thridBean.getType()) {
//                 case PayConfig.PaymentType.XINBEI:
//                     tokenKey = PayConfig.XINBEI.TOKEN_KEY; break;
//                 case PayConfig.PaymentType.XINBEIWECHAT:
//                     tokenKey = PayConfig.XINBEI.TOKEN_KEY_WECHAT; break;
//                 case PayConfig.PaymentType.XINBEIALIPAY:
//                     tokenKey = PayConfig.XINBEI.TOKEN_KEY_ALIPAY; break;
//                 case PayConfig.PaymentType.XINBEIQQPAY:
//                     tokenKey = PayConfig.XINBEI.TOKEN_KEY_QQPAY; break;
//                 default:
//                     break;
//             }
//
//             String serverSign = "Version=[%s]MerchantCode=[%s]OrderId=[%s]OrderDate=[%s]TradeIp=[%s]SerialNo=[%s]Amount=[%s]PayCode=[%s]State=[%s]FinishTime=[%s]TokenKey=[%s]";
//             serverSign = String.format(serverSign, Version, thridBean.getMerCode(), OrderId, OrderDate, TradeIp, SerialNo, Amount, PayCode, State, FinishTime, tokenKey);
//             serverSign = DigestUtils.md5Hex(serverSign).toUpperCase();
//
//             if (serverSign.equalsIgnoreCase(SignValue)){
//                 if ("8888".equals(State)) {
//                     RechargePay pay = new RechargePay();
//                     pay.setAmount(Double.parseDouble(Amount));//金额
//                     pay.setBillno(OrderId);//订单编号
//                     pay.setTradeNo(SerialNo);//支付订单
//                     pay.setPayTime(new Moment().fromTime(FinishTime, "yyyyMMddHHmmss").toDate());//订单通知时间
//                     pay.setTradeStatus(State);
//                     return pay;
//                 }
//                 else {
//                     log.warn("新贝回调状态表示充值不成功，回调状态码：" + State + ",请求信息：" + JSON.toJSONString(resMap));
//                 }
//             }else {
//                 log.error("新贝回调验证失败，服务器验证：" + serverSign + ",请求信息：" + JSON.toJSONString(resMap));
//                 return null;
//             }
//         } catch (Exception e) {
//             log.error("新贝回调发生异常,请求信息：" + JSON.toJSONString(resMap), e);
//         }
//         return null;
//     }
//
//     private static String getCusSign(String merCode, String merKey, String billno, String orderDate, String ip, String money) {
//         return DigestUtils.md5Hex(merCode + merKey + billno + orderDate + ip + money + VERITY_MD5_KEY).toUpperCase();
//     }
// }
