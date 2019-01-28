package lottery.domains.content.payment.cf;

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
import lottery.domains.content.payment.lepay.utils.StringUtil;
import lottery.domains.content.payment.utils.MoneyFormat;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 创富代付，把MD5 key设置到md5Key字段，并绑定代付IP
 * Created by Nick on 2017-11-24.
 */
@Component
public class CFPayment extends AbstractPayment implements InitializingBean {
    private static final String BATCH_BIZ_TYPE = "00000";
    private static final String BATCH_VERSION = "00";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "SHA";

    private static Map<Integer, String> BRANCH_NAMES = new HashMap<>();
    private static Map<Integer, String> BANK_NAMES = new HashMap<>();

    @Value("${cf.daifu.url}")
    private String daifuUrl;

    @Override
    public void afterPropertiesSet() throws Exception {
        BRANCH_NAMES.put(1, "中国工商银行股份有限公司上海市龙江路支行");
        BRANCH_NAMES.put(2, "中国建设银行北京市分行营业部");
        BRANCH_NAMES.put(3, "中国农业银行股份有限公司忻州和平分理处");
        BRANCH_NAMES.put(4, "招商银行股份有限公司厦门金湖支行");
        BRANCH_NAMES.put(5, "中国银行股份有限公司赣州市客家大道支行");
        BRANCH_NAMES.put(6, "交通银行北京安翔里支行");
        BRANCH_NAMES.put(7, "上海浦东发展银行安亭支行");
        BRANCH_NAMES.put(8, "兴业银行北京安华支行");
        BRANCH_NAMES.put(9, "中信银行北京安贞支行");
        BRANCH_NAMES.put(10, "宁波银行股份有限公司北京东城支行");
        BRANCH_NAMES.put(11, "上海银行股份有限公司北京安贞支行");
        BRANCH_NAMES.put(12, "杭州银行股份有限公司上海北新泾支行");
        BRANCH_NAMES.put(13, "渤海银行股份有限公司北京朝阳门支行");
        BRANCH_NAMES.put(14, "浙商银行股份有限公司杭州滨江支行");
        BRANCH_NAMES.put(15, "广发银行股份有限公司北京朝阳北路支行");
        BRANCH_NAMES.put(16, "中国邮政储蓄银行股份有限公司北京昌平区北七家支行");
        BRANCH_NAMES.put(17, "深圳发展银行");
        BRANCH_NAMES.put(18, "中国民生银行股份有限公北京西大望路支行");
        BRANCH_NAMES.put(19, "中国光大银行股份有限公司北京安定门支行");
        BRANCH_NAMES.put(20, "华夏银行北京德外支行");
        BRANCH_NAMES.put(21, "北京银行安定门支行");
        BRANCH_NAMES.put(22, "南京银行股份有限公司北京车公庄支行");
        BRANCH_NAMES.put(23, "平安银行股份有限公司北京北苑支行");
        BRANCH_NAMES.put(24, "北京农村商业银行股份有限公司漷县支行");

        BANK_NAMES.put(1, "中国工商银行");
        BANK_NAMES.put(2, "中国建设银行");
        BANK_NAMES.put(3, "中国农业银行");
        BANK_NAMES.put(4, "招商银行");
        BANK_NAMES.put(5, "中国银行");
        BANK_NAMES.put(6, "交通银行");
        BANK_NAMES.put(7, "上海浦东发展银行");
        BANK_NAMES.put(8, "兴业银行");
        BANK_NAMES.put(9, "中信银行");
        BANK_NAMES.put(10, "宁波银行");
        BANK_NAMES.put(11, "上海银行");
        BANK_NAMES.put(12, "杭州银行");
        BANK_NAMES.put(13, "渤海银行");
        BANK_NAMES.put(14, "浙商银行");
        BANK_NAMES.put(15, "广发银行");
        BANK_NAMES.put(16, "中国邮政储蓄银行");
        BANK_NAMES.put(18, "中国民生银行");
        BANK_NAMES.put(19, "中国光大银行");
        BANK_NAMES.put(20, "华夏银行");
        BANK_NAMES.put(21, "北京银行");
        BANK_NAMES.put(22, "南京银行");
        BANK_NAMES.put(23, "平安银行");
        BANK_NAMES.put(24, "北京农村商业银行");
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
        try {
            String bankName = BANK_NAMES.get(card.getBankId());
            if (StringUtil.isEmpty(bankName)) {
                json.set(2, "2-4012");
                return null;
            }

            String amount = MoneyFormat.pasMoney(order.getRecMoney());

            // 明细部分
            StringBuffer batchContent = new StringBuffer();
            batchContent.append("1").append(","); // 序号
            batchContent.append(card.getCardId()).append(","); // 银行账户
            batchContent.append(card.getCardName()).append(","); // 开户名
            batchContent.append(bankName).append(",");  // 开户行名称
            String branchName = BRANCH_NAMES.get(card.getBankId());
            batchContent.append(branchName).append(","); // 分行
            batchContent.append(branchName).append(","); // 支行
            batchContent.append("私").append(","); // 公/私
            batchContent.append(amount).append(","); // 金额
            batchContent.append("CNY").append(","); // 币种
            batchContent.append("北京").append(","); // 省
            batchContent.append("北京").append(","); // 市
            batchContent.append("").append(","); // 手机号
            batchContent.append("").append(","); // 证件类型
            batchContent.append("").append(","); // 证件号
            batchContent.append("").append(","); // 用户协议号
            batchContent.append(order.getBillno()).append(","); // 商户订单号
            batchContent.append("APIPAY"); // 备注

            String date = new Moment().format("yyyyMMdd");

            // 参数
            Map<String, String> paramsMap = new TreeMap<>();
            paramsMap.put("batchAmount", amount); // 总金额
            paramsMap.put("batchBiztype", BATCH_BIZ_TYPE); // 提交批次类型，默认00000
            paramsMap.put("batchContent", batchContent.toString()); // 明细部分
            paramsMap.put("batchCount", "1"); // 总笔数
            paramsMap.put("batchDate", date); // 提交日期
            paramsMap.put("batchNo", order.getBillno()); // 批次号，不能重复
            paramsMap.put("batchVersion", BATCH_VERSION); // 版本号，固定值为00
            paramsMap.put("charset", CHARSET); // 输入编码
            paramsMap.put("merchantId", channel.getMerCode()); // 商户号 ID

            String signStr = ToUrlParamUtils.toUrlParam(paramsMap, "&", true) + channel.getMd5Key();
            String sign = sign(signStr, CHARSET);
            paramsMap.put("signType", SIGN_TYPE);
            paramsMap.put("sign", sign);

            String url = daifuUrl + "/agentPay/v1/batch/" + channel.getMerCode() + "-" + order.getBillno();

            String retStr = HttpClientUtil.post(url, paramsMap, null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, bank, channel, "接口返回空，可能是请求超时");
                json.set(-1, "-1"); // 这里-1表示连接异常
                return null;
            }

            CFPayResult result = JSON.parseObject(retStr, CFPayResult.class);

            if (result == null) {
                logError(order, bank, channel, "请求失败，解析返回数据失败，返回数据为：" + retStr);
                json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                return null;
            }

            if ("S0001".equalsIgnoreCase(result.getRespCode())) {
                logSuccess(order, order.getBillno(), channel);
                return order.getBillno();
            }
            else {
                if (StringUtils.isEmpty(result.getRespMessage())) {
                    // 返回失败，查询订单状态
                    String msg = "未知错误";
                    logError(order, bank, channel, "请求返回空的错误消息，返回数据："+retStr+"，开始查询订单状态");

                    CFPayQueryResult queryResult = query(order, channel, date);
                    if (isAccepted(queryResult)) {
                        logSuccess(order, queryResult.getBatchNo(), channel);
                        return queryResult.getBatchNo();
                    }
                    else {
                        logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                        json.setWithParams(2, "2-4002", msg);
                        return null;
                    }
                }
                else {
                    logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4002", result.getRespMessage());
                    return null;
                }

            }
        } catch (Exception e) {
            logException(order, bank, channel, "代付请求失败", e);
            json.set(-1, "-1"); // 这里-1表示连接异常
            return null;
        }
    }

    public CFPayQueryResult query(UserWithdraw order, PaymentChannel channel){
        String date = new Moment().fromTime(order.getOperatorTime()).format("yyyyMMdd");
        return query(order, channel, date);
    }

    private CFPayQueryResult query(UserWithdraw order, PaymentChannel channel, String date){
        try {
            // 参数
            Map<String, String> paramsMap = new TreeMap<>();
            paramsMap.put("batchDate", date); // 批次上传提交日期
            paramsMap.put("batchNo", order.getBillno()); // 批次号
            paramsMap.put("batchVersion", BATCH_VERSION); // 版本号
            paramsMap.put("charset", CHARSET); // 输入编码
            paramsMap.put("merchantId", channel.getMerCode()); // 商户号 ID

            String signStr = ToUrlParamUtils.toUrlParam(paramsMap, "&", true) + channel.getMd5Key();
            String sign = sign(signStr, CHARSET);
            paramsMap.put("signType", SIGN_TYPE);
            paramsMap.put("sign", sign);

            String paramsStr = ToUrlParamUtils.toUrlParam(paramsMap, "&", true);

            String url = daifuUrl + "/agentPay/v1/batch/" + channel.getMerCode() + "-" + order.getBillno() + "?" + paramsStr;
            String retStr = HttpClientUtil.get(url, null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, null, channel, "查询请求失败，发送请求后返回空数据");
                return null;
            }

            logInfo(order, null, channel, "查询返回数据：" + retStr);

            CFPayQueryResult result = JSON.parseObject(retStr, CFPayQueryResult.class);

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

    private static final String SIGN_ALGORITHMS = "SHA-1";

    /**
     * SHA1 安全加密算法
     */
    private static String sign(String content,String inputCharset)  {
        //获取信息摘要 - 参数字典排序后字符串
        try {
            //指定sha1算法
            MessageDigest digest = MessageDigest.getInstance(SIGN_ALGORITHMS);
            digest.update(content.getBytes(inputCharset));
            //获取字节数组
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

    /**
     * 第三方是否已经接受请求
     */
    public boolean isAccepted(CFPayQueryResult result) {
        return "S0001".equalsIgnoreCase(result.getRespCode())
                && StringUtils.isNotEmpty(result.getBatchNo());
    }

    /**
     * 把创富状态转换为本系统状态
     */
    public int transferBankStatus(String batchContent) {
        String bankStatus = getBankStatusFromBatchContent(batchContent);

        if (null == bankStatus) return Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING;
        if ("null".equalsIgnoreCase(bankStatus)) return Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING;
        if ("成功".equalsIgnoreCase(bankStatus)) return Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED;
        if ("success".equalsIgnoreCase(bankStatus)) return Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED;
        if ("failure".equalsIgnoreCase(bankStatus)) return Global.USER_WITHDRAW_REMITSTATUS_THIRD_PROCESS_FAILED;
        if ("失败".equalsIgnoreCase(bankStatus)) return Global.USER_WITHDRAW_REMITSTATUS_THIRD_PROCESS_FAILED;

        return Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN;
    }

    private String getBankStatusFromBatchContent(String batchContent) {
        String[] batchContents = batchContent.split(",");
        String bankStatus = batchContents[12];
        // 反馈状态，null：处理中；success：成功；failure：失败；
        return bankStatus;
    }

    public static void main(String[] args) {
        String content = "1,171206111554nkjWivyJ,6217000010074430920,弋会超,中国建设银行北京市分行营业部,中国建设银行北京市分行营业部,中国建设银行,0,1100.00,CNY,APIPAY,,成功,";
        String[] batchContents = content.split(",");
        String bankStatus = batchContents[12];
        // 反馈状态，null：处理中；success：成功；failure：失败；
        System.out.println(bankStatus);
    }
}
