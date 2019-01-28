package lottery.domains.content.payment.tgf.utils;


/** Title: Config 基础参数配置类
 *  Description:
 *  设置商户相关信息及证书文件和通知地址等
 *  以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *  该代码仅供学习和研究接口使用，只是提供一个参考。
 *  Copyright: Copyright (c) 2017 91cloud, All Rights Reserved
 *  
 *
 *  @author Java Development Group
 *  @version 1.0
 */
public class Config {

	// 请选择签名类型，MD5或RSA， 默认MD5
    public static final String SIGN_TYPE = "MD5";
    //public static final String SIGN_TYPE = "RSA";
    
    
  
    // 商户私钥文件--用于商户数据签名
  	public static String PFX_FILE = "c:/temp/test/mzh.pfx";
    // 支付公钥文件--用于摩宝支付返回数据验签s
  	public static String CERT_FILE = "c:/temp/test/epay.cer";
    // 私钥文件密码
  	public static String PASSWD = "mzh";
    // 商户的MD5密钥
    public static final String KEY = "";

    public static final String MERCHANT_ID = "";

    // 商户的通知地址
    public static final String MERCHANT_NOTIFY_URL = "";

    // 商户的网关地址
    public static final String GATEWAY_URL = "";

    // 商户的版本号
    public static final String API_VERSION = "1.0.0.0";

    // 商户的配置
    public static final String APINAME_PAY = "TRADE.B2C";
    public static final String APINAME_SCANPAY = "TRADE.SCANPAY";
    public static final String APINAME_H5PAY = "TRADE.H5PAY";
    public static final String APINAME_QUICKPAY_APPLY = "TRADE.QUICKPAY.APPLY";
    public static final String APINAME_QUERY = "TRADE.QUERY";
    public static final String APINAME_REFUND = "TRADE.REFUND";
    public static final String APINAME_SETTLE = "TRADE.SETTLE";
    public static final String APINAME_SETTLE_QUERY = "TRADE.SETTLE.QUERY";
    public static final String APINAME_NOTIFY = "TRADE.NOTIFY";
    public static final String APINAME_BALANCE_QUERY = "BALANCE.QUERY";
    public static final String APINAME_QUICKPAY_CONFIRM = "TRADE.QUICKPAY.CONFIRM";
    
    
    
    
}
