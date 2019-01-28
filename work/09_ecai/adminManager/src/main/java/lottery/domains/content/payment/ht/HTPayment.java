package lottery.domains.content.payment.ht;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.http.HttpClientUtil;
import lottery.domains.content.AbstractPayment;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.payment.utils.MoneyFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 汇通代付，把MD5 key设置到md5Key字段，并绑定代付IP
 * Created by Nick on 2017-09-04.
 */
@Component
public class HTPayment extends AbstractPayment{
    @Value("${ht.daifu.url}")
    private String daifuUrl;

    @Value("${ht.daifu.queryurl}")
    private String daifuQueryUrl;

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
        try {
            String amount = MoneyFormat.pasMoney(order.getRecMoney());
            String time = new Moment().toSimpleTime();

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("merchant_code", channel.getMerCode());
            paramsMap.put("order_amount", amount);
            paramsMap.put("trade_no", order.getBillno());
            paramsMap.put("order_time", time);
            paramsMap.put("bank_code", bank.getCode());
            paramsMap.put("account_name", order.getCardName());
            paramsMap.put("account_number", order.getCardId());
            String sign = getSign(paramsMap, channel.getMd5Key());
            paramsMap.put("sign", sign);

            String retStr = HttpClientUtil.post(daifuUrl, paramsMap, null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, bank, channel, "接口返回空，可能是请求超时");
                json.set(-1, "-1"); // 这里-1表示连接异常
                return null;
            }

            HTPayResult result = JSON.parseObject(retStr, HTPayResult.class);

            if (result == null) {
                logError(order, bank, channel, "请求失败，解析返回数据失败，返回数据为：" + retStr);
                json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                return null;
            }

            if (isAccepted(result)) {
                logSuccess(order, result.getOrderId(), channel);
                return result.getOrderId();
            }
            else {
                if (StringUtils.isEmpty(result.getErrrorMsg())) {
                    // 返回失败，查询订单状态
                    String msg = "未知错误";
                    logError(order, bank, channel, "请求返回空的错误消息，返回数据："+retStr+"，开始查询订单状态");

                    HTPayResult queryResult = query(order, channel);
                    if (queryResult != null && isAccepted(queryResult)) {
                        logSuccess(order, queryResult.getOrderId(), channel);
                        return queryResult.getOrderId();
                    }
                    else {
                        logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                        json.setWithParams(2, "2-4002", msg);
                        return null;
                    }
                }
                else {
                    logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4002", result.getErrrorMsg());
                    return null;
                }

            }
        } catch (Exception e) {
            // 连接超时
            logException(order, bank, channel, "代付请求失败", e);
            json.set(-1, "-1"); // 这里-1表示连接异常
            return null;
        }
    }

    public HTPayResult query(UserWithdraw order, PaymentChannel channel){
        try {
            String time = new Moment().toSimpleTime();
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("merchant_code", channel.getMerCode());
            paramsMap.put("now_date", time);
            paramsMap.put("trade_no", order.getBillno());
            String sign = getSign(paramsMap, channel.getMd5Key());
            paramsMap.put("sign", sign);

            String retStr = HttpClientUtil.post(daifuQueryUrl, paramsMap, null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, null, channel, "查询请求失败，发送请求后返回空数据");
                return null;
            }

            logInfo(order, null, channel, "查询返回数据：" + retStr);

            HTPayResult result = JSON.parseObject(retStr, HTPayResult.class);

            if (result == null) {
                logError(order, null, channel, "查询请求失败，解析返回数据失败");
                return null;
            }

            return result;
        } catch (Exception e) {
            logException(order, null, channel, "查询请求失败", e);
            return null;
        }
    }

    private static String getSign(Map<String, String> paramsMap, String md5Key) {
        TreeMap<String, String> signMap = new TreeMap<>(paramsMap);

        StringBuffer signStr = new StringBuffer();

        Iterator<Map.Entry<String, String>> it = signMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            if ("sign".equalsIgnoreCase(key) || "reqBody".equalsIgnoreCase(key)) {
                continue;
            }

            String value = entry.getValue();
            if (StringUtils.isEmpty(value)) { // 主动忽略空值并且值为空
                continue;
            }

            signStr.append(entry.getKey()).append("=") .append(value);
            if (it.hasNext()) {
                signStr.append("&");
            }
        }

        signStr.append("&key=").append(md5Key);

        String sign = DigestUtils.md5Hex(signStr.toString());

        return sign;
    }

    /**
     * 第三方是否已经接受请求
     */
    public boolean isAccepted(HTPayResult result) {
        return "true".equalsIgnoreCase(result.getIsSuccess())
                && isAcceptedBankStatus(result.getBankStatus())
                && StringUtils.isNotEmpty(result.getOrderId());
    }

    public int transferBankStatus(String bankStatus) {
        int remitStatus = Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN;
        switch (bankStatus) {
            case "0": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_THIRD_UNPROCESS; break;
            case "1": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING; break;
            case "2": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED; break;
            case "3": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_THIRD_PROCESS_FAILED; break;
        }
        return remitStatus;
    }

    /**
     * 是否第三方待处理
     */
    public boolean isUnprocess(HTPayResult result) {
        return "true".equalsIgnoreCase(result.getIsSuccess())
                && "0".equals(result.getBankStatus())
                && StringUtils.isNotEmpty(result.getOrderId());
    }

    /**
     * 是否银行处理中
     */
    public boolean isBankingProcessing(HTPayResult result) {
        return "true".equalsIgnoreCase(result.getIsSuccess())
                && "1".equals(result.getBankStatus())
                && StringUtils.isNotEmpty(result.getOrderId());
    }

    /**
     * 是否已打款
     */
    public boolean isBankingProcessSuccessed(HTPayResult result) {
        return "true".equalsIgnoreCase(result.getIsSuccess())
                && "2".equals(result.getBankStatus())
                && StringUtils.isNotEmpty(result.getOrderId());
    }

    /**
     * 是否失败
     */
    public boolean isBankingProcessedFaild(HTPayResult result) {
        return "true".equalsIgnoreCase(result.getIsSuccess())
                && "3".equals(result.getBankStatus())
                && StringUtils.isNotEmpty(result.getOrderId());
    }

    private boolean isAcceptedBankStatus(String status) {
        if ("0".equalsIgnoreCase(status)) return true;
        if ("1".equalsIgnoreCase(status)) return true;
        if ("2".equalsIgnoreCase(status)) return true;

        return false;
    }
}
