// package lottery.domains.pool.payment.dinpay;
//
//
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.apache.commons.lang.StringUtils;
// import org.apache.http.message.BasicNameValuePair;
//
// import com.itrus.util.sign.RSAWithSoftware;
//
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import net.sf.json.JSONObject;
//
// public class DinPayer {
//
// 	/**
// 	 * 封装支付请求参数
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
// 	public static void prepare(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco,PaymentThrid thridBean,
// 			String notifyUrl, String resultUrl,String host, String ip) {
// 		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 		// 接收表单参数（To receive the parameter）
// 		Date now = new Date();
// 		String service_type = "direct_pay";
// 		String interface_version = "V3.0";
// 		String input_charset = "UTF-8";
// 		String notify_url = notifyUrl;
// 		String order_no = billno;
// 		String order_time = sdf.format(now);
// 		String order_amount = MoneyFormat.fonmatDinpay(Amount);
// 		String product_name = "cz";
// 		String signType= "RSA-S";
// 		String dinpayType = "b2c";
// 		StringBuffer signStr = new StringBuffer();
// 		//签名参数顺序不能乱
// 		if (StringUtils.isNotEmpty(bankco)) {
// 			signStr.append("bank_code=").append(bankco).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(ip)) {
// 			signStr.append("client_ip=").append(ip).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(host)) {
// 			signStr.append("extra_return_param=").append(host).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(input_charset)) {
// 			signStr.append("input_charset=").append(input_charset).append("&");
// 		}
// 		signStr.append("interface_version=").append(interface_version).append("&");
// 		if (StringUtils.isNotEmpty(thridBean.getMerCode())) {
// 			signStr.append("merchant_code=").append(thridBean.getMerCode()).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(notify_url)) {
// 			signStr.append("notify_url=").append(notify_url).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(order_amount)) {
// 			signStr.append("order_amount=").append(order_amount).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(order_no)) {
// 			signStr.append("order_no=").append(order_no).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(order_time)) {
// 			signStr.append("order_time=").append(order_time).append("&");
// 		}
// 		signStr.append("pay_type=").append(dinpayType).append("&");
// 		signStr.append("product_name=").append(product_name).append("&");
// 		if (StringUtils.isNotEmpty(resultUrl)) {
// 			signStr.append("return_url=").append(resultUrl).append("&");
// 		}
// 		signStr.append("service_type=").append(service_type);
//
// 		String signInfo = signStr.toString();
// 		String sign = null;
// 		try {
// 			//商户公钥
// 			sign = RSAWithSoftware.signByPrivateKey(signInfo, PayConfig.DINPAY_CFG.rsa_private_key_pkcs8);
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
//
// 		DinpayEntity entity = new DinpayEntity();
// 		entity.setMerchant_code(thridBean.getMerCode());
// 		entity.setService_type(service_type);
// 		entity.setInterface_version(interface_version);
// 		entity.setSign_type(signType);
// 		entity.setInput_charset(input_charset);
// 		entity.setNotify_url(notify_url);
// 		entity.setOrder_no(order_no);
// 		entity.setOrder_time(order_time);
// 		entity.setOrder_amount(order_amount);
// 		entity.setProduct_name(product_name);
// 		entity.setPay_type(dinpayType);
// 		entity.setSign(sign);
// 		entity.setProduct_code("");
// 		entity.setProduct_desc("");
// 		entity.setProduct_num("");
// 		entity.setShow_url("");
// 		entity.setClient_ip(ip);
// 		entity.setBank_code(bankco);
// 		entity.setRedo_flag("");
// 		entity.setExtend_param("");
// 		entity.setExtra_return_param(host);
// 		entity.setReturn_url(resultUrl);
//
// 		result.setPayUrl(thridBean.getLink()+"?input_charset=UTF-8");
// 		result.setJsonValue(JSONObject.fromObject(entity));
// 	}
//
// 	/**
// 	 * 解析支付结果
// 	 *
// 	 * @param payment
// 	 * @param resMap
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception {
// 		try {
// 			String merchant_code = thridBean.getMerCode();
// 			String notify_type = resMap.get("notify_type");
// 			String notify_id = resMap.get("notify_id");
// 			String dinpaySign = resMap.get("sign");
// 			String order_no = resMap.get("order_no");
// 			String order_time = resMap.get("order_time");
// 			String order_amount = resMap.get("order_amount");
// 			String trade_no = resMap.get("trade_no");
// 			String trade_time = resMap.get("trade_time");
// 			String trade_status = resMap.get("trade_status");
// 			String bank_seq_no = resMap.get("bank_seq_no");
// 			String extra_return_param = resMap.get("extra_return_param");
// 			//签名参数顺序不能乱
// 			StringBuilder signStr = new StringBuilder();
// 			if (null != bank_seq_no && !bank_seq_no.equals("")) {
// 				signStr.append("bank_seq_no=").append(bank_seq_no).append("&");
// 			}
// 			if (null != extra_return_param && !extra_return_param.equals("")) {
// 				signStr.append("extra_return_param=").append(extra_return_param).append("&");
// 			}
// 			signStr.append("interface_version=V3.0").append("&");
// 			signStr.append("merchant_code=").append(merchant_code).append("&");
// 			if (null != notify_id && !notify_id.equals("")) {
// 				signStr.append("notify_id=").append(notify_id).append("&notify_type=").append(notify_type).append("&");
// 			}
// 			signStr.append("order_amount=").append(order_amount).append("&");
// 			signStr.append("order_no=").append(order_no).append("&");
// 			signStr.append("order_time=").append(order_time).append("&");
// 			signStr.append("trade_no=").append(trade_no).append("&");
// 			signStr.append("trade_status=").append(trade_status).append("&");
// 			signStr.append("trade_time=").append(trade_time);
// 			String signInfo = signStr.toString();
// 			//智付公钥
// 			if (RSAWithSoftware.validateSignByPublicKey(signInfo, PayConfig.DINPAY_CFG.dinpay_public_key, dinpaySign)
// 					&& "SUCCESS".equals(trade_status)) {
// 				RechargePay pay = new RechargePay();
// 				pay.setAmount(Double.parseDouble(order_amount));
// 				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				pay.setPayTime(sdf.parse(trade_time));
// 				pay.setRecvName(merchant_code);
// 				pay.setBillno(order_no);
// 				pay.setTradeNo(trade_no);//字符订单号
// 				pay.setRecvCardNo(merchant_code);
// 				pay.setNotifyType(notify_type);
// 				pay.setTradeStatus(trade_status);
// 				return pay;
// 			} else {
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
//
// 	/**
// 	 * 解析页面跳转结果
// 	 *
// 	 * @param payment
// 	 * @param resMap
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static RechargePay result(PaymentThrid thridBean, Map<String, String> resMap) throws Exception {
// 		try {
// 			String merchant_code = thridBean.getMerCode();
// 			String notify_type = resMap.get("notify_type");
// 			String notify_id = resMap.get("notify_id");
// 			String dinpaySign = resMap.get("sign");
// 			String order_no = resMap.get("order_no");
// 			String order_time = resMap.get("order_time");
// 			String order_amount = resMap.get("order_amount");
// 			String trade_no = resMap.get("trade_no");
// 			String trade_time = resMap.get("trade_time");
// 			String trade_status = resMap.get("trade_status");
// 			String bank_seq_no = resMap.get("bank_seq_no");
// 			String extra_return_param = resMap.get("extra_return_param");
// 			StringBuilder signStr = new StringBuilder();
// 			//签名参数顺序不能乱
// 			if (null != bank_seq_no && !bank_seq_no.equals("")) {
// 				signStr.append("bank_seq_no=").append(bank_seq_no).append("&");
// 			}
// 			if (null != extra_return_param && !extra_return_param.equals("")) {
// 				signStr.append("extra_return_param=").append(extra_return_param).append("&");
// 			}
// 			signStr.append("interface_version=V3.0").append("&");
// 			signStr.append("merchant_code=").append(merchant_code).append("&");
// 			if (null != notify_id && !notify_id.equals("")) {
// 				signStr.append("notify_id=").append(notify_id).append("&notify_type=").append(notify_type).append("&");
// 			}
// 			signStr.append("order_amount=").append(order_amount).append("&");
// 			signStr.append("order_no=").append(order_no).append("&");
// 			signStr.append("order_time=").append(order_time).append("&");
// 			signStr.append("trade_no=").append(trade_no).append("&");
// 			signStr.append("trade_status=").append(trade_status).append("&");
// 			signStr.append("trade_time=").append(trade_time);
// 			String signInfo = signStr.toString();
// 			//智付公钥
// 			if (RSAWithSoftware.validateSignByPublicKey(signInfo, PayConfig.DINPAY_CFG.dinpay_public_key, dinpaySign)
// 					&& "SUCCESS".equals(trade_status)) {
// 				RechargePay pay = new RechargePay();
// 				pay.setAmount(Double.parseDouble(order_amount));
// 				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				pay.setPayTime(sdf.parse(trade_time));
// 				pay.setRecvName(merchant_code);
// 				pay.setBillno(order_no);//自己的订单号
// 				pay.setRecvCardNo(merchant_code);
// 				pay.setTradeNo(trade_no);//支付订单
// 				pay.setRequestHost(extra_return_param);
// 				pay.setNotifyType(notify_type);
// 				pay.setTradeStatus(trade_status);
// 				return pay;
// 			} else {
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
//
// 	/**
// 	 * 封装 微信  支付请求参数
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
// 	public static void prepareWeiXin(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco,PaymentThrid thridBean,
// 			String notifyUrl, String resultUrl,String host, String ip){
// 		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 		// 接收表单参数（To receive the parameter）
// 		Date now = new Date();
// 		String service_type = "wxpay";
// 		String interface_version = "V3.0";
// 		String notify_url = notifyUrl;
// 		String order_no = billno;
// 		String order_time = sdf.format(now);
// 		String order_amount = MoneyFormat.fonmatDinpay(Amount);
// 		String product_name = "cz";
// 		String signType= "RSA-S";
// 		//签名参数顺序不能乱
// 		StringBuffer signStr = new StringBuffer();
// 		signStr.append("interface_version=").append(interface_version).append("&");
// 		if (StringUtils.isNotEmpty(thridBean.getMerCode())) {
// 			signStr.append("merchant_code=").append(thridBean.getMerCode()).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(notify_url)) {
// 			signStr.append("notify_url=").append(notify_url).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(order_amount)) {
// 			signStr.append("order_amount=").append(order_amount).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(order_no)) {
// 			signStr.append("order_no=").append(order_no).append("&");
// 		}
// 		if (StringUtils.isNotEmpty(order_time)) {
// 			signStr.append("order_time=").append(order_time).append("&");
// 		}
// 		signStr.append("product_name=").append(product_name).append("&");
// 		signStr.append("service_type=").append(service_type);
// 		String signInfo = signStr.toString();
// 		String sign = null;
// 		try {
// 			//商户公钥
// 			sign = RSAWithSoftware.signByPrivateKey(signInfo, PayConfig.DINPAY_CFG.rsa_private_key_pkcs8);
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
//
// //		WechatPayEntity entity = new WechatPayEntity();
// //		entity.setMerchant_code(thridBean.getMerCode());
// //		entity.setService_type(service_type);
// //		entity.setNotify_url(notify_url);
// //		entity.setInterface_version(interface_version);
// //		entity.setSign_type(signType);
// //		entity.setSign(sign);
// //		entity.setOrder_no(order_no);
// //		entity.setOrder_time(order_time);
// //		entity.setOrder_amount(order_amount);
// //		entity.setProduct_name(product_name);
// //		entity.setProduct_code("");
// //		entity.setProduct_num("");
// //		entity.setProduct_desc("");
// //		entity.setExtra_return_param("");
// //		entity.setExtend_param("");
//
// 		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//
// 		params.add(new BasicNameValuePair("merchant_code", thridBean.getMerCode()));
//         params.add(new BasicNameValuePair("service_type", service_type));
//         params.add(new BasicNameValuePair("notify_url", notify_url));
//         params.add(new BasicNameValuePair("interface_version", interface_version));
//         params.add(new BasicNameValuePair("sign_type", signType));
//         params.add(new BasicNameValuePair("sign",sign));
//         params.add(new BasicNameValuePair("order_no", order_no));
//         params.add(new BasicNameValuePair("order_time", order_time));
//         params.add(new BasicNameValuePair("order_amount",order_amount));
//         params.add(new BasicNameValuePair("product_name",product_name));
//         params.add(new BasicNameValuePair("product_code",""));
//         params.add(new BasicNameValuePair("product_num",""));
//         params.add(new BasicNameValuePair("product_desc",""));
//         params.add(new BasicNameValuePair("extra_return_param",""));
//         params.add(new BasicNameValuePair("extend_param",""));
//
// 		result.setPayUrl(thridBean.getLink());
// 		result.setWeChatParam(params);
// 	}
//
//
//
// 	/**
// 	 * 微信支付请求响应结果解析
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static WechatResultEntity weChatRequestResult(Map<String,String> resMap) throws Exception{
//
// 		String resp_code = resMap.get("resp_code");
// 		String resp_desc = resMap.get("resp_desc");
// 		String sign = resMap.get("sign");
// 		if(null != sign && !"null".equals(sign)){
// 			if("SUCCESS".equals(resp_code)){
// 				String qrcode = resMap.get("qrcode");
// 				StringBuilder signStr = new StringBuilder();
// 				signStr.append("resp_code=").append(resp_code).append("&");
// 				signStr.append("resp_desc=").append(resp_desc).append("&");
// 				signStr.append("qrcode=").append(qrcode);
// 				if (RSAWithSoftware.validateSignByPublicKey(signStr.toString(),
// 						PayConfig.DINPAY_CFG.dinpay_public_key, sign)){
// 					WechatResultEntity result = new WechatResultEntity();
// 					result.setPayResult("SUCCESS");
// 					result.setQrcode(qrcode);
// 					result.setResp_code(resp_code);
// 					result.setResp_desc(resp_desc);
// 					return result;
// 				}else{
// 					WechatResultEntity result = new WechatResultEntity();
// 					result.setPayResult("checkError");
// 					return result;
// 				}
// 			}else{
// 				StringBuilder signStr = new StringBuilder();
// 				signStr.append("resp_code=").append(resp_code).append("&");
// 				signStr.append("resp_desc=").append(resp_desc).append("&");
// 				if (RSAWithSoftware.validateSignByPublicKey(signStr.toString(),
// 						PayConfig.DINPAY_CFG.dinpay_public_key, sign)){
// 					WechatResultEntity result = new WechatResultEntity();
// 					result.setPayResult("ERROR");
// 					result.setResp_code(resp_code);
// 					result.setResp_desc(resp_desc);
// 					return result;
// 				}else{
// 					WechatResultEntity result = new WechatResultEntity();
// 					result.setPayResult("checkError");
// 					return result;
// 				}
// 			}
// 		}else{
// 			WechatResultEntity result = new WechatResultEntity();
// 			result.setPayResult("checkError");
// 			return result;
// 		}
// 	}
//
// //	/**
// //	 * 解析 微信支付结果
// //	 *
// //	 * @param payment
// //	 * @param resMap
// //	 * @return
// //	 * @throws Exception
// //	 */
// //	public static RechargePay notifyWechat(PaymentThrid thridBean, Map<String, String> resMap) throws Exception {
// //		String merchant_code = thridBean.getMerCode();
// //		String notify_type = resMap.get("notify_type");
// //		String notify_id = resMap.get("notify_id");
// //		String dinpaySign = resMap.get("sign");
// //		String order_no = resMap.get("order_no");
// //		String order_time = resMap.get("order_time");
// //		String order_amount = resMap.get("order_amount");
// //		String extra_return_param = resMap.get("extra_return_param");
// //		String trade_no = resMap.get("trade_no");
// //		String trade_time = resMap.get("trade_time");
// //		String trade_status = resMap.get("trade_status");
// //		String bank_seq_no = resMap.get("bank_seq_no");
// //
// //		//签名参数顺序不能乱
// //		StringBuilder signStr = new StringBuilder();
// //		if (null != bank_seq_no && !bank_seq_no.equals("")) {
// //			signStr.append("bank_seq_no=").append(bank_seq_no).append("&");
// //		}
// //		if (null != extra_return_param && !extra_return_param.equals("")) {
// //			signStr.append("extra_return_param=").append(extra_return_param).append("&");
// //		}
// //		signStr.append("interface_version=V3.0").append("&");
// //		signStr.append("merchant_code=").append(merchant_code).append("&");
// //		if (null != notify_id && !notify_id.equals("")) {
// //			signStr.append("notify_id=").append(notify_id).append("&notify_type=").append(notify_type).append("&");
// //		}
// //		signStr.append("order_amount=").append(order_amount).append("&");
// //		signStr.append("order_no=").append(order_no).append("&");
// //		signStr.append("order_time=").append(order_time).append("&");
// //		signStr.append("trade_no=").append(trade_no).append("&");
// //		signStr.append("trade_status=").append(trade_status).append("&");
// //		signStr.append("trade_time=").append(trade_time);
// //		String signInfo = signStr.toString();
// //		//智付公钥
// //		if (RSAWithSoftware.validateSignByPublicKey(signInfo, PayConfig.DINPAY_CFG.dinpay_public_key, dinpaySign)
// //				&& "SUCCESS".equals(trade_status)) {
// //			RechargePay pay = new RechargePay();
// //			pay.setAmount(Double.parseDouble(order_amount));
// //			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// //			pay.setPayTime(sdf.parse(trade_time));
// //			pay.setRecvName(merchant_code);
// //			pay.setBillno(order_no);
// //			pay.setRecvCardNo(trade_no);
// //			pay.setNotifyType(notify_type);
// //			return pay;
// //		} else {
// //			return null;
// //		}
// //	}
//
// 	public static void main(String[] args) {
// 		PaymentThrid thridBean = new PaymentThrid();
// 		thridBean.setId(7);
// 		thridBean.setName("新秋龙");
// 		thridBean.setMerCode("2000600283");
// 		thridBean.setMerKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCB95IYcuqImkBn6YT3Ez94p3q5/IqYwyEXWZmv/RtYhyhH0OVSyCjTAMjpSVpTkrkc5pzmjDh1NsLW1eTv+3NunCcVYCUjA5TM/tmqHagyJSu1NXSNz0Bh4GyGgyOHX1swXKUKzZFplRrQXv4mfUR96w37i87/B6TVVWdVjFFcnwIDAQAB ");
//
// 		Map<String, String> resMap = new HashMap<>();
// 		resMap.put("trade_no", "1238458012");
// 		resMap.put("extra_return_param", "http://www.yx888.info");
// 		resMap.put("sign_type", "RSA-S");
// 		resMap.put("notify_type", "page_notify");
// 		resMap.put("reqBody", "");
// 		resMap.put("merchant_code", "2000600283");
// 		resMap.put("order_no", "20160905815454812122");
// 		resMap.put("trade_status", "SUCCESS");
// 		resMap.put("sign", "Gv+Lj2rfq/SQdAd7td0mO+S4Zg19urLW11eSQKeDVGJJlu9I2+4hsUiF8vZ1S2FHoSYn/d/8CsmN7dYj5gBxPRstF3aXrXoR6ScMLV4PsDD5aFZmXypu/SnXdDzCWA/rD06z2Zu2JxHuFSU4yzMA0PGiC1wcAyQK+3rYjlozsUM=");
// 		resMap.put("order_amount", "1");
// 		resMap.put("interface_version", "V3.0");
// 		resMap.put("bank_seq_no", "HFG000008126130819");
// 		resMap.put("order_time", "2016-09-05 21:25:42");
// 		resMap.put("notify_id", "37db3cd6aa1c45599f34d0fdbe99937e");
// 		resMap.put("trade_time", "2016-09-05 21:25:38");
//
// 		try {
// 			RechargePay result = result(thridBean, resMap);
// 			System.out.println(result.getRequestHost());
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 	}
//
// }
