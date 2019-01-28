package lottery.domains.content.payment.fkt;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import javautils.http.HttpClientUtil;
import javautils.http.ToUrlParamUtils;
import lottery.domains.content.AbstractPayment;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.payment.utils.MoneyFormat;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.TreeMap;

/**
 * 福卡通代付
 * Created by Nick on 2017-12-30.
 */
@Component
public class FKTPayment extends AbstractPayment{
    private static final String INPUT_CHARSET = "UTF-8";
    private static final String REMARK = "df";

    private static final String OUTPUT_SUCCESS = "success";
    private static final String OUTPUT_FAILED = "failed";

    @Value("${fkt.daifu.url}")
    private String daifuUrl;

    @Value("${fkt.daifu.queryurl}")
    private String queryUrl;

    @Autowired
    private LotteryDataFactory dataFactory;

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
        try {
            String amount = MoneyFormat.moneyToYuanForPositive(order.getRecMoney()+"");
            String currentDate  = new Moment().toSimpleTime();

            Map<String, String> paramsMap = new TreeMap<>();
            paramsMap.put("input_charset", INPUT_CHARSET);
            paramsMap.put("merchant_code", channel.getMerCode());
            paramsMap.put("amount", amount);
            paramsMap.put("transid", order.getBillno());
            paramsMap.put("bitch_no", order.getBillno());
            paramsMap.put("currentDate", currentDate);
            paramsMap.put("bank_name", bank.getCode());
            paramsMap.put("account_name", order.getCardName());
            paramsMap.put("account_number", order.getCardId());
            paramsMap.put("remark", REMARK);
//            String notifyUrl = dataFactory.getWithdrawConfig().getApiPayNotifyUrl() + "/api-pay-notify/" + channel.getId();
//            String notifyUrl = notify;
//            paramsMap.put("url", notifyUrl);

            String sign = sign(paramsMap, channel);
            paramsMap.put("sign", sign);// 签名数据

            String url = daifuUrl + "?_=" + System.currentTimeMillis();

            String retStr = HttpClientUtil.post(url, paramsMap, null, 60000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, bank, channel, "接口返回空，可能是请求超时");
                json.set(-1, "-1"); // 这里-1表示连接异常
                return null;
            }

            FKTPayResult result = JSON.parseObject(retStr, FKTPayResult.class);

            if (result == null) {
                logError(order, bank, channel, "请求失败，解析返回数据失败，返回数据为：" + retStr);
                json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                return null;
            }

