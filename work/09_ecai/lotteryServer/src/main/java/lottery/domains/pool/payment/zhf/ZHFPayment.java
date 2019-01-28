// package lottery.domains.pool.payment.zhf;
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
// import lottery.domains.pool.payment.dinpay.DinpayEntity;
// import lottery.domains.pool.payment.zhf.util.HttpClientUtil;
// import net.sf.json.JSONObject;
// import org.apache.commons.lang.RandomStringUtils;
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.w3c.dom.Document;
// import org.w3c.dom.Node;
// import org.w3c.dom.NodeList;
// import org.xml.sax.InputSource;
//
// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import java.io.StringReader;
// import java.net.URLDecoder;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * 智汇付
//  * Created by Nick on 2017-05-13.
//  */
// public class ZHFPayment {
//     private static final Logger log = LoggerFactory.getLogger(ZHFPayment.class);
//     private static final String SERVICE_TYPE_ALIPAY_SCAN = "alipay_scan";
//     private static final String SERVICE_TYPE_WEIXIN_SCAN = "weixin_scan";
//     private static final String SERVICE_TYPE_TENPAY_SCAN = "tenpay_scan";
//     private static final String SERVICE_TYPE_DIRECT_PAY = "direct_pay";
//
//     public static void prepare(RechargeResult result,String payType, String billno,
//                                String Amount, String bankco,PaymentThrid thridBean,
//                                String notifyUrl, String resultUrl,String host, String ip) {
//         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//         // 接收表单参数（To receive the parameter）
//         Date now = new Date();
//         String interface_version = "V3.0";
//         String input_charset = "UTF-8";
//         String notify_url = notifyUrl;
//         String order_no = billno;
//         String order_time = sdf.format(now);
//         String order_amount = MoneyFormat.fonmatDinpay(Amount);
//         String product_name = "cz";
//         String signType= "RSA-S";
//         String dinpayType = "b2c";
//         StringBuffer signStr = new StringBuffer();
//         //签名参数顺序不能乱
//         if (StringUtils.isNotEmpty(bankco)) {
//             signStr.append("bank_code=").append(bankco).append("&");
//         }
//         if (StringUtils.isNotEmpty(ip)) {
//             signStr.append("client_ip=").append(ip).append("&");
//         }
//         if (StringUtils.isNotEmpty(host)) {
//             signStr.append("extra_return_param=").append(host).append("&");
//         }
//         if (StringUtils.isNotEmpty(input_charset)) {
//             signStr.append("input_charset=").append(input_charset).append("&");
//         }
//         signStr.append("interface_version=").append(interface_version).append("&");
//         if (StringUtils.isNotEmpty(thridBean.getMerCode())) {
//             signStr.append("merchant_code=").append(thridBean.getMerCode()).append("&");
//         }
//         if (StringUtils.isNotEmpty(notify_url)) {
//             signStr.append("notify_url=").append(notify_url).append("&");
//         }
//         if (StringUtils.isNotEmpty(order_amount)) {
//             signStr.append("order_amount=").append(order_amount).append("&");
//         }
//         if (StringUtils.isNotEmpty(order_no)) {
//             signStr.append("order_no=").append(order_no).append("&");
//         }
//         if (StringUtils.isNotEmpty(order_time)) {
//             signStr.append("order_time=").append(order_time).append("&");
//         }
//         signStr.append("pay_type=").append(dinpayType).append("&");
//         signStr.append("product_name=").append(product_name).append("&");
//         if (StringUtils.isNotEmpty(resultUrl)) {
//             signStr.append("return_url=").append(resultUrl).append("&");
//         }
//         signStr.append("service_type=").append(SERVICE_TYPE_DIRECT_PAY);
//
//         String signInfo = signStr.toString();
//         String sign = null;
//         try {
//             //商户公钥
//             sign = RSAWithSoftware.signByPrivateKey(signInfo, PayConfig.ZHF.PRIVATE_KEY);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//
//         DinpayEntity entity = new DinpayEntity();
//         entity.setMerchant_code(thridBean.getMerCode());
//         entity.setService_type(SERVICE_TYPE_DIRECT_PAY);
//         entity.setInterface_version(interface_version);
//         entity.setSign_type(signType);
//         entity.setInput_charset(input_charset);
//         entity.setNotify_url(notify_url);
//         entity.setOrder_no(order_no);
//         entity.setOrder_time(order_time);
//         entity.setOrder_amount(order_amount);
//         entity.setProduct_name(product_name);
//         entity.setPay_type(dinpayType);
//         entity.setSign(sign);
//         entity.setProduct_code("");
//         entity.setProduct_desc("");
//         entity.setProduct_num("");
//         entity.setShow_url("");
//         entity.setClient_ip(ip);
//         entity.setBank_code(bankco);
//         entity.setRedo_flag("");
//         entity.setExtend_param("");
//         entity.setExtra_return_param(host);
//         entity.setReturn_url(resultUrl);
//
//         result.setPayUrl(PayConfig.ZHF.PAY_GATEWAY);
//         result.setJsonValue(JSONObject.fromObject(entity));
//     }
//
//     public static void prepareWeChat(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean,
//                                      String notifyUrl, String resultUrl, String host, String ip){
//         String orderTime = new Moment().toSimpleTime();
//         String orderAmount = MoneyFormat.fonmatDinpay(Amount);
//         String productName = RandomStringUtils.random(6, true, true);
//
//         Map<String, String> reqMap = new HashMap<String, String>();
//         reqMap.put("merchant_code", thridBean.getMerCode());
//         reqMap.put("service_type", SERVICE_TYPE_WEIXIN_SCAN);
//         reqMap.put("notify_url", notifyUrl);
//         reqMap.put("interface_version", "V3.1");
//         reqMap.put("client_ip", ip);
//         reqMap.put("sign_type", "RSA-S");
//         reqMap.put("order_no", billno);
//         reqMap.put("order_time", orderTime);
//         reqMap.put("order_amount", orderAmount);
//         reqMap.put("product_name", productName);
//
//         StringBuffer signSrc= new StringBuffer();
//         signSrc.append("client_ip=").append(ip).append("&");
//         signSrc.append("interface_version=").append("V3.1").append("&");
//         signSrc.append("merchant_code=").append(thridBean.getMerCode()).append("&");
//         signSrc.append("notify_url=").append(notifyUrl).append("&");
//         signSrc.append("order_amount=").append(orderAmount).append("&");
//         signSrc.append("order_no=").append(billno).append("&");
//         signSrc.append("order_time=").append(orderTime).append("&");
//         signSrc.append("product_name=").append(productName).append("&");
//         signSrc.append("service_type=").append(SERVICE_TYPE_WEIXIN_SCAN);
//
//         String signInfo = signSrc.toString();
//         String sign;	// 签名   signInfo签名参数排序，  merchant_private_key商户私钥
//         try {
//             sign = RSAWithSoftware.signByPrivateKey(signInfo, PayConfig.ZHF.PRIVATE_KEY);
//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//             return;
//         }
//
//         reqMap.put("sign", sign);
//         try {
//             String xml = new HttpClientUtil().doPost(PayConfig.ZHF.SCAN_GATEWAY, reqMap, "utf-8");		 	// 向智付发送POST请求
//             if (StringUtils.isEmpty(xml)) {
//                 log.error("智汇付微信封装数据时返回空,订单号：" + billno);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//             DinpayXMLResult xmlResult = resolveXMLResult(billno, xml);
//
//             if (xmlResult == null) {
//                 log.error("智汇付微信封装数据时解析空");
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (!"SUCCESS".equals(xmlResult.getRespCode())) {
//                 log.error("智汇付微信封装数据时返回数据resp_code不是SUCCESS：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (!"0".equals(xmlResult.getResultCode())) {
//                 log.error("智汇付微信封装数据时返回数据result_code不是0：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (StringUtils.isEmpty(xmlResult.getQrcode())) {
//                 log.error("智汇付微信封装数据时返回二维码为空：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//             result.setReturnValue(ImageUtil.encodeQR(xmlResult.getQrcode(), 200, 200));
//             result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//         } catch (Exception e) {
//             e.printStackTrace();
//             log.error("智汇付微信封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static void prepareAlipay(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean,
//                                      String notifyUrl, String resultUrl, String host, String ip){
//         String orderTime = new Moment().toSimpleTime();
//         String orderAmount = MoneyFormat.fonmatDinpay(Amount);
//         String productName = RandomStringUtils.random(6, true, true);
//
//         Map<String, String> reqMap = new HashMap<String, String>();
//         reqMap.put("merchant_code", thridBean.getMerCode());
//         reqMap.put("service_type", SERVICE_TYPE_ALIPAY_SCAN);
//         reqMap.put("notify_url", notifyUrl);
//         reqMap.put("interface_version", "V3.1");
//         reqMap.put("client_ip", ip);
//         reqMap.put("sign_type", "RSA-S");
//         reqMap.put("order_no", billno);
//         reqMap.put("order_time", orderTime);
//         reqMap.put("order_amount", orderAmount);
//         reqMap.put("product_name", productName);
//
//         StringBuffer signSrc= new StringBuffer();
//         signSrc.append("client_ip=").append(ip).append("&");
//         signSrc.append("interface_version=").append("V3.1").append("&");
//         signSrc.append("merchant_code=").append(thridBean.getMerCode()).append("&");
//         signSrc.append("notify_url=").append(notifyUrl).append("&");
//         signSrc.append("order_amount=").append(orderAmount).append("&");
//         signSrc.append("order_no=").append(billno).append("&");
//         signSrc.append("order_time=").append(orderTime).append("&");
//         signSrc.append("product_name=").append(productName).append("&");
//         signSrc.append("service_type=").append(SERVICE_TYPE_ALIPAY_SCAN);
//
//         String signInfo = signSrc.toString();
//         String sign;	// 签名   signInfo签名参数排序，  merchant_private_key商户私钥
//         try {
//             sign = RSAWithSoftware.signByPrivateKey(signInfo, PayConfig.ZHF.PRIVATE_KEY);
//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//             return;
//         }
//
//         reqMap.put("sign", sign);
//         try {
//             String xml = new HttpClientUtil().doPost(PayConfig.ZHF.SCAN_GATEWAY, reqMap, "utf-8");		 	// 向智付发送POST请求
//             if (StringUtils.isEmpty(xml)) {
//                 log.error("智汇付支付宝封装数据时返回空,订单号：" + billno);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//             DinpayXMLResult xmlResult = resolveXMLResult(billno, xml);
//
//             if (xmlResult == null) {
//                 log.error("智汇付支付宝封装数据时解析空");
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (!"SUCCESS".equals(xmlResult.getRespCode())) {
//                 log.error("智汇付支付宝封装数据时返回数据resp_code不是SUCCESS：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (!"0".equals(xmlResult.getResultCode())) {
//                 log.error("智汇付支付宝封装数据时返回数据result_code不是0：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (StringUtils.isEmpty(xmlResult.getQrcode())) {
//                 log.error("智汇付支付宝封装数据时返回二维码为空：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//             result.setReturnValue(ImageUtil.encodeQR(xmlResult.getQrcode(), 200, 200));
//             result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//         } catch (Exception e) {
//             e.printStackTrace();
//             log.error("智汇付支付宝封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     public static void prepareQQ(RechargeResult result, String payType, String billno,
//                                      String Amount, String bankco, PaymentThrid thridBean,
//                                      String notifyUrl, String resultUrl, String host, String ip){
//         String orderTime = new Moment().toSimpleTime();
//         String orderAmount = MoneyFormat.fonmatDinpay(Amount);
//         String productName = RandomStringUtils.random(6, true, true);
//
//         Map<String, String> reqMap = new HashMap<String, String>();
//         reqMap.put("merchant_code", thridBean.getMerCode());
//         reqMap.put("service_type", SERVICE_TYPE_TENPAY_SCAN);
//         reqMap.put("notify_url", notifyUrl);
//         reqMap.put("interface_version", "V3.1");
//         reqMap.put("client_ip", ip);
//         reqMap.put("sign_type", "RSA-S");
//         reqMap.put("order_no", billno);
//         reqMap.put("order_time", orderTime);
//         reqMap.put("order_amount", orderAmount);
//         reqMap.put("product_name", productName);
//
//         StringBuffer signSrc= new StringBuffer();
//         signSrc.append("client_ip=").append(ip).append("&");
//         signSrc.append("interface_version=").append("V3.1").append("&");
//         signSrc.append("merchant_code=").append(thridBean.getMerCode()).append("&");
//         signSrc.append("notify_url=").append(notifyUrl).append("&");
//         signSrc.append("order_amount=").append(orderAmount).append("&");
//         signSrc.append("order_no=").append(billno).append("&");
//         signSrc.append("order_time=").append(orderTime).append("&");
//         signSrc.append("product_name=").append(productName).append("&");
//         signSrc.append("service_type=").append(SERVICE_TYPE_TENPAY_SCAN);
//
//         String signInfo = signSrc.toString();
//         String sign;	// 签名   signInfo签名参数排序，  merchant_private_key商户私钥
//         try {
//             sign = RSAWithSoftware.signByPrivateKey(signInfo, PayConfig.ZHF.PRIVATE_KEY);
//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//             return;
//         }
//
//         reqMap.put("sign", sign);
//         try {
//             String xml = new HttpClientUtil().doPost(PayConfig.ZHF.SCAN_GATEWAY, reqMap, "utf-8");		 	// 向智付发送POST请求
//             if (StringUtils.isEmpty(xml)) {
//                 log.error("智汇付支付宝封装数据时返回空,订单号：" + billno);
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//             DinpayXMLResult xmlResult = resolveXMLResult(billno, xml);
//
//             if (xmlResult == null) {
//                 log.error("智汇付支付宝封装数据时解析空");
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (!"SUCCESS".equals(xmlResult.getRespCode())) {
//                 log.error("智汇付支付宝封装数据时返回数据resp_code不是SUCCESS：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (!"0".equals(xmlResult.getResultCode())) {
//                 log.error("智汇付支付宝封装数据时返回数据result_code不是0：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//             if (StringUtils.isEmpty(xmlResult.getQrcode())) {
//                 log.error("智汇付支付宝封装数据时返回二维码为空：" + JSON.toJSONString(xmlResult));
//                 result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//                 return;
//             }
//
//             String decodeQRCode = URLDecoder.decode(xmlResult.getQrcode(), "UTF-8");
//             result.setReturnValue(ImageUtil.encodeQR(decodeQRCode, 200, 200));
//             result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
//         } catch (Exception e) {
//             e.printStackTrace();
//             log.error("智汇付支付宝封装数据异常", e);
//             result.setReturnCode(PayConfig.wxPayReturnCode.payException);
//         }
//     }
//
//     private static DinpayXMLResult resolveXMLResult(String billno, String xml) {
//         try {
//             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//             DocumentBuilder builder = factory.newDocumentBuilder();
//             Document doc = builder.parse(new InputSource(new StringReader(xml)));
//             Node node = doc.getFirstChild().getFirstChild();
//             NodeList childNodes = node.getChildNodes();
//
//             DinpayXMLResult result = new DinpayXMLResult();
//
//             int length = childNodes.getLength();
//             for (int i = 0; i < length; i++) {
//                 Node item = childNodes.item(i);
//                 String nodeName = item.getNodeName();
//
//                 switch (nodeName) {
//                     case "resp_code":
//                         result.setRespCode(item.getTextContent()); break;
//                     case "resp_desc":
//                         result.setRespDesc(item.getTextContent()); break;
//                     case "error_code":
//                         result.setErrorCode(item.getTextContent()); break;
//                     case "interface_version":
//                         result.setInterfaceVersion(item.getTextContent()); break;
//                     case "result_code":
//                         result.setResultCode(item.getTextContent()); break;
//                     case "result_desc":
//                         result.setResultDesc(item.getTextContent()); break;
//                     case "sign_type":
//                         result.setSignType(item.getTextContent()); break;
//                     case "sign":
//                         result.setSign(item.getTextContent()); break;
//                     case "qrcode":
//                         result.setQrcode(item.getTextContent()); break;
//                     case "merchant_code":
//                         result.setMerchantCode(item.getTextContent()); break;
//                     case "order_no":
//                         result.setOrderNo(item.getTextContent()); break;
//                     case "order_time":
//                         result.setOrderTime(item.getTextContent()); break;
//                     case "order_amount":
//                         result.setOrderAmount(item.getTextContent()); break;
//                     case "extra_return_param":
//                         result.setExtraReturnParam(item.getTextContent()); break;
//                     case "trade_no":
//                         result.setTradeNo(item.getTextContent()); break;
//                     case "trade_time":
//                         result.setTradeTime(item.getTextContent()); break;
//                     default: break;
//                 }
//             }
//             return result;
//         } catch (Exception e) {
//             log.error("解析新智付扫码支付返回的xml时出错，订单号：" + billno, e);
//             e.printStackTrace();
//             return null;
//         }
//     }
//
//     public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
//         try {
//             String merchant_code = thridBean.getMerCode();
//             String notify_type = resMap.get("notify_type");
//             String notify_id = resMap.get("notify_id");
//             String dinpaySign = resMap.get("sign");
//             String order_no = resMap.get("order_no");
//             String order_time = resMap.get("order_time");
//             String order_amount = resMap.get("order_amount");
//             String trade_no = resMap.get("trade_no");
//             String trade_time = resMap.get("trade_time");
//             String trade_status = resMap.get("trade_status");
//             String bank_seq_no = resMap.get("bank_seq_no");
//             String extra_return_param = resMap.get("extra_return_param");
//             //签名参数顺序不能乱
//             StringBuilder signStr = new StringBuilder();
//             if (null != bank_seq_no && !bank_seq_no.equals("")) {
//                 signStr.append("bank_seq_no=").append(bank_seq_no).append("&");
//             }
//             if (null != extra_return_param && !extra_return_param.equals("")) {
//                 signStr.append("extra_return_param=").append(extra_return_param).append("&");
//             }
//             signStr.append("interface_version=V3.0").append("&");
//             signStr.append("merchant_code=").append(merchant_code).append("&");
//             if (null != notify_id && !notify_id.equals("")) {
//                 signStr.append("notify_id=").append(notify_id).append("&notify_type=").append(notify_type).append("&");
//             }
//             signStr.append("order_amount=").append(order_amount).append("&");
//             signStr.append("order_no=").append(order_no).append("&");
//             signStr.append("order_time=").append(order_time).append("&");
//             signStr.append("trade_no=").append(trade_no).append("&");
//             signStr.append("trade_status=").append(trade_status).append("&");
//             signStr.append("trade_time=").append(trade_time);
//             String signInfo = signStr.toString();
//             //智汇付公钥
//             if (RSAWithSoftware.validateSignByPublicKey(signInfo, PayConfig.ZHF.PLATFORM_PUBLIC_KEY, dinpaySign)
//                     && "SUCCESS".equals(trade_status)) {
//                 RechargePay pay = new RechargePay();
//                 pay.setAmount(Double.parseDouble(order_amount));
//                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                 pay.setPayTime(sdf.parse(trade_time));
//                 pay.setRecvName(merchant_code);
//                 pay.setBillno(order_no);
//                 pay.setTradeNo(trade_no);
//                 pay.setRecvCardNo(merchant_code);
//                 pay.setNotifyType(notify_type);
//                 pay.setTradeStatus(trade_status);
//                 return pay;
//             } else {
//                 log.warn("智汇付回调验签失败，请求信息{}", JSON.toJSONString(resMap));
//                 return null;
//             }
//         } catch (Exception e) {
//             log.error("智汇付回调发生异常，请求信息：" + JSON.toJSONString(resMap), e);
//         }
//         return null;
//     }
// }
