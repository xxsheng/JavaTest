package lottery.domains.pool.payment.tgf;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import javautils.http.HttpUtil;
import javautils.image.ImageUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.tgf.utils.Config;
import lottery.domains.pool.payment.tgf.utils.QueryResponseEntity;
import lottery.domains.pool.payment.tgf.utils.RefundResponseEntity;
import lottery.domains.pool.payment.tgf.utils.SignUtil;

/**
 * 创富支付 Created by Nick on 2017-11-21.
 */
public class TGFPayment extends AbstractPayment {
	private static final Logger log = LoggerFactory.getLogger(TGFPayment.class);

	private static final String OUTPUT_SUCCESS = "SUCCESS";
	private static final String OUTPUT_FAILED = "failed";

	public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
			PaymentChannel channel, HttpServletRequest request) {
		try {
			switch (channel.getChannelCode()) {
			case Global.PAYMENT_CHANNEL_TGF:
				return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_TGFQUICK:
				return prepareQuick(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_TGFQQ:
				return prepareQQ(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_TGFJDPAY:
				return prepareJDPay(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			}

			return null;
		} catch (Exception e) {
			log.error(channel.getName() + "封装支付参数异常", e);
			return retPrepareFailed("支付通道异常，请重试！");
		}
	}

	/**
	 * 网银
	 */
	private static PrepareResult prepareWangYing(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		Map<String, String> paramsMap = new TreeMap<>();
		try {
			paramsMap.put("service", Config.APINAME_PAY);
			paramsMap.put("version", Config.API_VERSION);
			paramsMap.put("merId", channel.getMerCode());
			paramsMap.put("tradeNo", billno);
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			paramsMap.put("tradeDate", sdf.format(d));
			paramsMap.put("amount", amount + "");
			paramsMap.put("notifyUrl", notifyUrl);
			paramsMap.put("extra", billno);
			paramsMap.put("summary", channel.getMerCode());
			paramsMap.put("expireTime", 900 + "");
			String ip = HttpUtil.getRealIp(null, request);
			paramsMap.put("clientIp", ip);
			paramsMap.put("bankId", bankco);
			String paramsStr = Merchant.generatePayRequest(paramsMap);
			String signMsg = SignUtil.signByMD5(paramsStr, channel.getMd5Key());

			paramsMap.put("sign", signMsg);
			String url = channel.getPayUrl();
			PrepareResult prepareResult = new PrepareResult();
			prepareResult.setFormParams(paramsMap);
			prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
			prepareResult.setSuccess(true);
			prepareResult.setFormUrl(url);
			return prepareResult;
		} catch (Exception e) {
			log.error(channel.getName() + "获取请求地址异常", e);
			return retPrepareFailed("获取请求地址失败，请重试！");
		}

	}
	
	
	/**
	 * 网银
	 */
	private static PrepareResult prepareQuick(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		Map<String, String> paramsMap = new TreeMap<>();
		try {
			paramsMap.put("service", Config.APINAME_PAY);
			paramsMap.put("version", Config.API_VERSION);
			paramsMap.put("merId", channel.getMerCode());
			paramsMap.put("tradeNo", billno);
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			paramsMap.put("tradeDate", sdf.format(d));
			paramsMap.put("amount", amount + "");
			paramsMap.put("notifyUrl", notifyUrl);
			paramsMap.put("extra", billno);
			paramsMap.put("summary", channel.getMerCode());
			paramsMap.put("expireTime", 900 + "");
			String ip = HttpUtil.getRealIp(null, request);
			paramsMap.put("clientIp", ip);
			paramsMap.put("bankId", "KJZF");
			String paramsStr = Merchant.generatePayRequest(paramsMap);
			String signMsg = SignUtil.signByMD5(paramsStr, channel.getMd5Key());

			paramsMap.put("sign", signMsg);
			String url = channel.getPayUrl();
			PrepareResult prepareResult = new PrepareResult();
			prepareResult.setFormParams(paramsMap);
			prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
			prepareResult.setSuccess(true);
			prepareResult.setFormUrl(url);
			return prepareResult;
		} catch (Exception e) {
			log.error(channel.getName() + "获取请求地址异常", e);
			return retPrepareFailed("获取请求地址失败，请重试！");
		}

	}

	/**
	 * 微信
	 */
	private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "WXPAY");
	}

	/**
	 * 支付宝
	 */
	private static PrepareResult prepareAlipay(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "ALIPAY");
	}

	/**
	 * QQ
	 */
	private static PrepareResult prepareQQ(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "3");
	}

	/**
	 * 京东
	 */
	private static PrepareResult prepareJDPay(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {

		return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request, "5");
	}
	

	/**
	 * 获取二维码
	 */
	private static PrepareResult prepareWithQRCode(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request, String defaultbank) {
		
        // 组织请求数据
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("service", Config.APINAME_SCANPAY);
        paramsMap.put("version", Config.API_VERSION);
        paramsMap.put("merId", channel.getMerCode());
        paramsMap.put("tradeNo", billno);
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		paramsMap.put("tradeDate", sdf.format(d));
		paramsMap.put("amount", amount + "");
        paramsMap.put("notifyUrl", notifyUrl);
        paramsMap.put("summary", channel.getMerCode());
		paramsMap.put("expireTime", 900 + "");
		String ip = HttpUtil.getRealIp(null, request);
		paramsMap.put("clientIp", ip);
		paramsMap.put("extra", billno);
        paramsMap.put("typeId", defaultbank);
 
        
		try {
		       
	        String paramsStr = Merchant.generateAlspQueryRequest(paramsMap);
	        String signMsg = SignUtil.signByMD5(paramsStr, channel.getMd5Key());
	        paramsStr += "&sign=" + signMsg;
	        String payGateUrl =channel.getPayUrl();
	        // 发送请求并接收返回
	        String responseMsg = Merchant.transact(paramsStr, payGateUrl);
	        
            // 解析返回数据
            RefundResponseEntity entity = new RefundResponseEntity();
            entity.parse(responseMsg,channel.getMd5Key());
            

		
			if (entity.getRespCode() == null) {
				log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
				return retPrepareFailed("获取二维码失败，请重试！");
			}

			if (!"00".equals(entity.getRespCode())) {
				log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + paramsStr);
				return retPrepareFailed("获取二维码失败，请重试！"+ entity.getRespDesc());
			}

	
			String pay_QR = entity.getQrCode();
			if (StringUtils.isEmpty(pay_QR)) {
				log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
				return retPrepareFailed("获取二维码失败，请重试！");
			}

			PrepareResult prepareResult = new PrepareResult();
			prepareResult.setJumpType(PrepareResult.JUMP_TYPE_QR);
			prepareResult.setSuccess(true);
			Base64 base64 = new Base64();
			String qrCode = ImageUtil.encodeQR( new String(base64.decode(pay_QR), "UTF-8"), 200, 200);
			prepareResult.setQrCode(qrCode);

			return prepareResult;
		} catch (Exception e) {
			log.error(channel.getName() + "获取二维码异常", e);
			return retPrepareFailed("获取二维码失败，请重试！");
		}
	}

	/**
	 * 验证回调
	 */
	public static VerifyResult verify(PaymentChannel channel, Map<String, String> resMap) {
		try {
			// 本地验证
			if (!localVerify(channel, resMap)) {
				return retVerifyFailed("签名验证失败");
			}

			// 远程验证
			VerifyResult verifyResult = remoteVerify(channel, resMap);

			return verifyResult;
		} catch (Exception e) {
			log.error(channel.getName() + "签名验证失败", e);
			return retVerifyFailed("签名验证失败");
		}
	}

	private static boolean localVerify(PaymentChannel channel, Map<String, String> resMap) {
		try {
			String service = Config.APINAME_NOTIFY;
			String merId = resMap.get("merId");
			String tradeNo = resMap.get("tradeNo");
			String tradeDate = resMap.get("tradeDate");
			String opeNo = resMap.get("opeNo");
			String opeDate = resMap.get("opeDate");
			String amount = resMap.get("amount");
			String status = resMap.get("status");
			String extra = resMap.get("extra");
			String payTime = resMap.get("payTime");
			String sign = resMap.get("sign");
			sign.replaceAll(" ", "\\+");
			if ("1".equals(status)) {

				String srcMsg = String.format(
						"service=%s&merId=%s&tradeNo=%s&tradeDate=%s&opeNo=%s&opeDate=%s&amount=%s&status=%s&extra=%s&payTime=%s",
						service, merId, tradeNo, tradeDate, opeNo, opeDate, amount, status, extra, payTime);
				String serverSign = SignUtil.signByMD5(srcMsg, channel.getMd5Key());

				if (serverSign.equalsIgnoreCase(sign)) {
					return true;
				} else {
					log.error(channel.getName() + "回调本地验证失败，参数签名与服务器不一致，服务器验证：" + serverSign + ",回调："
							+ JSON.toJSONString(resMap));
					return false;
				}
			} else {
				log.warn(channel.getName() + "回调本地验证失败，参数状态表示不成功，回调：" + JSON.toJSONString(resMap));
				return false;
			}
		} catch (Exception e) {
			log.error(channel.getName() + "回调本地验证发生异常", e);
		}
		return false;
	}

	private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap) {
		try {
		String outTradeNo = resMap.get("tradeNo");
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("service", Config.APINAME_QUERY);
        paramsMap.put("version", Config.API_VERSION);
        paramsMap.put("merId", channel.getMerCode());
        paramsMap.put("tradeNo", outTradeNo);
        paramsMap.put("tradeDate", resMap.get("tradeDate"));
        paramsMap.put("amount",  resMap.get("amount"));
        String paramsStr = Merchant.generateQueryRequest(paramsMap);
        
		String signMsg = SignUtil.signByMD5(paramsStr, channel.getMd5Key()).replaceAll("\r", "").replaceAll("\n", "");
        paramsStr += "&sign=" + signMsg;

        String payGateUrl = channel.getPayUrl();
         
        // 发送请求并接收返回
        String responseMsg = Merchant.transact(paramsStr, payGateUrl);

        // 解析返回数据
        QueryResponseEntity entity = new QueryResponseEntity();
        entity.parse(responseMsg,channel.getMd5Key());
        

        if (!"00".equals(entity.getRespCode())) {
            log.error(channel.getName() + "自动上分远程验证失败，返回状态表示执行不成功：请求：" + JSON.toJSONString(resMap) + "，返回：" + entity.getRespDesc());
            return retVerifyFailed("远程验证失败，订单未支付");
        }
        
        if (!"1".equals(entity.getStatus())) {
            log.error(channel.getName() + "自动上分远程验证失败，返回状态表示执行不成功：请求：" + JSON.toJSONString(resMap) + "，返回：" + entity.getRespDesc());
            return retVerifyFailed("远程验证失败，订单未支付");
        }
			VerifyResult verifyResult = new VerifyResult();
			verifyResult.setSelfBillno(outTradeNo);
			verifyResult.setChannelBillno(outTradeNo);
			double money = Double.parseDouble(resMap.get("amount"));
			verifyResult.setRequestMoney(money);
			verifyResult.setReceiveMoney(money);
			verifyResult.setPayTime(new Date());
			verifyResult.setSuccess(true);
			verifyResult.setOutput(OUTPUT_SUCCESS);
			verifyResult.setSuccessOutput(OUTPUT_SUCCESS);
			return verifyResult;
		} catch (Exception e) {
			log.error(channel.getName() + "自动上分远程验证异常", e);
			return retVerifyFailed("远程验证失败，获取订单失败");
		}
	}

	private static final String SIGN_ALGORITHMS = "SHA-1";

	/**
	 * SHA1 安全加密算法
	 */
	private static String sign(String content, String inputCharset) {
		// 获取信息摘要 - 参数字典排序后字符串
		try {
			// 指定sha1算法
			MessageDigest digest = MessageDigest.getInstance(SIGN_ALGORITHMS);
			digest.update(content.getBytes(inputCharset));
			// 获取字节数组
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