            if (isAcceptedRequest(result.getIsSuccess())
                    && isAcceptedBankStatus(result.getBankStatus())) {
                logSuccess(order, result.getOrderId(), channel);
                return result.getOrderId();
            }
            else {
                if (StringUtils.isEmpty(result.getErrrorMsg())) {
                    // 返回失败，查询订单状态
                    String msg = "未知错误";
                    logError(order, bank, channel, "请求返回空的错误消息，返回数据："+retStr+"，开始查询订单状态");

                    FKTPayResult queryResult = query(order, channel);
                    if (isAcceptedRequest(queryResult.getIsSuccess())
                            && isAcceptedBankStatus(queryResult.getBankStatus())) {
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
            logException(order, bank, channel, "代付请求失败", e);
            json.set(-1, "-1"); // 这里-1表示连接异常
            return null;
        }
    }

    public FKTPayResult query(UserWithdraw order, PaymentChannel channel){
        return query(order.getPayBillno(), channel);
    }

    public FKTPayResult query(String orderId, PaymentChannel channel){
        try {
            Map<String, String> paramsMap = new TreeMap<>();
            paramsMap.put("input_charset", INPUT_CHARSET);
            paramsMap.put("merchant_code", channel.getMerCode());
            paramsMap.put("currentDate", new Moment().toSimpleTime());
            paramsMap.put("order_id", orderId);

            String sign = sign(paramsMap, channel);
            paramsMap.put("sign", sign);// 签名数据

            String url = queryUrl + "?_=" + System.currentTimeMillis();

            String retStr = HttpClientUtil.post(url, paramsMap, null, 60000);

            if (StringUtils.isEmpty(retStr)) {
                logError(channel, "查询请求失败，发送请求后返回空数据");
                return null;
            }

            logInfo(channel, "查询返回数据：" + retStr);

            FKTPayResult result = JSON.parseObject(retStr, FKTPayResult.class);

            if (result == null) {
                logError(channel, "查询请求失败，解析返回数据失败");
                return null;
            }

            return result;
        } catch (Exception e) {
            logException(channel, "查询请求失败", e);
            return null;
        }
    }

    private static String sign(Map<String, String> paramsMap, PaymentChannel channel)  {
        String sign = ToUrlParamUtils.toUrlParamWithoutEmpty(paramsMap, "&");
        sign += "&key=" + channel.getMd5Key();

        sign = DigestUtils.md5Hex(sign);
        sign = DigestUtils.md5Hex(sign.toUpperCase());
        sign = DigestUtils.md5Hex(sign);
        return sign;
    }

    public boolean isAcceptedRequest(String isSuccess) {
        return "true".equalsIgnoreCase(isSuccess);
    }

    public boolean isAcceptedBankStatus(String bankStatus) {
        if ("0".equalsIgnoreCase(bankStatus)) return true;
        if ("1".equalsIgnoreCase(bankStatus)) return true;
        if ("2".equalsIgnoreCase(bankStatus)) return true;
        return false;
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

    public static void main(String[] args) {
        try {
            Map<String, String> paramsMap = new TreeMap<>();
            paramsMap.put("input_charset", INPUT_CHARSET);
            paramsMap.put("merchant_code", "84354848");
            paramsMap.put("currentDate", "2017-12-30 18:07:07");
//            paramsMap.put("currentDate", Base64Util.getBase64("2017-12-30 18:27:07"));
//            paramsMap.put("order_id", "17123018270684892640");
            paramsMap.put("order_id", "171230180325dYO6Xf9X");
//            paramsMap.put("order_id", "171230180325dYO6Xf9X");
//            paramsMap.put("transid", "17123018270684892640");

            String sign = ToUrlParamUtils.toUrlParamWithoutEmpty(paramsMap, "&");
            sign += "&key=" + "aa34ccc93e5202cc7142b883d0aef189";

            sign = DigestUtils.md5Hex(sign);
            sign = DigestUtils.md5Hex(sign.toUpperCase());
            sign = DigestUtils.md5Hex(sign);

            paramsMap.put("sign", sign);// 签名数据

            System.out.println("请求参数：" + paramsMap);

            String url = "http://df.fktpay.vip/gateway/df_query.html";

            System.out.println("POST请求（Form表单）：" + url);


            String paramsUrl = ToUrlParamUtils.toUrlParam(paramsMap);
            String _url = url + "?_=" + System.currentTimeMillis() + "&" + paramsUrl;
            String retStr = new RestTemplate().postForObject(_url, null, String.class);

            System.out.println("请求返回：" + retStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            Map<String, String> paramsMap = new TreeMap<>();
//            paramsMap.put("input_charset", INPUT_CHARSET);
//            paramsMap.put("merchant_code", "84354848");
//            paramsMap.put("currentDate", "2017-12-30 18:07:07");
////            paramsMap.put("currentDate", Base64Util.getBase64("2017-12-30 18:27:07"));
//            paramsMap.put("order_id", "171230180325dYO6Xf9X");
////            paramsMap.put("transid", "1712301827068492640");
//
//            String sign = ToUrlParamUtils.toUrlParamWithoutEmpty(paramsMap, "&");
//            sign += "&key=" + "aa34ccc93e5202cc7142b883d0aef189";
//
//            sign = DigestUtils.md5Hex(sign);
//            sign = DigestUtils.md5Hex(sign.toUpperCase());
//            sign = DigestUtils.md5Hex(sign);
//
//            paramsMap.put("sign", sign);// 签名数据
//
//            System.out.println("请求参数：" + paramsMap);
//
//            String url = "http://df.fktpay.vip/gateway/df_query.html";
//
//            System.out.println("POST请求（Form表单）：" + url);
//
//            String retStr = HttpClientUtil.post(url, paramsMap, null, 30000);
//
//            System.out.println("请求返回：" + retStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
