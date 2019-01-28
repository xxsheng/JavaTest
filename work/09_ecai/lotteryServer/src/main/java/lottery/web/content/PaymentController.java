package lottery.web.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.http.HttpUtil;
import javautils.math.MathUtil;
import lottery.domains.content.biz.PaymentCardService;
import lottery.domains.content.biz.PaymentChannelQrCodeService;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.biz.UserRechargeService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelQrCode;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.pay.PaymentCardVO;
import lottery.domains.content.vo.pay.PaymentChannelVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;

@Controller
public class PaymentController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserRechargeService uRechargeService;

	@Autowired
	private PaymentCardService paymentCardService;

	@Autowired
	private PaymentChannelService paymentChannelService;
	@Autowired
	private PaymentChannelQrCodeService paymentChannelQrCodeService;

	@Autowired
	private UserReadService uReadService;

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
	 * 生成流水号
	 */
	private String billno() {
		return new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(8, true, true);
	}

	/**
	 * 查询支付 方式列表
	 * 
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = WUC.PAYMENT_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> PAYMENT_LIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}
		if(uEntity.getUpid() == 0){
			json.set(0, "2-13");
			return json.toJson();
		}
		
		
		boolean hasWithdrawPwd = StringUtil.isNotNull(uEntity.getWithdrawPassword()) ? true : false;
		
		if (hasWithdrawPwd) {
			List<PaymentChannelVO> channelVOs = paymentChannelService.getAvailableByUserId(sessionUser);
			json.data("channels", channelVOs);
		}

		
		json.data("hasWithdrawPwd", hasWithdrawPwd);
		json.set(0, "0-1");
		return json.toJson();
	}

	/**
	 * 第三方 生成支付订单
	 * 
	 * @param session
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WUC.RECHARGE_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> RECHARGE_ADD(HttpSession session, HttpServletRequest request) throws Exception {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int pid = HttpUtil.getIntParameter(request, "pid");
		String ip = HttpUtil.getRealIp(null, request);
		Integer bankId = HttpUtil.getIntParameter(request, "bankId");
		double amount = HttpUtil.getIntParameter(request, "amount");
		String bankco = HttpUtil.getStringParameterTrim(request, "bankco");
		PaymentChannel channel = dataFactory.getPaymentChannel(pid);
		if (!paymentChannelService.isAvailable(json, sessionUser, amount, channel)) {
			return json.toJson();
		}

		if (bankId != null && bankId <= 0) {
			bankId = null;
		}

		String billno = billno();
		boolean result = uRechargeService.addRecharge(sessionUser.getId(), amount, billno, channel, bankId,ip);
		if (result) {
			String link = channel.getArmourUrl(); // 马甲地址
			Map<String, Object> data = new HashMap<>();

			String Mer_key = channel.getMd5Key();
			String pidStr = String.valueOf(pid);
			String amountStr = MathUtil.doubleToString(amount, 0);
			String Signature = DigestUtils.md5Hex("pid=" + pidStr + "&billno=" + billno + "&amount="
					+ String.valueOf(amountStr) + "&bankco=" + bankco + "|" + Mer_key);
			// String qrUrlCode = channel.getQrUrlCode();

			if (channel.getType() == Global.PAYMENT_CHANNEL_TYPE_MOBILE) {
				String qrCode = null;
				// 微信扫码
				if (channel.getSubType() == Global.PAYMENT_CHANNEL_SUB_TYPE_MOBILE_WECHAT_SCAN) {
					if (channel.getFixedQRAmount() == 1) {
						int qrCodeId = HttpUtil.getIntParameter(request, "qrCodeId");
						PaymentChannelQrCode paymentChannelQrCode = paymentChannelQrCodeService.getById(qrCodeId);
						if (null != paymentChannelQrCode) {
							qrCode = paymentChannelQrCode.getQrUrlCode();
						}
					} else {
						qrCode = channel.getQrUrlCode();
					}
				}
				// 支付宝扫码
				else if (channel.getSubType() == Global.PAYMENT_CHANNEL_SUB_TYPE_MOBILE_ALIPAY_SCAN) {
					if (channel.getFixedQRAmount() == 1) {
						int qrCodeId = HttpUtil.getIntParameter(request, "qrCodeId");
						PaymentChannelQrCode paymentChannelQrCode = paymentChannelQrCodeService.getById(qrCodeId);
						if (null != paymentChannelQrCode) {
							qrCode = paymentChannelQrCode.getQrUrlCode();
						}
					} else {
						qrCode = channel.getQrUrlCode();
					}
				}
				// QQ钱包扫码
				else if (channel.getSubType() == Global.PAYMENT_CHANNEL_SUB_TYPE_MOBILE_QQ_SCAN) {
					if (channel.getFixedQRAmount() == 1) {
						int qrCodeId = HttpUtil.getIntParameter(request, "qrCodeId");
						PaymentChannelQrCode paymentChannelQrCode = paymentChannelQrCodeService.getById(qrCodeId);
						if (null != paymentChannelQrCode) {
							qrCode = paymentChannelQrCode.getQrUrlCode();
						}
					} else {
						qrCode = channel.getQrUrlCode();
					}
				}

				if (qrCode != null) {
					data.put("qrUrl", qrCode);
				}
			}

			data.put("link", link);
			data.put("pid", pidStr);
			data.put("billno", billno);
			data.put("type", channel.getType());
			data.put("amount", amountStr); // 金额
			data.put("bankco", bankco); // 银行(假如微信或者支付宝 WEIXIN ALIPAY)
			data.put("Signature", Signature);
			data.put("channelCode", channel.getChannelCode());// 支付方式
			json.data("data", data);
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	/**
	 * <p>
	 * Title: 查询网银l转帐支付银行
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WUC.BANK_TRANSFERS_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> BANK_TRANSFERS_LOAD(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int pid = HttpUtil.getIntParameter(request, "pid");
		double amount = HttpUtil.getDoubleParameter(request, "amount");
		PaymentChannel channel = dataFactory.getPaymentChannel(pid);
		if (!paymentChannelService.isAvailable(json, sessionUser, amount, channel)) {
			return json.toJson();
		}

		List<PaymentCardVO> pcList = paymentCardService.getAvailableCardsByUserId(sessionUser.getId(), amount);
		if (pcList != null && pcList.size() > 0) {
			json.data("data", pcList.get(0));
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}

	/**
	 * 网银转账
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WUC.BANK_TRANSFERS, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> BANK_TRANSFERS(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int pid = HttpUtil.getIntParameter(request, "pid");
		int bankId = HttpUtil.getIntParameter(request, "bankId");
		double amount = HttpUtil.getDoubleParameter(request, "amount");
		String name = HttpUtil.getStringParameterTrim(request, "name"); // 转账人
		String postscript = HttpUtil.getStringParameterTrim(request, "postscript");
		if (HttpUtil.isSpecialChar(postscript)) {
			json.set(2, "2-2023"); 
			return json.toJson();
		}
		if (HttpUtil.isSpecialChar(name)) {
			json.set(2, "2-2023"); 
			return json.toJson();
		}
		int count = uRechargeService.checkUnPaidWangYingTransfersRecharge( json,  sessionUser.getId(),  null);
		if (count>=5){
			json.set(2, "2-6029");	
			return json.toJson();
		}
		PaymentChannel channel = dataFactory.getPaymentChannel(pid);
		if (!paymentChannelService.isAvailable(json, sessionUser, amount, channel)) {
			return json.toJson();
		}

		PaymentCardVO cardBean = paymentCardService.getRandomAvailableByUserId(sessionUser.getId(), amount);
		if(cardBean != null) {
			String billno = billno();
			String ip = HttpUtil.getRealIp(null, request);
		    uRechargeService.addWangYingTransfersRecharge(sessionUser.getId(), amount, billno, name, cardBean.getId(), channel, bankId,postscript,ip);
			PaymentBank paymentBank = dataFactory.getPaymentBank(bankId);
			if(postscript != null) {
				Map<String, Object> data = new HashMap<>();
				data.put("billno", billno);
				data.put("bankName", cardBean.getBankName());
				data.put("branchName", cardBean.getBranchName());
				data.put("cardName", cardBean.getCardName());
				data.put("cardId", cardBean.getCardId());
				data.put("amount", amount);
				data.put("postscript", postscript);
				data.put("name", name);
				data.put("payType", "bankTransfer");
				data.put("url", paymentBank.getUrl());
				json.data("data", data);
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		} else {
			json.set(2, "2-1079");
		}
		return json.toJson();
	}
}