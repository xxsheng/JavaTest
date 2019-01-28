package lottery.domains.content.payment.jiushui;

import admin.web.WebJSONObject;
import com.alibaba.fastjson.JSON;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.payment.jiushui.util.SignUtils;
import lottery.domains.content.payment.lepay.utils.WebUtil;
import lottery.domains.content.payment.mkt.URLUtils;
import lottery.domains.content.payment.utils.MoneyFormat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 玖水
 * Created by Nick on 2017-05-28.
 */
@Component
public class JiushuiPayment {
    private static final Logger log = LoggerFactory.getLogger(JiushuiPayment.class);

    @Value("${jiushui.daifu.url}")
    private String daifuUrl;

    @Value("${jiushui.daifu.private_key}")
    private String daifuPrivateKey;

    /**
     * 代付接口，返回第三方的注单ID
     */
    public String daifu(WebJSONObject json, PaymentChannel channel, double money, String billno,
                        String opnbnk, String opnbnknam, String name, String card, String branchName) {
        try {
            log.debug("开始玖水代付,注单ID:{},姓名:{},卡号:{},分行:{}", billno, name, card, branchName);
            return daifuInternel(json, channel.getMerCode(), money, billno, opnbnk, opnbnknam, name, card, branchName);
        } catch (Exception e) {
            log.error("玖水代付发生异常", e);
            json.set(2, "2-4000");
            return null;
        }
    }


    private String daifuInternel(WebJSONObject json, String merCode, double money, String billno,
                                 String opnbnk, String opnbnknam, String name, String card, String branchName){
        try {
            long fenMoney = MoneyFormat.yuanToFenMoney(money+"");

            StringBuffer signSrc= new StringBuffer();
            signSrc.append("CP_NO=").append(billno);
            signSrc.append("&TXNAMT=").append(fenMoney);
            signSrc.append("&OPNBNK=").append(opnbnk);
            signSrc.append("&OPNBNKNAM=").append(opnbnknam);
            signSrc.append("&ACTNO=").append(card);
            signSrc.append("&ACTNAM=").append(name);
            signSrc.append("&ACTIDCARD=").append("440901197709194316");
            signSrc.append("&ACTMOBILE=").append("16888888888");

            String dataStr = signSrc.toString();
            String sign;
            try {
                sign = SignUtils.Signaturer(dataStr, daifuPrivateKey);
            } catch (Exception e) {
                log.error("玖水代付发生签名异常", e);
                json.set(2, "2-4003");
                return null;
            }


            Map<String, String> paramsMap = new LinkedHashMap<>();
            paramsMap.put("MERCNUM", merCode);
            paramsMap.put("TRANDATA", URLUtils.encode(dataStr, "UTF-8"));
            paramsMap.put("SIGN", URLUtils.encode(sign, "UTF-8"));

            String strResult = WebUtil.doPost(daifuUrl, paramsMap, "UTF-8", 3000, 15000);

            if (StringUtils.isEmpty(strResult)) {
                log.error("玖水代付请求失败，发送请求后返回空数据");
                json.set(2, "2-4006");
                return null;
            }

            Map<String, String> retMap = JSON.parseObject(strResult, HashMap.class);
            String reCode = retMap.get("RECODE"); // 返回码 000000为成功其他均为失败，根据返回信息处理。
            String reMsg = retMap.get("REMSG"); // 返回信息
            String PROXYNO = retMap.get("PROXYNO"); // 平台代付订单号
            String CP_NO = retMap.get("CP_NO"); // 商户代付订单号

            if ("000000".equals(reCode)) {
                if (StringUtils.isNotEmpty(PROXYNO)) {
                    log.debug("玖水代付请求返回订单号：{}", PROXYNO);
                    return PROXYNO;
                }
                else {
                    log.error("玖水代付返回订单ID为空,我方订单ID:" + billno);
                    json.setWithParams(2, "2-4014");
                    return null;
                }
            }
            else {
                log.error("玖水代付请求失败,返回数据为：" + strResult + ",我方订单ID:" + billno);
                json.setWithParams(2, "2-4002", reMsg);
                return null;
            }
        } catch (Exception e) {
            log.error("玖水代付失败,发生异常", e);
            json.set(2, "2-4000");
            return null;
        }
    }
}
