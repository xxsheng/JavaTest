package lottery.domains.content.payment.RX;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javautils.http.HttpClientUtil;
import lottery.domains.content.AbstractPayment;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.payment.RX.utils.Base64;
import lottery.domains.content.payment.RX.utils.Base64Utils;
import lottery.domains.content.payment.RX.utils.RSAEncrypt;
import lottery.domains.content.payment.zs.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 荣讯代付，把MD5 key设置到md5Key字段，并绑定代付IP
 * Created by Cavan on 2017-09-04.
 */
@Component
public class RXPayment extends AbstractPayment implements InitializingBean {
    private static Map<Integer, String> BRANCH_IDS = new HashMap<>();
    public static final String NONE_NOTIFY_URL = "http://www.baidu.com";

    @Value("${rx.daifu.url}")
    private String daifuUrl;

    @Value("${rx.daifu.queryurl}")
    private String daifuQueryUrl;

    @Override
    public void afterPropertiesSet() throws Exception {
        BRANCH_IDS.put(1, "01000017");
        BRANCH_IDS.put(2, "01050000");
        BRANCH_IDS.put(3, "01000001");
        BRANCH_IDS.put(4, "01000010");
        BRANCH_IDS.put(5, "01000002");
        BRANCH_IDS.put(6, "01000003");
        BRANCH_IDS.put(7, "01000012");
        BRANCH_IDS.put(8, "01000011");
        BRANCH_IDS.put(9, "01000004");
        BRANCH_IDS.put(13, "01000015");
        BRANCH_IDS.put(14, "01000014");
        BRANCH_IDS.put(15, "01000008");
        BRANCH_IDS.put(16, "01000000");
        BRANCH_IDS.put(18, "01000007");
        BRANCH_IDS.put(19, "01000005");
        BRANCH_IDS.put(20, "01000006");
        BRANCH_IDS.put(23, "01000009");
    }

    @Override
    public String daifu(WebJSONObject json, UserWithdraw order, UserCard card, PaymentChannelBank bank, PaymentChannel channel) {
        try {
            logStart(order, bank, channel);
            return daifuInternal(json, order, card, bank, channel);
        } catch (Exception e) {
            logException(order, bank, channel, "代付请求失败", e);
            json.set(2, "2-4000");
            return null;
        }
    }

    private String daifuInternal(WebJSONObject json, UserWithdraw order, UserCard card, PaymentChannelBank bank, PaymentChannel channel){
        String bankCode = BRANCH_IDS.get(card.getBankId());
        if (bankCode == null) {
            json.set(2, "2-4012");
            return null;
        }

        try {
    		String orderId = order.getBillno();//商户订单号
    		Long totalAmount = StringUtil.changeY2F(order.getRecMoney()+"");//金额单位 分
    		String merchantCode = channel.getMerCode();//商户号
    		String intoCardNo = order.getCardId();////收款账号
    		String intoCardName = order.getCardName();//收款账户名
    		String type = "ToPay";
    		String data = "{\"accName\":\"" + URLEncoder.encode(intoCardName, "utf-8") + "\"," +
    		        "\"accNo\":\"" + intoCardNo + "\"," + "\"account\":\"" + merchantCode + "\"," +
    		        "\"amount\":\"" + totalAmount + "\"," + "\"banktype\":\"" + bankCode + "\"," +
    		        "\"notify_url\":\"" + URLEncoder.encode(NONE_NOTIFY_URL, "utf-8") + "\"," + "\"orderId\":\"" + orderId + "\"," +
    		        "\"type\":\"" + type + "\"" + "}";

            //公钥加密过程
            byte[] cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(channel.getRsaPlatformPublicKey()), data.getBytes());
            String cipher = Base64Utils.encode(cipherData);
            //私钥加密过程
            cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(channel.getRsaPrivateKey()), data.getBytes());
            String signature = Base64Utils.encode(cipherData);
            JSONObject params = new JSONObject();
            params.put("data", cipher); // 持卡人姓名、银行卡号、 商户号、代付金额、银行类型、  异步通知地址、代付订单号、请求类型等参数组成json串， 使用通道公钥生成公钥加密串 后Base64转码
            params.put("signature", signature); //  持卡人姓名、银行卡号、商户号、代付金额、银行类型、   异步通知地址、代付订单号、 请求类型等参数组成json串，使用商户私钥生成签名数据 后Base64转码  签名算法MD5withRSA

