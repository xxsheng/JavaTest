// package lottery.domains.pool.payment.beecloud;
//
// import cn.beecloud.BCEumeration;
// import cn.beecloud.BCPay;
// import cn.beecloud.BeeCloud;
// import cn.beecloud.bean.BCException;
// import cn.beecloud.bean.BCOrder;
// import com.alibaba.fastjson.JSON;
// import javautils.image.ImageUtil;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
//
// /**
//  *beecloud支付工具类
//  */
// public class BCPayment {
// 	private static final Logger log = LoggerFactory.getLogger(BCPayment.class);
//
// 	static {
// 		// 注册
// 		BeeCloud.registerApp(PayConfig.BC_WECHART.APP_ID, null, PayConfig.BC_WECHART.APP_SECRET, PayConfig.BC_WECHART.MASTER_SECRET);
// 	}
//
// 	public static void prepareWeiXin(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip){
// 		try {
// 			int money = Double.valueOf(Amount).intValue();
// 			money *= 100;
// 			BCOrder bcOrder = new BCOrder(BCEumeration.PAY_CHANNEL.BC_NATIVE, money, billno, "cz");
// 			bcOrder.setBillTimeout(360);
// 			bcOrder.setNotifyUrl(notifyUrl);
// 			bcOrder = BCPay.startBCPay(bcOrder);
// 			String qr = ImageUtil.encodeQR(bcOrder.getCodeUrl(), 200, 200);
// 			result.setReturnValue(qr);
// 			result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// 		} catch (BCException e) {
// 			log.error("BeeCloud封装数据异常", e);
// 			result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		}
// 	}
//
// 	/**
// 	 * 支付成功通知
// 	 * @param thridBean
// 	 * @param resMap
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean,Map<String, String> resMap) throws Exception{
// 		try {
// 			String reqBody = resMap.get("reqBody");
// 			if (StringUtils.isEmpty(reqBody)) {
// 				return null;
// 			}
// 			HashMap resultMap = JSON.parseObject(reqBody, HashMap.class);
// 	        if(!resultMap.containsKey("signature")){
//         		return null;
//         	}
// 			String signature = resultMap.get("signature").toString();
// 	        String channelType = resultMap.get("channel_type").toString();
// 	        String transactionType = resultMap.get("transaction_type").toString();
// 	        String channelTransactionId = resultMap.get("channel_transaction_id").toString();
// 	        String transactionId = resultMap.get("transaction_id").toString();
// 	        String transactionFee = resultMap.get("transaction_fee").toString();
// 	        String tradeSuccess = resultMap.get("trade_success").toString();
// 	        String appId = resultMap.get("app_id").toString();
//
// 			boolean verify = verfitySignature(signature, transactionId, transactionType, channelType, transactionFee);
// 			if (!verify) {
// 				return null;
//             }
// 	        if ("true".equalsIgnoreCase(tradeSuccess)){
// 				RechargePay pay = new RechargePay();
// 				pay.setRecvCardNo(appId); // 商户号
// 	        	pay.setAmount(Double.parseDouble(transactionFee)/100);//金额
// 	        	pay.setBillno(transactionId);//订单编号
// 	        	pay.setTradeNo(channelTransactionId);//支付订单
// 	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				pay.setPayTime(sdf.parse(sdf.format(new Date())));//订单通知时间
// 				pay.setTradeStatus(tradeSuccess);//支付状态(true 交易成功)
// 				return pay;
// 	        }else {
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
//
// 	private static boolean verfitySignature(String signature, String transactionId, String transactionType, String channelType, String transactionFee) {
// 		String md5 = DigestUtils.md5Hex(PayConfig.BC_WECHART.APP_ID + transactionId + transactionType + channelType + transactionFee + PayConfig.BC_WECHART.MASTER_SECRET);
// 		return md5.equals(signature);
// 	}
// }
