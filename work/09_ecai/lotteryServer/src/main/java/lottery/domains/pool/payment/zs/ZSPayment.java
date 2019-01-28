 package lottery.domains.pool.payment.zs;

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
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

 /**
  * 泽圣支付
  * Created by Nick on 2017-05-28.
  */
 public class ZSPayment extends AbstractPayment {
     private static final Logger log = LoggerFactory.getLogger(ZSPayment.class);

     private static final String CUS_EXT1 = "FFHcg,S$QL:H[tkl0dlqbd98o{2xyT";
     private static final String OUTPUT_SUCCESS = "{\"code\":\"00\",\"msg\":\"处理成功\"}";
     private static final String OUTPUT_FAILED = "{{\"code\":\"01\",\"msg\":\"处理失败\"}}";

     public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                         PaymentChannel channel, HttpServletRequest request){
         try {
             switch(channel.getChannelCode()) {
                 case Global.PAYMENT_CHANNEL_ZS:
                     return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                 case Global.PAYMENT_CHANNEL_ZSWECHAT:
                     return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                 case Global.PAYMENT_CHANNEL_ZSALIPAY:
                     return prepareAlipay(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                 case Global.PAYMENT_CHANNEL_ZSQQ:
                     return prepareQQ(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
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
         long fenMoney = MoneyFormat.yuanToFenMoney(amount+"");
         String orderCreateTime = new Moment().format("yyyyMMddHHmmss");
         String lastPayTime = new Moment().add(20, "minutes").format("yyyyMMddHHmmss");

         HashMap<String, String> params = new HashMap<>();
         params.put("merchantCode", channel.getMerCode()); // 泽圣分配的商户号
         params.put("outOrderId", billno); // 商户系统唯一的订单编号
         params.put("totalAmount", fenMoney+""); // 支付金额 单位分
         params.put("goodsName", RandomStringUtils.random(8, true, true)); // 商品名称20 个字符
         params.put("goodsExplain", RandomStringUtils.random(8, true, true)); // 商品描述20 个字符
         params.put("orderCreateTime", orderCreateTime); // 创建时间 yyyyMMddHHmmss
         params.put("lastPayTime", lastPayTime); // 最晚支付时间 yyyyMMddHHmmss
         params.put("merUrl", resultUrl); // 商户取货URL
         params.put("noticeUrl", notifyUrl); // 通知商户服务端地址
         params.put("bankCode", bankco); // 支付银行代码 看 5.1 支持银行列表
         params.put("bankCardType", "01"); // 支付银行卡类型 00： B2C 借贷记综合 01： B2C 纯借记 03： B2B 企业网银

         // 自定义验签，把EXT1设置一个值
         String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), billno, fenMoney);
         params.put("ext", cusSign); // 扩展字段 异步通知原样返回

         String md5Sign = "lastPayTime=%s&merchantCode=%s&orderCreateTime=%s&outOrderId=%s&totalAmount=%s&KEY=%s";
         md5Sign = String.format(md5Sign, lastPayTime, channel.getMerCode(), orderCreateTime, billno, fenMoney, channel.getMd5Key());
         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();

         params.put("sign", md5Sign); // 签名

         String formUrl = channel.getPayUrl() + "ebank/pay.do";
         return retPrepareWangYing(formUrl, params);
     }

     private static PrepareResult prepareAlipay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                      PaymentChannel channel, HttpServletRequest request) {
         // 21 微信， 30-支付宝 31-QQ 钱包 不传值则默认 21
         return prepareWithQRCode(billno, amount, "30", notifyUrl, resultUrl, channel, request);
     }


     private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                      PaymentChannel channel, HttpServletRequest request) {
         // 21 微信， 30-支付宝 31-QQ 钱包 不传值则默认 21
         return prepareWithQRCode(billno, amount, "21", notifyUrl, resultUrl, channel, request);
     }


     private static PrepareResult prepareQQ(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                  PaymentChannel channel, HttpServletRequest request) {
         // 21 微信， 30-支付宝 31-QQ 钱包 不传值则默认 21
         return prepareWithQRCode(billno, amount, "31", notifyUrl, resultUrl, channel, request);
     }

     private static PrepareResult prepareWithQRCode(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                          PaymentChannel channel, HttpServletRequest request) {
         long fenMoney = MoneyFormat.yuanToFenMoney(amount+"");
         String orderCreateTime = new Moment().format("yyyyMMddHHmmss");
         String lastPayTime = new Moment().add(20, "minutes").format("yyyyMMddHHmmss");

         Map<String, String> params = new HashMap<>();
         params.put("model", "QR_CODE"); // 模块名 传入固定值:QR_CODE
         params.put("merchantCode", channel.getMerCode()); // 泽圣分配的商户号
         params.put("outOrderId", billno); // 商户系统唯一的订单编号
         params.put("deviceNo", "123456"); // 设备号
         params.put("amount", fenMoney+""); // 支付金额 单位分

         String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), billno, fenMoney);
         params.put("ext", cusSign); // 扩展字段 异步通知原样返回
         params.put("orderCreateTime", orderCreateTime); // 创建时间 yyyyMMddHHmmss
         params.put("lastPayTime", lastPayTime); // 最晚支付时间 yyyyMMddHHmmss
         params.put("noticeUrl", notifyUrl); // 通知商户服务端地址
         params.put("isSupportCredit", "1"); // 是否支持信用卡： 1是（ 默认） 0-否
         String ip = HttpUtil.getRealIp(null, request);

         if (ip == null) {
             return retPrepareFailed("不是有效的IPV4地址！");
         }

         params.put("ip", ip); // app 和网页支付提交用户端 ip
         params.put("KEY", channel.getMd5Key());


         String md5Sign = "amount=%s&isSupportCredit=%s&merchantCode=%s&noticeUrl=%s&orderCreateTime=%s&outOrderId=%s&KEY=%s";
         md5Sign = String.format(md5Sign, fenMoney, "1", channel.getMerCode(), notifyUrl, orderCreateTime, billno, channel.getMd5Key());
         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();

         params.put("sign", md5Sign);
         params.put("payChannel", bankco); // 21 微信， 30-支付宝 31-QQ 钱包 不传值则默认 21

         try {
             String url = channel.getPayUrl() + "scan/entrance.do";
             String retStr = HttpClientUtil.post(url, params, null, 10000);
             if (StringUtils.isEmpty(retStr)) {
                 log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                 return retPrepareFailed("获取二维码失败，请重试！");
             }

             ZSScanResult scanResult = JSON.parseObject(retStr, ZSScanResult.class);
             if (scanResult == null || scanResult.getData() == null) {
                 log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                 return retPrepareFailed("获取二维码失败，请重试！");
             }
             if (!"00".equals(scanResult.getCode())) {
                 log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                 return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getCode() + "，" + scanResult.getMsg());
             }
             if (StringUtils.isEmpty(scanResult.getData().getUrl())) {
                 log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                 return retPrepareFailed("获取二维码失败，请重试！");
             }

             return retPrepareQRCode(scanResult.getData().getUrl());
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
             String merchantCode = resMap.get("merchantCode"); // 商户号
             String instructCode = resMap.get("instructCode"); // 交易订单号
             String transType = resMap.get("transType"); // 交易类型 00200-消费
             String outOrderId = resMap.get("outOrderId"); // 商户订单
             String transTime = resMap.get("transTime"); // 交易时间 yyyyMMddHHmmss
             String totalAmount = resMap.get("totalAmount"); //  消费金额 单位 分
             String ext = resMap.get("ext"); // 扩展字段
             String sign = resMap.get("sign"); // 签名
             String code = resMap.get("code"); // 响应码 00-成功,商户收到支付通知， 就应该返回 00. 其他失败
             String msg = resMap.get("msg"); // 响应消息

