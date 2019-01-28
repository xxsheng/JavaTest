// package lottery.domains.pool.payment.mkt;
//
// import com.alibaba.fastjson.JSON;
// import javautils.date.Moment;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.pay41.utils.AppConstants;
// import lottery.domains.pool.payment.pay41.utils.DateUtils;
// import lottery.domains.pool.payment.pay41.utils.KeyValue;
// import lottery.domains.pool.payment.pay41.utils.KeyValues;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * 秒卡通支付接口
//  *
//  */
// public class MKTPayment {
// 	private static final String VEFITY_MD5_KEY = "df$tkd0ada232a12123oo@kda0d(9id";
// 	private static final Logger log = LoggerFactory.getLogger(MKTPayment.class);
//
// 	public static void prepare(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
// 		act = MKTAES.encrypt(act, PayConfig.MKT.MD5_SECRET);
// 		KeyValues kvs = new KeyValues();
// 		kvs.add(new KeyValue(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset));// 字符集
// 		kvs.add(new KeyValue("inform_url", notifyUrl));// 异步通知地址
// 		kvs.add(new KeyValue(AppConstants.RETURN_URL, resultUrl));// 同步通知地址
// 		kvs.add(new KeyValue(AppConstants.PAY_TYPE, "1"));// 1：网银支付 2：微信 3：支付宝
// 		kvs.add(new KeyValue(AppConstants.BANK_CODE, bankco));// 银行编码
// 		kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, thridBean.getMerCode()));// 商户号
// 		kvs.add(new KeyValue(AppConstants.ORDER_NO, billno));// 订单编号
// 		kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, act));// 订单金额
// 		kvs.add(new KeyValue(AppConstants.ORDER_TIME, DateUtils.format(new Date())));// 订单时间
// 		kvs.add(new KeyValue(AppConstants.REQ_REFERER, host));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_IP, ip));
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, billno);
// 		kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, cusSign)); // 如果商户支付请求时传递了该参数，则通知商户支付成功时会回传该参数
// 		String sign = kvs.sign(PayConfig.MKT.MD5_SECRET, PayConfig.PAY41_CFG.input_charset);
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset);
// 		params.put("inform_url", notifyUrl);
// 		params.put(AppConstants.RETURN_URL, resultUrl);
// 		params.put(AppConstants.PAY_TYPE, "1");
// 		params.put(AppConstants.BANK_CODE, bankco);
// 		params.put(AppConstants.MERCHANT_CODE, thridBean.getMerCode());
// 		params.put(AppConstants.ORDER_NO, billno);
// 		params.put(AppConstants.ORDER_AMOUNT, act);
// 		params.put(AppConstants.ORDER_TIME, DateUtils.format(new Date()));
// 		params.put(AppConstants.REQ_REFERER, host);
// 		params.put(AppConstants.CUSTOMER_IP, ip);
// 		params.put(AppConstants.RETURN_PARAMS, cusSign);
// 		params.put(AppConstants.SIGN, sign);
// 		result.setPayUrl(thridBean.getLink());
// 		result.setParamsMap(params);
// 	}
//
// 	public static void prepareWechat(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
// 		act = MKTAES.encrypt(act, PayConfig.MKT.MD5_SECRET);
// 		KeyValues kvs = new KeyValues();
// 		kvs.add(new KeyValue(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset));// 字符集
// 		kvs.add(new KeyValue("inform_url", notifyUrl));// 异步通知地址
// 		kvs.add(new KeyValue(AppConstants.RETURN_URL, resultUrl));// 同步通知地址
// 		kvs.add(new KeyValue(AppConstants.PAY_TYPE, "2"));// 1：网银支付 2：微信 3：支付宝
// 		kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, thridBean.getMerCode()));// 商户号
// 		kvs.add(new KeyValue(AppConstants.ORDER_NO, billno));// 订单编号
// 		kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, act));// 订单金额
// 		kvs.add(new KeyValue(AppConstants.ORDER_TIME, DateUtils.format(new Date())));// 订单时间
// 		kvs.add(new KeyValue(AppConstants.REQ_REFERER, host));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_IP, ip));
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, billno);
// 		kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, cusSign));
// 		String sign = kvs.sign(PayConfig.MKT.MD5_SECRET, PayConfig.PAY41_CFG.input_charset);
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset);
// 		params.put("inform_url", notifyUrl);
// 		params.put(AppConstants.RETURN_URL, resultUrl);
// 		params.put(AppConstants.PAY_TYPE, "2");
// 		params.put(AppConstants.MERCHANT_CODE, thridBean.getMerCode());
// 		params.put(AppConstants.ORDER_NO, billno);
// 		params.put(AppConstants.ORDER_AMOUNT, act);
// 		params.put(AppConstants.ORDER_TIME, DateUtils.format(new Date()));
// 		params.put(AppConstants.REQ_REFERER, host);
// 		params.put(AppConstants.CUSTOMER_IP, ip);
// 		params.put(AppConstants.RETURN_PARAMS, cusSign);
// 		params.put(AppConstants.SIGN, sign);
// 		result.setPayUrl(thridBean.getLink());
// 		result.setParamsMap(params);
// 	}
//
// 	public static void prepareAlipay(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
// 		act = MKTAES.encrypt(act, PayConfig.MKT.MD5_SECRET);
// 		KeyValues kvs = new KeyValues();
// 		kvs.add(new KeyValue(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset));// 字符集
// 		kvs.add(new KeyValue("inform_url", notifyUrl));// 异步通知地址
// 		kvs.add(new KeyValue(AppConstants.RETURN_URL, resultUrl));// 同步通知地址
// 		kvs.add(new KeyValue(AppConstants.PAY_TYPE, "3"));// 1：网银支付 2：微信 3：支付宝
// 		kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, thridBean.getMerCode()));// 商户号
// 		kvs.add(new KeyValue(AppConstants.ORDER_NO, billno));// 订单编号
// 		kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, act));// 订单金额
// 		kvs.add(new KeyValue(AppConstants.ORDER_TIME, DateUtils.format(new Date())));// 订单时间
// 		kvs.add(new KeyValue(AppConstants.REQ_REFERER, host));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_IP, ip));
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, billno);
// 		kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, cusSign));
// 		String sign = kvs.sign(PayConfig.MKT.MD5_SECRET, PayConfig.PAY41_CFG.input_charset);
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset);
// 		params.put("inform_url", notifyUrl);
// 		params.put(AppConstants.RETURN_URL, resultUrl);
// 		params.put(AppConstants.PAY_TYPE, "3");
// 		params.put(AppConstants.MERCHANT_CODE, thridBean.getMerCode());
// 		params.put(AppConstants.ORDER_NO, billno);
// 		params.put(AppConstants.ORDER_AMOUNT, act);
// 		params.put(AppConstants.ORDER_TIME, DateUtils.format(new Date()));
// 		params.put(AppConstants.REQ_REFERER, host);
// 		params.put(AppConstants.CUSTOMER_IP, ip);
// 		params.put(AppConstants.RETURN_PARAMS, cusSign);
// 		params.put(AppConstants.SIGN, sign);
// 		result.setPayUrl(thridBean.getLink());
// 		result.setParamsMap(params);
// 	}
//
//
// 	/**
// 	 * 支付成功通知
// 	 * @param thridBean
// 	 * @param resMap
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean,Map<String, String> resMap) throws Exception{
// 		try {
// 			String merchantCode = resMap.get(AppConstants.MERCHANT_CODE);
// 			String orderNo = resMap.get(AppConstants.ORDER_NO);
// 			String orderAmount = resMap.get(AppConstants.ORDER_AMOUNT);
// 			String orderTime = resMap.get(AppConstants.ORDER_TIME);
// 			String returnParams = resMap.get(AppConstants.RETURN_PARAMS);
// 			String tradeNo = resMap.get(AppConstants.TRADE_NO);
// 			String tradeStatus = resMap.get(AppConstants.TRADE_STATUS);
// 			String sign = resMap.get(AppConstants.SIGN);
// 			KeyValues kvs = new KeyValues();
//
// 			if (!thridBean.getMerCode().equals(merchantCode)) {
// 				return null;
// 			}
//
// 			kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, merchantCode));
// 			kvs.add(new KeyValue(AppConstants.ORDER_NO, orderNo));
// 			kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, orderAmount));
// 			kvs.add(new KeyValue(AppConstants.ORDER_TIME, orderTime));
// 			kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, returnParams));
// 			kvs.add(new KeyValue(AppConstants.TRADE_NO, tradeNo));
// 			kvs.add(new KeyValue(AppConstants.TRADE_STATUS, tradeStatus));
// 			String thizSign = kvs.sign(PayConfig.MKT.MD5_SECRET, PayConfig.PAY41_CFG.input_charset);
// 			if (thizSign.equalsIgnoreCase(sign) && tradeStatus.equals("success")){
//
// 				// 自定义签名验证
// 				String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, orderNo);
// 				if (cusSign.equals(returnParams)) {
// 					RechargePay pay = new RechargePay();
// 					pay.setRecvCardNo(merchantCode);//商户号
// 					pay.setAmount(Double.parseDouble(orderAmount));//金额
// 					pay.setBillno(orderNo);//订单编号
// 					pay.setTradeNo(tradeNo);//支付订单
// 					pay.setPayTime(new Moment().fromTime(orderTime).toDate());//订单通知时间
// 					pay.setTradeStatus(tradeStatus);//支付状态(success 交易成功,failed 交易失败,paying 交易中)
// 					return pay;
// 				}
// 				else {
// 					log.error("秒卡通回调自定义验证失败，服务器验证：" + cusSign + ",请求信息：" + JSON.toJSONString(resMap));
// 				}
// 			}else {
// 				log.error("秒卡通回调验证失败，服务器验证：" + thizSign + ",请求信息：" + JSON.toJSONString(resMap));
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			log.error("秒卡通回调发生异常", e);
// 		}
// 		return null;
// 	}
//
// 	private static String getCusSign(String merCode, String merKey, String billno) {
// 		return DigestUtils.md5Hex(merCode + merKey + billno + VEFITY_MD5_KEY);
// 	}
// }
