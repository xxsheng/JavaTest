//package lottery.domains.content.payment.lepay;
//
//import admin.web.WebJSONObject;
//import javautils.http.HttpClientUtil;
//import lottery.domains.content.entity.PaymentChannel;
//import lottery.domains.content.payment.cfg.PayConfig;
//import lottery.domains.content.payment.lepay.response.FCSOpenApiResponse;
//import lottery.domains.content.payment.lepay.utils.CoderUtil;
//import lottery.domains.content.payment.lepay.utils.JsonUtil;
//import lottery.domains.content.payment.lepay.utils.RSACoderUtil;
//import lottery.domains.content.payment.lepay.utils.WebUtil;
//import lottery.domains.content.payment.utils.MoneyFormat;
//import net.sf.json.JSONObject;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * 乐付
// * Created by Nick on 2017/4/20.
// */
//@Component
//public class LepayPayment {
//    private static final Logger log = LoggerFactory.getLogger(LepayPayment.class);
//
//    @Value("${lepay.daifu.url}")
//    private String daifuUrl;
//
//    /**
//     * 代付接口，返回第三方的注单ID
//     * @param money 代付金额，元，最多2位小数
//     * @param billno 代付订单号
//     * @param bankCode 银行编码
//     * @param name 用户姓名
//     * @param card 卡号
//     */
//    public String daifu(WebJSONObject json, PaymentChannel channel, double money, String billno,
//                        String bankCode, String name, String card, String branchName, String returnUrl) {
//        try {
//            log.debug("开始乐付代付,注单ID:{},姓名:{},卡号:{},分行:{}", billno, name, card, branchName);
//            return daifuInternel(json, channel, money, billno, bankCode, name, card, branchName, returnUrl);
//        } catch (Exception e) {
//            log.error("乐付代付发生异常", e);
//            json.set(2, "2-4000");
//            return null;
//        }
//    }
//
//
//
//    private String daifuInternel(WebJSONObject json, PaymentChannel channel, double money, String billno,
//                                 String bankCode, String name, String card, String branchName, String returnUrl){
//        try {
//            String amount = MoneyFormat.FormatPay41(money+"");
//
//            Map<String, String> paramsMap = new LinkedHashMap<>();
//            paramsMap.put("service", "pay"); // 代付:pay
//            paramsMap.put("partner", channel.getMerCode()); // 商户合作号，由平台注册提供
//            paramsMap.put("out_trade_no", billno); // 原始商户订单
//            paramsMap.put("amount_str", amount); // 金额（0.01～9999999999.99）
//            paramsMap.put("bank_sn", bankCode); // 银行编码
//            paramsMap.put("bank_site_name", branchName); // 开户网点，如果非空，最多80个汉字
//            paramsMap.put("bank_account_name", name); // 开户名
//            paramsMap.put("bank_account_no", card); // 银行账号
//            paramsMap.put("bus_type", "11"); // 00:对公 11:对私（不填默认对私,暂时只对私）
//            paramsMap.put("bank_mobile_no", "13888888888"); // 银行帐号预留手机号码
//            paramsMap.put("bank_province", "北京"); // 银行卡省份
//            paramsMap.put("bank_city", "北京"); // 银行卡省份
//            paramsMap.put("user_agreement", "1"); // 用户同意标示 1:同意
//            paramsMap.put("remark", "df"); // 备注
//            paramsMap.put("return_url", returnUrl); // 后台通知地址
//
//            String paramsString = WebUtil.getURL(paramsMap);
//            //以上为加密字符串
//
//            String sign = URLEncoder.encode(RSACoderUtil.sign(paramsString.getBytes("UTF-8"), PayConfig.LEPAY.PRIVATE_KEY), "UTF-8");
//            Map<String, String> contents = new HashMap<>();
//            contents.put("partner", channel.getMerCode());
//            contents.put("content", RSACoderUtil.getParamsWithDecodeByPublicKey(paramsString, "UTF-8", PayConfig.LEPAY.FCS_PUBLIC_KEY));
//            contents.put("input_charset", "UTF-8");
//            contents.put("sign_type", "SHA1WithRSA");
//            contents.put("sign", sign);
//            // String strResult = WebUtil.doPost(daifuUrl, contents, "UTF-8", 3000, 15000);
//
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//            String strResult = HttpClientUtil.postAsStream(daifuUrl, contents, headers, 5000);
//            FCSOpenApiResponse openApiResponse = JsonUtil.parseObject(strResult, FCSOpenApiResponse.class);
//
//            if (openApiResponse.getIs_succ().equals("T")) {
//                String responseCharset = openApiResponse.getCharset();
//                byte[] byte64 = CoderUtil.decryptBASE64(openApiResponse.getResponse());
//                String responseResult = new String(RSACoderUtil.decryptByPrivateKey(byte64, PayConfig.LEPAY.PRIVATE_KEY), responseCharset);
//                if (StringUtils.isNotEmpty(responseResult)) {
//                    JSONObject jsonObject = JSONObject.fromObject(responseResult);
//                    String tradeId = jsonObject.getString("trade_id");
//                    if (StringUtils.isNotEmpty(tradeId)) {
//                        log.debug("乐付代付请求返回订单号：{}", tradeId);
//                        return tradeId;
//                    }
//                    else {
//                        log.error("乐付返回订单ID为空,我方订单ID:" + billno);
//                        json.setWithParams(2, "2-4014");
//                        return null;
//                    }
//                }
//                else{
//                    log.error("乐付代付请求失败，解析返回数据失败：" + strResult + ",我方订单ID:" + billno);
//                    json.setWithParams(2, "2-4007", strResult);
//                    return null;
//                }
//            }
//            else {
//                log.error("乐付代付请求失败，返回数据为：" + strResult + ",我方订单ID:" + billno);
//                json.setWithParams(2, "2-4002", openApiResponse.getFault_reason());
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("乐付代付失败,发生异常", e);
//            json.set(2, "2-4000");
//            return null;
//        }
//    }
//
//    /**
//     * 乐付快捷代付接口，返回第三方的注单ID
//     * @param channel 第三方账号的bean
//     * @param money 代付金额，元，最多2位小数
//     * @param billno 代付订单号
//     * @param bankCode 银行编码
//     * @param name 用户姓名
//     * @param card 卡号
//     */
//    public String daifuSpeed(WebJSONObject json, PaymentChannel channel, double money, String billno,
//                             String bankCode, String name, String card, String branchName, String returnUrl) {
//        try {
//            log.debug("开始乐付快捷代付,注单ID:{},姓名:{},卡号:{},分行:{}", billno, name, card, branchName);
//            return daifuSpeedInternel(json, channel, money, billno, bankCode, name, card, branchName, returnUrl);
//        } catch (Exception e) {
//            log.error("乐付快捷代付发生异常", e);
//            json.set(2, "2-4000");
//            return null;
//        }
//    }
//
//
//
//    private String daifuSpeedInternel(WebJSONObject json, PaymentChannel channel, double money, String billno,
//                                      String bankCode, String name, String card, String branchName, String returnUrl){
//        try {
//            String amount = MoneyFormat.FormatPay41(money+"");
//
//            Map<String, String> paramsMap = new LinkedHashMap<>();
//            paramsMap.put("service", "pay"); // 代付:pay
//            paramsMap.put("partner", channel.getMerCode()); // 商户合作号，由平台注册提供
//            paramsMap.put("out_trade_no", billno); // 原始商户订单
//            paramsMap.put("amount_str", amount); // 金额（0.01～9999999999.99）
//            paramsMap.put("bank_sn", bankCode); // 银行编码
//            paramsMap.put("bank_site_name", branchName); // 开户网点，如果非空，最多80个汉字
//            paramsMap.put("bank_account_name", name); // 开户名
//            paramsMap.put("bank_account_no", card); // 银行账号
//            paramsMap.put("bus_type", "11"); // 00:对公 11:对私（不填默认对私,暂时只对私）
//            paramsMap.put("bank_mobile_no", "13888888888"); // 银行帐号预留手机号码
//            paramsMap.put("bank_province", "北京"); // 银行卡省份
//            paramsMap.put("bank_city", "北京"); // 银行卡省份
//            paramsMap.put("user_agreement", "1"); // 用户同意标示 1:同意
//            paramsMap.put("remark", "df"); // 备注
//            paramsMap.put("return_url", returnUrl); // 后台通知地址
//
//            String paramsString = WebUtil.getURL(paramsMap);
//            //以上为加密字符串
//
//            String sign = URLEncoder.encode(RSACoderUtil.sign(paramsString.getBytes("UTF-8"), PayConfig.LEPAY.SPEED_PAY_PRIVATE_KEY), "UTF-8");
//            Map<String, String> contents = new HashMap<>();
//            contents.put("partner", channel.getMerCode());
//            contents.put("content", RSACoderUtil.getParamsWithDecodeByPublicKey(paramsString, "UTF-8", PayConfig.LEPAY.FCS_PUBLIC_KEY));
//            contents.put("input_charset", "UTF-8");
//            contents.put("sign_type", "SHA1WithRSA");
//            contents.put("sign", sign);
//            // String strResult = WebUtil.doPost(daifuUrl, contents, "UTF-8", 3000, 15000);
//
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//            String strResult = HttpClientUtil.postAsStream(daifuUrl, contents, headers, 5000);
//            FCSOpenApiResponse openApiResponse = JsonUtil.parseObject(strResult, FCSOpenApiResponse.class);
//
//            if (openApiResponse.getIs_succ().equals("T")) {
//                String responseCharset = openApiResponse.getCharset();
//                byte[] byte64 = CoderUtil.decryptBASE64(openApiResponse.getResponse());
//                String responseResult = new String(RSACoderUtil.decryptByPrivateKey(byte64, PayConfig.LEPAY.SPEED_PAY_PRIVATE_KEY), responseCharset);
//                if (StringUtils.isNotEmpty(responseResult)) {
//                    JSONObject jsonObject = JSONObject.fromObject(responseResult);
//                    String tradeId = jsonObject.getString("trade_id");
//                    if (StringUtils.isNotEmpty(tradeId)) {
//                        log.debug("乐付快捷代付请求返回订单号：{}", tradeId);
//                        return tradeId;
//                    }
//                    else {
//                        log.error("乐付快捷返回订单ID为空,我方订单ID:" + billno);
//                        json.setWithParams(2, "2-4014");
//                        return null;
//                    }
//                }
//                else{
//                    log.error("乐付快捷代付请求失败，解析返回数据失败：" + strResult + ",我方订单ID:" + billno);
//                    json.setWithParams(2, "2-4007", strResult);
//                    return null;
//                }
//            }
//            else {
//                log.error("乐付快捷代付请求失败，返回数据为：" + strResult + ",我方订单ID:" + billno);
//                json.setWithParams(2, "2-4002", openApiResponse.getFault_reason());
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("乐付快捷代付失败,发生异常", e);
//            json.set(2, "2-4000");
//            return null;
//        }
//    }
//}
