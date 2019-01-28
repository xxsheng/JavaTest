package lottery.domains.pool.payment.af;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import javautils.date.Moment;
import javautils.http.HttpClientUtil;
import javautils.http.HttpUtil;
import javautils.http.UrlParamUtils;
import javautils.image.ImageUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.cf.CFScanResult;
import lottery.domains.pool.payment.cfg.MoneyFormat;

/**
 * 艾付支付 Created by Nick on 2017-05-28.
 */
public class AFPayment extends AbstractPayment {
	private static final Logger log = LoggerFactory.getLogger(AFPayment.class);

	private static final String CUS_EXT1 = "FFHcg,S$QL:H[tkl0dlqbd98o{2xyT";
	private static final String OUTPUT_SUCCESS = "OK";
	private static final String OUTPUT_FAILED = "{{\"code\":\"01\",\"msg\":\"处理失败\"}}";

	public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
			PaymentChannel channel, HttpServletRequest request) {
		try {
			switch (channel.getChannelCode()) {
			case Global.PAYMENT_CHANNEL_AF:
				return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_AFWECHAT:
				return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_AFQUICK:
				return prepareQuick(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_AFQQ:
				return prepareWithQRCode(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
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
		//String orderCreateTime = new Moment().format("yyyy-MM-dd");
		//String lastPayTime = new Moment().add(20, "minutes").format("yyyyMMddHHmmss");

		HashMap<String, String> params = new HashMap<>();

		params.put("version", "v1"); // 艾付分配的商户号
		params.put("merchant_no", channel.getMerCode()); // 艾付分配的商户号
		params.put("order_no", billno); // 商户系统唯一的订单编号
		params.put("goods_name", MD5.base64Encoder("temp", "UTF-8"));
		params.put("order_amount", amount + "");
		params.put("backend_url", notifyUrl);
		params.put("frontend_url", resultUrl);
		params.put("reserve", channel.getMerCode());
		params.put("pay_mode", "01");
		params.put("bank_code", bankco);
		params.put("card_type", "2");		
		String src = "version=" + params.get("version") + "&merchant_no=" + params.get("merchant_no") + "&order_no="
				+ billno + "&goods_name=" + params.get("goods_name") + "&order_amount=" + params.get("order_amount")
				+ "&backend_url=" + params.get("backend_url") + "&frontend_url=" + resultUrl + "&reserve="
				+ params.get("reserve") + "&pay_mode=" + params.get("pay_mode") + "&bank_code=" + bankco + "&card_type="
				+ params.get("card_type");
		src += "&key=" + channel.getMd5Key();
	    String sign  =  DigestUtils.md5Hex(src);
		params.put("sign", sign); // 签名
		try {
			String url = "https://pay.ifeepay.com/gateway/pay.jsp";
			PrepareResult prepareResult = new PrepareResult();
			prepareResult.setFormParams(params);
			prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
			prepareResult.setSuccess(true);
			prepareResult.setFormUrl(url);
			return prepareResult;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return retPrepareFailed("获取请求地址失败，请重试！");
	}

	private static PrepareResult prepareQuick(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		HashMap<String, String> params = new HashMap<>();
		params.put("version", "v1"); // 艾付分配的商户号
		params.put("merchant_no", channel.getMerCode()); // 艾付分配的商户号
		params.put("order_no", billno); // 商户系统唯一的订单编号
		params.put("goods_name", MD5.base64Encoder("temp", "UTF-8"));
		params.put("order_amount", amount + "");
		params.put("backend_url", notifyUrl);
		params.put("frontend_url", resultUrl);
		params.put("reserve", channel.getMerCode());
		params.put("pay_mode", "07");
		params.put("bank_code", "QUICKPAY");
		params.put("card_type", "0");
		params.put("merchant_user_id", billno);
		String src = "version=" + params.get("version") + "&merchant_no=" + params.get("merchant_no") + "&order_no="
				+ billno + "&goods_name=" + params.get("goods_name") + "&order_amount=" + params.get("order_amount")
				+ "&backend_url=" + params.get("backend_url") + "&frontend_url=" + resultUrl + "&reserve="
				+ params.get("reserve") + "&pay_mode=" + params.get("pay_mode") + "&bank_code=" + params.get("bank_code") + "&card_type="
				+ params.get("card_type")+ "&merchant_user_id="
						+ params.get("merchant_user_id");
		
		src += "&key=" + channel.getMd5Key();
	    String sign  =  DigestUtils.md5Hex(src);
		params.put("sign", sign); // 签名
		try {
			String url = "https://pay.ifeepay.com/gateway/pay.jsp";
			PrepareResult prepareResult = new PrepareResult();
			prepareResult.setFormParams(params);
			prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
			prepareResult.setSuccess(true);
			prepareResult.setFormUrl(url);
			return prepareResult;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return retPrepareFailed("获取请求地址失败，请重试！");
	}

	private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		// 21 微信， 30-支付宝 31-QQ 钱包 不传值则默认 21
		HashMap<String, String> params = new HashMap<>();
		params.put("version", "v1"); // 艾付分配的商户号
		params.put("merchant_no", channel.getMerCode()); // 艾付分配的商户号
		params.put("order_no", billno); // 商户系统唯一的订单编号
		params.put("goods_name", MD5.base64Encoder("temp", "UTF-8"));
		params.put("order_amount", amount + "");
		params.put("backend_url", notifyUrl);
		params.put("frontend_url", resultUrl);
		params.put("reserve", channel.getMerCode());
		params.put("pay_mode", "09");
		params.put("bank_code", "WECHAT");
		params.put("card_type", "2");
		String src = "version=" + params.get("version") + "&merchant_no=" + params.get("merchant_no") + "&order_no="
				+ billno + "&goods_name=" + params.get("goods_name") + "&order_amount=" + params.get("order_amount")
				+ "&backend_url=" + params.get("backend_url") + "&frontend_url=" + resultUrl + "&reserve="
				+ params.get("reserve") + "&pay_mode=" + params.get("pay_mode") + "&bank_code=" + params.get("bank_code") + "&card_type="
				+ params.get("card_type");
		src += "&key=" + channel.getMd5Key();
	    String sign  =  DigestUtils.md5Hex(src);
		params.put("sign", sign); // 签名
        try {
        	String url = "https://pay.ifeepay.com/gateway/pay.jsp";
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            AFScanResult scanResult = JSON.parseObject(retStr, AFScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            if (!"00".equals(scanResult.getResult_code())) {
                log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getResult_code() + "，" + scanResult.getResult_msg());
            }
            
             String pay_QR= scanResult.getCode_url();
            if (StringUtils.isEmpty(pay_QR)) {
                log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setJumpType(PrepareResult.JUMP_TYPE_QR);
            prepareResult.setSuccess(true);
            String qrCode = ImageUtil.encodeQR(pay_QR, 200, 200);
            prepareResult.setQrCode(qrCode);
            return prepareResult;
        } catch (Exception e) {
            log.error(channel.getName() + "获取二维码异常", e);
            return retPrepareFailed("获取二维码失败，请重试！");
        }
	}

	private static PrepareResult prepareQQ(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		// 21 微信， 30-支付宝 31-QQ 钱包 不传值则默认 21
		return prepareWithQRCode(billno, amount, "31", notifyUrl, resultUrl, channel, request);
	}

	private static PrepareResult prepareWithQRCode(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request) {
		HashMap<String, String> params = new HashMap<>();
		params.put("version", "v1"); // 艾付分配的商户号
		params.put("merchant_no", channel.getMerCode()); // 艾付分配的商户号
		params.put("order_no", billno); // 商户系统唯一的订单编号
		params.put("goods_name", MD5.base64Encoder("temp", "UTF-8"));
		params.put("order_amount", amount + "");
		params.put("backend_url", notifyUrl);
		params.put("frontend_url", resultUrl);
		params.put("reserve", channel.getMerCode());
		params.put("pay_mode", "09");
		params.put("bank_code", "QQSCAN");
		params.put("card_type", "2");
		String src = "version=" + params.get("version") + "&merchant_no=" + params.get("merchant_no") + "&order_no="
				+ billno + "&goods_name=" + params.get("goods_name") + "&order_amount=" + params.get("order_amount")
				+ "&backend_url=" + params.get("backend_url") + "&frontend_url=" + resultUrl + "&reserve="
				+ params.get("reserve") + "&pay_mode=" + params.get("pay_mode") + "&bank_code=" + params.get("bank_code") + "&card_type="
				+ params.get("card_type");
		src += "&key=" + channel.getMd5Key();
	    String sign  =  DigestUtils.md5Hex(src);
		params.put("sign", sign); // 签名
        try {
        	String url = "https://pay.ifeepay.com/gateway/pay.jsp";
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            AFScanResult scanResult = JSON.parseObject(retStr, AFScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            if (!"00".equals(scanResult.getResult_code())) {
                log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getResult_code() + "，" + scanResult.getResult_msg());
            }
            
             String pay_QR= scanResult.getCode_url();
            if (StringUtils.isEmpty(pay_QR)) {
                log.error(channel.getName() + "获取二维码时返回空的二维码,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setJumpType(PrepareResult.JUMP_TYPE_QR);
            prepareResult.setSuccess(true);
            String qrCode = ImageUtil.encodeQR(pay_QR, 200, 200);
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
			Map<String, String> params = new TreeMap<>();
			params.put("merchant_no", resMap.get("merchant_no"));
			params.put("order_no", resMap.get("order_no"));
			params.put("order_amount", resMap.get("order_amount"));
			params.put("original_amount", resMap.get("original_amount"));
			params.put("upstream_settle", resMap.get("upstream_settle"));
			params.put("result", resMap.get("result"));
			params.put("pay_time", resMap.get("pay_time"));
			params.put("trace_id", resMap.get("trace_id"));
			params.put("reserve", resMap.get("reserve"));
			
			String serverSign = resMap.get("sign");

			if (!channel.getMerCode().equalsIgnoreCase(params.get("merchant_no"))) {
				log.warn(channel.getName() + "回调本地验证失败，商户号不一致，回调：" + JSON.toJSONString(resMap));
				return false;
			}
			String src = "merchant_no=" + params.get("merchant_no") + "&order_no="
					+ params.get("order_no") + "&order_amount=" + params.get("order_amount") + "&original_amount=" + params.get("original_amount") + "&upstream_settle=" + params.get("upstream_settle")
					+ "&result=" + params.get("result") + "&pay_time=" + params.get("pay_time") + "&trace_id="
					+ params.get("trace_id") + "&reserve=" + params.get("reserve") ;
			src += "&key=" + channel.getMd5Key();
		    String sign  =  DigestUtils.md5Hex(src);
		    
			if (!serverSign.equalsIgnoreCase(sign)) {
				log.error(channel.getName() + "回调本地验证失败，参数签名与服务器不一致，服务器验证：" + serverSign + ",回调："
						+ JSON.toJSONString(resMap));
				return false;
			}

			return true;
		} catch (Exception e) {
			log.error(channel.getName() + "回调本地验证发生异常", e);
		}
		return false;
	}

	private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap) {
		String outOrderId = resMap.get("order_no"); // 商户订单
		Map<String, String> params = new HashMap<>();
		params.put("order_no", outOrderId); // 商户系统唯一的订单编号
		params.put("merchant_no", resMap.get("merchant_no"));
		String src = "merchant_no=" + resMap.get("merchant_no") + "&order_no="+ params.get("order_no")  + "&key=" + channel.getMd5Key();
	    String sign  =  DigestUtils.md5Hex(src);
		params.put("sign", sign);
		try {
			String url ="https://pay.ifeepay.com/gateway/queryOrder.jsp";

			String retStr = HttpClientUtil.post(url, params, null, 10000);
			if (StringUtils.isEmpty(retStr)) {
				log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(resMap));
				return retVerifyFailed("远程验证失败，获取订单失败");
			}

			AFQueryResult queryResult = JSON.parseObject(retStr, AFQueryResult.class);
			if (queryResult == null) {
				log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(resMap));
				return retVerifyFailed("远程验证失败，获取订单失败");
			}

			if (!"000000".equals(queryResult.getResult_code())) {
				log.error(
						channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
				return retVerifyFailed("远程验证失败，订单未支付");
			}
			
			if (!"S".equals(queryResult.getResult())) {
				log.error(
						channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
				return retVerifyFailed("远程验证失败，订单未支付");
			}
			
			VerifyResult verifyResult = new VerifyResult();
			verifyResult.setSelfBillno(outOrderId);
			verifyResult.setChannelBillno(outOrderId);
			double money = Double.parseDouble(queryResult.getOrder_amount());
			verifyResult.setRequestMoney(money);
			verifyResult.setReceiveMoney(money);
			verifyResult.setPayTime(new Date());
			verifyResult.setSuccess(true);
			verifyResult.setOutput(OUTPUT_SUCCESS);
			verifyResult.setSuccessOutput(OUTPUT_SUCCESS);
			verifyResult.setFailedOutput(OUTPUT_FAILED);
			return verifyResult;
		} catch (Exception e) {
			log.error(channel.getName() + "自动上分远程验证异常", e);
			return retVerifyFailed("远程验证失败，获取订单失败");
		}
	}

	private static String getCusSign(String merCode, String ext1, String billno, long money) {
		String _ext1 = ext1;
		if (StringUtils.isEmpty(_ext1)) {
			_ext1 = CUS_EXT1;
		}
		return DigestUtils.md5Hex(merCode + _ext1 + billno + money).toUpperCase();
	}
}
