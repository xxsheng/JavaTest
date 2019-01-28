// package lottery.domains.pool.payment.pay41;
//
// import lottery.domains.content.entity.PaymentChannel;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.dinpay.WechatResultEntity;
// import lottery.domains.pool.payment.pay41.utils.*;
// import org.apache.http.message.BasicNameValuePair;
//
// import java.text.SimpleDateFormat;
// import java.util.*;
//
// /**
//  *通汇支付接口
//  *
//  */
// public class Pay41Payment {
//
// 	/**
// 	 * 封装请求数据
// 	 */
// 	public static void prepare(RechargeResult result, PaymentChannel channel, String billno,
// 							   String Amount, String bankco, String notifyUrl, String resultUrl, String host,
// 							   String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
// 		KeyValues kvs = new KeyValues();
// 		kvs.add(new KeyValue(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset));// 字符集
// 		kvs.add(new KeyValue(AppConstants.NOTIFY_URL, notifyUrl));// 异步通知地址
// 		kvs.add(new KeyValue(AppConstants.RETURN_URL, resultUrl));// 同步通知地址
// 		kvs.add(new KeyValue(AppConstants.PAY_TYPE, PayConfig.PAY41_CFG.pay_type));// 支付方式（目前暂时只支持网银支付）
// 		kvs.add(new KeyValue(AppConstants.BANK_CODE, bankco));// 银行编码
// 		kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, channel.getMerCode()));// 商户号
// 		kvs.add(new KeyValue(AppConstants.ORDER_NO, billno));// 订单编号
// 		kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, act));// 订单金额
// 		kvs.add(new KeyValue(AppConstants.ORDER_TIME, DateUtils.format(new Date())));// 订单时间
// 		kvs.add(new KeyValue(AppConstants.PRODUCT_NAME, "ZYCZ"));
// 		kvs.add(new KeyValue(AppConstants.PRODUCT_NUM, "1"));
// 		kvs.add(new KeyValue(AppConstants.REQ_REFERER, host));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_IP, ip));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_PHONE, "17000000000"));
// 		kvs.add(new KeyValue(AppConstants.RECEIVE_ADDRESS, "湖北"));
// 		kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, "zy"));
// 		String sign = kvs.sign(channel.getMd5Key(), PayConfig.PAY41_CFG.input_charset);
//
// 		StringBuilder sb = new StringBuilder();
// 		sb.append(PayConfig.PAY41_CFG.par_url);
// 		result.setPayUrl(PayConfig.PAY41_CFG.par_url);
// 		URLUtils.appendParam(sb, AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset, false);
// 		URLUtils.appendParam(sb, AppConstants.NOTIFY_URL, notifyUrl, PayConfig.PAY41_CFG.input_charset);
// 		URLUtils.appendParam(sb, AppConstants.RETURN_URL, resultUrl, PayConfig.PAY41_CFG.input_charset);
// 		URLUtils.appendParam(sb, AppConstants.PAY_TYPE, PayConfig.PAY41_CFG.pay_type);
// 		URLUtils.appendParam(sb, AppConstants.BANK_CODE, bankco);
// 		URLUtils.appendParam(sb, AppConstants.MERCHANT_CODE, channel.getMerCode());
// 		URLUtils.appendParam(sb, AppConstants.ORDER_NO, billno);
// 		URLUtils.appendParam(sb, AppConstants.ORDER_AMOUNT, act);
// 		URLUtils.appendParam(sb, AppConstants.ORDER_TIME, DateUtils.format(new Date()));
// 		URLUtils.appendParam(sb, AppConstants.PRODUCT_NAME, "ZYCZ", PayConfig.PAY41_CFG.input_charset);
// 		URLUtils.appendParam(sb, AppConstants.PRODUCT_NUM, "1");
// 		URLUtils.appendParam(sb, AppConstants.REQ_REFERER, host, PayConfig.PAY41_CFG.input_charset);
// 		URLUtils.appendParam(sb, AppConstants.CUSTOMER_IP, ip);
// 		URLUtils.appendParam(sb, AppConstants.CUSTOMER_PHONE, "17000000000");
// 		URLUtils.appendParam(sb, AppConstants.RECEIVE_ADDRESS, "湖北", PayConfig.PAY41_CFG.input_charset);
// 		URLUtils.appendParam(sb, AppConstants.RETURN_PARAMS, "zy", PayConfig.PAY41_CFG.input_charset);
// 		URLUtils.appendParam(sb, AppConstants.SIGN, sign);
// 		result.setSignature(sign);
// 		result.setRedirectUrl(sb.toString());
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset);
// 		params.put(AppConstants.NOTIFY_URL, notifyUrl);
// 		params.put(AppConstants.RETURN_URL, resultUrl);
// 		params.put(AppConstants.PAY_TYPE, PayConfig.PAY41_CFG.pay_type);
// 		params.put(AppConstants.BANK_CODE, bankco);
// 		params.put(AppConstants.MERCHANT_CODE, channel.getMerCode());
// 		params.put(AppConstants.ORDER_NO, billno);
// 		params.put(AppConstants.ORDER_AMOUNT, act);
// 		params.put(AppConstants.ORDER_TIME, DateUtils.format(new Date()));
// 		params.put(AppConstants.PRODUCT_NAME, "ZYCZ");
// 		params.put(AppConstants.PRODUCT_NUM, "1");
// 		params.put(AppConstants.REQ_REFERER, host);
// 		params.put(AppConstants.CUSTOMER_IP, ip);
// 		params.put(AppConstants.CUSTOMER_PHONE, "17000000000");
// 		params.put(AppConstants.RECEIVE_ADDRESS, "湖北");
// 		params.put(AppConstants.RETURN_PARAMS, "zy");
// 		params.put(AppConstants.SIGN, sign);
// 		result.setParamsMap(params);
// 	}
//
// 	/**
// 	 * 封装微信支付参数
// 	 * @param result
// 	 * @param payType
// 	 * @param billno
// 	 * @param Amount
// 	 * @param bankco
// 	 * @param thridBean
// 	 * @param notifyUrl
// 	 * @param resultUrl
// 	 * @param host
// 	 * @param ip
// 	 */
// 	public static void prepareWeiXin(RechargeResult result,String billno, String amount, String bankco, PaymentChannel channel, String notifyUrl, String resultUrl, String host, String ip) {
// 		String act = MoneyFormat.FormatPay41(amount);
// 		KeyValues kvs = new KeyValues();
// 		kvs.add(new KeyValue(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset));// 字符集
// 		kvs.add(new KeyValue(AppConstants.NOTIFY_URL, notifyUrl));// 异步通知地址
// 		kvs.add(new KeyValue(AppConstants.RETURN_URL, resultUrl));// 同步通知地址
// 		kvs.add(new KeyValue(AppConstants.PAY_TYPE, PayConfig.PAY41_CFG.pay_type_wx));// 支付方式
// 		kvs.add(new KeyValue(AppConstants.BANK_CODE, "WEIXIN"));// 银行编码
// 		kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, channel.getMerCode()));// 商户号
// 		kvs.add(new KeyValue(AppConstants.ORDER_NO, billno));// 订单编号
// 		kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, act));// 订单金额
// 		kvs.add(new KeyValue(AppConstants.ORDER_TIME, DateUtils.format(new Date())));// 订单时间
// 		kvs.add(new KeyValue(AppConstants.PRODUCT_NAME, "ZYCZ"));
// 		kvs.add(new KeyValue(AppConstants.PRODUCT_NUM, "1"));
// 		kvs.add(new KeyValue(AppConstants.REQ_REFERER, host));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_IP, ip));
// 		kvs.add(new KeyValue(AppConstants.CUSTOMER_PHONE, "17000000000"));
// 		kvs.add(new KeyValue(AppConstants.RECEIVE_ADDRESS, "湖北"));
// 		String sign = kvs.sign(channel.getMd5Key(), PayConfig.PAY41_CFG.input_charset);
//
// 		result.setSignature(sign);
// 		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
// 		params.add(new BasicNameValuePair(AppConstants.INPUT_CHARSET, PayConfig.PAY41_CFG.input_charset));
//         params.add(new BasicNameValuePair(AppConstants.NOTIFY_URL, notifyUrl));// 异步通知地址
//         params.add(new BasicNameValuePair(AppConstants.RETURN_URL, resultUrl));// 同步通知地址
//         params.add(new BasicNameValuePair(AppConstants.PAY_TYPE, PayConfig.PAY41_CFG.pay_type_wx));// 支付方式
//         params.add(new BasicNameValuePair(AppConstants.BANK_CODE, "WEIXIN"));// 银行编码
//         params.add(new BasicNameValuePair(AppConstants.MERCHANT_CODE, channel.getMerCode()));// 商户号
//         params.add(new BasicNameValuePair(AppConstants.ORDER_NO, billno));// 订单编号
//         params.add(new BasicNameValuePair(AppConstants.ORDER_AMOUNT, act));// 订单金额
//         params.add(new BasicNameValuePair(AppConstants.ORDER_TIME, DateUtils.format(new Date())));// 订单时间
//         params.add(new BasicNameValuePair(AppConstants.PRODUCT_NAME, "ZYCZ"));
//         params.add(new BasicNameValuePair(AppConstants.PRODUCT_NUM, "1"));
//         params.add(new BasicNameValuePair(AppConstants.REQ_REFERER, host));
//         params.add(new BasicNameValuePair(AppConstants.CUSTOMER_IP, ip));
//         params.add(new BasicNameValuePair(AppConstants.CUSTOMER_PHONE, "17000000000"));
//         params.add(new BasicNameValuePair(AppConstants.RETURN_PARAMS, "zy"));
//         params.add(new BasicNameValuePair(AppConstants.SIGN, sign));
//
// 		result.setPayUrl(PayConfig.PAY41_CFG.par_url);
// 		result.setWeChatParam(params);
// 	}
//
//
// 	/**
// 	 * 解析微信支付请求返回数据
// 	 * @param resMap
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static WechatResultEntity weChatRequestResult(Map<String,String> resMap) throws Exception{
// 		WechatResultEntity result = new WechatResultEntity();
// 		return result;
// 	}
//
//
//
// 	/**
// 	 * 支付成功通知
// 	 */
// 	public static RechargePay notify(PaymentChannel channel, Map<String, String> resMap) throws Exception{
// 		try {
// 			String merchantCode = resMap.get(AppConstants.MERCHANT_CODE);
// 	        String notifyType = resMap.get(AppConstants.NOTIFY_TYPE);
// 	        String orderNo = resMap.get(AppConstants.ORDER_NO);
// 	        String orderAmount = resMap.get(AppConstants.ORDER_AMOUNT);
// 	        String orderTime = resMap.get(AppConstants.ORDER_TIME);
// 	        String returnParams = resMap.get(AppConstants.RETURN_PARAMS);
// 	        String tradeNo = resMap.get(AppConstants.TRADE_NO);
// 	        String tradeTime = resMap.get(AppConstants.TRADE_TIME);
// 	        String tradeStatus = resMap.get(AppConstants.TRADE_STATUS);
// 	        String sign = resMap.get(AppConstants.SIGN);
// 	        KeyValues kvs = new KeyValues();
//
// 	        kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, merchantCode));
// 	        kvs.add(new KeyValue(AppConstants.NOTIFY_TYPE, notifyType));
// 	        kvs.add(new KeyValue(AppConstants.ORDER_NO, orderNo));
// 	        kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, orderAmount));
// 	        kvs.add(new KeyValue(AppConstants.ORDER_TIME, orderTime));
// 	        kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, returnParams));
// 	        kvs.add(new KeyValue(AppConstants.TRADE_NO, tradeNo));
// 	        kvs.add(new KeyValue(AppConstants.TRADE_TIME, tradeTime));
// 	        kvs.add(new KeyValue(AppConstants.TRADE_STATUS, tradeStatus));
// 	        String thizSign = kvs.sign(channel.getMd5Key(), PayConfig.PAY41_CFG.input_charset);
// 	        if (thizSign.equalsIgnoreCase(sign) && tradeStatus.equals("success")){
// 	        	RechargePay pay = new RechargePay();
// 	        	pay.setRecvCardNo(merchantCode);//商户号
// 	        	pay.setAmount(Double.parseDouble(orderAmount));//金额
// 	        	pay.setBillno(orderNo);//订单编号
// 	        	pay.setNotifyType(notifyType);//通知方式
// 	        	pay.setTradeNo(tradeNo);//支付订单
// 	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				pay.setPayTime(sdf.parse(tradeTime));//订单通知时间
// 				pay.setTradeStatus(tradeStatus);//支付状态(success 交易成功,failed 交易失败,paying 交易中)
// 	        	return pay;
// 	        }else {
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
// }
