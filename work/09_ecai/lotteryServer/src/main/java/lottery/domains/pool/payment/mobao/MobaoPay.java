// package lottery.domains.pool.payment.mobao;
//
// import java.util.HashMap;
// import java.util.Map;
//
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
//
// public class MobaoPay {
// 	/**
// 	 * 摩宝微信支付
// 	 * 封装支付参数
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
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		try {
// 			Mobo360SignUtil.init(thridBean.getMerKey());
// 			// 组织请求数据
// 			HashMap<String, String> paramsMap = new HashMap<String, String>();
// 			paramsMap.put("apiName", Mobo360Config.MOBAOPAY_APINAME_PAY);
// 			paramsMap.put("apiVersion", Mobo360Config.MOBAOPAY_API_VERSION);
// 			paramsMap.put("platformID", thridBean.getMerCode());
// 			paramsMap.put("merchNo",thridBean.getMerCode());
// 			paramsMap.put("orderNo", billno);
// 			paramsMap.put("tradeDate", UtilDate.getDate());
// 			paramsMap.put("amt", MoneyFormat.fonmatMobao(Amount));
// 			paramsMap.put("merchUrl", notifyUrl);
// 			paramsMap.put("merchParam","CZ");
// 			paramsMap.put("tradeSummary","YXWXCZ");
// 			paramsMap.put("customerIP",ip);
// 			/**
//              * bankCode为空，提交表单后浏览器在新窗口显示摩宝支付收银台页面，在这里可以通过账户余额支付或者选择银行支付；
//              * bankCode不为空，取值只能是接口文档中列举的银行代码，提交表单后浏览器将在新窗口直接打开选中银行的支付页面。
//              * 无论选择上面两种方式中的哪一种，支付成功后收到的通知都是同一接口。
//              **/
//
// 			paramsMap.put("bankCode","");
// 			paramsMap.put("choosePayType",Mobo360Config.MOBAOWX);
//
// 			String paramsStr = Mobo360Merchant.generatePayRequest(paramsMap);	// 签名源数据
// 			String signMsg = Mobo360SignUtil.signData(paramsStr);	// 签名数据
// 			String epayUrl = Mobo360Config.MOBAOPAY_GETWAY;	//支付网关地址
// 			paramsMap.put("signMsg", signMsg);
//
// 			if(!"".equals(signMsg)){
// 				result.setPayUrl(epayUrl);
// 				result.setParamsMap(paramsMap);
// 			}else{
// 				result.setPayUrl("");
// 				result.setParamsMap(null);
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 	}
// 	/**
// 	 * 支付宝支付
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
// 	public static void prepareAlipay(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		try {
// 			Mobo360SignUtil.init(thridBean.getMerKey());
// 			// 组织请求数据
// 			HashMap<String, String> paramsMap = new HashMap<String, String>();
// 			paramsMap.put("apiName", Mobo360Config.MOBAOPAY_APINAME_PAY);
// 			paramsMap.put("apiVersion", Mobo360Config.MOBAOPAY_API_VERSION);
// 			paramsMap.put("platformID", thridBean.getMerCode());
// 			paramsMap.put("merchNo",thridBean.getMerCode());
// 			paramsMap.put("orderNo", billno);
// 			paramsMap.put("tradeDate", UtilDate.getDate());
// 			paramsMap.put("amt", MoneyFormat.fonmatMobao(Amount));
// 			paramsMap.put("merchUrl", notifyUrl);
// 			paramsMap.put("merchParam","CZ");
// 			paramsMap.put("tradeSummary","YXWXCZ");
// 			paramsMap.put("customerIP",ip);
// 			/**
//              * bankCode为空，提交表单后浏览器在新窗口显示摩宝支付收银台页面，在这里可以通过账户余额支付或者选择银行支付；
//              * bankCode不为空，取值只能是接口文档中列举的银行代码，提交表单后浏览器将在新窗口直接打开选中银行的支付页面。
//              * 无论选择上面两种方式中的哪一种，支付成功后收到的通知都是同一接口。
//              **/
//
// 			paramsMap.put("bankCode","");
// 			paramsMap.put("choosePayType",Mobo360Config.MOBAOALIPAY);//摩宝支付宝支付
//
// 			String paramsStr = Mobo360Merchant.generatePayRequest(paramsMap);	// 签名源数据
// 			String signMsg = Mobo360SignUtil.signData(paramsStr);	// 签名数据
// 			String epayUrl = Mobo360Config.MOBAOPAY_GETWAY;	//支付网关地址
// 			paramsMap.put("signMsg", signMsg);
//
// 			if(!"".equals(signMsg)){
// 				result.setPayUrl(epayUrl);
// 				result.setParamsMap(paramsMap);
// 			}else{
// 				result.setPayUrl("");
// 				result.setParamsMap(null);
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 	}
// 	/**
// 	 * 摩宝QQ钱包支付
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
// 	public static void prepareQQpay(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip) {
// 		try {
// 			Mobo360SignUtil.init(thridBean.getMerKey());
// 			// 组织请求数据
// 			HashMap<String, String> paramsMap = new HashMap<String, String>();
// 			paramsMap.put("apiName", Mobo360Config.MOBAOPAY_APINAME_PAY);
// 			paramsMap.put("apiVersion", Mobo360Config.MOBAOPAY_API_VERSION);
// 			paramsMap.put("platformID", thridBean.getMerCode());
// 			paramsMap.put("merchNo",thridBean.getMerCode());
// 			paramsMap.put("orderNo", billno);
// 			paramsMap.put("tradeDate", UtilDate.getDate());
// 			paramsMap.put("amt", MoneyFormat.fonmatMobao(Amount));
// 			paramsMap.put("merchUrl", notifyUrl);
// 			paramsMap.put("merchParam","CZ");
// 			paramsMap.put("tradeSummary","YXWXCZ");
// 			paramsMap.put("customerIP",ip);
// 			/**
//              * bankCode为空，提交表单后浏览器在新窗口显示摩宝支付收银台页面，在这里可以通过账户余额支付或者选择银行支付；
//              * bankCode不为空，取值只能是接口文档中列举的银行代码，提交表单后浏览器将在新窗口直接打开选中银行的支付页面。
//              * 无论选择上面两种方式中的哪一种，支付成功后收到的通知都是同一接口。
//              **/
//
// 			paramsMap.put("bankCode","");
// 			paramsMap.put("choosePayType",Mobo360Config.MOBAOQQPAY);//摩宝QQ钱包支付
//
// 			String paramsStr = Mobo360Merchant.generatePayRequest(paramsMap);	// 签名源数据
// 			String signMsg = Mobo360SignUtil.signData(paramsStr);	// 签名数据
// 			String epayUrl = Mobo360Config.MOBAOPAY_GETWAY;	//支付网关地址
// 			paramsMap.put("signMsg", signMsg);
//
// 			if(!"".equals(signMsg)){
// 				result.setPayUrl(epayUrl);
// 				result.setParamsMap(paramsMap);
// 			}else{
// 				result.setPayUrl("");
// 				result.setParamsMap(null);
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 	}
//
// 	/**
// 	 * 异步通知
// 	 * @param thridBean
// 	 * @param resMap
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception {
// 		try{
// 			String apiName = resMap.get("apiName");
// 			String notifyTime = resMap.get("notifyTime");
// 			String tradeAmt = resMap.get("tradeAmt");
// 			String merchNo = resMap.get("merchNo");
// 			String merchParam = resMap.get("merchParam");
// 			String orderNo = resMap.get("orderNo");
// 			String tradeDate = resMap.get("tradeDate");
// 			String accNo = resMap.get("accNo");
// 			String accDate = resMap.get("accDate");
// 			String orderStatus = resMap.get("orderStatus");
// 			String signMsg = resMap.get("signMsg");
// 			String notifyType = resMap.get("notifyType");
// 			signMsg.replaceAll(" ", "\\+");
//
// 			String srcMsg = String.format(
// 					"apiName=%s&notifyTime=%s&tradeAmt=%s&merchNo=%s&merchParam=%s&orderNo=%s&tradeDate=%s&accNo=%s&accDate=%s&orderStatus=%s",
// 					apiName, notifyTime, tradeAmt, merchNo, merchParam, orderNo, tradeDate, accNo, accDate, orderStatus);
// 			Mobo360SignUtil.init(thridBean.getMerKey());
// 			// 验证签名
// 			boolean verifyRst = signMsg.equalsIgnoreCase(Mobo360SignUtil.signData(srcMsg));
// 			if (orderStatus.equals("1")) {
// 				/**
// 				 * 验证通过后，请在这里加上商户自己的业务逻辑处理代码. 比如： 1、根据商户订单号取出订单数据
// 				 * 2、根据订单状态判断该订单是否已处理（因为通知会收到多次），避免重复处理 3、比对一下订单数据和通知数据是否一致，例如金额等
// 				 * 4、接下来修改订单状态为已支付或待发货 5、...
// 				 */
// 				// 判断通知类型，若为后台通知需要回写"SUCCESS"给支付系统表明已收到支付通知
// 				// 否则支付系统将按一定的时间策略在接下来的24小时内多次发送支付通知。
// //				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				RechargePay pay = new RechargePay();
// 				pay.setAmount(Double.parseDouble(tradeAmt));
// //				pay.setPayTime(sdf.parse(tradeDate));
// 				pay.setRecvName(thridBean.getMerCode());
// 				pay.setBillno(orderNo);//自己的订单号
// 				pay.setRecvCardNo(merchNo);
// 				pay.setTradeNo(accNo);//支付订单号
// 				pay.setNotifyType(notifyType);
// 				pay.setTradeStatus(orderStatus);
// 				return pay;
// 			} else {
// 				return null;
// 			}
// 		}catch(Exception e){
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
//
//
// //
// //	public static void test(){
// //		// 签名初始化
// ////			Mobo360SignUtil.init();
// //
// //			// 获取请求参数，并将数据组织成前面验证源字符串
// //			//request.setCharacterEncoding("utf-8");
// //			String apiName =  Mobo360Config.MOBAOPAY_APINAME_PAY;
// //			String notifyTime = "2016-09-26 23:23:23";
// //			String tradeAmt = "1.00";
// //			String merchNo ="210001350010722";
// //			String merchParam = "cz";
// //			String orderNo = "20160926643676355769";
// //			String tradeDate = "2016-09-26";
// //			String accNo ="210001350010722";
// //			String accDate = "2016-09-26";
// //			String orderStatus = "1";
// ////				String signMsg = request.getParameter("signMsg");
// ////				signMsg.replaceAll(" ", "\\+");
// //
// //			String srcMsg = String
// //					.format(
// //							"apiName=%s&notifyTime=%s&tradeAmt=%s&merchNo=%s&merchParam=%s&orderNo=%s&tradeDate=%s&accNo=%s&accDate=%s&orderStatus=%s",
// //							apiName, notifyTime, tradeAmt, merchNo,
// //							merchParam, orderNo, tradeDate, accNo, accDate,
// //							orderStatus);
// //			System.out.println(srcMsg);
// ////				System.out.println(signMsg);
// //
// //			// 验证签名
// //			boolean verifyRst = true;//Mobo360SignUtil.verifyData(signMsg, srcMsg);
// //			try {
// //				System.out.println(Mobo360SignUtil.signByMD5(srcMsg,"55438e2ff462ad539a2e12a33dc9f2e9"));
// //			} catch (Exception e) {
// //				// TODO Auto-generated catch block
// //				e.printStackTrace();
// //			}
// //			String verifyStatus = "未验证";
// //			if (verifyRst) {
// //				verifyStatus = "验签通过";
// //				/**
// //				 * 验证通过后，请在这里加上商户自己的业务逻辑处理代码.
// //				 * 比如：
// //				 * 1、根据商户订单号取出订单数据
// //				 * 2、根据订单状态判断该订单是否已处理（因为通知会收到多次），避免重复处理
// //				 * 3、比对一下订单数据和通知数据是否一致，例如金额等
// //				 * 4、接下来修改订单状态为已支付或待发货
// //				 * 5、...
// //				 */
// //			//
// //			// // 判断通知类型，若为后台通知需要回写"SUCCESS"给支付系统表明已收到支付通知
// //			// // 否则支付系统将按一定的时间策略在接下来的24小时内多次发送支付通知。
// //			// if (request.getParameter("notifyType").equals("1")) {
// //			// // 回写‘SUCCESS’方式一：
// //			// 重定向到一个专门用于处理回写‘SUCCESS’的页面，这样可以保证输出内容中只有'SUCCESS'这个字符串。
// //			// response.setContentType("text/html; charset=UTF-8");
// //			// response.sendRedirect("notify.jsp");
// //			// // 回写‘SUCCESS’方式二： 直接让当前输出流中包含‘SUCCESS’字符串。两种方式都可以，但建议采用第一种。
// //			// // out.println("SUCCESS");
// //		}
// //	}
// //
// //	public static void main(String[] args) {
// //		test();
// //		//apiName=WEB_PAY_B2C&notifyTime=2016-09-26 23:23:23&tradeAmt=1.00&merchNo=210001350010722&merchParam=cz&orderNo=20160926643676355769&tradeDate=2016-09-26&accNo=210001350010722&accDate=2016-09-26&orderStatus=1
// //	}
// }
