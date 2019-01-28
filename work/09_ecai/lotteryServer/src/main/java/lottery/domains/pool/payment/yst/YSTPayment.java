// package lottery.domains.pool.payment.yst;
//
// import com.alibaba.fastjson.JSON;
// import javautils.image.ImageUtil;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.apache.commons.lang.RandomStringUtils;
// import org.apache.commons.lang.StringUtils;
// import org.apache.commons.lang.math.RandomUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import pay.api.merchant.notice.Notice1001;
// import pay.api.merchant.request.M1010Request;
// import pay.api.merchant.response.M1010Response;
//
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.Map;
//
// /**
//  * 银商通
//  */
// public class YSTPayment {
// 	private static final Logger log = LoggerFactory.getLogger(YSTPayment.class);
// 	private static final String VEFITY_MD5_KEY = "df$tkd0aoo@kda0d(9id";
// 	private static final String[] ProductcategoryS = new String[]{"1", "3", "4", "6", "7", "10", "11", "13", "14", "17"};
//
// 	public static void prepareWeiXin(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip){
// 		try {
// 			long transtime = new Date().getTime();
// 			String amount = String.valueOf(MoneyFormat.yuanToFenMoney(Amount));
//
// 			M1010Request req = new M1010Request();
// 			req.setOrder(billno); // 商户订单号, 商户生成的唯一订单号，注意不能重复，最长50位
// 			req.setTranstime(transtime+""); // 交易时间, 时间戳，例如：1361324896000，精确到毫秒
// 			req.setAmount(amount); // 交易金额 以"分"为单位的整型，必须大于零
// 			req.setProductcategory(ProductcategoryS[RandomUtils.nextInt(ProductcategoryS.length)]); // 商品种类 1:虚拟产品
// 			req.setProductname(RandomStringUtils.random(6, true, true)); // 商品名称, 最长100位
// 			req.setProductdesc(RandomStringUtils.random(6, true, true)); // 商品描述, 最长500位
// 			req.setProductprice(amount); // 商品单价, 以"分"为单位的整型，必须大于零
// 			req.setProductcount("1"); // 商品数量, 最大数量值99999
//
// 			String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
// 			req.setMerrmk(cusSign); // 商户备注信息, 最长1000位，商户自用备注信息,回调时会将该信息原样返回给商户
// 			req.setUserua(""); // 终端UA, 用户使用的移动终端的UA信息，最大长度200
// 			req.setUserip(ip); // 用户IP, 用户支付时使用的网络终端IP，当为微信H5支付时，该IP必须为用户发起支付客户端真实IP，否则不能发起支付
// 			// req.setUserip("222.127.35.46"); // 用户IP, 用户支付时使用的网络终端IP，当为微信H5支付时，该IP必须为用户发起支付客户端真实IP，否则不能发起支付
// 			req.setAreturl(notifyUrl); // 商户后台回调地址, 用来通知商户支付结果，后台发送post请求，前后台回调地址的回调内容相同，长度100位 ，商户收到请求必须回复内容，内容不能为空
// 			req.setSreturl(resultUrl); // 商户前台回调地址, 用来通知商户支付结果，前后台回调地址的回调内容相同。用户在网页支付成功页面，点击“返回商户”时的回调地址,长度100位
// 			// m1010.setOrderSubOpenid("");// 用户子标识, 用户在子商户appid下的唯一标识。公众号支付下单前需要调用
// 			// m1010.setAuthCode(""); // 一维码（条码编号） 用户在微信或者支付宝一维码支付接口时传入
// 			req.setPnc("86002"); // 支付节点编码 86002-微信扫码支付 88002-支付宝扫码支付
//
// 			M1010Response rsp = req.process();
// 			if(rsp.getError_code()!=null && rsp.getError_msg()!=null){
// 				log.error("银商通微信封装数据异常:" + rsp.getError_code() + "," + rsp.getError_msg());
// 				if ("70007001".equals(rsp.getError_code())) {
// 					result.setReturnCode(PayConfig.wxPayReturnCode.duplicate);
// 				}
// 				else if ("70006001".equals(rsp.getError_code())) {
// 					result.setReturnCode(PayConfig.wxPayReturnCode.bankError);
// 				}
// 				else {
// 					result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 				}
// 			}else{
// 				if(rsp.getRespCode().equals("1")){
//
// 					String qr = ImageUtil.encodeQR(rsp.getCodeUrl(), 200, 200);
// 					result.setReturnValue(qr);
// 					result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// 				}else{
// 					log.error("银商通微信封装数据异常:" + rsp.getRespMsg());
// 					result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 				}
// 			}
// 		} catch (Exception e) {
// 			log.error("银商通微信封装数据异常", e);
// 			result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		}
// 	}
//
// 	public static void prepareAlipay(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip){
// 		try {
// 			long transtime = new Date().getTime();
// 			String amount = String.valueOf(MoneyFormat.yuanToFenMoney(Amount));
//
// 			M1010Request req = new M1010Request();
// 			req.setOrder(billno); // 商户订单号, 商户生成的唯一订单号，注意不能重复，最长50位
// 			req.setTranstime(transtime+""); // 交易时间, 时间戳，例如：1361324896000，精确到毫秒
// 			req.setAmount(amount); // 交易金额 以"分"为单位的整型，必须大于零
// 			req.setProductcategory(ProductcategoryS[RandomUtils.nextInt(ProductcategoryS.length)]); // 商品种类 1:虚拟产品
// 			req.setProductname(RandomStringUtils.random(6, true, true)); // 商品名称, 最长100位
// 			req.setProductdesc(RandomStringUtils.random(6, true, true)); // 商品描述, 最长500位
// 			req.setProductprice(amount); // 商品单价, 以"分"为单位的整型，必须大于零
// 			req.setProductcount("1"); // 商品数量, 最大数量值99999
//
// 			String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), billno);
// 			req.setMerrmk(cusSign); // 商户备注信息, 最长1000位，商户自用备注信息,回调时会将该信息原样返回给商户
//
// 			req.setUserua(""); // 终端UA, 用户使用的移动终端的UA信息，最大长度200
// 			req.setUserip(ip); // 用户IP, 用户支付时使用的网络终端IP，当为微信H5支付时，该IP必须为用户发起支付客户端真实IP，否则不能发起支付
// 			// req.setUserip("222.127.35.46"); // 用户IP, 用户支付时使用的网络终端IP，当为微信H5支付时，该IP必须为用户发起支付客户端真实IP，否则不能发起支付
// 			req.setAreturl(notifyUrl); // 商户后台回调地址, 用来通知商户支付结果，后台发送post请求，前后台回调地址的回调内容相同，长度100位 ，商户收到请求必须回复内容，内容不能为空
// 			req.setSreturl(resultUrl); // 商户前台回调地址, 用来通知商户支付结果，前后台回调地址的回调内容相同。用户在网页支付成功页面，点击“返回商户”时的回调地址,长度100位
// 			// req.setOrderSubOpenid("");// 用户子标识, 用户在子商户appid下的唯一标识。公众号支付下单前需要调用
// 			// req.setAuthCode(""); // 一维码（条码编号） 用户在微信或者支付宝一维码支付接口时传入
// 			req.setPnc("88002"); // 支付节点编码 86002-微信扫码支付 88002-支付宝扫码支付
//
// 			M1010Response rsp = req.process();
// 			if(rsp.getError_code()!=null && rsp.getError_msg()!=null){
// 				log.error("银商通支付宝封装数据异常:" + rsp.getError_code() + "," + rsp.getError_msg());
// 				if ("70007001".equals(rsp.getError_code())) {
// 					result.setReturnCode(PayConfig.wxPayReturnCode.duplicate);
// 				}
// 				else if ("70006001".equals(rsp.getError_code())) {
// 					result.setReturnCode(PayConfig.wxPayReturnCode.bankError);
// 				}
// 				else {
// 					result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 				}
// 			}else{
// 				if(rsp.getRespCode().equals("1")){
//
// 					String qr = ImageUtil.encodeQR(rsp.getCodeUrl(), 200, 200);
// 					result.setReturnValue(qr);
// 					result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);
// 				}else{
// 					log.error("银商通支付宝封装数据异常:" + rsp.getRespMsg());
// 					result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 				}
// 			}
// 		} catch (Exception e) {
// 			log.error("银商通支付宝封装数据异常", e);
// 			result.setReturnCode(PayConfig.wxPayReturnCode.payException);
// 		}
// 	}
//
// 	/**
// 	 * 支付成功通知
// 	 * @param thridBean
// 	 * @param resMap
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean, Map<String, String> resMap) throws Exception{
// 		try {
// 			String data = resMap.get("data");
// 			String encryptkey = resMap.get("encryptkey");
// 			String merchantaccount = resMap.get("merchantaccount");
// 			if (StringUtils.isEmpty(data) || StringUtils.isEmpty(encryptkey) || StringUtils.isEmpty(merchantaccount)) {
// 				return null;
// 			}
//
// 			Notice1001 n1001 = new Notice1001();
// 			n1001.verify(data, encryptkey);
//
// 			if(null != n1001.getStatus() && n1001.getStatus().equals(Notice1001.tranStatuType.SUCCEED.get())){//后台通知返回成功
//
// 				if (!n1001.getMerid().equals(thridBean.getMerCode())) {
// 					log.error("银商通回调验证商户失败，商户ID与平台不一致,传送过来:" + n1001.getMerid() + ",后台配置为：" + thridBean.getMerCode());
// 					return null;
// 				}
//
// 				String cusSign = getCusSign(thridBean.getMerCode(), thridBean.getMerKey(), n1001.getOrder());
// 				if (!cusSign.equals(n1001.getMerrmk())) {
// 					log.error("银商通回调验证自定义签名错误,请求参数:" + JSON.toJSONString(resMap));
// 					return null;
// 				}
//
// 				RechargePay pay = new RechargePay();
// 				pay.setRecvCardNo(n1001.getMerid()); // 商户号
// 				pay.setAmount(Double.parseDouble(n1001.getAmount())/100);//金额
// 				pay.setBillno(n1001.getOrder());// 商户自己的订单号
// 				pay.setTradeNo(n1001.getPayorderid()); // 支付平台订单号
// 				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				pay.setPayTime(sdf.parse(sdf.format(new Date())));//订单通知时间
// 				pay.setTradeStatus("1");//支付状态(1 交易成功, 0 失败)
// 				return pay;
// 			}else{//后台通知返回失败
// 				log.error("银商通验证失败:" + JSON.toJSONString(n1001));
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			log.error("银商通验证失败", e);
// 		}
// 		return null;
// 	}
//
// 	private static String getCusSign(String merCode, String merKey, String billno) {
// 		return DigestUtils.md5Hex(merCode + merKey + billno + VEFITY_MD5_KEY);
// 	}
// }
