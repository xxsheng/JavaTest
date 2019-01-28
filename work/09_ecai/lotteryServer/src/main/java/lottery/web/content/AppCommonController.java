package lottery.web.content;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.mobile.MobileChecker;
import lottery.domains.content.biz.LotteryPlayRulesService;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.biz.UserLoginLogService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.SysLotteryConfigVO;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;
import lottery.domains.content.vo.lottery.LotteryVO;
import lottery.domains.content.vo.pay.PaymentChannelVO;
import lottery.domains.content.vo.user.UserBaseVO;
import lottery.domains.content.vo.user.UserCodeVO;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.payment.utils.RequestUtils;
import lottery.web.AppWUC;
import lottery.web.WSC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class AppCommonController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private LotteryPlayRulesService lPlayRulesService;

	@Autowired
	private PaymentChannelService paymentChannelService;

	@Autowired
	private UserService uService;

	@Autowired
	private UserReadService uReadService;

	@Autowired
	private UserLoginLogService uLoginLogService;

	/**
	 * Validate
	 */
	@Autowired
	private UserValidate uValidate;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@RequestMapping(value = AppWUC.APP_LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_LOGIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = getSessionUser(json, session, request);
		if (sessionUser != null) {
			json.set(2, "2-16"); // 您已登录另一账号，请先退出
			return json.toJson();
		}
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		String username = HttpUtil.getStringParameterTrim(request, "username"); // 用户名是不允许有空格的
		String password = request.getParameter("password"); // MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)
		Moment thisTime = new Moment();
		String userAgent = request.getHeader("User-Agent");
		userAgent = HttpUtil.escapeInput(userAgent);

		boolean isMobile = MobileChecker.checkIsMobile(userAgent);
		if (!isMobile) {
			json.set(2, "2-9");
			return json.toJson();
		}

		//先增加ip黑名单验证，这是后台加的，属于永久冻结
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}

		// 验证登录参数是否合法
		if(!uValidate.testAppLoginParams(json, username, password)) return json.toJson();

		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}

		User uEntity = uService.getByUsername(username);
		if(uEntity == null) {
			json.set(2, "2-1074"); // 用户名或密码错误
			return json.toJson();
		}

		if(uEntity.getAStatus() != 0) {
			json.set(2, "2-1"); // 您的账号已被系统冻结，请联系客服处理!
			session.removeAttribute(WSC.LOGIN_VALIDATE_CODE);
			return json.toJson();
		}

		// 验证登录密码
		if(!PasswordUtil.validatePassword(uEntity.getPassword(), disposableToken, password)) {
			json.set(2, "2-1074"); // 用户名或密码错误
			return json.toJson();
		}

		// 登录成功
		String loginLine = RequestUtils.getReferer(request);
		uLoginLogService.add(uEntity.getId(), ip, userAgent, thisTime.toSimpleTime(),loginLine);
		setSessionUser(session, uEntity);
		UserBaseVO uBaseVO = new UserBaseVO(uEntity);
		json.data("uBean", uBaseVO);
		json.set(0, "0-1");

		return json.toJson();
	}

	@RequestMapping(value = AppWUC.APP_LOTTERY, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_LOTTERY(HttpSession session,HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		Map<String, Object> data = new HashMap<>();

		int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
		Lottery lottery = dataFactory.getLottery(lotteryId);
		if (lottery != null) {
			UserCodeVO UserData = new UserCodeVO(uEntity);
			SysLotteryConfigVO Config = new SysLotteryConfigVO(dataFactory);
			Map<String, LotteryPlayRulesGroupVO> groups = dataFactory.listLotteryPlayRulesGroupVOs(lottery.getId());
			Map<String, LotteryPlayRulesVO> rules = dataFactory.listLotteryPlayRulesVOs(lottery.getId());

			// 福彩3D默认降点40
			if(lottery.getType() == 4) {
				final int lotteryDownCode = 40; // 默认降点40
				UserData.setCode(UserData.getCode() - lotteryDownCode);
			}

			data.put("Lottery", lottery == null ? null : new LotteryVO(lottery));
			data.put("UserData", UserData);
			data.put("Config", Config);
			data.put("PlayRulesGroup", JSONObject.fromObject(groups));
			data.put("PlayRules", JSONObject.fromObject(rules));
		}
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = AppWUC.APP_CHECK_LOGIN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_CHECK_LOGIN(HttpSession session,HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);

		Map<String, Object> data = new HashMap<>();
		data.put("isLogin", sessionUser == null ? false : true);

		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = AppWUC.APP_LOGOUT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_LOGOUT(HttpSession session, HttpServletRequest request) {
		super.logOut(session, request);

		WebJSON json = new WebJSON(dataFactory);

		Map<String, Object> data = new HashMap<>();
		data.put("success", true);

		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	/**
	 * 查询支付 方式列表
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = AppWUC.APP_LIST_PAYMENT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_LIST_PAYMENT(HttpSession session, HttpServletRequest request) {
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

		boolean hasWithdrawPwd = StringUtil.isNotNull(uEntity.getWithdrawPassword()) ? true : false;
		if (hasWithdrawPwd) {
//			List<PaymentChannelVO> channelVOs = paymentChannelService.getAvailableByUserId(sessionUser);

			List<PaymentChannelVO> thridList = paymentChannelService.getAvailableByUserId(sessionUser);

			List<PaymentChannelVO> mobileList = new ArrayList<>();
			Iterator<PaymentChannelVO> iterator = thridList.iterator();
			while (iterator.hasNext()) {
				PaymentChannelVO next = iterator.next();

				if (CollectionUtils.isNotEmpty(next.getBanklist())) {
					Iterator<PaymentChannelBank> iterator1 = next.getBanklist().iterator();
					while(iterator1.hasNext()) {
						PaymentChannelBank next1 = iterator1.next();
						// 删除工商银行，手机不支持
						if (next1.getBankId() == 1) {
							iterator1.remove();
							break;
						}
					}
				}

				mobileList.add(next);
			}

			json.data("channels", mobileList);
		}
		json.data("hasWithdrawPwd", hasWithdrawPwd);

		json.set(0, "0-1");
		return json.toJson();
	}

	/**
	 * 最近所有彩种开奖
	 */
	@RequestMapping(value = AppWUC.APP_RECENT_OPEN_CODE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> APP_RECENT_OPEN_CODE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		List<Lottery> lotteries = dataFactory.listLotteryByStatus(Global.LOTTERY_STATUS_OPEN);

		Map<Integer, LotteryOpenCodeVO> codeMap = new TreeMap<>();
		for (Lottery lottery : lotteries) {
			List<LotteryOpenCodeVO> openCodes = dataFactory.listLotteryOpenCode(lottery.getId(), 1, sessionUser.getId());

			if (CollectionUtils.isNotEmpty(openCodes)) {
				LotteryOpenCodeVO openCode = openCodes.get(0);
				codeMap.put(lottery.getSort(), openCode);
			}
		}

		json.data("data", codeMap.values());
		json.set(0, "0-1");
		return json.toJson();
	}
}