// package lottery.domains.pool.payment.jiushui;
//
// import com.alibaba.fastjson.JSON;
// import com.itrus.util.sign.RSAWithSoftware;
// import javautils.date.Moment;
// import javautils.image.ImageUtil;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.jiushui.util.SignUtils;
// import lottery.domains.pool.payment.lepay.utils.RSACoderUtil;
// import lottery.domains.pool.payment.lepay.utils.WebUtil;
// import lottery.domains.pool.payment.pay41.utils.URLUtils;
// import org.apache.commons.lang.RandomStringUtils;
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.util.HashMap;
// import java.util.LinkedHashMap;
// import java.util.Map;
//
// /**
//  * Created by Nick on 2017-05-30.
//  */
// public class JiushuiPayment {
//     private static final Logger log = LoggerFactory.getLogger(JiushuiPayment.class);
//     private static final String CHANNELCODE_WECHAT = "WEIXIN";
//     private static final String CHANNELCODE_ALIPAY = "ALIPAY";
//
//     public static void prepare(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip){
//
//     }
//
//     public static void prepareAlipay(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip){
//         try {
//             long fenMoney = MoneyFormat.yuanToFenMoney(Amount);
//             String remark = RandomStringUtils.random(6, true, true);
//
//             StringBuffer signSrc= new StringBuffer();
//             signSrc.append("ORDERNO=").append(billno);
//             signSrc.append("&TXNAMT=").append(fenMoney);
//             signSrc.append("&CHANNELCODE=").append(CHANNELCODE_ALIPAY);
//             signSrc.append("&REMARK=").append(remark);
//             signSrc.append("&RETURNURL=").append(notifyUrl);
//
//             String dataStr = signSrc.toString();
//             String sign;
//             try {
//                 sign = SignUtils.Signaturer(dataStr, PayConfig.JIUSHUI.PRIVATE_KEY);
//             } catch (Exception e) {
//                 log.error("玖水支付宝发生签名异常", e);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//
//             Map<String, String> paramsMap = new LinkedHashMap<>();
//             paramsMap.put("MERCNUM", thridBean.getMerCode());
//             paramsMap.put("TRANDATA", URLUtils.encode(dataStr, "UTF-8"));
//             paramsMap.put("SIGN", URLUtils.encode(sign, "UTF-8"));
//
//             String strResult = WebUtil.doPost(PayConfig.JIUSHUI.SCAN_GATEWAY, paramsMap, "UTF-8", 3000, 15000);
//
//             if (StringUtils.isEmpty(strResult)) {
//                 log.error("玖水支付宝封装异常，返回数据为空");
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payExceptionWithDetails);
//                 result.setErrorMsg("通道未正确返回数据,请重试");
//             }
//
//             Map<String, String> retMap = JSON.parseObject(strResult, HashMap.class);
//             String reCode = retMap.get("RECODE"); // 返回码 000000为成功其他均为失败，根据返回信息处理。
//             String reMsg = retMap.get("REMSG"); // 返回信息
//             String orcodeUrl = retMap.get("ORCODEURL"); // 二维码支付地址
//             String orderNo = retMap.get("ORDERNO"); // 渠道订单号
//
//             if ("000000".equals(reCode)) {
//                 if (StringUtils.isEmpty(orcodeUrl)) {
//                     log.error("玖水支付宝封装异常，返回状态是正常的，但二维码并没有返回：" + strResult);
//                     result.setReturnCode(PayConfig.wxPayReturnCode.payExceptionWithDetails);
//                     result.setErrorMsg("通道未正确返回二维码,请重试");
//                 }
//                 else {
//                     result.setReturnValue(ImageUtil.encodeQR(orcodeUrl, 200, 200));
//                     result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//                 }
//             }
//             else {
//                 log.error("玖水支付宝封装异常:" + strResult);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payExceptionWithDetails);
//                 result.setErrorMsg("支付方返回：" + reMsg);
//             }
//         } catch (Exception e) {
//             log.error("玖水支付宝宝封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static void prepareWeChat(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
//                                      String ip){
//         try {
//             long fenMoney = MoneyFormat.yuanToFenMoney(Amount);
//             String remark = RandomStringUtils.random(6, true, true);
//
//             StringBuffer signSrc= new StringBuffer();
//             signSrc.append("ORDERNO=").append(billno);
//             signSrc.append("&TXNAMT=").append(fenMoney);
//             signSrc.append("&CHANNELCODE=").append(CHANNELCODE_WECHAT);
//             signSrc.append("&REMARK=").append(remark);
//             signSrc.append("&RETURNURL=").append(notifyUrl);
//
//             String dataStr = signSrc.toString();
//             String sign;
//             try {
//                 sign = SignUtils.Signaturer(dataStr, PayConfig.JIUSHUI.PRIVATE_KEY);
//             } catch (Exception e) {
//                 log.error("玖水微信发生签名异常", e);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//
//             Map<String, String> paramsMap = new LinkedHashMap<>();
//             paramsMap.put("MERCNUM", thridBean.getMerCode());
//             paramsMap.put("TRANDATA", URLUtils.encode(dataStr, "UTF-8"));
//             paramsMap.put("SIGN", URLUtils.encode(sign, "UTF-8"));
//
//             String strResult = WebUtil.doPost(PayConfig.JIUSHUI.SCAN_GATEWAY, paramsMap, "UTF-8", 3000, 15000);
//
//             if (StringUtils.isEmpty(strResult)) {
//                 log.error("玖水微信封装异常，返回数据为空");
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payExceptionWithDetails);
//                 result.setErrorMsg("通道未正确返回数据,请重试");
//             }
//
//             Map<String, String> retMap = JSON.parseObject(strResult, HashMap.class);
//             String reCode = retMap.get("RECODE"); // 返回码 000000为成功其他均为失败，根据返回信息处理。
//             String reMsg = retMap.get("REMSG"); // 返回信息
//             String orcodeUrl = retMap.get("ORCODEURL"); // 二维码支付地址
//             String orderNo = retMap.get("ORDERNO"); // 渠道订单号
//
//             if ("000000".equals(reCode)) {
//                 if (StringUtils.isEmpty(orcodeUrl)) {
//                     log.error("玖水微信封装异常，返回状态是正常的，但二维码并没有返回：" + strResult);
//                     result.setReturnCode(PayConfig.wxPayReturnCode.payExceptionWithDetails);
//                     result.setErrorMsg("通道未正确返回二维码,请重试");
//                 }
//                 else {
//                     result.setReturnValue(ImageUtil.encodeQR(orcodeUrl, 200, 200));
//                     result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//                 }
//             }
//             else {
//                 log.error("玖水微信封装异常:" + strResult);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payExceptionWithDetails);
//                 result.setErrorMsg("支付方返回：" + reMsg);
//             }
//         } catch (Exception e) {
//             log.error("玖水微信宝封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String ORDERNO = resMap.get("ORDERNO");
//             String RECODE = resMap.get("RECODE");
//             String REMSG = resMap.get("REMSG");
//             String TXNAMT = resMap.get("TXNAMT");
//             String PAYORDNO = resMap.get("PAYORDNO");
//             String ORDSTATUS = resMap.get("ORDSTATUS");
//             String SIGN = resMap.get("SIGN");
//
//             //签名参数顺序不能乱
//             StringBuffer signSrc= new StringBuffer();
//             signSrc.append("ORDERNO=").append(ORDERNO);
//             signSrc.append("&TXNAMT=").append(TXNAMT);
//             signSrc.append("&ORDSTATUS=").append(ORDSTATUS);
//
//             String signInfo = signSrc.toString();
//             if (SignUtils.validataSign(signInfo, SIGN, PayConfig.JIUSHUI.PLATFORM_PUBLIC_KEY)) {
//                 if ("01".equals(ORDSTATUS)) {
//                     RechargePay pay = new RechargePay();
//                     pay.setAmount(Double.valueOf(TXNAMT)/100);
//                     pay.setPayTime(new Moment().toDate());
//                     pay.setRecvName(thridBean.getMerCode());
//                     pay.setBillno(ORDERNO);
//                     pay.setTradeNo(PAYORDNO);
//                     pay.setSuccess(true);
//                     pay.setTradeStatus(ORDSTATUS);
//                     return pay;
//                 }
//             } else {
//                 log.warn("玖水回调验签失败，请求信息{}", JSON.toJSONString(resMap));
//                 return null;
//             }
//         } catch (Exception e) {
//             log.error("玖水回调发生异常，请求信息：" + JSON.toJSONString(resMap), e);
//         }
//         return null;
//     }
// }
