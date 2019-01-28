package lottery.web.content;

import javautils.array.ArrayUtils;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.read.*;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserMainReportVO;
import lottery.domains.content.vo.bill.UserSelfReportVO;
import lottery.domains.content.vo.user.UserBaseVO;
import lottery.domains.content.vo.user.UserLotteryReportSelfStatisticsVO;
import lottery.domains.content.vo.user.UserVO;
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
import java.util.*;

@Controller
public class UserReportController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserMainReportReadService uMainReportReadService;

	@Autowired
	private UserLotteryReportReadService uLotteryReportReadService;

	@Autowired
	private UserGameReportReadService uGameReportReadService;

	@Autowired
	private UserReadService uReadService;

	@Autowired
	private UserBetsReadService uBetsReadService;

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

	@RequestMapping(value = WUC.USER_MAIN_REPORT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_MAIN_REPORT(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");
		Integer start = HttpUtil.getIntParameter(request, "start");
		Integer limit = HttpUtil.getIntParameter(request, "limit");

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			List<UserMainReportVO> list = uMainReportReadService.report(user, sTime, eTime);
			int totalCount = list.size() - 1;
			if (start != null && limit != null) {
				List<UserMainReportVO> subList = new ArrayList<>();
				subList.add(list.get(0)); // 总计

				if (start < list.size() - 1) {
					for (int i = start+1; i < list.size(); i++) {
						if (subList.size()-1 < limit) {
							subList.add(list.get(i));
						}
					}
				}

				list = subList;
			}

			json.data("data", list);
			json.data("totalCount", totalCount);
		}
		json.set(0, "0-1");
		return json.toJson();


		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
        //
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
        //
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// String sTime = request.getParameter("sTime");
		// String eTime = request.getParameter("eTime");
		// Integer start = HttpUtil.getIntParameter(request, "start");
		// Integer limit = HttpUtil.getIntParameter(request, "limit");
        //
		// User queryUser = uEntity;
        //
		// if(StringUtil.isNotNull(username)) {
		// 	User targetUser = uReadService.getByUsernameFromRead(username);
		// 	if(uProxyValidate.isLowerUser(uEntity, targetUser) || uProxyValidate.isRelated(uEntity, targetUser)) {
		// 		queryUser = targetUser;
		// 	}
		// }
        //
		// List<String> userLevels = getUserLevels(uEntity, queryUser);
        //
		// List<UserMainReportVO> list = uMainReportReadService.report(queryUser, sTime, eTime);
		// int totalCount = list.size() - 1;
		// if (start != null && limit != null) {
		// 	List<UserMainReportVO> subList = new ArrayList<>();
		// 	subList.add(list.get(0)); // 总计
        //
		// 	if (start < list.size() - 1) {
		// 		for (int i = start+1; i < list.size(); i++) {
		// 			if (subList.size()-1 < limit) {
		// 				subList.add(list.get(i));
		// 			}
		// 		}
		// 	}
        //
		// 	list = subList;
		// }
		// json.data("userLevels", userLevels);
		// json.data("data", list);
		// json.data("totalCount", totalCount);
		// json.set(0, "0-1");
		// return json.toJson();
	}

	@RequestMapping(value = WUC.USER_LOTTERY_REPORT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LOTTERY_REPORT(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");
		Integer start = HttpUtil.getIntParameter(request, "start");
		Integer limit = HttpUtil.getIntParameter(request, "limit");

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			List<UserLotteryReportVO> list = uLotteryReportReadService.reportByUser(user, sTime, eTime);
			int totalCount = list.size() - 1;
			if (start != null && limit != null) {
				List<UserLotteryReportVO> subList = new ArrayList<>();
				subList.add(list.get(0)); // 总计

				if (start < list.size() - 1) {
					for (int i = start+1; i < list.size(); i++) {
						if (subList.size()-1 < limit) {
							subList.add(list.get(i));
						}
					}
				}

				list = subList;
			}

			json.data("data", list);
			json.data("totalCount", totalCount);
		}

		json.data("userLevels", userLevels);
		json.set(0, "0-1");
		return json.toJson();
		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
        //
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
        //
		// User queryUser = uEntity;
        //
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// String sTime = request.getParameter("sTime");
		// String eTime = request.getParameter("eTime");
		// Integer start = HttpUtil.getIntParameter(request, "start");
		// Integer limit = HttpUtil.getIntParameter(request, "limit");
        //
		// if(StringUtil.isNotNull(username)) {
		// 	User targetUser = uReadService.getByUsernameFromRead(username);
		// 	if(uProxyValidate.isLowerUser(sessionUser, targetUser) || uProxyValidate.isRelated(uEntity, targetUser)) {
		// 		queryUser = targetUser;
		// 	}
		// }
        //
		// List<String> userLevels = getUserLevels(uEntity, queryUser);
        //
		// List<UserLotteryReportVO> list = uLotteryReportReadService.reportByUser(queryUser, sTime, eTime);
		// int totalCount = list.size() - 1;
		// if (start != null && limit != null) {
		// 	List<UserLotteryReportVO> subList = new ArrayList<>();
		// 	subList.add(list.get(0)); // 总计
        //
		// 	if (start < list.size() - 1) {
		// 		for (int i = start+1; i < list.size(); i++) {
		// 			if (subList.size()-1 < limit) {
		// 				subList.add(list.get(i));
		// 			}
		// 		}
		// 	}
        //
		// 	list = subList;
		// }
		// json.data("userLevels", userLevels);
		// json.data("data", list);
		// json.data("totalCount", totalCount);
		// json.set(0, "0-1");
		// return json.toJson();
	}

	@RequestMapping(value = WUC.USER_SELF_REPORT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_SELF_REPORT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String sTime = HttpUtil.getStringParameterTrim(request, "sTime");
		String eTime = HttpUtil.getStringParameterTrim(request, "eTime");

		UserLotteryReportVO uLotteryReportVO = uLotteryReportReadService.getSelfReport(sessionUser.getId(), sTime, eTime);
		UserMainReportVO userMainReportVO = uMainReportReadService.getSelfReport(sessionUser.getId(), sTime, eTime);

		UserSelfReportVO userSelfReportVO = new UserSelfReportVO(uLotteryReportVO, userMainReportVO);

		json.data("data", userSelfReportVO);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_TODAY_REPORT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_TODAY_REPORT(HttpSession session, HttpServletRequest request) {
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

		String sTime = DateUtil.getToday();
		String eTime = DateUtil.getTomorrow();

		// 返回彩票余额、未结算金额、今日投注

		// 报表
		UserLotteryReportVO uLotteryReportVO = uLotteryReportReadService.getSelfReport(sessionUser.getId(), sTime, eTime);

		// 未结算金额
		double unSettleMoney = uBetsReadService.getUnSettleMoney(sessionUser.getId(), sTime, eTime);

		// 个人信息
		UserBaseVO userBaseVO = new UserBaseVO(uEntity);

		Map<String, Object> data = new HashMap<>();
		data.put("lotteryReport", uLotteryReportVO);
		data.put("unSettleMoney", unSettleMoney);
		data.put("user", userBaseVO);

		json.data("data", data);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_LOTTERY_REPORT_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_LOTTERY_REPORT_LIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		String sTime = new Moment().subtract(4, "days").toSimpleDate();
		String eTime = new Moment().add(1, "days").toSimpleDate();

		// 获取彩票报表
		List<UserLotteryReport> lotteryReports = uLotteryReportReadService.getSelfReportByTime(sessionUser.getId(), sTime, eTime);
		// 获取主账户报表
		List<UserMainReport> mainReports = uMainReportReadService.getSelfReportByTime(sessionUser.getId(), sTime, eTime);

		// 汇总
		List<UserLotteryReportSelfStatisticsVO> list = new ArrayList<>();
		for (UserLotteryReport lotteryReport : lotteryReports) {
			for (UserMainReport mainReport : mainReports) {
				if (lotteryReport.getTime().equals(mainReport.getTime())) {
					list.add(new UserLotteryReportSelfStatisticsVO(lotteryReport, mainReport));
					break;
				}
			}
		}

		json.data("data", list);
		json.set(0, "0-1");
		return json.toJson();
	}

	@RequestMapping(value = WUC.USER_GAME_REPORT, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> USER_GAME_REPORT(HttpSession session, HttpServletRequest request) {
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

		String username = HttpUtil.getStringParameterTrim(request, "username");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");
		Integer start = HttpUtil.getIntParameter(request, "start");
		Integer limit = HttpUtil.getIntParameter(request, "limit");

		User user; // 目标用户
		if (StringUtils.isNotEmpty(username) && !StringUtils.equals(username, uEntity.getUsername())) {
			user = uReadService.getByUsernameFromRead(username); // 查询指定用户
			if (uProxyValidate.isLowerUser(uEntity, user) || uProxyValidate.isRelated(uEntity, user)) {
				// 合法用户
			}
			else {
				user = null;
			}
		}
		else {
			user = uEntity; // 查询自己
		}

		// 层级用户
		List<String> userLevels = getUserLevels(uEntity, user);
		json.data("userLevels", userLevels);

		if (user == null) { // 目标用户为空
			json.data("data", new ArrayList<>());
			json.data("totalCount", 0);
		}
		else {
			List<UserGameReportVO> list = uGameReportReadService.reportByUser(user, sTime, eTime);
			int totalCount = list.size() - 1;
			if (start != null && limit != null) {
				List<UserGameReportVO> subList = new ArrayList<>();
				subList.add(list.get(0)); // 总计

				if (start < list.size() - 1) {
					for (int i = start+1; i < list.size(); i++) {
						if (subList.size()-1 < limit) {
							subList.add(list.get(i));
						}
					}
				}

				list = subList;
			}

			json.data("data", list);
			json.data("totalCount", totalCount);
		}

		json.set(0, "0-1");
		return json.toJson();
		// WebJSON json = new WebJSON(dataFactory);
		// SessionUser sessionUser = super.getSessionUser(json, session, request);
		// if (sessionUser == null) {
		// 	return json.toJson();
		// }
        //
		// User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
		// if (uEntity == null) {
		// 	super.logOut(session, request);
		// 	json.set(2, "2-10"); // 您的账户信息发生变更，请重新登录
		// 	return json.toJson();
		// }
        //
		// User queryUser = uEntity;
        //
		// String username = HttpUtil.getStringParameterTrim(request, "username");
		// String sTime = request.getParameter("sTime");
		// String eTime = request.getParameter("eTime");
		// Integer start = HttpUtil.getIntParameter(request, "start");
		// Integer limit = HttpUtil.getIntParameter(request, "limit");
        //
		// if(StringUtil.isNotNull(username)) {
		// 	User targetUser = uReadService.getByUsernameFromRead(username);
		// 	if(uProxyValidate.isLowerUser(sessionUser, targetUser) || uProxyValidate.isRelated(uEntity, targetUser)) {
		// 		queryUser = targetUser;
		// 	}
		// }
        //
		// List<String> userLevels = getUserLevels(uEntity, queryUser);
        //
		// List<UserGameReportVO> list = uGameReportReadService.reportByUser(queryUser, sTime, eTime);
		// int totalCount = list.size() - 1;
		// if (start != null && limit != null) {
		// 	List<UserGameReportVO> subList = new ArrayList<>();
		// 	subList.add(list.get(0)); // 总计
        //
		// 	if (start < list.size() - 1) {
		// 		for (int i = start+1; i < list.size(); i++) {
		// 			if (subList.size()-1 < limit) {
		// 				subList.add(list.get(i));
		// 			}
		// 		}
		// 	}
        //
		// 	list = subList;
		// }
		// json.data("userLevels", userLevels);
		// json.data("data", list);
		// json.data("totalCount", totalCount);
		// json.set(0, "0-1");
		// return json.toJson();
	}

	private List<String> getUserLevels(User currentUser, User searchUser) {
		List<String> userLevels = new LinkedList<>();
		userLevels.add(currentUser.getUsername()); // 第一个是自己

		if (searchUser == null || currentUser.getUsername().equalsIgnoreCase(searchUser.getUsername())) {
			return userLevels;
		}

		if (StringUtils.isEmpty(searchUser.getUpids())) {
			return userLevels;
		}

		String upids = null;
		// 直属下级
		if (uProxyValidate.isLowerUser(currentUser, searchUser)) {
			upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), currentUser.getId(), true);
		}
		// 关联下级
		else if (uProxyValidate.isRelatedLowers(currentUser, searchUser)) {
			int relatedUpid = 0;

			// 找出是通过哪个会员进行关联的
			int[] relatedLowerIds = ArrayUtils.transGetIds(currentUser.getRelatedLowers());
			int[] upidArr = ArrayUtils.transGetIds(searchUser.getUpids());
			for (int upid : upidArr) {
				for (int relatedLowerId : relatedLowerIds) {
					if (upid == relatedLowerId) {
						relatedUpid = relatedLowerId;
						UserVO lowerUser = dataFactory.getUser(relatedLowerId);
						if (lowerUser != null) {
							userLevels.add(lowerUser.getUsername());
						}
						break;
					}
				}
			}

			if (relatedUpid != 0) {
				upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), relatedUpid, true);
			}
		}
		// 关联会员
		else if (uProxyValidate.isRelatedUsers(currentUser, searchUser)) {
			int relatedUpid = 0;

			// 找出是通过哪个会员进行关联的
			int[] relatedUserIds = ArrayUtils.transGetIds(currentUser.getRelatedUsers());
			int[] upidArr = ArrayUtils.transGetIds(searchUser.getUpids());
			for (int upid : upidArr) {
				for (int relatedUserId : relatedUserIds) {
					if (upid == relatedUserId) {
						relatedUpid = relatedUserId;
						UserVO lowerUser = dataFactory.getUser(relatedUserId);
						if (lowerUser != null) {
							userLevels.add(lowerUser.getUsername());
						}
						break;
					}
				}
			}

			if (relatedUpid != 0) {
				upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), relatedUpid, true);
			}
		}

		if (StringUtils.isNotEmpty(upids)) {
			int[] upidArr = ArrayUtils.transGetIds(upids);

			for (int i = upidArr.length - 1; i >= 0; i--) {
				UserVO lowerUser = dataFactory.getUser(upidArr[i]);
				if (lowerUser != null) {
					userLevels.add(lowerUser.getUsername());
				}
			}
		}

		userLevels.add(searchUser.getUsername());
		return userLevels;

		// List<String> userLevels = new LinkedList<>();
		// userLevels.add(currentUser.getUsername()); // 第一个是自己
		//
		// if (searchUser != null && !currentUser.getUsername().equals(searchUser.getUsername())) {
		// 	String upids = ArrayUtils.deleteInsertIds(searchUser.getUpids(), currentUser.getId(), true);
		// 	if (StringUtils.isNotEmpty(upids)) {
		// 		int[] upidArr = ArrayUtils.transGetIds(upids);
		//
		// 		for (int i = upidArr.length - 1; i >= 0; i--) {
		// 			UserVO lowerUser = dataFactory.getUser(upidArr[i]);
		// 			if (lowerUser != null) {
		// 				userLevels.add(lowerUser.getUsername());
		// 			}
		// 		}
		// 	}
		//
		// 	userLevels.add(searchUser.getUsername());
		// }
		//
		// return userLevels;
	}
}