//package lottery.domains.content.payment.gst;
//
//import admin.web.WebJSONObject;
//import com.alibaba.fastjson.JSON;
//import javautils.http.ToUrlParamUtils;
//import lottery.domains.content.entity.PaymentChannel;
//import lottery.domains.content.payment.mkt.KeyValue;
//import lottery.domains.content.payment.mkt.KeyValues;
//import lottery.domains.content.payment.utils.MoneyFormat;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.TreeMap;
//
///**
// * 国盛通
// */
//@Component
//public class GSTPayment {
//	private static final Logger log = LoggerFactory.getLogger(GSTPayment.class);
//
//	@Value("${gst.daifu.url}")
//	private String daifuUrl;
//	@Value("${gst.daifu.queryurl}")
//	private String daifuQueryUrl;
//
//	private String md5Key = "";
//
//	@Autowired
//	private RestTemplate restTemplate;
//
//	/**
//	 * 代付接口，返回第三方的注单ID
//	 * @param money 代付金额，元，最多2位小数
//	 * @param billno 代付订单号
//	 * @param bitchNo 批次号
//	 * @param bankCode 银行编码
//	 * @param operatorTime 操作时间
//	 * @param name 用户姓名
//	 * @param card 卡号
//	 * @param remarks 备注
//	 */
//	public String daifu(WebJSONObject json, PaymentChannel channel, double money, String billno, String bitchNo,
//						String bankCode, String operatorTime, String name, String card, String remarks) {
//		try {
//			log.debug("开始国盛通代付,注单ID:{},姓名:{},卡号:{}", billno, name, card);
//			return daifuInternel(json, channel, money, billno, bitchNo, bankCode, operatorTime, name, card, remarks);
//		} catch (Exception e) {
//			log.error("国盛通代付发生异常", e);
//			json.set(2, "2-4000");
//			return null;
//		}
//	}
//
//	/**
//	 * 代付查询
//	 * @param json
//	 * @param billno 订单
//	 * @param operatorTime 操作时间
//	 * @return
//	 */
//	public String daifuQuery(WebJSONObject json, PaymentChannel channel, String billno, String operatorTime) {
//		try {
//			return daifuQueryInternel(json, channel, billno, operatorTime);
//		} catch (Exception e) {
//			log.error("国盛通代付发生异常", e);
//			json.set(2, "2-4000");
//			return null;
//		}
//	}
//
//	private String daifuInternel(WebJSONObject json, PaymentChannel channel, double money, String billno, String bitchNo,
//						 String bankCode, String operatorTime, String name, String card, String remarks) {
//		String amount = MoneyFormat.FormatPay41(money+"");
//
//		KeyValues kvs = new KeyValues();
//		kvs.add(new KeyValue("input_charset", "UTF-8")); // 同支付接口的参数字符集编码
//		kvs.add(new KeyValue("merchant_code", channel.getMerCode()));// 异步通知地址
//		kvs.add(new KeyValue("amount", amount));// 总金额单位为元（支持两位小数）
//		kvs.add(new KeyValue("transid", billno));// 平台里唯一的订单号
//		kvs.add(new KeyValue("bitch_no", bitchNo));// 付款的批次号
//		kvs.add(new KeyValue("currentDate", operatorTime));// 字符串格式要求为：yyyy-MM-dd HH:mm:ss 例如：2015-01-01 12:45:52
//		kvs.add(new KeyValue("bank_name", bankCode));// 银行编码
//		// String accountName;
//		// try {
//		// 	accountName = URLEncoder.encode(name, "UTF-8");
//		// } catch (UnsupportedEncodingException e) {
//		// 	log.error("国盛通代付发生异常，URLEncoder.encode(" + name + ")时出错", e);
//		// 	json.set(2, "2-4003");
//		// 	return null;
//		// }
//		// if (StringUtils.isEmpty(accountName)) {
//		// 	log.error("国盛通代付失败,accountName编码后为空，传进来的值：" + name);
//		// 	json.set(2, "2-4004");
//		// 	return null;
//		// }
//		if (remarks != null && remarks.length() > 200) {
//			log.error("国盛通代付失败,remarks长度超过200");
//			json.set(2, "2-4005");
//			return null;
//		}
//		kvs.add(new KeyValue("account_name", name));// 收款人姓名 URLEncoder.encode方法进行编码并指定编码格式
//		kvs.add(new KeyValue("account_number", card));// 收款卡号
//		kvs.add(new KeyValue("remark", remarks));// 备注
//
//		// 代付和代付查询接口，先加密一次，结果转大写在加密两次（两次加密都不用转大写）
//		String sign = kvs.sign("", "UTF-8");
//		sign = DigestUtils.md5Hex(sign.toUpperCase());
//		sign = DigestUtils.md5Hex(sign);
//
//		Map<String, String> params = new TreeMap<>();
//		params.put("input_charset", "UTF-8");// 同支付接口的参数字符集编码
//		params.put("merchant_code", md5Key);// 商户注册签约后，支付平台分配的唯一标识号
//		params.put("sign", sign);// 签名数据
//		params.put("amount", amount);// 总金额单位为元（支持两位小数）
//		params.put("transid", billno);// 平台里唯一的订单号
//		params.put("bitch_no", bitchNo);// 付款的批次号
//		params.put("currentDate", operatorTime);// 字符串格式要求为：yyyy-MM-dd HH:mm:ss 例如：2015-01-01 12:45:52
//		params.put("bank_name", bankCode);// 银行编码
//		params.put("account_name", name);// URLEncoder.encode方法进行编码并指定编码格式
//		params.put("account_number", card);
//		params.put("remark", remarks); // String(200)
//
//		return postAndResolveRetStr(json, daifuUrl, channel, params);
//	}
//
//	private String daifuQueryInternel(WebJSONObject json, PaymentChannel channel, String billno,String operatorTime) {
//		KeyValues kvs = new KeyValues();
//		kvs.add(new KeyValue("input_charset", "UTF-8")); // 同支付接口的参数字符集编码
//		kvs.add(new KeyValue("merchant_code", channel.getMerCode()));// 异步通知地址
//		kvs.add(new KeyValue("currentDate", operatorTime));// 字符串格式要求为：yyyy-MM-dd HH:mm:ss 例如：2015-01-01 12:45:52
//		kvs.add(new KeyValue("order_id", billno));// 平台里唯一的订单号
//		String sign = kvs.sign(md5Key, "UTF-8");
//
//		// 代付和代付查询接口，先加密一次，结果转大写在加密两次（两次加密都不用转大写）
//		sign = DigestUtils.md5Hex(sign.toUpperCase());
//		sign = DigestUtils.md5Hex(sign);
//
//		Map<String, String> params = new HashMap<>();
//		params.put("input_charset", "UTF-8");// 同支付接口的参数字符集编码
//		params.put("merchant_code", channel.getMerCode());// 商户注册签约后，支付平台分配的唯一标识号
//		params.put("sign", sign); // 签名数据
//		params.put("currentDate", operatorTime);// 字符串格式要求为：yyyy-MM-dd HH:mm:ss 例如：2015-01-01 12:45:52
//		params.put("order_id", billno);// 平台里唯一的订单号
//
//		return postAndResolveRetStr(json, daifuQueryUrl, channel, params);
//	}
//
//	private String postAndResolveRetStr(WebJSONObject json, String url, PaymentChannel channel, Map<String, String> params) {
//		String retStr;
//		try {
//			String paramsUrl = ToUrlParamUtils.toUrlParam(params);
//			String _url = url + "?_=" + System.currentTimeMillis() + "&" + paramsUrl;
//			retStr = restTemplate.postForObject(_url, null, String.class);
//
//			// String paramsUrl = ToUrlParamUtils.toUrlParam(params);
//			// String _url = url + "?_=" + System.currentTimeMillis();
//			// retStr = WebUtil.doPost(_url, params, "UTF-8", 6000, 30000); // restTemplate.getForObject(_url, String.class);
//			log.debug("国盛通代付请求返回{}", retStr);
//		} catch (Exception e) {
//			log.error("国盛通代付请求失败，发生连接异常", e);
//			json.set(2, "2-4001");
//			return null;
//		}
//
//		if (StringUtils.isEmpty(retStr)) {
//			log.error("国盛通代付请求失败，发送请求后返回空数据");
//			json.set(2, "2-4006");
//			return null;
//		}
//
//		Map<String, Object> retMap = JSON.parseObject(retStr, HashMap.class);
//		if (retMap == null || retMap.isEmpty()) {
//			log.error("国盛通代付请求失败，解析返回数据失败，返回数据为：" + retStr);
//			json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
//			return null;
//		}
//
//		Object is_success = retMap.get("is_success"); // 仅表示提交数据是否成功，取值为：TRUE、FALSE
//		if (is_success != null && !Boolean.valueOf(is_success.toString())) {
//			log.error("国盛通代付请求失败，" + retStr);
//			json.setWithParams(2, "2-4002", retMap.get("errror_msg").toString());
//			return null;
//		}
//
//		KeyValues kvs = new KeyValues();
//		kvs.add(new KeyValue("is_success", retMap.get("is_success").toString()));
//		kvs.add(new KeyValue("errror_msg", retMap.get("errror_msg").toString()));
//		kvs.add(new KeyValue("transid", retMap.get("transid").toString()));
//		kvs.add(new KeyValue("order_id", retMap.get("order_id").toString()));
//		kvs.add(new KeyValue("bank_status", retMap.get("bank_status").toString()));
//		String serverRetSign = kvs.sign(md5Key, "UTF-8");
//
//		// 代付和代付查询接口，先加密一次，结果转大写在加密两次（两次加密都不用转大写）
//		serverRetSign = DigestUtils.md5Hex(serverRetSign.toUpperCase());
//		serverRetSign = DigestUtils.md5Hex(serverRetSign);
//
//		String retSign = retMap.get("sign").toString();
//		retMap.remove("sign");
//
//		String transid = retMap.get("transid").toString(); // 商户系统中的唯一订单号
//		String order_id = retMap.get("order_id").toString(); // 支付平台里唯一的订单号
//		String bank_status = retMap.get("bank_status").toString(); // 0未处理，1银行处理中 2 已打款 3 失败
//
//		if (StringUtils.isEmpty(order_id)) {
//			log.error("国盛通代付请求成功，但返回空订单号：" + retStr);
//			json.set(2, "2-4014");
//			return null;
//		}
//
//		if (!serverRetSign.equals(retSign)) {
//			log.error("国盛通代付请求成功，返回数据验签不对，服务器验签：" + serverRetSign+"，返回数据为：" + retStr);
//			json.set(2, "2-4008");
//			return order_id;
//		}
//
//		if ("1".equals(bank_status) || "2".equals(bank_status)) {
//			return order_id;
//		}
//		if ("0".equals(bank_status)) {
//			log.error("国盛通代付请求成功，但银行未处理:" + retStr);
//			json.set(2, "2-4009");
//			return order_id;
//		}
//		if ("3".equals(bank_status)) {
//			log.error("国盛通代付请求成功，但银行处理失败:" + retStr);
//			json.set(2, "2-4010");
//			return order_id;
//		}
//
//		log.error("国盛通代付请求成功，出现未知银行状态:" + retStr);
//		json.setWithParams(2, "2-4013", bank_status);
//		return order_id;
//	}
//}
