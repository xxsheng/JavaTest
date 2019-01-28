package lottery.domains.pool.payment.tgf.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class QueryResponseEntity {

	protected String respCode;
	protected String respDesc;
	protected String accDate;
	protected String accNo;
	protected String orderNo;
	protected String status;
	protected String signMsg;

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}

	public String getAccDate() {
		return accDate;
	}

	public void setAccDate(String accDate) {
		this.accDate = accDate;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSignMsg() {
		return signMsg;
	}

	public void setSignMsg(String signMsg) {
		this.signMsg = signMsg;
	}

	private static Map<String, String> ORDER_STATUS = new HashMap<String, String>();
	static {
		// 0-未支付 1-成功 2-失败 4-部分退款 5-全额退款 9-退款处理中 10-未支付 11-订单过期
		ORDER_STATUS.put("0", "未支付");
		ORDER_STATUS.put("1", "成功");
		ORDER_STATUS.put("2", "失败");
		ORDER_STATUS.put("4", "部分退款");
		ORDER_STATUS.put("5", "全额退款");
		ORDER_STATUS.put("9", "退款处理中");
		ORDER_STATUS.put("10", "未支付");
		ORDER_STATUS.put("11", "订单过期");
	}

	public void parse(String respStr,String mk5key) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		XMLParserUtil.parse(respStr, resultMap);
		Document doc = DocumentHelper.parseText(respStr);
		Element root = doc.getRootElement();
		Element respData = root.element("detail");
		String srcData = respData.asXML();
		respCode = resultMap.get("/message/detail/code");
		if (StringUtils.isBlank(respCode)) {
			throw new Exception("响应信息格式错误：不存在'code'节点。");
		}
		respDesc = resultMap.get("/message/detail/desc");
		if (StringUtils.isBlank(respDesc)) {
			throw new Exception("响应信息格式错误：不存在'desc'节点");
		}
		if ("00".equalsIgnoreCase(respCode)) {
			accDate = resultMap.get("/message/detail/opeDate");
			if (StringUtils.isBlank(accDate)) {
				throw new Exception("响应信息格式错误：不存在'opeDate'节点。");
			}
			accNo = resultMap.get("/message/detail/opeNo");
			if (StringUtils.isBlank(accNo)) {
				throw new Exception("响应信息格式错误：不存在'opeNo'节点。");
			}
			orderNo = resultMap.get("/message/detail/tradeNo");
			if (StringUtils.isBlank(orderNo)) {
				throw new Exception("响应信息格式错误：不存在'tradeNo'节点。");
			}
			status = resultMap.get("/message/detail/status");
			if (StringUtils.isBlank(status)) {
				throw new Exception("响应信息格式错误：不存在'status'节点。");
			} 
		}
		signMsg = resultMap.get("/message/sign");
		if (StringUtils.isBlank(signMsg)) {
			throw new Exception("响应信息格式错误：不存在'sign'节点");
		}
		
		if (!getSignMsg().equalsIgnoreCase(SignUtil.signByMD5(srcData, mk5key))) {
			throw new Exception("响应信息格式错误：md5验证签名失败");
		}
	
	}

}
