package lottery.web.content;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javautils.date.Moment;
import javautils.http.HttpUtil;
import javautils.image.ImageUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserRechargeService;
import lottery.domains.content.biz.read.UserRechargeReadService;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.utils.RequestUtils;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;

@Controller
public class UserRechargeController extends AbstractActionController {
	public static final String SECRET_KEY = "adafs#af290adfjaldf1amko99asd";
	private static final String BROWSER_KEY = "browser";
	private static Logger logger = LoggerFactory.getLogger(UserRechargeController.class);
	private static ConcurrentHashMap<String, Date> LAST_COMMIT_CACHE = new ConcurrentHashMap<>(); // 防频繁提交消耗服务器资源,key:订单ID;value:上一次提交的时间,保留3个小时数据
	private static ConcurrentHashMap<String, Date> NOT_FOUND_OR_PROCESSED_CACHE = new ConcurrentHashMap<>(); // 未找到的订单，防止频繁提交消耗服务器资源,保留3个小时数据
	private static ConcurrentHashMap<String, Date> DUPLICATE_CACHE = new ConcurrentHashMap<>(); // 重复发起的订单，防止频繁提交消耗服务器资源,保留3个小时数据
	private static ConcurrentHashMap<String, Map<String, Object>> QR_CODE_CACHE = new ConcurrentHashMap<>(); // 已经生成的二维码缓存，保留2个小时
	private static ConcurrentHashMap<String, Boolean> NOTIFIED_CACHE = new ConcurrentHashMap<>(); // 已经通知成功处理的订单
	private static final String HTML_START = "<html><head><title>Pay Center</title><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'></head><body>";
	private static final String HTML_START_REFRESH = "<html><head><title>Pay Center</title><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'><meta http-equiv='refresh' content='5'/></head><body>";
	private static final String HTML_END = "</body></html>";
 

	/**
	 * Service
	 */
	@Autowired
	private UserRechargeService uRechargeService;
	@Autowired
	private UserRechargeReadService uRechargeReadService;

