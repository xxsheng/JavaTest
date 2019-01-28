package lottery.domains.pool.payment.pay41.utils;


import sun.net.util.URLUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by thinkpad on 2015/3/28.
 */
public class THConfig {

    private static String GATEWAY_URL = "http://127.0.0.1:8080/gateway"; //支付网关地址
    private static String MER_NO = "10000100"; //这里填写商户号
    private static String MER_KEY = "064aa7ced87911e4abbe00259079b2c3"; //这里填写签名时需要的私钥key
    private static String CHARSET = "UTF-8"; //这里填写当前系统字符集编码，取值UTF-8或者GBK
    private static String BACK_NOTIFY_URL = "http://www.mer.com/back"; // 这里填写支付完成后，支付平台后台通知当前支付是否成功的URL
    private static String PAGE_NOTIFY_URL = "http://127.0.0.1:8089/pay_return.jsp"; // 这里填写支付完成后，页面跳转到商户页面的URL，同时告知支付是否成功
    private static String PAY_TYPE = "1"; //支付方式，目前暂只支持网银支付，取值为1
    private static String REQ_REFERER = "127.0.0.1";//请指定当前系统的域名，用来防钓鱼，例如www.mer.com

    private static String REQ_CUSTOMER_IP = null;

    public static String getUrl(HttpServletRequest request)
    {
        try {
            request.setCharacterEncoding(CHARSET);
        } catch (UnsupportedEncodingException e) {
        }
        String bankCode = request.getParameter(AppConstants.BANK_CODE);
        String orderNo = request.getParameter(AppConstants.ORDER_NO);
        String orderAmount = request.getParameter(AppConstants.ORDER_AMOUNT);
        if (StringUtils.isNullOrEmpty(orderNo))
        {
            throw new RuntimeException("请求的参数订单号为空");
        }
        if (StringUtils.isNullOrEmpty(orderAmount))
        {
            throw new RuntimeException("请求的参数订单金额为空");
        }
        String productName = request.getParameter(AppConstants.PRODUCT_NAME);
        String productNum = request.getParameter(AppConstants.PRODUCT_NUM);

        String referer = REQ_REFERER;
        String customerIp = HttpUtils.getAddr(request);
        if (REQ_CUSTOMER_IP != null)
            customerIp = REQ_CUSTOMER_IP;
        String customerPhone = request.getParameter(AppConstants.CUSTOMER_PHONE);
        String receiveAddr = request.getParameter(AppConstants.RECEIVE_ADDRESS);
        String returnParams = request.getParameter(AppConstants.RETURN_PARAMS);
        String currentDate = DateUtils.format(new Date());

        KeyValues kvs = new KeyValues();
        kvs.add(new KeyValue(AppConstants.INPUT_CHARSET, CHARSET));
        kvs.add(new KeyValue(AppConstants.NOTIFY_URL, BACK_NOTIFY_URL));
        kvs.add(new KeyValue(AppConstants.RETURN_URL, PAGE_NOTIFY_URL));
        kvs.add(new KeyValue(AppConstants.PAY_TYPE, PAY_TYPE));
        kvs.add(new KeyValue(AppConstants.BANK_CODE, bankCode));
        kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, MER_NO));
        kvs.add(new KeyValue(AppConstants.ORDER_NO, orderNo));
        kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, orderAmount));
        kvs.add(new KeyValue(AppConstants.ORDER_TIME, currentDate));
        kvs.add(new KeyValue(AppConstants.PRODUCT_NAME, productName));
        kvs.add(new KeyValue(AppConstants.PRODUCT_NUM, productNum));
        kvs.add(new KeyValue(AppConstants.REQ_REFERER, referer));
        kvs.add(new KeyValue(AppConstants.CUSTOMER_IP, customerIp));
        kvs.add(new KeyValue(AppConstants.CUSTOMER_PHONE, customerPhone));
        kvs.add(new KeyValue(AppConstants.RECEIVE_ADDRESS, receiveAddr));
        kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, returnParams));
        String sign = kvs.sign(MER_KEY, CHARSET);

        StringBuilder sb = new StringBuilder();
        sb.append(GATEWAY_URL);
        URLUtils.appendParam(sb, AppConstants.INPUT_CHARSET, CHARSET, false);
        URLUtils.appendParam(sb, AppConstants.NOTIFY_URL, BACK_NOTIFY_URL, CHARSET);
        URLUtils.appendParam(sb, AppConstants.RETURN_URL, PAGE_NOTIFY_URL, CHARSET);
        URLUtils.appendParam(sb, AppConstants.PAY_TYPE, PAY_TYPE);
        URLUtils.appendParam(sb, AppConstants.BANK_CODE, bankCode);
        URLUtils.appendParam(sb, AppConstants.MERCHANT_CODE, MER_NO);
        URLUtils.appendParam(sb, AppConstants.ORDER_NO, orderNo);
        URLUtils.appendParam(sb, AppConstants.ORDER_AMOUNT, orderAmount);
        URLUtils.appendParam(sb, AppConstants.ORDER_TIME, currentDate);
        URLUtils.appendParam(sb, AppConstants.PRODUCT_NAME, productName, CHARSET);
        URLUtils.appendParam(sb, AppConstants.PRODUCT_NUM, productNum);
        URLUtils.appendParam(sb, AppConstants.REQ_REFERER, referer, CHARSET);
        URLUtils.appendParam(sb, AppConstants.CUSTOMER_IP, customerIp);
        URLUtils.appendParam(sb, AppConstants.CUSTOMER_PHONE, customerPhone);
        URLUtils.appendParam(sb, AppConstants.RECEIVE_ADDRESS, receiveAddr, CHARSET);
        URLUtils.appendParam(sb, AppConstants.RETURN_PARAMS, returnParams, CHARSET);
        URLUtils.appendParam(sb, AppConstants.SIGN, sign);
        return sb.toString();
    }

    public static boolean validPageNotify(HttpServletRequest req)
    {
        String merchantCode = req.getParameter(AppConstants.MERCHANT_CODE);
        String notifyType = req.getParameter(AppConstants.NOTIFY_TYPE);
        String orderNo = req.getParameter(AppConstants.ORDER_NO);
        String orderAmount = req.getParameter(AppConstants.ORDER_AMOUNT);
        String orderTime = req.getParameter(AppConstants.ORDER_TIME);
        String returnParams = req.getParameter(AppConstants.RETURN_PARAMS);
        String tradeNo = req.getParameter(AppConstants.TRADE_NO);
        String tradeTime = req.getParameter(AppConstants.TRADE_TIME);
        String tradeStatus = req.getParameter(AppConstants.TRADE_STATUS);
        String sign = req.getParameter(AppConstants.SIGN);
        KeyValues kvs = new KeyValues();

        kvs.add(new KeyValue(AppConstants.MERCHANT_CODE, merchantCode));
        kvs.add(new KeyValue(AppConstants.NOTIFY_TYPE, notifyType));
        kvs.add(new KeyValue(AppConstants.ORDER_NO, orderNo));
        kvs.add(new KeyValue(AppConstants.ORDER_AMOUNT, orderAmount));
        kvs.add(new KeyValue(AppConstants.ORDER_TIME, orderTime));
        kvs.add(new KeyValue(AppConstants.RETURN_PARAMS, returnParams));
        kvs.add(new KeyValue(AppConstants.TRADE_NO, tradeNo));
        kvs.add(new KeyValue(AppConstants.TRADE_TIME, tradeTime));
        kvs.add(new KeyValue(AppConstants.TRADE_STATUS, tradeStatus));
        String thizSign = kvs.sign(MER_KEY, CHARSET);
        if (thizSign.equalsIgnoreCase(sign))
            return true;
        else
            return false;
    }

    public static String getGatewayUrl() {
        return GATEWAY_URL;
    }

    public static void setGatewayUrl(String gatewayUrl) {
        GATEWAY_URL = gatewayUrl;
    }

    public static String getMerNo() {
        return MER_NO;
    }

    public static void setMerNo(String merNo) {
        MER_NO = merNo;
    }

    public static String getMerKey() {
        return MER_KEY;
    }

    public static void setMerKey(String merKey) {
        MER_KEY = merKey;
    }

    public static String getCharset() {
        return CHARSET;
    }

    public static void setCharset(String charset) {
        THConfig.CHARSET = charset;
    }

    public static String getBackNotifyUrl() {
        return BACK_NOTIFY_URL;
    }

    public static void setBackNotifyUrl(String backNotifyUrl) {
        BACK_NOTIFY_URL = backNotifyUrl;
    }

    public static String getPageNotifyUrl() {
        return PAGE_NOTIFY_URL;
    }

    public static void setPageNotifyUrl(String pageNotifyUrl) {
        PAGE_NOTIFY_URL = pageNotifyUrl;
    }

    public static String getPayType() {
        return PAY_TYPE;
    }

    public static void setPayType(String payType) {
        PAY_TYPE = payType;
    }

    public static String getReqReferer() {
        return REQ_REFERER;
    }

    public static void setReqReferer(String reqReferer) {
        REQ_REFERER = reqReferer;
    }

    public static void setReqCustomerIp(String reqCustomerIp) {
        REQ_CUSTOMER_IP = reqCustomerIp;
    }
}
