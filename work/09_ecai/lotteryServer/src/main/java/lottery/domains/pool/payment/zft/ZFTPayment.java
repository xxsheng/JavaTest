// package lottery.domains.pool.payment.zft;
//
// import com.alibaba.fastjson.JSON;
//
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.zft.util.HuihepaySubmit;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.util.*;
//
// /**
//  * Created by Nick on 2017-08-21.
//  */
// public class ZFTPayment {
// //    private static final String VERITY_MD5_KEY = "JEweSm,6]CN@awweedsfae,LSJAj$;aFSaade(15):+&";
//     private static final Logger log = LoggerFactory.getLogger(ZFTPayment.class);
//    // private static final String PAY_TYPE_WY = "1";//网银
//     private static final String PAY_TYPE_WX = "WXPAY";//微信扫码
//     private static final String PAY_TYPE_QQ = "QQPAY";//qq钱包
//     private static final String PAY_TYPE_ALIPAY = "ALIPAY";//支付宝
//     private static final String PAY_TYPE = "1";//支付类型固定传1
//     private static final String PAY_METHOD = "directPay";//支付方式  bankPay收银台 directPay直连模式
//     public static void prepare(RechargeResult result, String payType, String billno,
//                                     String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                     String ip) {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         HashMap<String, String> params = new HashMap<>();
//         params.put("service", "online_pay"); //固定值 online_pay，表示网上支付
//         params.put("merchantId", thridBean.getMerCode()); //我司分配的商户号
//         params.put("notifyUrl", notifyUrl); //异步通知地址
//         params.put("returnUrl", resultUrl); //同步通知地址
//         params.put("title", "cz"); //商品名称
//         params.put("body", "b2c"); //商品描述
//         params.put("orderNo", billno); //商户订单号
//         params.put("totalFee", money); //订单总金额
//         params.put("paymentType", PAY_TYPE); //支付类型固定传1
//         params.put("defaultbank", bankco); //网银代码
//         params.put("paymethod", PAY_METHOD); //支付方式  bankPay收银台 directPay直连模式
//         params.put("isApp", "web"); //接入方式
//         params.put("sellerEmail", "vip2017hs@163.com"); //卖家email 开通商户号时填写的email
//         params.put("charset", "UTF-8"); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//        // String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
//         result.setPayUrl(PayConfig.ZFT.GATEWAY+thridBean.getMerCode()+"-"+billno);
//         result.setParamsMap(new HashMap<String, String>(map));
//     }
//
//     public static void prepareQQ(RechargeResult result, String payType, String billno,
//                                     String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                     String ip) throws Exception {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         HashMap<String, String> params = new HashMap<>();
//         params.put("service", "online_pay"); //固定值 online_pay，表示网上支付
//         params.put("merchantId", thridBean.getMerCode()); //我司分配的商户号
//         params.put("notifyUrl", notifyUrl); //异步通知地址
//         params.put("returnUrl", resultUrl); //同步通知地址
//         params.put("title", "cz"); //商品名称
//         params.put("body", "b2c"); //商品描述
//         params.put("orderNo", billno); //商户订单号
//         params.put("totalFee", money); //订单总金额
//         params.put("paymentType", PAY_TYPE); //支付类型固定传1
//         params.put("defaultbank", PAY_TYPE_QQ); //网银代码
//         params.put("paymethod", PAY_METHOD); //支付方式  bankPay收银台 directPay直连模式
//         params.put("isApp", "web"); //接入方式
//         params.put("sellerEmail", "vip2017hs@163.com"); //卖家email 开通商户号时填写的email
//         params.put("charset", "UTF-8"); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//        // String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
//         result.setPayUrl(PayConfig.ZFT.GATEWAY+thridBean.getMerCode()+"-"+billno);
//         result.setParamsMap(new HashMap<String, String>(map));
// //        String jsonString =HttpClientUtil.post(PayConfig.ZFT.GATEWAY, map);
// //        if (StringUtils.isEmpty(jsonString)) {
// //            log.error("支付通QQ钱包封装数据时返回空,订单号：" + billno);
// //            result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// //            return;
// //        }
// //        JSONObject jsonObject=new JSONObject();
// //        jsonObject=JSONObject.parseObject(jsonString);
// //        if (!jsonObject.getString("respCode").equals("S0001")) {
// //        	log.error("支付通QQ钱包封装数据时返回数据Code不是S0001：" + JSON.toJSONString(jsonObject));
// //            result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// //            return;
// //		}
// //        log.info(jsonObject.toJSONString());
// //        result.setReturnValue(ImageUtil.encodeQR(jsonObject.getString("codeUrl"), 200, 200));
// //        result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//     }
//
//     public static void prepareWeChat(RechargeResult result, String payType, String billno,
//             String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//             String ip) throws Exception {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         HashMap<String, String> params = new HashMap<>();
//         params.put("service", "online_pay"); //固定值 online_pay，表示网上支付
//         params.put("merchantId", thridBean.getMerCode()); //我司分配的商户号
//         params.put("notifyUrl", notifyUrl); //异步通知地址
//         params.put("returnUrl", resultUrl); //同步通知地址
//         params.put("title", "cz"); //商品名称
//         params.put("body", "b2c"); //商品描述
//         params.put("orderNo", billno); //商户订单号
//         params.put("totalFee", money); //订单总金额
//         params.put("paymentType", PAY_TYPE); //支付类型固定传1
//         params.put("defaultbank", PAY_TYPE_WX); //网银代码
//         params.put("paymethod", PAY_METHOD); //支付方式  bankPay收银台 directPay直连模式
//         params.put("isApp", "web"); //接入方式
//         params.put("sellerEmail", "vip2017hs@163.com"); //卖家email 开通商户号时填写的email
//         params.put("charset", "UTF-8"); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//        // String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
//         Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
//         result.setPayUrl(PayConfig.ZFT.GATEWAY+thridBean.getMerCode()+"-"+billno);
//         result.setParamsMap(new HashMap<String, String>(map));
// //        Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
// //        String jsonString =HttpClientUtil.post(PayConfig.ZFT.GATEWAY, map);
// //        if (StringUtils.isEmpty(jsonString)) {
// //            log.error("支付通QQ钱包封装数据时返回空,订单号：" + billno);
// //            result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// //            return;
// //        }
// //        JSONObject jsonObject=new JSONObject();
// //        jsonObject=JSONObject.parseObject(jsonString);
// //        if (!jsonObject.getString("respCode").equals("S0001")) {
// //        	log.error("支付通QQ钱包封装数据时返回数据Code不是S0001：" + JSON.toJSONString(jsonObject));
// //            result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// //            return;
// //		}
// //        log.info(jsonObject.toJSONString());
// //        result.setReturnValue(ImageUtil.encodeQR(jsonObject.getString("codeUrl"), 200, 200));
// //        result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// }
//
//     public static void prepareAlipay(RechargeResult result, String payType, String billno,
//             String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//             String ip) throws Exception {
//         String money = MoneyFormat.moneyToYuanForPositive(Amount);
//         HashMap<String, String> params = new HashMap<>();
//         params.put("service", "online_pay"); //固定值 online_pay，表示网上支付
//         params.put("merchantId", thridBean.getMerCode()); //我司分配的商户号
//         params.put("notifyUrl", notifyUrl); //异步通知地址
//         params.put("returnUrl", resultUrl); //同步通知地址
//         params.put("title", "cz"); //商品名称
//         params.put("body", "b2c"); //商品描述
//         params.put("orderNo", billno); //商户订单号
//         params.put("totalFee", money); //订单总金额
//         params.put("paymentType", PAY_TYPE); //支付类型固定传1
//         params.put("defaultbank", PAY_TYPE_ALIPAY); //网银代码
//         params.put("paymethod", PAY_METHOD); //支付方式  bankPay收银台 directPay直连模式
//         params.put("isApp", "web"); //接入方式
//         params.put("sellerEmail", "vip2017hs@163.com"); //卖家email 开通商户号时填写的email
//         params.put("charset", "UTF-8"); //发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//         Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
//         result.setPayUrl(PayConfig.ZFT.GATEWAY+thridBean.getMerCode()+"-"+billno);
//         result.setParamsMap(new HashMap<String, String>(map));
//        // String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
// //        Map<String, String> map=HuihepaySubmit.buildRequestPara(params);
// //        String jsonString =HttpClientUtil.post(PayConfig.ZFT.GATEWAY, map);
// //        if (StringUtils.isEmpty(jsonString)) {
// //            log.error("支付通支付宝封装数据时返回空,订单号：" + billno);
// //            result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// //            return;
// //        }
// //        JSONObject jsonObject=new JSONObject();
// //        jsonObject=JSONObject.parseObject(jsonString);
// //        if (!jsonObject.getString("respCode").equals("S0001")) {
// //        	log.error("支付通支付宝封装数据时返回数据Code不是S0001：" + JSON.toJSONString(jsonObject));
// //            result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// //            return;
// //		}
// //        log.info(jsonObject.toJSONString());
// //        result.setReturnValue(ImageUtil.encodeQR(jsonObject.getString("codeUrl"), 200, 200));
// //        result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// }
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String tradeStatus = resMap.get("trade_status");//结果代码,返回0表示成功
//             String sign = resMap.get("sign");//商户请求参数的签名串，详见签名
//             String tradeNo = resMap.get("trade_no");//该交易在我司系统中的交易号
//             String orderNo = resMap.get("order_no");//商户订单号，原样返回
//             String totalFee = resMap.get("total_fee");//订单支付金额
// //            String PassbackParams = resMap.get("PassbackParams");//扩展参数，支付成功后原样返回，如果未传此值，则不返回此参数
// //            String cusSign = getCusSign(AppId, thridBean.getMerKey(), orderNo);
// //            if (cusSign.equalsIgnoreCase(PassbackParams)) {
//             Map<String, String> map=HuihepaySubmit.buildRequestPara(resMap);
//                 if (sign.equalsIgnoreCase(map.get("sign"))){
//                     if (tradeStatus.equals("TRADE_FINISHED")) {
//                         RechargePay pay = new RechargePay();
//                         pay.setAmount(Double.parseDouble(totalFee));//金额
//                         pay.setBillno(orderNo);//订单编号
//                         pay.setTradeNo(tradeNo);//支付订单
//                         pay.setPayTime(new Date());//订单通知时间
//                         pay.setSuccess(true);
//                         return pay;
//                     }else {
//                         log.warn("支付通回调状态表示充值不成功，回调状态码：" + tradeStatus + ",请求信息：" + JSON.toJSONString(resMap));
//                     }
//                 }else {
//                     log.error("支付通回调验证失败，服务器验证：" + sign + ",请求信息：" + JSON.toJSONString(resMap));
//                     return null;
//                 }
//         } catch (Exception e) {
//             log.error("支付通回调发生异常", e);
//         }
//         return null;
//     }
// //    private static String getCusSign(String merCode, String merKey, String billno) {
// //        return DigestUtils.md5Hex(merCode + merKey + billno + VERITY_MD5_KEY).toUpperCase();
// //    }
// }
