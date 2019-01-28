package lottery.domains.pool.payment.htf;

import com.alibaba.fastjson.JSON;
import javautils.http.HttpClientUtil;
import javautils.http.UrlParamUtils;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.cfg.MoneyFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 汇天付支付
 * Created by Nick on 2017-12-30.
 */
public class HTFPayment extends AbstractPayment {
    private static final Logger log = LoggerFactory.getLogger(HTFPayment.class);

    private static final String P_ISSMART = "0";

    private static final String CHANNEL_ID_WY = "1"; // 网银支付
    private static final String CHANNEL_ID_ALIPAY = "2"; // 支付宝扫码
    private static final String CHANNEL_ID_WECHAT = "3"; // 微信扫码
    private static final String CHANNEL_ID_QQ = "89"; // QQ 扫码
    private static final String CHANNEL_ID_JDPAY = "91"; // 京东钱包

    private static final String OUTPUT_SUCCESS = "ErrCode=0";
    private static final String OUTPUT_FAILED = "ProcessError";

    public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                                PaymentChannel channel, HttpServletRequest request) {
        try {
            switch(channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_HTF:
                    return prepareWangYing(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_HTFQQ:
                    return prepareQQ(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_HTFWECHAT:
                    return prepareWeChat(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_HTFALIPAY:
                    return prepareAlipay(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_HTFJDPAY:
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
    private static PrepareResult prepareWangYing(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                               PaymentChannel channel, HttpServletRequest request) {
        return prepareCommon(billno, amount, bankco, notifyUrl, resultUrl, channel, CHANNEL_ID_WY);
    }

    /**
     * 微信
     */
    private static PrepareResult prepareWeChat(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                     PaymentChannel channel, HttpServletRequest request) {
        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_WECHAT);
    }



    /**
     * 支付宝
     */
    private static PrepareResult prepareAlipay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                              PaymentChannel channel, HttpServletRequest request) {
        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_ALIPAY);
    }

    /**
     * QQ
     */
    private static PrepareResult prepareQQ(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                 PaymentChannel channel, HttpServletRequest request) {
        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_QQ);
    }

    /**
     * 京东
     */
    private static PrepareResult prepareJDPay(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                    PaymentChannel channel, HttpServletRequest request) {

        return prepareCommon(billno, amount, "", notifyUrl, resultUrl, channel, CHANNEL_ID_JDPAY);
    }

    private static PrepareResult prepareCommon(String billno, double amount, String bankco, String notifyUrl, String resultUrl, PaymentChannel channel, String pChannelID) {
        String money = MoneyFormat.moneyToYuanForPositive(amount+"");
        Map<String, String> params = new TreeMap<>();

        String subject = RandomStringUtils.random(8, true, true);
        String price = RandomUtils.nextInt(1, 200) + "";
        String quantity = RandomUtils.nextInt(1, 5) + "";

        params.put("P_UserID", channel.getMerCode()); // 商户编号如 1000001
        params.put("P_OrderID", billno); // 商户定单号（要保证唯一） ， 长度最长 32 字符
        params.put("P_CardID", ""); // 点卡交易时的充值卡卡号
        params.put("P_CardPass", ""); // 点卡交易时的充充值卡卡密
        params.put("P_FaceValue", money); // 申明交易金额
        params.put("P_ChannelID", pChannelID); // 支付方式， 支付方式编码： 参照附录 6.1
        params.put("P_Subject", subject); // 商品标题
        params.put("P_Price", price); // 商品售价
        params.put("P_Quantity", quantity); // 商品数量
        params.put("P_Description", bankco); // 支付方式为网银时的银行编码， 参照附录 6.2
        params.put("P_Notic", ""); // 商户备注信息
        params.put("P_ISsmart", P_ISSMART); // 固定参数 0

        params.put("P_Result_URL", notifyUrl); // 充值状态异步通知地址
        params.put("P_Notify_URL", resultUrl); // 充值后网页同步跳转地址

        String format = "%s|%s|%s|%s|%s|%s|%s";
        String sign = String.format(format, channel.getMerCode(), billno, "", "", money, pChannelID, channel.getMd5Key());
//        sign = sign.toLowerCase();
        sign = DigestUtils.md5Hex(sign).toLowerCase();

        params.put("P_PostKey", sign); // MD5 签名结果

        String formURL = channel.getPayUrl() + "/Pay/KDBank.aspx";
        return retPrepareWangYing(formURL, params);
    }

    /**
     * 验证回调
     */
    public static VerifyResult verify(PaymentChannel channel, Map<String, String> resMap){
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
            String P_UserId = resMap.get("P_UserId");
            String P_OrderId = resMap.get("P_OrderId");
            String P_CardId = resMap.get("P_CardId");
            String P_CardPass = resMap.get("P_CardPass");
            String P_FaceValue = resMap.get("P_FaceValue");
            String P_ChannelId = resMap.get("P_ChannelId");
            String P_PayMoney = resMap.get("P_PayMoney");
            String P_ErrCode = resMap.get("P_ErrCode");  // 交易状态码， 0 为支付成功， 其他参照附录 7.1
            String P_Subject = resMap.get("P_Subject");
            String P_Price = resMap.get("P_Price");
            String P_Quantity = resMap.get("P_Quantity");
            String P_Description = resMap.get("P_Description");
            String P_Notic = resMap.get("P_Notic");
            String P_ErrMsg = resMap.get("P_ErrMsg");
            String P_PostKey = resMap.get("P_PostKey");

            if ("0".equalsIgnoreCase(P_ErrCode)) {
                String format = "%s|%s|%s|%s|%s|%s|%s|%s|%s";
                String signStr = String.format(format, channel.getMerCode(), P_OrderId, P_CardId, P_CardPass, P_FaceValue, P_ChannelId, P_PayMoney, P_ErrCode, channel.getMd5Key());
//                signStr = signStr.toLowerCase();
                String serverSign = DigestUtils.md5Hex(signStr).toLowerCase();

                if (serverSign.equalsIgnoreCase(P_PostKey)){
                    return true;
                }
                else {
                    log.error(channel.getName() + "回调本地验证失败，参数签名与服务器不一致，服务器验证：" + serverSign + ",回调：" + JSON.toJSONString(resMap));
                    return false;
                }
            }
            else {
                log.warn(channel.getName() + "回调本地验证失败，参数状态表示不成功，回调：" + JSON.toJSONString(resMap));
                return false;
            }
        } catch (Exception e) {
            log.error(channel.getName() + "回调本地验证发生异常", e);
        }
        return false;
    }

    private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap){
        String P_OrderId = resMap.get("P_OrderId");
        String P_CardId = resMap.get("P_CardId");
        String P_FaceValue = resMap.get("P_FaceValue");
        String P_ChannelId = resMap.get("P_ChannelId");

        Map<String, String> params = new HashMap<>();
        params.put("P_UserID", channel.getMerCode()); // 商户编号如 100000
        params.put("P_OrderID", P_OrderId); // 商户定单号
        params.put("P_CardID", P_CardId); // 点卡交易时的充值卡卡号
        params.put("P_FaceValue", P_FaceValue); // 申明交易金额
        params.put("P_ChannelID", P_ChannelId); // 申明交易金额


        String format = "P_UserId=%s&P_OrderId=%s&P_ChannelId=%s&P_CardId=%s&P_FaceValue=%s&P_PostKey=%s";
        String sign = String.format(format, channel.getMerCode(), P_OrderId, P_ChannelId, P_CardId, P_FaceValue, channel.getMd5Key());
        sign = DigestUtils.md5Hex(sign).toLowerCase();
        params.put("P_PostKey", sign);

        try {
            String url = channel.getPayUrl() + "/Pay/Query.aspx";

            String retStr = HttpClientUtil.post(url, params, null, 100000);
            if (StringUtils.isEmpty(retStr)) {
                log.error(channel.getName() + "自动上分远程验证失败，返回空信息：请求信息：" + JSON.toJSONString(resMap));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            HTFQueryResult queryResult = transferResult(retStr);
            if (queryResult == null) {
                log.error(channel.getName() + "自动上分远程验证失败，解析JSON失败：请求信息：" + JSON.toJSONString(resMap));
                return retVerifyFailed("远程验证失败，获取订单失败");
            }

            if (!"1".equals(queryResult.getP_flag())) {
                log.error(channel.getName() + "自动上分远程验证失败，返回状态表示执行不成功：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

            if (!"1".equals(queryResult.getP_status())) {
                log.error(channel.getName() + "自动上分远程验证失败，订单不是支付成功状态：请求：" + JSON.toJSONString(resMap) + "，返回：" + retStr);
                return retVerifyFailed("远程验证失败，订单未支付");
            }

            VerifyResult verifyResult = new VerifyResult();
            verifyResult.setSelfBillno(queryResult.getP_OrderId());
            verifyResult.setChannelBillno(queryResult.getP_OrderId());
            double money = Double.parseDouble(queryResult.getP_payMoney());
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

    private static HTFQueryResult transferResult(String retStr) {
        Map<String, String> resultMap = UrlParamUtils.fromUrlParam(retStr);

        String P_UserId = resultMap.get("P_UserId");
        String P_OrderId = resultMap.get("P_OrderId");
        String P_ChannelId = resultMap.get("P_ChannelId");
        String P_CardId = resultMap.get("P_CardId");
        String P_payMoney = resultMap.get("P_payMoney");
        String P_flag = resultMap.get("P_flag");
        String P_status = resultMap.get("P_status");
        String P_ErrMsg = resultMap.get("P_ErrMsg");
        String P_PostKey = resultMap.get("P_PostKey");

        HTFQueryResult queryResult = new HTFQueryResult();
        queryResult.setP_UserId(P_UserId);
        queryResult.setP_OrderId(P_OrderId);
        queryResult.setP_ChannelId(P_ChannelId);
        queryResult.setP_CardId(P_CardId);
        queryResult.setP_payMoney(P_payMoney);
        queryResult.setP_flag(P_flag);
        queryResult.setP_status(P_status);
        queryResult.setP_ErrMsg(P_ErrMsg);
        queryResult.setP_PostKey(P_PostKey);
        return queryResult;
    }
}
