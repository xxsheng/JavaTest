package lottery.domains.pool.payment.ay;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import javautils.http.HttpClientUtil;
import javautils.image.ImageUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;

/**
 * 安亿支付 Created by Nick on 2018-05-09.
 */
public class AYPayment extends AbstractPayment {
	private static final Logger log = LoggerFactory.getLogger(AYPayment.class);

	private static final String CUS_EXT1 = "FFHcg,S$QL:H[tkl0dlqbd98o{2xyT";
	private static final String OUTPUT_SUCCESS = "true";
	private static final String OUTPUT_FAILED = "false";

	public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
			PaymentChannel channel, HttpServletRequest request) {
		try {
			switch (channel.getChannelCode()) {
			case Global.PAYMENT_CHANNEL_AYWECHAT:
				return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request,"wechat");
			case Global.PAYMENT_CHANNEL_AYALIPAY:
				return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request,"alipay");
			}

			return null;
		} catch (Exception e) {
			log.error(channel.getName() + "封装支付参数异常", e);
			return retPrepareFailed("支付通道异常，请重试！");
		}
	}
	
	 private static String generateSignature(final Map<String,String> data,String key){
		  Set<String> keySet = data.keySet();
		  String[]keyArray = keySet.toArray(new String[keySet.size()]);
		  Arrays.sort(keyArray);
		  StringBuilder sb = new StringBuilder(256);
		  for(String k:keyArray){
		   if(data.get(k) != null && data.get(k).trim().length()>0){
		    sb.append(data.get(k).trim());
		   }
		   
		  }
		  sb.append(key);
		  return MD5.MD5Encoder(sb.toString(),"UTF-8");
		 }



	private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl,
			String resultUrl, PaymentChannel channel, HttpServletRequest request,String type) {
		HashMap<String, String> params = new HashMap<>();
		params.put("platSource", channel.getMerCode()); // 商户号
		params.put("payType", type); // 商户号
		params.put("orderNo", billno); // 商户系统唯一的订单编号
		params.put("payAmt", amount+"");
		params.put("notifyUrl", notifyUrl);
		String signStr = generateSignature(params,channel.getMd5Key());
        String sign  =  signStr.toUpperCase();
		params.put("sign", sign); // 签名
        try {
          	String url = channel.getPayUrl()+"scan/getQrCode";
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            AYScanResult scanResult = JSON.parseObject(retStr, AYScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            if (!"true".equals(scanResult.getSuccess() )) {
                log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getStatus()+ "，" + scanResult.getMessage());
            }
            
            Map<String,String> data = scanResult.getInfo();
            if (data.isEmpty()) {
                log.error(channel.getName() + "获取请求地址返回空的,订单号：" + billno);
                return retPrepareFailed("获取请求地址失败，请重试！");
            }
             String pay_QR= data.get("qrCode");
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
        	String url = channel.getPayUrl()+"/gateway/pay.jsp";
            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "获取二维码时返回空,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

            AYScanResult scanResult = JSON.parseObject(retStr, AYScanResult.class);
            if (scanResult == null) {
                log.error(channel.getName() + "获取二维码时解析JSON数据失败,订单号：" + billno);
                return retPrepareFailed("获取二维码失败，请重试！");
            }

/*            if (!"00".equals(scanResult.getResult_code())) {
                log.error(channel.getName() + "获取二维码时返回状态表示失败,订单号：" + billno + "，返回：" + retStr);
                return retPrepareFailed("获取二维码失败，请重试！" + scanResult.getResult_code() + "，" + scanResult.getResult_msg());
            }*/
            
          String pay_QR= null;
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
			params.put("platSource", resMap.get("platSource"));
			params.put("orderNo", resMap.get("orderNo"));
			params.put("payAmt", resMap.get("payAmt"));
			params.put("success", resMap.get("success"));
			String serverSign = resMap.get("sign");

			if (!channel.getMerCode().equalsIgnoreCase(params.get("platSource"))) {
				log.warn(channel.getName() + "回调本地验证失败，商户号不一致，回调：" + JSON.toJSONString(resMap));
				return false;
			}
			String signStr = generateSignature(params,channel.getMd5Key());
	        String sign  =  signStr.toUpperCase();
		    
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
		String outOrderId = resMap.get("orderNo"); // 商户订单
		Map<String, String> params = new HashMap<>();
		params.put("orderNo", outOrderId); // 商户系统唯一的订单编号
		params.put("platSource", resMap.get("platSource"));
		String signStr = generateSignature(params,channel.getMd5Key());
        String sign  =  signStr.toUpperCase();
		params.put("sign", sign);
		try {
          	String url = channel.getExt1();
			String retStr = HttpClientUtil.post(url, params, null, 10000);
			if (StringUtils.isEmpty(retStr)) {
				log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(resMap));
				return retVerifyFailed("远程验证失败，获取订单失败");
			}

			AYScanResult queryResult = JSON.parseObject(retStr, AYScanResult.class);
			if (queryResult == null) {
				log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(resMap));
				return retVerifyFailed("远程验证失败，获取订单失败");
			}
		
			if (!"true".equals(queryResult.getSuccess())) {
				log.error(
						channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
				return retVerifyFailed("远程验证失败，订单未支付");
			}
			List<AyPayData> aypayList = JSON.parseArray(queryResult.getInfo().get("tradeSerialList"),AyPayData.class);
			
			VerifyResult verifyResult = new VerifyResult();
			verifyResult.setSelfBillno(outOrderId);
			verifyResult.setChannelBillno(outOrderId);
			AyPayData payData = new AyPayData();
			if(aypayList != null && aypayList.size()!=0){
				 payData = aypayList.get(0);
				 if(payData != null && ("收款中".equals(payData.getStatus()) || "支付成功".equals(payData.getStatus()))){
						double money = Double.parseDouble(payData.getTradeAmt());
						verifyResult.setRequestMoney(money);
						verifyResult.setReceiveMoney(money);
						verifyResult.setPayTime(new Date());
						verifyResult.setSuccess(true);
						verifyResult.setOutput(OUTPUT_SUCCESS);
						verifyResult.setSuccessOutput(OUTPUT_SUCCESS);
						verifyResult.setFailedOutput(OUTPUT_FAILED);
						return verifyResult;
				 }
			}
			double money = Double.parseDouble("0");
			verifyResult.setRequestMoney(money);
			verifyResult.setReceiveMoney(money);
			verifyResult.setPayTime(new Date());
			verifyResult.setSuccess(false);
			verifyResult.setOutput(OUTPUT_FAILED);
			verifyResult.setSuccessOutput(OUTPUT_FAILED);
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
