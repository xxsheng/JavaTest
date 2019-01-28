package lottery.domains.content.payment.tgf;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import admin.web.WebJSONObject;
import lottery.domains.content.AbstractPayment;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.payment.tgf.utils.Config;
import lottery.domains.content.payment.tgf.utils.Merchant;
import lottery.domains.content.payment.tgf.utils.QueryResponseEntity;
import lottery.domains.content.payment.tgf.utils.RefundResponseEntity;
import lottery.domains.content.payment.tgf.utils.SignUtil;
import sun.misc.BASE64Encoder;

/**
 * 艾付代付，把MD5 key设置到md5Key字段，并绑定代付IP
 * Created by Cavan on 2017-09-04.
 */
@Component
public class TGFPayment extends AbstractPayment implements InitializingBean{
	@Autowired
	private ServletContext servletContext;
    private static Map<Integer, String> BRANCH_NAMES = new HashMap<>();
    
    public static final String NONE_NOTIFY_URL = "http://www.baidu.com";
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

    @Value("${af.daifu.url}")
    private String daifuUrl;

    @Value("${af.daifu.queryurl}")
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


	public static String base64Encoder(final String ss, String charset) {
		BASE64Encoder en = new BASE64Encoder(); // base64编码
		String encStr = "";
		if (charset == null || "".equals(charset)) {
			encStr = en.encode(ss.getBytes());
			return encStr;
		}
		try {
			encStr = en.encode(ss.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encStr;
	}
	
    private String daifuInternal(WebJSONObject json, UserWithdraw order, UserCard card, PaymentChannelBank bank, PaymentChannel channel){
        try {
        	
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("service", Config.APINAME_SETTLE);
            paramsMap.put("version", Config.API_VERSION);
            paramsMap.put("merId",  channel.getMerCode());
            paramsMap.put("tradeNo", order.getBillno());
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			paramsMap.put("tradeDate", sdf.format(d));
            paramsMap.put("amount",order.getRecMoney() + "");
            
            paramsMap.put("notifyUrl", NONE_NOTIFY_URL);
            paramsMap.put("extra", order.getBillno());
            paramsMap.put("summary", order.getBillno());
            paramsMap.put("bankCardNo", order.getCardId());
            paramsMap.put("bankCardName", order.getCardName());
            paramsMap.put("bankId", bank.getCode());
            paramsMap.put("bankName", BRANCH_NAMES.get(card.getBankId()));
            paramsMap.put("purpose", "");

            String paramsStr = Merchant.generateSingleSettRequest(paramsMap);
        	String signMsg = SignUtil.signByMD5(paramsStr, channel.getMd5Key());
            paramsStr += "&sign=" + signMsg;

            String payGateUrl = channel.getPayUrl();

            // 发送请求并接收返回
            String responseMsg = Merchant.transact(paramsStr, payGateUrl);

            System.out.println(responseMsg);

            // 处理返回数据
            QueryResponseEntity entity = new QueryResponseEntity();
            entity.parse(responseMsg,channel.getMd5Key());
            if ("00".equals(entity.getRespCode())) {
                logSuccess(order, order.getBillno(), channel);
                return order.getBillno();
            }
            else {
                logError(order, bank, channel, "请求返回错误消息，返回数据："+responseMsg+"，开始查询订单状态");
                QueryResponseEntity queryResult = query(order, channel);
                if (isSuccessRequested(queryResult)) {
                    logSuccess(order,  order.getBillno(), channel);
                    return order.getBillno();
                }else {
                    logError(order, bank, channel, "请求失败，返回数据为：" + responseMsg);
                    json.setWithParams(2, "2-4002", entity.getRespDesc());
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

    public QueryResponseEntity query(UserWithdraw order, PaymentChannel channel){
        try {
        	
        	
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("service", Config.APINAME_SETTLE_QUERY);
            paramsMap.put("version", Config.API_VERSION);
            paramsMap.put("merId", channel.getMerCode());
            paramsMap.put("tradeNo", order.getBillno());
            Date d= new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			paramsMap.put("tradeDate", sdf.format(d));

            String paramsStr = Merchant.generateSingleSettQueryRequest(paramsMap);
        	String signMsg = SignUtil.signByMD5(paramsStr, channel.getMd5Key());
            paramsStr += "&sign=" + signMsg;
            String payGateUrl =channel.getPayUrl();
            
            // 发送请求并接收返回
            System.out.println(paramsStr);
            String responseMsg = Merchant.transact(paramsStr, payGateUrl);
            System.out.println(responseMsg);

            // 处理返回数据
            QueryResponseEntity entity = new QueryResponseEntity();
            entity.parse(responseMsg,channel.getMd5Key());
            
          /*  logInfo(order, null, channel, "查询返回数据：" + responseMsg);
            
            TGFDaifuQueryResult result = new  TGFDaifuQueryResult();
            if(entity.getRespCode().equals("00")){
            		
            }*/
            
           // entity.set
 /*           try {
            	 result = JSON.parseObject(retStr, TGFDaifuQueryResult.class);
                 if (result == null) {
                     logError(order, null, channel, "查询请求失败，解析返回数据失败");
                     return null;
                 }
			} catch (Exception e) {
				 logError(order, null, channel, "查询请求失败，解析返回数据失败");
				return null;
			}*/
            

            return entity;
        } catch (Exception e) {
            logException(order, null, channel, "查询请求失败", e);
            return null;
        }
    }

    public boolean isSuccessRequested(QueryResponseEntity queryResult) {
        if (StringUtils.isEmpty(queryResult.getRespCode())) return false;
       if ("00".equalsIgnoreCase(queryResult.getRespCode())) return true;
        if ("1".equalsIgnoreCase(queryResult.getStatus())) return true;
        return false;
    }

    public int transferBankStatus(String bankStatus) {
        int remitStatus = Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN;
        switch (bankStatus) {
            case "5": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING; break;
            case "1": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED; break;
            case "2": remitStatus = Global.USER_WITHDRAW_REMITSTATUS_PROCESS_FAILED; break;
        }
        return remitStatus;
    }
}
