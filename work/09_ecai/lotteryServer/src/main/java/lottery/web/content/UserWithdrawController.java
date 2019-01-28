package lottery.web.content;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.*;
import lottery.domains.content.biz.read.UserWithdrawReadService;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.WithdrawConfig;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserDailySettleValidate;
import lottery.web.content.validate.UserDividendValidate;
import lottery.web.content.validate.UserValidate;
import lottery.web.content.validate.UserWithdrawValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserWithdrawController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserCardService uCardService;

	@Autowired
	private UserWithdrawService uWithdrawService;
	@Autowired
	private UserWithdrawReadService uWithdrawReadService;

	@Autowired
	private UserBankcardUnbindService UnbindService;

	@Autowired
	private UserActionLogService uActionLogService;

	@Autowired
	private UserService uService;

	/**
	 * Validate
	 */
	@Autowired
	private UserWithdrawValidate uWithdrawValidate;

	@Autowired
	private UserDividendValidate uDividendValidate;

	@Autowired
	private UserDailySettleValidate uDailySettleValidate;

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
	
	@RequestMapping(value = WUC.USER_WITHDRAWALS_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_WITHDRAWALS_LOAD(HttpSession session, HttpServletRequest request) {
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
		
		if (uEntity.getType() == Global.USER_TYPE_FICTITIOUS ) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if(uEntity.getNickname().equals("试玩用户")){
			json.set(0, "2-13");
			return json.toJson();
		}
		// 用户资金
		WithdrawConfig config = dataFactory.getWithdrawConfig();
		double minAmount = config.getMinAmount();
		double maxAmount = config.getMaxAmount();
		List<UserCardVO> cList = uCardService.listByUserId(uEntity.getId());
		double totalMoney = uEntity.getTotalMoney() > 0 ? uEntity.getTotalMoney() : 0;
		double lotteryMoney = uEntity.getLotteryMoney() > 0 ? uEntity.getLotteryMoney() : 0;
		boolean hasWithdrawPwd = StringUtil.isNotNull(uEntity.getWithdrawPassword()) ? true : false;
		Map<String, Object> data = new HashMap<>();
		data.put("minWithdrawals", minAmount); // 最小取款金额
		data.put("maxWithdrawals", maxAmount); // 最大取款金额
		data.put("availableMoney", totalMoney); // 可提款金额
		data.put("totalMoney", totalMoney); // 主账户余额
		data.put("lotteryMoney", lotteryMoney); // 彩票账户金额
		data.put("hasWithdrawPwd", hasWithdrawPwd); // 是否有取款密码
		data.put("cList", cList); // 用户绑定银行卡列表
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_WITHDRAWALS_APPLY, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_WITHDRAWALS_APPLY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		//先增加ip黑名单验证
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}
		boolean blackList = uValidate.testIpBlackList(ip);
		if(blackList){
			json.set(2, "2-1073", ip);
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		if (uEntity.getType() == Global.USER_TYPE_PLAYER &&uEntity.getNickname().equals("试玩用户")) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		
		if (uEntity.getType() == Global.USER_TYPE_FICTITIOUS ) {
			json.set(2, "2-13"); // 您的账号无法使用该功能！
			return json.toJson();
		}
		
		if (uEntity.getAllowWithdraw() != 1) {
			json.set(2, "2-1091"); // 您没有被允许提现,请联系客服！
			return json.toJson();
		}

		// 获取参数
		int cid = HttpUtil.getIntParameter(request, "cid");
		double amount = HttpUtil.getDoubleParameter(request, "amount");
		String withdrawPwd = request.getParameter("withdrawPwd"); // MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)

		// 验证取款服务时间
	if(!uWithdrawValidate.validateTime(json)) return json.toJson();

		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}

		// 验证资金密码
		if(!PasswordUtil.validatePassword(uEntity.getWithdrawPassword(), disposableToken, withdrawPwd)) {
			json.set(2, "2-1009");
			return json.toJson();
		}

		// 验证注册时间
		if(!uWithdrawValidate.validateRegisterTime(json, uEntity)) return json.toJson();

		// 验证账号锁定时间
		if(!uWithdrawValidate.validateLockTime(json, uEntity)) return json.toJson();

		// 验证是否有未发放的分红金额
		if (!uDividendValidate.validateUnIssue(json, uEntity.getId())) return json.toJson();

		// 验证是否有未发放的日结金额
		if (!uDailySettleValidate.validateUnIssue(json, uEntity.getId())) return json.toJson();

		UserCard uCard = uCardService.getById(cid, uEntity.getId());

		// 验证卡片状态
		if(!uWithdrawValidate.validateCard(json, uCard)) return json.toJson();

		// 验证用户及卡片黑名单
		if(!uWithdrawValidate.testBlackWhitelist(json, uEntity, uCard)) return json.toJson();

		// 验证解锁次数
		if(!uWithdrawValidate.validateCardUnBind(json, uCard)) return json.toJson();

		// 验证今日取款限额
		Moment thisTime = new Moment();
		int todayCount = uWithdrawService.getDateCashCount(uEntity.getId(), thisTime.toSimpleDate());
		double todayMoney = uWithdrawService.getDateCashMoney(uEntity.getId(), thisTime.toSimpleDate());
		if(!uWithdrawValidate.validateToCard(json, uEntity, amount, todayCount, todayMoney)) return json.toJson();

		WithdrawConfig config = dataFactory.getWithdrawConfig();
		int freeTimes = config.getFreeTimes();
		double fee = config.getFee();
		double maxFee = config.getMaxFee();
		// 计算手续费
		double recMoney = amount; // 到账金额
		double feeMoney = 0; // 手续费
		if(todayCount >= freeTimes) { // 如果免费次数用完了
			feeMoney = amount * fee; // 计算手续费
			if(feeMoney > maxFee) { // 最高手续费
				feeMoney = maxFee;
			}
			recMoney = amount - feeMoney; // 实际到账金额
		}
		boolean result = uWithdrawService.apply(uEntity.getId(), uCard, amount, recMoney, feeMoney);
		if(result) {
			uActionLogService.withdrawApply(uEntity.getId(), ip, uCard, amount, recMoney, feeMoney);
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_WITHDRAWALS_SEARCH, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_WITHDRAWALS_SEARCH(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String billno = request.getParameter("billno");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		Integer status = HttpUtil.getIntParameter(request, "status");
		PageList pList = uWithdrawReadService.search(sessionUser.getId(), billno, sTime, eTime, status, start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}
	
}