// package lottery.domains.pool.payment.xunbao;
//
// import com.alibaba.fastjson.JSON;
// import javautils.date.Moment;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  * 讯宝支付接口
//  *
//  */
// public class XUNBAOPayment {
// 	private static final String VERITY_MD5_KEY = "d123f$tkd40faada23215a12123oo5@kda0d(9id";
// 	private static final Logger log = LoggerFactory.getLogger(XUNBAOPayment.class);
//
// 	public static void prepare(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("parter", thridBean.getMerCode()); // 商户id，由讯宝商务分配
// 		params.put("type", bankco); // 银行类型，具体请参考附录1
// 		params.put("value", act); // 单位元（人民币），2位小数，最小支付金额为0.02
// 		params.put("orderid", billno); // 商户系统订单号，该订单号将作为讯宝商务接口的返回数据。该值需在商户系统内唯一，讯宝商务系统暂时不检查该值是否唯一
// 		params.put("callbackurl", notifyUrl); // 下行异步通知过程的返回地址，需要以http://开头且没有任何参数
// 		params.put("hrefbackurl", ""); // 下行同步通知过程的返回地址(在支付完成后讯宝商务接口将会跳转到的商户系统连接地址)。 注：若提交值无该参数，或者该参数值为空，则在支付完成后，讯宝商务接口将不会跳转到商户系统，用户将停留在讯宝商务接口系统提示支付成功的页面
// 		params.put("payerIp", ip); // 支付用户IP 用户在下单时的真实IP，讯宝商务接口将会判断玩家支付时的ip和该值是否相同。若不相同，讯宝商务接口将提示用户支付风险
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, billno); // PayConfig.MKT.MD5_SECRET用的临时值，随时换，但要保证notify方法一致，
// 		params.put("attach", cusSign); // 备注信息，下行中会原样返回。若该值包含中文，请注意编码
//
// 		String md5Sign = "parter=%s&type=%s&value=%s&orderid=%s&callbackurl=%s%s";
// 		md5Sign = String.format(md5Sign, thridBean.getMerCode(), bankco, act, billno, notifyUrl, thridBean.getMerKey());
// 		md5Sign = DigestUtils.md5Hex(md5Sign);
//
// 		params.put("sign", md5Sign); // 32位小写MD5签名值，GB2312编码
//
// 		result.setPayUrl(PayConfig.XUNBAO.GATEWAY);
// 		result.setParamsMap(params);
// 	}
//
// 	public static void prepareAlipay(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("parter", thridBean.getMerCode()); // 商户id，由讯宝商务分配
// 		params.put("type", "8012"); // 银行类型，具体请参考附录1
// 		params.put("value", act); // 单位元（人民币），2位小数，最小支付金额为0.02
// 		params.put("orderid", billno); // 商户系统订单号，该订单号将作为讯宝商务接口的返回数据。该值需在商户系统内唯一，讯宝商务系统暂时不检查该值是否唯一
// 		params.put("callbackurl", notifyUrl); // 下行异步通知过程的返回地址，需要以http://开头且没有任何参数
// 		params.put("hrefbackurl", ""); // 下行同步通知过程的返回地址(在支付完成后讯宝商务接口将会跳转到的商户系统连接地址)。 注：若提交值无该参数，或者该参数值为空，则在支付完成后，讯宝商务接口将不会跳转到商户系统，用户将停留在讯宝商务接口系统提示支付成功的页面
// 		params.put("payerIp", ip); // 支付用户IP 用户在下单时的真实IP，讯宝商务接口将会判断玩家支付时的ip和该值是否相同。若不相同，讯宝商务接口将提示用户支付风险
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, billno);
// 		params.put("attach", cusSign); // 备注信息，下行中会原样返回。若该值包含中文，请注意编码
//
// 		String md5Sign = "parter=%s&type=%s&value=%s&orderid=%s&callbackurl=%s%s";
// 		md5Sign = String.format(md5Sign, thridBean.getMerCode(), "8012", act, billno, notifyUrl, thridBean.getMerKey());
// 		md5Sign = DigestUtils.md5Hex(md5Sign);
//
// 		params.put("sign", md5Sign); // 32位小写MD5签名值，GB2312编码
//
// 		result.setPayUrl(PayConfig.XUNBAO.GATEWAY);
// 		result.setParamsMap(params);
// 	}
//
//
// 	public static void prepareQQ(RechargeResult result,String payType, String billno,
// 									 String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 									 String ip) {
// 		String act = MoneyFormat.FormatPay41(Amount);
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("parter", thridBean.getMerCode()); // 商户id，由讯宝商务分配
// 		params.put("type", "993"); // 银行类型，具体请参考附录1
// 		params.put("value", act); // 单位元（人民币），2位小数，最小支付金额为0.02
// 		params.put("orderid", billno); // 商户系统订单号，该订单号将作为讯宝商务接口的返回数据。该值需在商户系统内唯一，讯宝商务系统暂时不检查该值是否唯一
// 		params.put("callbackurl", notifyUrl); // 下行异步通知过程的返回地址，需要以http://开头且没有任何参数
// 		params.put("hrefbackurl", ""); // 下行同步通知过程的返回地址(在支付完成后讯宝商务接口将会跳转到的商户系统连接地址)。 注：若提交值无该参数，或者该参数值为空，则在支付完成后，讯宝商务接口将不会跳转到商户系统，用户将停留在讯宝商务接口系统提示支付成功的页面
// 		params.put("payerIp", ip); // 支付用户IP 用户在下单时的真实IP，讯宝商务接口将会判断玩家支付时的ip和该值是否相同。若不相同，讯宝商务接口将提示用户支付风险
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, billno);
// 		params.put("attach", cusSign); // 备注信息，下行中会原样返回。若该值包含中文，请注意编码
//
// 		String sign = "parter=%s&type=%s&value=%s&orderid=%s&callbackurl=%s%s";
// 		sign = String.format(sign, thridBean.getMerCode(), "993", act, billno, notifyUrl, thridBean.getMerKey());
// 		sign = DigestUtils.md5Hex(sign);
//
// 		params.put("sign", sign); // 32位小写MD5签名值，GB2312编码
//
// 		result.setPayUrl(PayConfig.XUNBAO.GATEWAY);
// 		result.setParamsMap(params);
// 	}
//
// 	/**
// 	 * 支付成功通知
// 	 * @param thridBean
// 	 * @param resMap
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
// 		try {
// 			String orderid = resMap.get("orderid"); // 商户订单号 上行过程中商户系统传入的orderid。
// 			String opstate = resMap.get("opstate"); // 订单结果 0：支付成功 -1 请求参数无效 -2 签名错误
// 			String ovalue = resMap.get("ovalue"); // 订单金额 订单实际支付金额，单位元
// 			String sign = resMap.get("sign"); // MD5签名 32位小写MD5签名值，GB2312编码
// 			String sysorderid = resMap.get("sysorderid"); // 此次订单过程中讯宝商务接口系统内的订单Id
// 			String systime = resMap.get("systime"); // 讯宝商务订单时间 此次订单过程中讯宝商务接口系统内的订单结束时间。格式为 年/月/日 时：分：秒，如2010/04/05 21:50:58
// 			String attach = resMap.get("attach"); // 备注信息，上行中attach原样返回
// 			String msg = resMap.get("msg"); // 订单结果说明
//
// 			String serverSign = "orderid=%s&opstate=%s&ovalue=%s&time=%s&sysorderid=%s%s";
// 			serverSign = String.format(serverSign, orderid, opstate, ovalue, systime, sysorderid, thridBean.getMerKey());
// 			serverSign = DigestUtils.md5Hex(serverSign);
//
// 			if (serverSign.equalsIgnoreCase(sign) && opstate.equals("0")){
//
//
// 				// 自定义签名验证
// 				String cusSign = getCusSign(thridBean.getMerCode(), PayConfig.MKT.MD5_SECRET, orderid);
// 				if (cusSign.equals(attach)) {
// 					RechargePay pay = new RechargePay();
// 					pay.setAmount(Double.parseDouble(ovalue));//金额
// 					pay.setBillno(orderid);//订单编号
// 					pay.setTradeNo(sysorderid);//支付订单
// 					pay.setPayTime(new Moment().fromTime(systime, "yyyy/MM/dd HH:mm:ss").toDate());//订单通知时间
// 					pay.setTradeStatus(opstate);//支付状态(0：支付成功 -1 请求参数无效 -2 签名错误)
// 					return pay;
// 				}
// 				else {
// 					log.error("讯宝回调自定义验证失败，服务器验证：" + cusSign + ",请求信息：" + JSON.toJSONString(resMap));
// 				}
// 			}else {
// 				log.error("讯宝回调验证失败，服务器验证：" + serverSign + ",请求信息：" + JSON.toJSONString(resMap));
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			log.error("讯宝回调发生异常", e);
// 		}
// 		return null;
// 	}
//
// 	private static String getCusSign(String merCode, String merKey, String billno) {
// 		return DigestUtils.md5Hex(merCode + merKey + billno + VERITY_MD5_KEY);
// 	}
// }