//             if (!"00".equals(code)) {
//                 log.warn(channel.getName() + "回调本地验证失败，参数状态表示不成功，回调：" + JSON.toJSONString(resMap));
//                 return false;
//             }

             if (!channel.getMerCode().equalsIgnoreCase(merchantCode)) {
                 log.warn(channel.getName() + "回调本地验证失败，商户号不一致，回调：" + JSON.toJSONString(resMap));
                 return false;
             }

             String serverSign = "instructCode=%s&merchantCode=%s&outOrderId=%s&totalAmount=%s&transTime=%s&transType=%s&KEY=%s";
             serverSign = String.format(serverSign, instructCode, channel.getMerCode() , outOrderId, totalAmount, transTime, transType, channel.getMd5Key());
             serverSign = DigestUtils.md5Hex(serverSign).toUpperCase();

             if (!serverSign.equalsIgnoreCase(sign)) {
                 log.error(channel.getName() + "回调本地验证失败，参数签名与服务器不一致，服务器验证：" + serverSign + ",回调：" + JSON.toJSONString(resMap));
                 return false;
             }

             String cusSign = getCusSign(channel.getMerCode(), channel.getExt1(), outOrderId, Long.valueOf(totalAmount));
             if (!cusSign.equalsIgnoreCase(ext)) {
                 log.error(channel.getName() + "回调本地验证失败，自定义参数签名与服务器不一致，服务器验证：" + cusSign + ",回调：" + JSON.toJSONString(resMap));
                 return false;
             }

             return true;
         } catch (Exception e) {
             log.error(channel.getName() + "回调本地验证发生异常", e);
         }
         return false;
     }


     private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap) {
         String outOrderId = resMap.get("outOrderId"); // 商户订单

         Map<String, String> params = new HashMap<>();
         params.put("merchantCode", channel.getMerCode()); // 泽圣分配的商户号
         params.put("outOrderId", outOrderId); // 商户系统唯一的订单编号

         String md5Sign = "merchantCode=%s&outOrderId=%s&KEY=%s";
         md5Sign = String.format(md5Sign, channel.getMerCode(), outOrderId, channel.getMd5Key());
         md5Sign = DigestUtils.md5Hex(md5Sign).toUpperCase();

         params.put("sign", md5Sign);

         try {
             String url = channel.getPayUrl() + "ebank/queryOrder.do";

             String retStr = HttpClientUtil.post(url, params, null, 10000);
             if (StringUtils.isEmpty(retStr)) {
                 log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(resMap));
                 return retVerifyFailed("远程验证失败，获取订单失败");
             }

             ZSQueryResult queryResult = JSON.parseObject(retStr, ZSQueryResult.class);
             if (queryResult == null || queryResult.getData() == null) {
                 log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(resMap));
                 return retVerifyFailed("远程验证失败，获取订单失败");
             }

             if (!"00".equals(queryResult.getCode())) {
                 log.error(channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                 return retVerifyFailed("远程验证失败，订单未支付");
             }

             if (!"00".equals(queryResult.getData().getReplyCode())) {
                 log.error(channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                 return retVerifyFailed("远程验证失败，订单未支付");
             }

             VerifyResult verifyResult = new VerifyResult();
             verifyResult.setSelfBillno(queryResult.getData().getOutOrderId());
             verifyResult.setChannelBillno(queryResult.getData().getInstructCode());

             long money = Long.valueOf(queryResult.getData().getAmount());
             double moneyDouble = money / 100;

             verifyResult.setRequestMoney(moneyDouble);
             verifyResult.setReceiveMoney(moneyDouble);
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

     private static String getCusSign(String merCode, String ext1, String billno, long money) {
         String _ext1 = ext1;
         if (StringUtils.isEmpty(_ext1)) {
             _ext1 = CUS_EXT1;
         }
         return DigestUtils.md5Hex(merCode + _ext1 + billno + money).toUpperCase();
     }
 }
