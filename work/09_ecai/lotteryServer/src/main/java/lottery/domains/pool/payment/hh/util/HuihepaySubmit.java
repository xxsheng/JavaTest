// package lottery.domains.pool.payment.hh.util;
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
//      * 提供给商户的服务接入网关URL(新)
//      */
// //    private static final String GATEWAY_NEW = "https://pay.huihepay.com/gateway/?";
//
//     /**
//      * 生成签名结果
//      * @param sPara 要签名的数组
//      * @return 签名结果字符串
//      */
// 	public static String buildRequestMysign(Map<String, String> sPara) {
//     	String prestr = HuihepayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
//         String mysign = MD5.sign(prestr, PayConfig.HH.WYKEY, "UTF-8");
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
//         sPara.put("Sign", mysign);
//         sPara.put("SignType", "MD5");
//
//         return sPara;
//     }
//
//     /**
//      * 建立请求，以表单HTML形式构造（默认）
//      * @param sParaTemp 请求参数数组
//      * @param strMethod 提交方式。两个值可选：post、get
//      * @param strButtonName 确认按钮显示文字
//      * @return 提交表单HTML文本
//      */
// //    public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
// //        //待请求参数数组
// //        Map<String, String> sPara = buildRequestPara(sParaTemp);
// //        List<String> keys = new ArrayList<String>(sPara.keySet());
// //
// //        StringBuffer sbHtml = new StringBuffer();
// //
// //        sbHtml.append("<form id=\"submit\" name=\"submit\" action=\"" + GATEWAY_NEW + "\" method=\"" + strMethod  + "\">");
// //        for (int i = 0; i < keys.size(); i++) {
// //            String name = (String) keys.get(i);
// //            String value = (String) sPara.get(name);
// //            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
// //        }
// //
// //        //submit按钮控件请不要含有name属性
// //        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
// //        sbHtml.append("<script>document.forms['submit'].submit();</script>");
// //
// //        return sbHtml.toString();
// //    }
//
//
// //    /**
// //     * MAP类型数组转换成NameValuePair类型
// //     * @param properties  MAP类型数组
// //     * @return NameValuePair类型数组
// //     */
// //    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
// //        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
// //        int i = 0;
// //        for (Map.Entry<String, String> entry : properties.entrySet()) {
// //            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
// //        }
// //
// //        return nameValuePair;
// //    }
// }