	/**
	 * Validate
	 */

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	/**
	 * 支付  数据封装 跳转
	 * @param request
	 * @return
	 */
	@RequestMapping(value = WUC.RECHARGE_SELF_REDIRECT, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public void RECHARGE_SELF_REDIRECT(HttpServletRequest request, HttpServletResponse response) {
		String pid = HttpUtil.getStringParameterTrim(request, "pid");
		String billno = HttpUtil.getStringParameterTrim(request, "billno");
		String amount = HttpUtil.getStringParameterTrim(request, "amount");
		String bankco = HttpUtil.getStringParameterTrim(request, "bankco");
		String Signature = HttpUtil.getStringParameterTrim(request, "Signature");
		String os = HttpUtil.getStringParameterTrim(request, "os");
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html");
			out = response.getWriter();

			if (StringUtils.isEmpty(pid) || StringUtils.isEmpty(billno) || StringUtils.isEmpty(amount) || StringUtils.isEmpty(Signature)) {
				out.print("参数不完整");
				return;
			}

			PaymentChannel channel = dataFactory.getPaymentChannel(Integer.valueOf(pid));
			if (channel == null) {
				out.print("暂不支持该充值通道");
			}
			else {
				StringBuffer sbHtml = new StringBuffer();
				sbHtml.append(HTML_START);
				sbHtml.append("跳转中...");
				sbHtml.append("<form id='payform' name='payform' action='"+ channel.getArmourUrl() + "/RechargeRedirect' method='post'>");
				sbHtml.append("<input type='hidden' name='pid' value='" + pid + "'/>");
				sbHtml.append("<input type='hidden' name='billno' value='" + billno + "'/>");
				sbHtml.append("<input type='hidden' name='amount' value='" + amount + "'/>");
				sbHtml.append("<input type='hidden' name='bankco' value='" + bankco + "'/>");
				sbHtml.append("<input type='hidden' name='os' value='" + os + "'/>");
				sbHtml.append("<input type='hidden' name='Signature' value='" + Signature + "'/>");
				sbHtml.append("</form>");
				sbHtml.append("<script>document.forms['payform'].submit();</script>");
				sbHtml.append(HTML_END);
				response.setCharacterEncoding("utf-8");
				out.print(sbHtml.toString());
			}
		} catch (Exception e) {
			logger.warn("支付处理数据异常pid={},billno={},amount={},bankco={},Signature={}", pid, billno, amount, bankco, Signature);
			logger.error("支付处理异常", e);
			out.print(HTML_START + "服务器繁忙，请稍候再试！" + HTML_END);
			return;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
	/**
	 * 支付  数据封装 跳转
	 * @param request
	 * @return
	 */
	@RequestMapping(value = WUC.RECHARGE_REDIRECT, method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public void RECHARGE_REDIRECT(HttpServletRequest request, HttpServletResponse response) {
		String pid = HttpUtil.getStringParameterTrim(request, "pid");
		String billno = HttpUtil.getStringParameterTrim(request, "billno");
		String amount = HttpUtil.getStringParameterTrim(request, "amount");
		String bankco = HttpUtil.getStringParameterTrim(request, "bankco");
		String Signature = HttpUtil.getStringParameterTrim(request, "Signature");
		String os = HttpUtil.getStringParameterTrim(request, "os");
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html");
			out = response.getWriter();

			// 防止提交非法数据
			if (StringUtils.isEmpty(pid)
					|| StringUtils.isEmpty(billno)
					|| StringUtils.isEmpty(amount)
					|| StringUtils.isEmpty(bankco)
					|| StringUtils.isEmpty(Signature)
					|| pid.length() >= 300
					|| billno.length() >= 300
					|| amount.length() >= 300
					
					|| bankco.length() >= 300
					|| Signature.length() >= 500) {
				out.print(HTML_START + "非法数据" + HTML_END);
				return;
			}

			// 防止提交不存在或已经处理的订单
			if (NOT_FOUND_OR_PROCESSED_CACHE.containsKey(billno)) {
				out.print(HTML_START + "该订单不存在或已处理" + HTML_END);
				return;
			}

			// 防止提交不存在或已经处理的订单
			if (DUPLICATE_CACHE.containsKey(billno)) {
				out.print(HTML_START + "订单重复，请关掉本窗口后重新选择！" + HTML_END);
				return;
			}

			// 防止订单频繁提交，也能防止一些恶意攻击
			Date lastCommitDate = LAST_COMMIT_CACHE.get(billno); // 上次刷新页面时间
			Moment nowMoment = new Moment();
			if (lastCommitDate != null) {
				Moment lastMoment = new Moment().fromDate(lastCommitDate);

				int seconds = nowMoment.difference(lastMoment, "second");
				if (seconds <= 3) { // 刷新间隔不能低于3秒
					out.print(HTML_START + "请不要频繁刷新..." + HTML_END);
					return;
				}
				else if (seconds >= 7200) {
					out.print(HTML_START + "该订单超过2个小时未处理，已作废" + HTML_END);
					return;
				}
			}

			// 重置订单上次提交时间
			LAST_COMMIT_CACHE.put(billno, new Date());

			// 是否已经处理过
			UserRecharge recharge = uRechargeService.getByBillno(billno);
			if (recharge == null || recharge.getStatus() != 0) {
				out.print(HTML_START + "该订单不存在或已处理" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			// 金额不一样
			double amountDouble = Double.valueOf(amount);
			if (recharge.getMoney() != amountDouble) {
				out.print(HTML_START + "检测到数据异常，请不要篡改内容！" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			// 查看订单是否已经超时
			Moment requestTime = new Moment().fromTime(recharge.getTime());
			int seconds = nowMoment.difference(requestTime, "second");
			if (seconds >= 7200) {
				out.print(HTML_START + "该订单超过2个小时未处理，已作废" + HTML_END);
				// 重置订单上次提交时间
				LAST_COMMIT_CACHE.put(billno, requestTime.toDate());
				return;
			}

			// 是否已经生成过二维码
			if (QR_CODE_CACHE.containsKey(billno)) {
				String qr = QR_CODE_CACHE.get(billno).get("qr").toString();
				out.print(HTML_START + "<div style='text-align: center;height:100%; background-color: #473b3b;'><div><div><img  style = 'margin-top:20%;margin-bottom:10px;' src='"+qr+"'></div><div><span  style='font-size: 18px;color:#db2222;'>请扫描二维码进行支付.</span></div></div>" + HTML_END);
				return;
			}

			// 检查充值方式是否开启
			PaymentChannel channel = dataFactory.getPaymentChannel(Integer.parseInt(pid));
			if (channel == null || channel.getStatus() != 0 || StringUtils.isEmpty(channel.getMerCode())) {
				out.print(HTML_START + "该通道暂不可用" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			String SignatureVal = DigestUtils.md5Hex("pid=" + pid + "&billno=" + billno + "&amount=" + amount + "&bankco=" + bankco + "|" + channel.getMd5Key());
			if (!StringUtils.equalsIgnoreCase(Signature, SignatureVal)) {
				out.print(HTML_START + "检测到数据异常，请不要篡改内容！" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			String ip = HttpUtil.getRealIp(null, request);
			if (ip == null) {
				out.print(HTML_START + "您当前所在网络不是合法IPV4地址，请您更换网络或线路后重试！" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			PrepareResult prepareResult = uRechargeService.prepare(channel, billno, amountDouble, bankco, request);
			if (prepareResult == null) {
				out.print(HTML_START + "通道异常，请重试" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			if (!prepareResult.isSuccess()) {
				out.print(HTML_START + prepareResult.getErrorMsg() + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
				return;
			}

			if (prepareResult.getJumpType() == PrepareResult.JUMP_TYPE_FORM) {
				
				outputForm(prepareResult, out, response);
			}
			
			if (prepareResult.getJumpType() == PrepareResult.QUICK_TYPE_FORM) {
				if(!BROWSER_KEY.equals(os)){
					prepareResult.setFormUrl(prepareResult.getQrCode());
				}
				outputForm(prepareResult, out, response);
			}
			else if (prepareResult.getJumpType() == PrepareResult.JUMP_TYPE_QR) {
				outputQRCode(prepareResult, billno, out, response);
			}
			else {
				out.print(HTML_START + "检测到数据异常，请不要篡改内容！" + HTML_END);
				NOT_FOUND_OR_PROCESSED_CACHE.put(billno, new Date());
			}
		} catch (Exception e) {
			logger.warn("支付处理数据异常pid={},billno={},amount={},bankco={},Signature={}", pid, billno, amount, bankco, Signature);
			logger.error("支付处理异常", e);
			out.print(HTML_START + "服务器繁忙，请稍候再试！" + HTML_END);
			return;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private void outputForm(PrepareResult prepareResult, PrintWriter out, HttpServletResponse response) {
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append(HTML_START);
		sbHtml.append("跳转中...");
		sbHtml.append("<form id='payform' name='payform' method='get' action='"+ prepareResult.getFormUrl() + "'>");
		Map<String, String> formParams = prepareResult.getFormParams();
		for (Map.Entry<String, String> entry : formParams.entrySet()) {
			sbHtml.append("<input type='hidden' name='"+ entry.getKey() + "' value='" + entry.getValue()+ "'/>");
		}
		sbHtml.append("</form>");
		sbHtml.append("<script>document.forms['payform'].submit();</script>");
		sbHtml.append(HTML_END);
		response.setCharacterEncoding("utf-8");
		out.print(sbHtml.toString());
	}

	private void outputQRCode(PrepareResult prepareResult, String billno, PrintWriter out, HttpServletResponse response) {
		out.print(HTML_START + "<div style='text-align: center;height:100%; background-color: #473b3b;'><div><div><img  style = 'margin-top:20%;margin-bottom:10px;' src='"+prepareResult.getQrCode()+"'></div><div><span  style='font-size: 18px;color:#db2222;'>请扫描二维码进行支付.</span></div></div>" + HTML_END);
		// 保存已经生成的二维码
		Map<String, Object> qrCache = new HashMap<>();
		qrCache.put("qr", prepareResult.getQrCode());
		qrCache.put("date", new Date());
		QR_CODE_CACHE.put(billno, qrCache);
	/*	try {
			response.sendRedirect(prepareResult.getQrCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    */    


	}

	
	/**
	 * 第三支付“异步”通知
	 * @return
	 */
	@RequestMapping(value = WUC.ANOTHER_PAYMENT_NOTIFY+"/{channelId}", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public void ANOTHER_PAYMENT_NOTIFY(@PathVariable(value = "channelId") Integer paymentId, HttpServletRequest request,HttpServletResponse response){
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String> resMap = new HashMap<>();
		for (Entry<String, String[]> each : params.entrySet()) {
			resMap.put(each.getKey(), each.getValue()[0]);
		}
		PaymentChannel channel = dataFactory.getPaymentChannel(paymentId);
		if (channel == null || channel.getStatus() != 0) {
			return;
		}

		PrintWriter out = null;
		try{
			String body = RequestUtils.inputStream2String(request.getInputStream());
			resMap.put("reqBody", body);

			out = response.getWriter();
			response.setCharacterEncoding("utf-8");
			response.setHeader("content-type","text/html;charset=UTF-8");
			out.println("ok");
			/*//解析通知参数
			VerifyResult verifyResult = uRechargeService.verify(channel, resMap);
			if(verifyResult == null){
				out.write("发生错误");
				return;
			}

			if (!verifyResult.isSuccess()) {
				out.write(verifyResult.getOutput());
				return;
			}

			// 防止提交不存在或已经处理的订单
			if (NOTIFIED_CACHE.containsKey(verifyResult.getSelfBillno())) {
				out.write("该订单正在处理或已处理！");
				return;
			}

			NOTIFIED_CACHE.put(verifyResult.getSelfBillno(), true);

			boolean recharged = uRechargeService.doRecharge(channel, verifyResult);
			if (recharged) {
				out.write(verifyResult.getSuccessOutput());
			}
			else {
				out.write(verifyResult.getFailedOutput());
			}*/
		}catch(Exception e){
			logger.error("支付回调发生异常", e);
			out.write("发生异常");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 第三支付“异步”通知
	 * @return
	 */
	@RequestMapping(value = WUC.USER_PAYMENT_NOTIFY+"/{channelId}", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public void USER_PAYMENT_NOTIFYURL(@PathVariable(value = "channelId") Integer paymentId, HttpServletRequest request,HttpServletResponse response){
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String> resMap = new HashMap<>();
		for (Entry<String, String[]> each : params.entrySet()) {
			resMap.put(each.getKey(), each.getValue()[0]);
		}
		PaymentChannel channel = dataFactory.getPaymentChannel(paymentId);
		if (channel == null || channel.getStatus() != 0) {
			return;
		}


		PrintWriter out = null;
 		try{
			String body = RequestUtils.inputStream2String(request.getInputStream());
			resMap.put("reqBody", body);

			out = response.getWriter();
			response.setCharacterEncoding("utf-8");
			response.setHeader("content-type","text/html;charset=UTF-8");

			//解析通知参数
			VerifyResult verifyResult = uRechargeService.verify(channel, resMap);
			if(verifyResult == null){
				out.write("发生错误");
				return;
			}

			if (!verifyResult.isSuccess()) {
				out.write(verifyResult.getOutput());
				return;
			}

			// 防止提交不存在或已经处理的订单
			if (NOTIFIED_CACHE.containsKey(verifyResult.getSelfBillno())) {
				out.write("SUCCESS");
				return;
			}

			NOTIFIED_CACHE.put(verifyResult.getSelfBillno(), true);

			boolean recharged = uRechargeService.doRecharge(channel, verifyResult);
			if (recharged) {
				out.write(verifyResult.getSuccessOutput());
			}
			else {
				out.write(verifyResult.getFailedOutput());
			}
		}catch(Exception e){
			logger.error("支付回调发生异常", e);
			out.write("发生异常");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 第三支付通知“同步”跳转
	 * @return
	 */
	@RequestMapping(value = WUC.USER_PAYMENT_RESULT+"/{channelId}", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public void USER_PAYMENT_RESULTURL(@PathVariable(value = "channelId") Integer channelId, HttpServletRequest request,HttpServletResponse response){
		try {
			String resultUrl = RequestUtils.getReferer(request) + "/manager?link=fund-recharge-record";
			redirect(response, resultUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void redirect(HttpServletResponse rsp, String url) throws IOException {
		rsp.setHeader("refresh", "0;url=" + url);
		rsp.flushBuffer();
	}

	/**
	 * 支付订单查询
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = WUC.USER_RECHARGE_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_RECHARGE_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		Integer type = HttpUtil.getIntParameter(request, "type");
		String billno = request.getParameter("billno");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		PageList pList = uRechargeReadService.search(sessionUser.getId(), type, billno, sTime, eTime, start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}



	/**
	 * 生成流水号
	 */
	private String billno() {
		// return new Moment().format("yyMMddss") + OrderUtil.getBillno(12, true);
		// return ObjectId.get().toString();
		return new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(8, true, true);
	}


	@Scheduled(cron = "0 0 0/2 * * ?")
	public void clearCache() {
		Moment nowMoment = new Moment();
		if (LAST_COMMIT_CACHE.size() > 500) {
			Set<Entry<String, Date>> entries = LAST_COMMIT_CACHE.entrySet();
			Iterator<Entry<String, Date>> iterator = entries.iterator();
			if (iterator.hasNext()) {
				Entry<String, Date> next = iterator.next();
				Moment lastMoment = new Moment().fromDate(next.getValue());

				int seconds = nowMoment.difference(lastMoment, "second");

				if (seconds >= 10800) {
					iterator.remove();
				}
			}
		}

		if (NOT_FOUND_OR_PROCESSED_CACHE.size() > 500) {
			Set<Entry<String, Date>> entries = NOT_FOUND_OR_PROCESSED_CACHE.entrySet();
			Iterator<Entry<String, Date>> iterator = entries.iterator();
			if (iterator.hasNext()) {
				Entry<String, Date> next = iterator.next();
				Moment lastMoment = new Moment().fromDate(next.getValue());

				int seconds = nowMoment.difference(lastMoment, "second");

				if (seconds >= 10800) {
					iterator.remove();
				}
			}
		}

		if (DUPLICATE_CACHE.size() > 500) {
			Set<Entry<String, Date>> entries = DUPLICATE_CACHE.entrySet();
			Iterator<Entry<String, Date>> iterator = entries.iterator();
			if (iterator.hasNext()) {
				Entry<String, Date> next = iterator.next();
				Moment lastMoment = new Moment().fromDate(next.getValue());

				int seconds = nowMoment.difference(lastMoment, "second");

				if (seconds >= 10800) {
					iterator.remove();
				}
			}
		}

		if (QR_CODE_CACHE.size() > 500) {
			Set<Entry<String, Map<String, Object>>> entries = QR_CODE_CACHE.entrySet();
			Iterator<Entry<String, Map<String, Object>>> iterator = entries.iterator();
			if (iterator.hasNext()) {
				Entry<String, Map<String, Object>> next = iterator.next();
				Moment lastMoment = new Moment().fromDate((Date) next.getValue().get("date"));

				int seconds = nowMoment.difference(lastMoment, "second");

				if (seconds >= 7500) {
					iterator.remove();
				}
			}
		}
	}

	@Scheduled(cron = "0 0 6 * * ?")
	public void clearFixedCache() {
		NOTIFIED_CACHE.clear();
	}
}