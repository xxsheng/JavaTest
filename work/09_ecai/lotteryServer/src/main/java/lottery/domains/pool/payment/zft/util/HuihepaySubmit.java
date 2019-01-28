// package lottery.domains.pool.payment.zft.util;
//
// import java.util.Map;
//
//
// import lottery.domains.pool.payment.cfg.PayConfig;
//
//
//
// public class HuihepaySubmit {
//
//     /**
//      * 生成签名结果
//      * @param sPara 要签名的数组
//      * @return 签名结果字符串
//      */
// 	public static String buildRequestMysign(Map<String, String> sPara) {
//     	String prestr = HuihepayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
//         String mysign = SHAUtils.sign(prestr, PayConfig.ZFT.WYKEY, "UTF-8");
//         return mysign;
//     }
//
//     /**
//      * 生成要请求给的参数数组
//      * @param sParaTemp 请求前的参数数组
//      * @return 要请求的参数数组
//      */
// 	public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
//         //除去数组中的空值和签名参数
//         Map<String, String> sPara = HuihepayCore.paraFilter(sParaTemp);
//         //生成签名结果
//         String mysign = buildRequestMysign(sPara);
//
//         //签名结果与签名方式加入请求提交参数组中
//         sPara.put("sign", mysign);
//         sPara.put("signType", "SHA");
//
//         return sPara;
//     }
// }
