package lottery.domains.pool.payment.ty;

import com.alibaba.fastjson.JSON;
import javautils.http.HttpClientUtil;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.af.AFQueryResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static lottery.domains.pool.payment.AbstractPayment.retPrepareFailed;

public class TyPayment {

    private static final String CUS_EXT1 = "FFHcg,S$QL:H[tkl0dlqbd98o{2xyT";
    private static final String OUTPUT_SUCCESS = "OK";
    private static final String OUTPUT_FAILED = "{{\"code\":\"01\",\"msg\":\"处理失败\"}}";


    private static final Logger log = LoggerFactory.getLogger(TyPayment.class);


    public static PrepareResult prepare(String billno, double amount, String bankco, String notifyUrl, String resultUrl,
                                        PaymentChannel channel, HttpServletRequest request) {
        try {
            switch (channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_TYWECHAT:
                    return prepareWithQRCode(billno, amount, "102", notifyUrl, resultUrl, channel, request);
                case Global.PAYMENT_CHANNEL_TYALIPAY:
                    return prepareWithQRCode(billno, amount, "101", notifyUrl, resultUrl, channel, request);
            }

            return null;
        } catch (Exception e) {
            log.error(channel.getName() + "封装支付参数异常", e);
            return retPrepareFailed("支付通道异常，请重试！");
        }
    }


    private static PrepareResult prepareWithQRCode(String billno, double amount, String type, String notifyUrl,
                                                   String resultUrl, PaymentChannel channel, HttpServletRequest request) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", channel.getMerCode()); // 商户id，由千应分配
        params.put("type", type); // 支付宝通道：101，微信通道：102
//        params.put("m", "1");//单位元（人民币），正整数（不能带小数，最小支付金额为1）
        params.put("m", Double.valueOf(amount).intValue() + "");//单位元（人民币），正整数（不能带小数，最小支付金额为1）
        params.put("orderid", billno); // 商户系统唯一的订单编号
        params.put("callbackurl", notifyUrl); //下行异步通知过程的返回地址，必须是有效网址，且不带参数
        params.put("gotrue", resultUrl); //下行同步通知过程的返回地址(在支付完成后千应接口将会跳转到的商户 系统连接地址)。该参数必须指定，并且网址是可以访问的！
        //注：若提交值无该参数，或者该参数值为空，则在支付完成后，用户将停留在千应接口系统提示支付成功的页面。
        params.put("gofalse", "/fund-recharge"); //下行同步通知过程的返回地址(在支付失败后千应接口将会跳转到的商户系统连接地址)。该参数必须指定，并且网址是可以访问的！
        params.put("charset", "UTF-8");
        params.put("token", channel.getId() + ""); //备注信息，下行中会原样返回。若该值包含中文，请注意编码

        params.put("uuid", billno); //商户系统用户ID，该值需在商户系统内唯一，可减少错单率


        String src = "uid=" + params.get("uid") + "&type=" + params.get("type") + "&m="
//                 + "&orderid=" + params.get("orderid") + "&callbackurl=" + params.get("callbackurl")+channel.getMd5Key();
                + params.get("m") + "&orderid=" + params.get("orderid") + "&callbackurl=" + params.get("callbackurl") + channel.getMd5Key();
        //"uid={0}&type={1}&m={2}&orderid={3}&callbackurl={4}"+apikey
        String sign = MD5.convert(src);
        params.put("sign", sign); // 签名
        try {
            String url = "https://www.qianyingnet.com/pay/";

            PrepareResult prepareResult = new PrepareResult();
            prepareResult.setFormParams(params);
            prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
            prepareResult.setSuccess(true);
            prepareResult.setFormUrl(url);
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

            VerifyResult verifyResult = remoteVerify(channel, resMap);
            return verifyResult;

        } catch (Exception e) {
            log.error(channel.getName() + "签名验证失败", e);
            return retVerifyFailed("签名验证失败");
        }
    }

    public static VerifyResult retVerifyFailed(String output) {
        VerifyResult verifyResult = new VerifyResult();
        verifyResult.setSuccess(false);
        verifyResult.setOutput(output);
        return verifyResult;
    }

    private static VerifyResult remoteVerify(PaymentChannel channel, Map<String, String> resMap) {

        Map<String, String> params = new TreeMap<>();
        params.put("oid", resMap.get("oid"));//上行过程中商户系统传入的唯一订单号。
//            订单状态
//            0未付款；1付款成功；
//            2超时未付款失效；3已删除；4异常；
//            5下发成功，订单正常完成；6补单；
//            7由于网关掉线等导致的失效（这种情况要尽量避免，因为极可能掉单）；8退款
        params.put("status", resMap.get("status"));

        params.put("m", resMap.get("m"));//订单实际支付金额，单位元
        params.put("sign", resMap.get("sign"));//32位小写MD5签名值，请注意编码

        params.put("oidMy", resMap.get("oidMy"));//此次订单过程中千应接口系统内的订单Id
        params.put("oidPay", resMap.get("oidPay"));//收款方的订单号（例如支付宝交易号）
        params.put("time", resMap.get("time"));
        params.put("token", resMap.get("token"));
        params.put("msg", resMap.get("msg"));

        String status = params.get("status");
        String param = "oid=" + params.get("oid") + "&status=" + params.get("status") + "&m=" + params.get("m") + channel.getMd5Key();
        if (params.get("sign").equalsIgnoreCase(MD5.convert(param))) {
            if (status.equals("1") || status.equals("5") || status.equals("6")) {
                VerifyResult verifyResult = new VerifyResult();
                verifyResult.setSelfBillno(params.get("oid"));
                verifyResult.setChannelBillno(params.get("oidPay"));
                double money = Double.parseDouble(params.get("m"));
                verifyResult.setRequestMoney(money);
                verifyResult.setReceiveMoney(money);
                verifyResult.setPayTime(new Date());
                verifyResult.setSuccess(true);
                verifyResult.setOutput(OUTPUT_SUCCESS);
                verifyResult.setSuccessOutput(OUTPUT_SUCCESS);
                verifyResult.setFailedOutput(OUTPUT_FAILED);
                return verifyResult;
            } else if (status.equals("4")) {
                log.error(channel.getName() + "远程验证失败，因为：" + params.get("msg"));
                return retVerifyFailed("远程验证失败，因为：" + params.get("msg"));
            } else if (status.equals("8")) {
                return retVerifyFailed("远程验证失败，订单已经退款");
            }
        } else {
            log.error(channel.getName() + "签名无效，视为无效数据!请求信息：" + JSON.toJSONString(resMap));
            return retVerifyFailed("签名无效，视为无效数据!");
        }
        log.error(channel.getName() + "自动上分远程验证异常!请求信息：" + JSON.toJSONString(resMap));
        return retVerifyFailed("远程验证失败，获取订单失败");
    }
}

