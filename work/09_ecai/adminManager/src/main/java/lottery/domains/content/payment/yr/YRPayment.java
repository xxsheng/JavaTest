package lottery.domains.content.payment.yr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import admin.web.WebJSONObject;
import javautils.http.HttpClientUtil;
import lottery.domains.content.AbstractPayment;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.payment.utils.UrlParamUtils;
import lottery.domains.content.payment.zs.utils.MD5Encrypt;
import lottery.domains.content.payment.zs.utils.StringUtil;

/**
 * 易去代付，把MD5 key设置到md5Key字段，并绑定代付IP
 * Created by Cavan on 2017-09-04.
 */
@Component
public class YRPayment extends AbstractPayment implements InitializingBean{
	@Autowired
	private ServletContext servletContext;
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

    @Value("${yr.daifu.url}")
    private String daifuUrl;

    @Value("${yr.daifu.queryurl}")
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
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("payKey", channel.getMerCode());
            paramsMap.put("outTradeNo", order.getBillno());
            paramsMap.put("orderPrice",order.getRecMoney() + "" );
            String pcdnDomain = (String) servletContext.getAttribute("pcdnDomain");
         //   paramsMap.put("notifyUrl",  pcdnDomain+channel.getArmourUrl()+channel.getId());
            paramsMap.put("proxyType", "T0");
            paramsMap.put("productType", "B2CPAY");
            paramsMap.put("bankAccountType", "PRIVATE_DEBIT_ACCOUNT");
            paramsMap.put("receiverAccountNo",   order.getCardId());
            paramsMap.put("receiverName", order.getCardName());
            String branchName = BRANCH_NAMES.get(card.getBankId());
            paramsMap.put("bankBranchNo", branchName);
            paramsMap.put("bankName", order.getBankName());
            
            String signStr = UrlParamUtils.toUrlParamWithoutEmpty(paramsMap, "&",true) + "&paySecret="+channel.getMd5Key();
            String sign  =  DigestUtils.md5Hex(signStr).toUpperCase();
            paramsMap.put("sign",sign);
            String retStr = HttpClientUtil.post(daifuUrl, paramsMap, null, 10000);

             if (StringUtils.isEmpty(retStr)) {
                logError(order, bank, channel, "接口返回空，可能是请求超时");
                json.set(-1, "-1"); // 这里-1表示连接异常
                return null;
            }
            
            YRDaifuResult result = JSON.parseObject(retStr, YRDaifuResult.class);
            if (result == null) {
                logError(order, bank, channel, "请求失败，解析返回数据失败，返回数据为：" + retStr);
                json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                return null;
            }
            if ("0000".equals(result.getResultCode())) {

                if (StringUtils.isEmpty(result.getOutOrderId())) {
                    logError(order, bank, channel, "请求失败，返回第三方注单号为空，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4007", StringUtils.abbreviate(retStr, 20));
                    return null;
                }

                logSuccess(order, result.getOutOrderId(), channel);
                return result.getOutOrderId();
            }
            else {
                logError(order, bank, channel, "请求返回错误消息，返回数据："+retStr+"，开始查询订单状态");
                YRDaifuQueryResult queryResult = query(order, channel);
                if (isSuccessRequested(queryResult)) {
                    logSuccess(order, queryResult.getOutTradeNo(), channel);
                    return queryResult.getOutTradeNo();
                }else {
                    logError(order, bank, channel, "请求失败，返回数据为：" + retStr);
                    json.setWithParams(2, "2-4002", result.getErrMsg());
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
    
    public static void main(String[] args) {
		
	}

    public YRDaifuQueryResult query(UserWithdraw order, PaymentChannel channel){
        try {
            String outOrderId = order.getBillno();
            String merchantCode = channel.getMerCode();
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("payKey ", merchantCode);
            paramsMap.put("outTradeNo", outOrderId);
            String signStr = UrlParamUtils.toUrlParamWithoutEmpty(paramsMap, "&",true) + "&paySecret="+channel.getMd5Key();
            String sign  =  DigestUtils.md5Hex(signStr);
            paramsMap.put("sign",sign.toUpperCase());
            String retStr = HttpClientUtil.post(daifuQueryUrl, paramsMap, null, 10000);
            if (StringUtils.isEmpty(retStr)) {
                logError(order, null, channel, "查询请求失败，发送请求后返回空数据");
                return null;
            }

            logInfo(order, null, channel, "查询返回数据：" + retStr);
            

            YRDaifuQueryResult result = null;
            try {
            	 result = JSON.parseObject(retStr, YRDaifuQueryResult.class);
                 if (result == null) {
                     logError(order, null, channel, "查询请求失败，解析返回数据失败");
                     return null;
                 }
			} catch (Exception e) {
				 logError(order, null, channel, "查询请求失败，解析返回数据失败");
				return null;
			}

            return result;
        } catch (Exception e) {
            logException(order, null, channel, "查询请求失败", e);
            return null;
        }
    }

    public boolean isSuccessRequested(YRDaifuQueryResult queryResult) {
        if (queryResult == null) {
            return false;
        }

        if (StringUtils.isEmpty(queryResult.getOutTradeNo())) return false;
       if ("0000".equalsIgnoreCase(queryResult.getResultCode())) return true;
       // if ("2".equalsIgnoreCase(queryResult.getStatus())) return true;
      
        return false;
    }

    public int transferBankStatus(String bankStatus) {
        int remitStatus = Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN;
        switch (bankStatus) {
            case "REMITTING": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING; break;
            case "REMIT_SUCCESS": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED; break;
            case "REMIT_FAIL": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_PROCESS_FAILED; break;
        }
        return remitStatus;
    }
}
