// package lottery.domains.pool.payment.ifbao;
//
// import java.util.Map;
//
// import org.apache.commons.lang.StringUtils;
//
// import lottery.domains.pool.payment.cfg.PayConfig;
//
// public class IfbaoSignUtil {
// 	/**
// 	 * 将由支付请求参数构成的map转换成支付串，并对参数做合法验证
// 	 *
// 	 * @param paramsMap
// 	 *            由支付请求参数构成的map
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static String generatePayRequest(Map<String, String> paramsMap)
// 			throws Exception {
// 		String paramsStr = "";
// 		// 验证输入数据合法性
// 		if (!paramsMap.containsKey("payType")
// 				|| StringUtils.isBlank(paramsMap.get("payType"))) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("consumerNo")
// 				|| StringUtils.isBlank(paramsMap.get("consumerNo"))) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("merOrderNum")
// 				|| StringUtils.isBlank(paramsMap.get("merOrderNum"))) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("tranAmt")
// 				|| StringUtils.isBlank("tranAmt")) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("callbackUrl")
// 				|| StringUtils.isBlank("callbackUrl")) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("goodsName")
// 				|| StringUtils.isBlank("goodsName")) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("goodsDetail")
// 				|| StringUtils.isBlank("goodsDetail")) {
// 			return paramsStr;
// 		}
// 		if (!paramsMap.containsKey("merRemark1")
// 				|| StringUtils.isBlank("merRemark1")) {
// 			return paramsStr;
// 		}
// 		// 输入数据组织成字符串
// 		paramsStr = "consumerNo=[" + paramsMap.get("consumerNo") + "]" +
// 				"merOrderNum=[" + paramsMap.get("merOrderNum") + "]tranAmt=[" + paramsMap.get("tranAmt") + "]" +
// 				"payType=["+paramsMap.get("payType")+"]callbackUrl=[" + paramsMap.get("callbackUrl") + "]" +
// 				"userKey=[" + PayConfig.IFBAOPAY.PUBLIC_KEY + "]";
// 		return paramsStr;
// 	}
// }
