// package lottery.domains.pool.payment.quickpay;
//
// import com.alibaba.fastjson.JSON;
// import javautils.date.Moment;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.apache.commons.lang.RandomStringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  *闪付
//  *
//  */
// public class QuickPayPayment {
// 	private static final Logger log = LoggerFactory.getLogger(QuickPayPayment.class);
// 	private static final String VEFITY_MD5_KEY = "4Few918ffkazkio$891kfj";
//
// 	/**
// 	 * 网银封装
// 	 */
// 	public static void prepare(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String OrderMoney = String.valueOf(MoneyFormat.yuanToFenMoney(Amount));
//
// 		String TradeDate = new Moment().format("yyyyMMddHHmmss");
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("MemberID", thridBean.getMerCode()); // 商户 ID,商户签约后通过邮件或者其他方式给予商户
// 		params.put("TerminalID", thridBean.getMerKey()); // 终端 ID,商户签约后通过邮件或者其他方式给予商户
// 		params.put("InterfaceVersion", "4.0"); // 接口版本,现在版本号为固定 4.0
// 		params.put("KeyType", "1"); // 加密类型,固定数值：1
// 		params.put("PayID", bankco); // 银行编码，见底部，留空表示收银台
// 		params.put("TradeDate", TradeDate); // 订单日期 14 位定长。 格式：年年年年月月日日时时分分秒秒,例如 2016010112121818
// 		params.put("TransID", billno); // 订单号,唯一订单号，5-32位 位字母和数字 闪付将以此作为结算的唯一凭证.每日内唯一。
// 		params.put("OrderMoney", OrderMoney); // 订单金额,单位：分，例：1 元=100 分
// 		params.put("ProductName", RandomStringUtils.random(6, true, true)); // 商品名称,须 URL 编码，长度不超过 64 位
// 		params.put("Amount", "1"); // 商品数量,无特殊需求则设置为 1
// 		params.put("Username", RandomStringUtils.random(6, true, true)); // 用户名称,须 URL 编码,长度不超过 64 位
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
// 		params.put("AdditionalInfo", cusSign); // 附加字段,须 URL 编码，长度不超过 128 位
// 		params.put("NoticeType", "1"); // 通知类型,固定数字：1
// 		params.put("PageUrl", resultUrl); // 页面返回地址,URL 编码 该地址通知服务不可以用作交易处理
// 		params.put("ReturnUrl", notifyUrl); // 交易通知地址,须 URL 编码，长度不超过 255 位 闪付后台异步通知，在此交易结果处理 不可使用页面通知地址作为交易通知地址
//
// 		// MD5参数拼接规则
// 		// String md5Str = "MerchantID|PayID|TradeDate|TransID|OrderMoney|PageUrl|ReturnUrl|NoticeType|密钥";
// 		String md5Str = "%s|%s|%s|%s|%s|%s|%s|%s|%s";
//
// 		md5Str = String.format(md5Str,
// 				params.get("MemberID"),
// 				params.get("PayID"),
// 				params.get("TradeDate"),
// 				params.get("TransID"),
// 				params.get("OrderMoney"),
// 				params.get("PageUrl"),
// 				params.get("ReturnUrl"),
// 				params.get("NoticeType"),
// 				PayConfig.QUICKPAY.MD5_SECRET);
// 		md5Str = DigestUtils.md5Hex(md5Str);
//
// 		params.put("Md5Sign", md5Str); // 将交易关键数据进行拼接，经 MD5 加密生成 并转化为小写的 32 位加密字符串。
// 		result.setPayUrl(thridBean.getLink());
// 		result.setParamsMap(params);
// 	}
//
// 	/**
// 	 * 支付宝封装
// 	 */
// 	public static void prepareAlipay(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		String OrderMoney = String.valueOf(MoneyFormat.yuanToFenMoney(Amount));
//
// 		String TradeDate = new Moment().format("yyyyMMddHHmmss");
//
// 		HashMap<String, String> params = new HashMap<>();
// 		params.put("MemberID", thridBean.getMerCode()); // 商户 ID,商户签约后通过邮件或者其他方式给予商户
// 		params.put("TerminalID", thridBean.getMerKey()); // 终端 ID,商户签约后通过邮件或者其他方式给予商户
// 		params.put("InterfaceVersion", "4.0"); // 接口版本,现在版本号为固定 4.0
// 		params.put("KeyType", "1"); // 加密类型,固定数值：1
// 		params.put("PayID", "758"); // 银行编码，见底部，留空表示收银台
// 		params.put("TradeDate", TradeDate); // 订单日期 14 位定长。 格式：年年年年月月日日时时分分秒秒,例如 2016010112121818
// 		params.put("TransID", billno); // 订单号,唯一订单号，5-32位 位字母和数字 闪付将以此作为结算的唯一凭证.每日内唯一。 // TODO　字母加数字
// 		params.put("OrderMoney", OrderMoney); // 订单金额,单位：分，例：1 元=100 分
// 		params.put("ProductName", RandomStringUtils.random(6, true, true)); // 商品名称,须 URL 编码，长度不超过 64 位
// 		params.put("Amount", "1"); // 商品数量,无特殊需求则设置为 1
// 		params.put("Username", RandomStringUtils.random(6, true, true)); // 用户名称,须 URL 编码,长度不超过 64 位
//
// 		String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
// 		params.put("AdditionalInfo", cusSign); // 附加字段,须 URL 编码，长度不超过 128 位
// 		params.put("NoticeType", "1"); // 通知类型,固定数字：1
// 		params.put("PageUrl", resultUrl); // 页面返回地址,URL 编码 该地址通知服务不可以用作交易处理
// 		params.put("ReturnUrl", notifyUrl); // 交易通知地址,须 URL 编码，长度不超过 255 位 闪付后台异步通知，在此交易结果处理 不可使用页面通知地址作为交易通知地址
//
// 		// MD5参数拼接规则
// 		// String md5Str = "MerchantID|PayID|TradeDate|TransID|OrderMoney|PageUrl|ReturnUrl|NoticeType|密钥";
// 		String md5Str = "%s|%s|%s|%s|%s|%s|%s|%s|%s";
//
// 		md5Str = String.format(md5Str,
// 				params.get("MemberID"),
// 				params.get("PayID"),
// 				params.get("TradeDate"),
// 				params.get("TransID"),
// 				params.get("OrderMoney"),
// 				params.get("PageUrl"),
// 				params.get("ReturnUrl"),
// 				params.get("NoticeType"),
// 				PayConfig.QUICKPAY.MD5_SECRET);
// 		md5Str = DigestUtils.md5Hex(md5Str);
//
// 		params.put("Md5Sign", md5Str); // 将交易关键数据进行拼接，经 MD5 加密生成 并转化为小写的 32 位加密字符串。
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
// 	public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
// 		try {
// 			String MemberID = resMap.get("MemberID"); // 商户 ID, 等同于商户提交的商户号
// 			String TerminalID = resMap.get("TerminalID"); // 终端 ID, 等同于商户提交的终端号
// 			String TransID = resMap.get("TransID"); // 订单 ID,等同于商户提交的订单
// 			String Result = resMap.get("Result"); // 支付结果, 代表与闪付的交易是否成功 1：成功  0：失败 可查看《附录：支付结果》
// 			String ResultDesc = resMap.get("ResultDesc"); // 订单结果,代表该订单处理的结果成功与否 跟交易结果一起判断该笔订单的最终状态 查看附录：《附录：支付结果描述》
// 			String FactMoney = resMap.get("FactMoney"); // 成功金额, 单位：分 银行订单时验证成功金额和订单金额的一致 卡类订单时验证成功金额不可超过订单金额
// 			String AdditionalInfo = resMap.get("AdditionalInfo"); // 订单附加信息,等同于商户提交的附加字段
// 			String SuccTime = resMap.get("SuccTime"); // 交易成功时间,交易成功，订单完成的时间 格式：年年年年月月日日时时分分秒秒
// 			String Md5Sign = resMap.get("Md5Sign"); // 将返回关键数据进行拼接，经 MD5 加密生成并转 化为小写的 32 位加密字符串
//
// 			if (!MemberID.equals(thridBean.getMerCode())) {
// 				log.error("闪付回调验证商户ID错误:" + MemberID);
// 			}
// 			if (!TerminalID.equals(thridBean.getMerKey())) {
// 				log.error("闪付回调验证终点ID错误:" + TerminalID);
// 			}
//
// 			// MD5参数拼接规则
// 			// String md5Str = "MemberID=MemberID|TerminalID=TerminalID|TransID=TransID|Result=Result|ResultDesc=ResultDesc|FactMoney=FactMoney|AdditionalInfo=AdditionalInfo|SuccTime=SuccTime|Md5Sign=密钥";
// 			String md5Str = "MemberID=%s~|~TerminalID=%s~|~TransID=%s~|~Result=%s~|~ResultDesc=%s~|~FactMoney=%s~|~AdditionalInfo=%s~|~SuccTime=%s~|~Md5Sign=%s";
//
// 			md5Str = String.format(md5Str,
// 					MemberID,
// 					TerminalID,
// 					TransID,
// 					Result,
// 					ResultDesc,
// 					FactMoney,
// 					AdditionalInfo,
// 					SuccTime,
// 					PayConfig.QUICKPAY.MD5_SECRET);
// 			md5Str = DigestUtils.md5Hex(md5Str);
//
// 			String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), TransID);
//
// 			// 自定义的参数验证
// 			if (cusSign.equals(AdditionalInfo)) {
// 				// 接到通知处理成功后请输出 “OK”,以便告知闪付通知成功，并取消后续通知。
//
// 				// Result=1 && ResultDesc=01 && MD5验签匹配 表示成功
// 				if ("1".equals(Result) && "01".equals(ResultDesc) && md5Str.equals(Md5Sign)) {
// 					RechargePay pay = new RechargePay();
// 					pay.setRecvCardNo(thridBean.getMerCode()); // 商户号
// 					pay.setAmount(Double.parseDouble(FactMoney)/100);//金额
// 					pay.setBillno(TransID);//订单编号
// 					pay.setTradeNo(TransID);//支付订单
// 					Moment moment = new Moment().fromTime(SuccTime, "yyyyMMddHHmmss");
// 					pay.setPayTime(moment.toDate());//订单完成时间
// 					pay.setTradeStatus("1");//支付状态(1为成功  0为失败)
// 					return pay;
// 				}
// 			}
// 			else {
// 				log.error("闪付回调验证自定义签名错误,请求参数:" + JSON.toJSONString(resMap));
// 			}
//
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
//
// 	private static String getCusSign(String MemberID, String TerminalID, String TransID) {
// 		return DigestUtils.md5Hex(MemberID + TerminalID + TransID + VEFITY_MD5_KEY);
// 	}
//
// }