            String retStr = HttpClientUtil.postAsStream(daifuUrl, JSON.toJSONString(params), null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, bank, channel, "接口返回空，可能是请求超时");
                json.set(-1, "-1"); // 这里-1表示连接异常
                return null;
            }

            JSONObject resp = JSONObject.parseObject(retStr);
            String retData = resp.getString("data");
            String retSignature = resp.getString("signature");
            String reqstate = resp.getString("state");
            String reqmessage = resp.getString("message");
            if (StringUtils.isNotEmpty(reqstate)) {
                logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                String msg = transferErrorMsg(retStr, reqmessage);
                json.setWithParams(2, "2-4002", StringUtils.abbreviate(msg, 20));
                return null;
			}

            if (StringUtils.isEmpty(retSignature) || StringUtils.isEmpty(retData)) {
                logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                String msg = transferErrorMsg(retStr, reqmessage);
                json.setWithParams(2, "2-4002", StringUtils.abbreviate(msg, 20));
                return null;
            }

            //商户私钥解密
            byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(channel.getRsaPrivateKey()), Base64.decode(retData));
        	String restr = new String(res);

        	boolean sign = RSAEncrypt.publicsign(restr, Base64.decode(retSignature), RSAEncrypt.loadPublicKeyByStr(channel.getRsaPlatformPublicKey()));

            JSONObject jsonObject = JSON.parseObject(restr);
            String resorderId = jsonObject.getString("orderId");//必须大于等于二十位
            String state = jsonObject.getString("state");//代付请求成功：38
            String message = jsonObject.getString("message");
            if (isSuccessDaifuState(state) && StringUtils.isNotEmpty(resorderId)) {
                if (!sign) {
                    logWarn(order, bank, channel, "请求成功，但数据验签失败，返回数据：" + retStr);
                    json.set(2, "2-4008");
                    return resorderId;
                }
                else {
                    logSuccess(order, resorderId, channel);
                    return resorderId;
                }
            }else{
                // 返回失败，查询订单状态
                logError(order, bank, channel, "请求返回状态表示失败，返回数据："+retStr+"，开始查询订单状态");
                RXDaifuQueryResult queryResult = query(order, channel);
                if (isSuccessDaifuQueryState(queryResult.getOrderId_state())
                        && "61".equalsIgnoreCase(queryResult.getState())
                        && StringUtils.isNotEmpty(queryResult.getOrderId())) {

                    logSuccess(order, queryResult.getOrderId(), channel);
                    return queryResult.getOrderId();

                }
                else if (!isSuccessDaifuQueryState(queryResult.getOrderId_state())) {
                    String stateStr = getDaifuQueryStateStr(queryResult.getOrderId_state());
                    logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4002", stateStr);
                    return null;
                }
                else {
                    logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                    String msg = transferErrorMsg(restr, message);
                    json.setWithParams(2, "2-4002", StringUtils.abbreviate(msg, 20));
                    return null;
                }
            }
        } catch (Exception e) {
            logException(order, bank, channel, "代付请求失败", e);
            json.set(-1, "-1"); // 这里-1表示连接异常
            return null;
        }
    }

    public RXDaifuQueryResult query(UserWithdraw order, PaymentChannel channel){
        try {
            String orderId = order.getBillno(); // 商户订单号
            String merchantCode = channel.getMerCode(); // 商户号
            String type = "ToQuery";
            String data = "{\"account\":\"" + merchantCode + "\"," +
    	    	        "\"orderId\":\"" + orderId + "\"," +
    	    	        "\"type\":\"" + type + "\"" + "}";
    	    //公钥加密过程
	        byte[] cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(channel.getRsaPlatformPublicKey()),data.getBytes());
	        String cipher = Base64Utils.encode(cipherData);
	        //私钥加密过程
	        cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(channel.getRsaPrivateKey()),data.getBytes());
	        String signature = Base64Utils.encode(cipherData);

	        JSONObject params = new JSONObject();
	        params.put("data", cipher); // 持卡人姓名、银行卡号、 商户号、代付金额、银行类型、  异步通知地址、代付订单号、请求类型等参数组成json串， 使用通道公钥生成公钥加密串 后Base64转码
	        params.put("signature", signature); //  持卡人姓名、银行卡号、商户号、代付金额、银行类型、   异步通知地址、代付订单号、 请求类型等参数组成json串，使用商户私钥生成签名数据 后Base64转码  签名算法MD5withRSA

	        String retStr = HttpClientUtil.postAsStream(daifuQueryUrl, JSON.toJSONString(params), null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, null, channel, "查询请求失败，发送请求后返回空数据");
                return null;
            }

            logInfo(order, null, channel, "查询返回数据：" + retStr);

            JSONObject resp = JSONObject.parseObject(retStr);
            String retData = resp.getString("data");
            String retSignature = resp.getString("signature");
            if (StringUtils.isEmpty(retSignature) || StringUtils.isEmpty(retData)) {
                logError(order, null, channel, "查询返回数据表示失败");
                return null;
            }

            //商户私钥解密
            byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(channel.getRsaPrivateKey()), Base64Utils.decode(retData));
            String restr = new String(res);
            RXDaifuQueryResult result = JSON.parseObject(restr, RXDaifuQueryResult.class);
            if (result == null) {
                logError(order, null, channel, "查询请求失败，解析返回数据失败");
                return null;
            }
            if (!"61".equals(result.getState())) {
                logError(order, null, channel, "查询返回不是61，表示失败");
                return null;
            }

            return result;
        } catch (Exception e) {
            logException(order, null, channel, "查询请求失败", e);
            return null;
        }
    }

    private String transferErrorMsg(String retStr, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return retStr;
        }

        return msg;
    }

    private boolean isSuccessDaifuState(String state) {
        return "38".equalsIgnoreCase(state);
    }

    private boolean isSuccessDaifuQueryState(String state) {
        if ("2".equalsIgnoreCase(state)) return true;
        if ("4".equalsIgnoreCase(state)) return true;

        return false;
    }

    private String getDaifuQueryStateStr(String state) {
        if ("1".equalsIgnoreCase(state)) return "代付失败";
        if ("2".equalsIgnoreCase(state)) return "代付受理中";
        if ("3".equalsIgnoreCase(state)) return "代付失败退回";
        if ("4".equalsIgnoreCase(state)) return "代付成功";
        return "未知状态";
    }

    public int transferBankStatus(String bankStatus) {
        int remitStatus = Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN;
        switch (bankStatus) {
            case "1": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_PROCESS_FAILED; break;
            case "2": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING; break;
            case "3": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_PROCESS_FAILED; break;
            case "4": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED; break;
        }
        return remitStatus;
    }
}
