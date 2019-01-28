package lottery.domains.content.payment.zs;

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
import lottery.domains.content.payment.zs.utils.MD5Encrypt;
import lottery.domains.content.payment.zs.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 泽圣代付，把MD5 key设置到md5Key字段，并绑定代付IP
 * Created by Cavan on 2017-09-04.
 */
@Component
public class ZSPayment extends AbstractPayment implements InitializingBean{
    private static Map<Integer, String> BRANCH_NAMES = new HashMap<>();
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
    }

    @Value("${zs.daifu.url}")
    private String daifuUrl;

    @Value("${zs.daifu.queryurl}")
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
            String nonceStr = StringUtil.getRandomNum(32); // 随机数
            String outOrderId = order.getBillno(); // 商户订单号
            Long totalAmount = StringUtil.changeY2F(order.getRecMoney() + ""); // 金额单位 分
            String merchantCode = channel.getMerCode(); // 商户号
            String intoCardNo = order.getCardId(); // 收款账号
            String intoCardName = order.getCardName(); // 收款账户名
            String intoCardType = "2";//收款账户类型1-对公2-对私
            String type = "04"; // 到账类型03-非实时到账，04-实时到账
            String bankName=  "" ;
            String remark = "";//备注|留言等
            String bankCode = "";
            String signStr = String.format(
                    "bankCode=%s&bankName=%s&intoCardName=%s&intoCardNo=%s"
                            + "&intoCardType=%s&merchantCode=%s&nonceStr=%s&outOrderId=%s&totalAmount=%s&type=%s&KEY=%s",
                    bankCode, bankName, intoCardName, intoCardNo, intoCardType, merchantCode, nonceStr, outOrderId, totalAmount, type, channel.getMd5Key());

            String sign = MD5Encrypt.getMessageDigest(signStr);
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("bankCode", bankCode);
            paramsMap.put("bankName", bankName);
            paramsMap.put("intoCardName",intoCardName );
            paramsMap.put("intoCardNo", intoCardNo);
            paramsMap.put("intoCardType", intoCardType);
            paramsMap.put("merchantCode", merchantCode);
            paramsMap.put("nonceStr",  nonceStr);
            paramsMap.put("outOrderId", outOrderId);
            paramsMap.put("totalAmount", totalAmount+"");
            paramsMap.put("type", type);
            paramsMap.put("remark", remark);
            paramsMap.put("sign",sign);

            String retStr = HttpClientUtil.post(daifuUrl, paramsMap, null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, bank, channel, "接口返回空，可能是请求超时");
                json.set(-1, "-1"); // 这里-1表示连接异常
                return null;
            }

            JSONObject jsonObject = JSON.parseObject(retStr);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            if ("00".equals(code)) {
                String data = jsonObject.getString("data");
                ZSDaifuResult result = JSON.parseObject(data, ZSDaifuResult.class);

                if (result == null) {
                    logError(order, bank, channel, "请求失败，解析返回数据失败，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                    return null;
                }

                if (StringUtils.isEmpty(result.getOrderId())) {
                    logError(order, bank, channel, "请求失败，返回第三方注单号为空，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                    return null;
                }

                logSuccess(order, result.getOrderId(), channel);
                return result.getOrderId();
            }
            else {
                logError(order, bank, channel, "请求返回错误消息，返回数据："+retStr+"，开始查询订单状态");
                ZSDaifuQueryResult queryResult = query(order, channel);
                if (isSuccessRequested(queryResult)) {
                    logSuccess(order, queryResult.getOrderId(), channel);
                    return queryResult.getOrderId();
                }else {
                    logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4002", msg);
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

    public ZSDaifuQueryResult query(UserWithdraw order, PaymentChannel channel){
        try {
            String nonceStr = StringUtil.getRandomNum(32);
            String outOrderId = order.getBillno();
            String md5Key = channel.getMd5Key();
            String merchantCode = channel.getMerCode();
            String signStr = String.format(
                    "merchantCode=%s&nonceStr=%s&outOrderId=%s&KEY=%s",
                    merchantCode,nonceStr,outOrderId,md5Key);
            String sign = MD5Encrypt.getMessageDigest(signStr);
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("merchantCode", merchantCode );
            paramsMap.put("nonceStr", nonceStr);
            paramsMap.put("outOrderId", outOrderId);
            paramsMap.put("sign", sign);

            String retStr = HttpClientUtil.post(daifuQueryUrl, paramsMap, null, 10000);

            if (StringUtils.isEmpty(retStr)) {
                logError(order, null, channel, "查询请求失败，发送请求后返回空数据");
                return null;
            }

            logInfo(order, null, channel, "查询返回数据：" + retStr);

            JSONObject jsonObject = JSON.parseObject(retStr);
            String code = jsonObject.getString("code");
            String data = null;
            if ("00".equals(code)) {
                data = jsonObject.getString("data");
            }

            if (StringUtils.isEmpty(data)) {
                logError(order, null, channel, "查询请求失败，解析返回数据失败");
                return null;
            }

            ZSDaifuQueryResult result = JSON.parseObject(data, ZSDaifuQueryResult.class);

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

    public boolean isSuccessRequested(ZSDaifuQueryResult queryResult) {
        if (queryResult == null) {
            return false;
        }

        if (StringUtils.isEmpty(queryResult.getOrderId())) return false;

        if ("00".equalsIgnoreCase(queryResult.getState())) return true;
        if ("90".equalsIgnoreCase(queryResult.getState())) return true;

        return false;
    }

    public int transferBankStatus(String bankStatus) {
        int remitStatus = Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN;
        switch (bankStatus) {
            case "00": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED; break;
            case "90": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING; break;
            case "02": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_PROCESS_FAILED; break;
        }
        return remitStatus;
    }
}
