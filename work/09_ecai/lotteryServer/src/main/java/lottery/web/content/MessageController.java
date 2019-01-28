package lottery.web.content;

import javautils.StringUtil;
import javautils.array.ArrayUtils;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserMessageService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.read.UserMessageReadService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.biz.read.UserSysMessageReadService;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserBaseVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserProxyValidate;
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
import java.util.Map;

@Controller
public class MessageController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserMessageService uMessageService;
	@Autowired
	private UserMessageReadService uMessageReadService;

	@Autowired
	private UserSysMessageService uSysMessageService;
	@Autowired
	private UserSysMessageReadService uSysMessageReadService;

	@Autowired
	private UserReadService uReadService;

	/**
	 * Validate
	 */
	@Autowired
	private UserProxyValidate uProxyValidate;

	/**
	 * Util
	 */

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	/**
	 * Global接口，用来返回用户未读信息，实时余额，请求不要太频繁
	 */
	@RequestMapping(value = WUC.GLOBAL_GET, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> GLOBAL_GET(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);

		// // 限制访问频率,10秒最多20次
		// boolean exceedTime = super.validateAccessTimeForAPI(session, request, WUC.GLOBAL_GET, 10, 20, -1);
		// if (exceedTime == true) {
		// 	json.set(2, "2-17"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }

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
		UserBaseVO uBean = new UserBaseVO(uEntity);
		int uMsgCount = uMessageReadService.getUnreadCount(uEntity.getId()); // 用户之间发的消息
		int uSysMsgCount = uSysMessageReadService.getUnreadCount(uEntity.getId()); // 系统消息
		Boolean isInitialPassword = PasswordUtil.generatePasswordByPlainString("a123456").equalsIgnoreCase(uEntity.getPassword());
		Map<String, Object> data = new HashMap<>();
		data.put("uBean", uBean);
		data.put("uMsgCount", uMsgCount);
		data.put("uSysMsgCount", uSysMsgCount);
		data.put("isInitialPassword", isInitialPassword);
		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_MESSAGE_SEND, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_MESSAGE_SEND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String target = request.getParameter("target");
		String toUsers = request.getParameter("toUsers");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");

		if (StringUtils.isEmpty(target) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(content)) {
			json.set(2, "2-12");
			return json.toJson();
		}

		// 主题最长120
		if (subject.length() > 120) {
			json.set(2, "2-1082", 120);
			return json.toJson();
		}
		// 内容最长512
		if (content.length() > 512) {
			json.set(2, "2-1083", 512);
			return json.toJson();
		}

		content = HttpUtil.escapeInput(content);
		subject = HttpUtil.escapeInput(subject);


		if("upper".equals(target)) {
			if(sessionUser.getUpid() != 0) {
				int type = Global.USER_MESSAGE_TYPE_USER;
				int fromUid = sessionUser.getId();
				int toUid = sessionUser.getUpid();
				boolean result = uMessageService.send(type, toUid, fromUid, subject, content);
				if(result) {
					json.set(0, "0-1");
				} else {
					json.set(1, "1-1");
				}
			} else {
				json.set(2, "2-1036");
			}
		}
		if("lower".equals(target)) {
			String[] toArrs = toUsers.split(",");

			// 一次最多发送100个下级
			if (toArrs != null && toArrs.length > 100) {
				json.set(2, "2-1084", 100);
				return json.toJson();
			}

			int succ = 0;
			for (String thisName : toArrs) {
				User thisUser = uReadService.getByUsernameFromRead(thisName);
				if(thisUser != null) {
					if(uProxyValidate.isDirectLower(sessionUser, thisUser)) {
						int type = Global.USER_MESSAGE_TYPE_USER;
						int fromUid = sessionUser.getId();
						int toUid = thisUser.getId();
						boolean result = uMessageService.send(type, toUid, fromUid, subject, content);
						if(result) succ++;
					}
				}
			}
			if(succ > 0) {
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		}
		if("system".equals(target)) {
			int type = Global.USER_MESSAGE_TYPE_SYSTEM;
			int fromUid = sessionUser.getId();
			int toUid = 0;
			boolean result = uMessageService.send(type, toUid, fromUid, subject, content);
			if(result) {
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_MESSAGE_INBOX, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_MESSAGE_INBOX(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		PageList pList = uMessageReadService.getInboxMessage(sessionUser.getId(), start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_MESSAGE_OUTBOX, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_MESSAGE_OUTBOX(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		PageList pList = uMessageReadService.getOutboxMessage(sessionUser.getId(), start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_MESSAGE_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_MESSAGE_DEL(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String type = request.getParameter("type");
		String ids = request.getParameter("ids");
		if(StringUtil.isNotNull(ids)) {
			int[] inIds = ArrayUtils.transGetIds(ids);
			if("inbox".equals(type)) {
				boolean result = uMessageService.updateInboxMessage(sessionUser.getId(), inIds, -1);
				if(result) {
					json.set(0, "0-1");
				} else {
					json.set(1, "1-1");
				}
			}
			if("outbox".equals(type)) {
				boolean result = uMessageService.updateOutboxMessage(sessionUser.getId(), inIds, -1);
				if(result) {
					json.set(0, "0-1");
				} else {
					json.set(1, "1-1");
				}
			}
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_MESSAGE_READ, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_MESSAGE_READ(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ids = request.getParameter("ids");
		if(StringUtil.isNotNull(ids)) {
			int[] inIds = ArrayUtils.transGetIds(ids);
			boolean result = uMessageService.updateInboxMessage(sessionUser.getId(), inIds, 1);
			if(result) {
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		}
		else {
			json.set(2, "2-6");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_SYS_MESSAGE, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_SYS_MESSAGE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		int start = HttpUtil.getIntParameter(request, "start");
		int limit = HttpUtil.getIntParameter(request, "limit");
		PageList pList = uSysMessageReadService.search(sessionUser.getId(), start, limit);
		json.data("totalCount", pList.getCount());
		json.data("data", pList.getList());
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_SYS_MESSAGE_READ, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_SYS_MESSAGE_READ(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ids = request.getParameter("ids");
		if(StringUtil.isNotNull(ids)) {
			int[] inIds = ArrayUtils.transGetIds(ids);
			boolean result = uSysMessageService.updateUnread(sessionUser.getId(), inIds);
			if(result) {
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		}
		else {
			json.set(2, "2-6");
		}
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_SYS_MESSAGE_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_SYS_MESSAGE_DEL(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String ids = request.getParameter("ids");
		if(StringUtil.isNotNull(ids)) {
			int[] inIds = ArrayUtils.transGetIds(ids);
			boolean result = uSysMessageService.deleteMsg(sessionUser.getId(), inIds);
			if(result) {
				json.set(0, "0-1");
			} else {
				json.set(1, "1-1");
			}
		}
		return json.toJson();
	}
}