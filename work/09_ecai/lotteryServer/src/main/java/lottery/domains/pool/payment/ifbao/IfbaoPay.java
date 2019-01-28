// package lottery.domains.pool.payment.ifbao;
//
// import java.util.HashMap;
// import java.util.Map;
//
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
//
// //雅付支付处理工具类
// public class IfbaoPay {
// 	/**
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
// 		HashMap<String, String> paramsMap = new HashMap<String, String>();
// 		try {
// 			paramsMap.put("payType", "02");//支付类型
// 			paramsMap.put("consumerNo", thridBean.getMerCode());//商户ID
// 			paramsMap.put("merOrderNum",billno);//订单号
// 			paramsMap.put("tranAmt", MoneyFormat.FormatIfbao(Amount));//金额
// 			paramsMap.put("callbackUrl", notifyUrl);//通知地址
// 			paramsMap.put("goodsName", "cz");//商品名称
// 			paramsMap.put("goodsDetail", "gameCZ");//详情
// 			paramsMap.put("merRemark1","rmrk");//扩展字段
//
//
// 			String paramstr = IfbaoSignUtil.generatePayRequest(paramsMap);
// 			String signValue = MD5Utils.generate(paramstr);
// 			paramsMap.put("signValue",signValue);//签名
//
// 			if(!"".equals(paramstr)){
// 				result.setPayUrl(PayConfig.IFBAOPAY.PAY_URL);
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
// 	 * 异部通知
// 	 * @return
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap){
// 		String consumerNo = resMap.get("consumerNo");
// 		String merOrderNum = resMap.get("merOrderNum");
// 		String tranAmt = resMap.get("tranAmt");
// 		String callbackUrl = resMap.get("callbackUrl");
// //		String goodsName = resMap.get("goodsName");
// 		String respCode = resMap.get("respCode");
// 		String orderId = resMap.get("orderId");
// //		String OutOrderId = resMap.get("OutOrderId");
// //		String merRemark1 = resMap.get("merRemark1");
// 		String sign = resMap.get("signValue");
//
// 		StringBuffer sb = new StringBuffer();
// 		sb.append("consumerNo=[" + consumerNo + "]");
// 		sb.append("merOrderNum=[" + merOrderNum + "]");
// 		sb.append("tranAmt=[" + tranAmt + "]");
// 		sb.append("callbackUrl=[" +callbackUrl + "]");
// 		sb.append("userKey=["+ PayConfig.IFBAOPAY.PUBLIC_KEY +"]");
//
// 		String verifySign = MD5Utils.generate(sb.toString());
// 		if(sign.equals(verifySign)){
// 			if(respCode.equals("ok")){
// 				RechargePay pay = new RechargePay();
// 				pay.setAmount(Double.parseDouble(tranAmt));
// 				pay.setRecvName(thridBean.getMerCode());
// 				pay.setBillno(merOrderNum);//自己的订单号
// 				pay.setRecvCardNo(consumerNo);
// 				pay.setTradeNo(orderId);//支付订单号
// 				pay.setTradeStatus(respCode);//成功为ok
// 				return pay;
// 			}
// 		}
// 		return null;
// 	}
// }
