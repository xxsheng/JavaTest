package lottery.web.content;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserActionLogService;
import lottery.domains.content.biz.UserBankcardUnbindService;
import lottery.domains.content.biz.UserCardService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBankcardUnbindRecord;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.vo.user.UserBankcardUnbindVO;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserCardValidate;
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
public class UserCardController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserCardService uCardService;

	@Autowired
	private UserActionLogService uActionLogService;

	@Autowired
	private UserBankcardUnbindService UnbindService;

	@Autowired
	private UserService uService;

	/**
	 * Validate
	 */
	@Autowired
	private UserCardValidate uCardValidate;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	@RequestMapping(value = WUC.USER_CARD_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_CARD_LIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		List<UserCardVO> list = uCardService.listByUserId(sessionUser.getId());
		json.data("data", list);
		json.set(0, "0-1");
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_CARD_BIND_NEED, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_CARD_BIND_NEED(HttpSession session, HttpServletRequest request) {
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

		String bindCardName = uEntity.getWithdrawName();
		if(StringUtil.isNotNull(bindCardName)) {
			bindCardName = bindCardName.substring(0, 1) + "**";
		}
		List<PaymentBank> bankList = dataFactory.listPaymentBank();
		List<UserCardVO> cList = uCardService.listByUserId(uEntity.getId());
		Map<String, Object> data = new HashMap<>();
		data.put("bindCardName", bindCardName);
		data.put("bankList", bankList);
		data.put("count", cList.size());
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_CARD_BIND, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_CARD_BIND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		User uEntity = uService.getById(sessionUser.getId());
		if (uEntity == null) {
			super.logOut(session, request);
			json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
			return json.toJson();
		}

		Integer bankId = HttpUtil.getIntParameter(request, "bankId");
		String bankBranch = HttpUtil.getStringParameterTrim(request, "bankBranch");
		bankBranch = HttpUtil.escapeInput(bankBranch);
		String cardId = HttpUtil.getStringParameterTrim(request, "cardId");
		cardId = HttpUtil.escapeInput(cardId);
		String withdrawPwd = request.getParameter("withdrawPwd"); // MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token)
		List<UserCardVO> list = uCardService.listByUserId(uEntity.getId());

		String disposableToken = getDisposableToken(session, request); // 获取并删除一次性token
		if (StringUtils.isEmpty(disposableToken)) {
			json.set(2, "2-15"); // 您的请求已超时，请重新提交 一次性验证token没有了，说明session失效
			return json.toJson();
		}


		if(list.size() < 5) {
			String cardName = uEntity.getWithdrawName();
			if(uCardValidate.required(json, bankId, cardName, cardId)) {

				if(uCardValidate.checkCardId(cardId)) {
					if(PasswordUtil.validatePassword(uEntity.getWithdrawPassword(), disposableToken, withdrawPwd)) {
						UserCard userCard = uCardService.getByCardId(cardId);
						if(userCard == null || StringUtils.isEmpty(userCard.getCardId())) {
							// 验证黑白名单
							boolean test = uCardValidate.testBlackWhiteList(json, uEntity, bankId, cardName, cardId, ip);
							if (test == false) {
								return json.toJson();
							}

							boolean result = uCardService.add(uEntity.getId(), bankId.intValue(), bankBranch, cardName, cardId, 0);
							if(result) {
								uActionLogService.bindCard(uEntity.getId(), ip, bankId, bankBranch, cardName, cardId);
								json.set(0, "0-1");
							} else {
								json.set(1, "1-1");
							}
						} else {
							json.set(2, "2-1015");
						}
					} else {
						json.set(2, "2-1009");
					}
				} else {
					json.set(2, "2-1014");
				}
			}
		} else {
			json.set(2, "2-1046");
		}
		return json.toJson();
	}
	
	@RequestMapping(value = WUC.USER_CARD_SET_DEFAULT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_CARD_SET_DEFAULT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int id = HttpUtil.getIntParameter(request, "id");
		boolean result = uCardService.setDefault(id, sessionUser.getId());
		if(result) {
			json.set(0, "0-1");
		} else {
			json.set(1, "1-1");
		}
		return json.toJson();
	}
	
	
	@RequestMapping(value = WUC.USER_UNBIND_CARD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_UNBIND_CARD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		try{
			int id = HttpUtil.getIntParameter(request, "id");
			UserCard result = uCardService.getById(id, sessionUser.getId());
			if(result == null){
				json.set(1, "2-4023");
				return json.toJson();
			}
			int validatenum = 2;
			UserBankcardUnbindVO unbindVo = UnbindService.getUnbindInfoBycardId(result.getCardId());
			if(unbindVo != null && unbindVo.getUnbindNum() >= validatenum){
				json.set(1, "2-4024",validatenum);
				return json.toJson();
			}

			Moment thisTime = new Moment();
			String nowDateTime = thisTime.toSimpleTime();
			int unbindnum = 0;
			String cardNum = result.getCardId();
			if(unbindVo != null && unbindVo.getUnbindNum() == 1){
				boolean deleteresult = uCardService.delete(id, sessionUser.getId());
				if(deleteresult){
					unbindnum = 2;
					String userIds = "#"+unbindVo.getUserIds() +"#"+String.valueOf(sessionUser.getId())+"#";
					UnbindService.updateByParam(userIds,cardNum, unbindnum, nowDateTime);
					//记录操作日志
					uActionLogService.userUnbidCard(sessionUser.getId(), sessionUser.getUsername(),
							result.getCardId(), ip, nowDateTime, String.valueOf(unbindnum));
					json.set(0, "0-1");
					return json.toJson();
				}
			}

			if(unbindVo == null){
				boolean deleteresult = uCardService.delete(id, sessionUser.getId());
				if(deleteresult){
					unbindnum = 1;
					UserBankcardUnbindRecord entity =new UserBankcardUnbindRecord(
							"#"+sessionUser.getId()+"#", cardNum, unbindnum, nowDateTime);
					UnbindService.add(entity);
					//记录操作日志
					uActionLogService.userUnbidCard(sessionUser.getId(), sessionUser.getUsername(),
							result.getCardId(), ip, nowDateTime, String.valueOf(unbindnum));
					json.set(0, "0-1");
					return json.toJson();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		json.set(1, "1-1");
		return json.toJson();
	}
}