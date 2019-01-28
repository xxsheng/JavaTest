package lottery.web.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserCriticalLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDailySettleVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;

@Controller
public class UserDailySettleController extends AbstractActionController {
	@Autowired
	private UserDao userDao;

	@Autowired
	private UserDailySettleService settleService;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private AdminUserCriticalLogJob adminUserCriticalLogJob;

	@Autowired
	private LotteryDataFactory dataFactory;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private UserService uService;

	// @Autowired
	// private DailySettleJob dailySettleJob;
	//
	// @Autowired
	// private DividendJob dividendJob;

	// @RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_MANUAL, method = {
	// RequestMethod.POST, RequestMethod.GET })
	// @ResponseBody
	// public void LOTTERY_USER_DAILY_SETTLE_MANUAL(HttpSession session,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	// String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_MANUAL;
	// long t1 = System.currentTimeMillis();
	// WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
	// AdminUser uEntity = super.getCurrUser(session, request, response);
	// if (uEntity != null) {
	// String startDate = request.getParameter("startDate");
	// String endDate = request.getParameter("endDate");
	// dailySettleJob.settleUp(startDate, endDate);
	// json.set(0, "0-5");
	// } else {
	// json.set(2, "2-6");
	// }
	// long t2 = System.currentTimeMillis();
	// if (uEntity != null) {
	// adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
	// }
	// HttpUtil.write(response, json.toString(), HttpUtil.json);
	// }
	//
	// @RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_MANUAL, method = {
	// RequestMethod.POST, RequestMethod.GET })
	// @ResponseBody
	// public void LOTTERY_USER_DIVIDEND_MANUAL(HttpSession session,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	// String actionKey = WUC.LOTTERY_USER_DIVIDEND_MANUAL;
	// long t1 = System.currentTimeMillis();
	// WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
	// AdminUser uEntity = super.getCurrUser(session, request, response);
	// if (uEntity != null) {
	// String startDate = request.getParameter("startDate");
	// String endDate = request.getParameter("endDate");
	// dividendJob.settleUp(startDate, endDate);
	// json.set(0, "0-5");
	// } else {
	// json.set(2, "2-6");
	// }
	// long t2 = System.currentTimeMillis();
	// if (uEntity != null) {
	// adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
	// }
	// HttpUtil.write(response, json.toString(), HttpUtil.json);
	// }

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_LIST(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Double minScale = HttpUtil.getDoubleParameter(request, "minScale");
				Double maxScale = HttpUtil.getDoubleParameter(request, "maxScale");
				Integer minValidUser = HttpUtil.getIntParameter(request, "minValidUser");
				Integer maxValidUser = HttpUtil.getIntParameter(request, "maxValidUser");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				Integer status = HttpUtil.getIntParameter(request, "status");
				List<Integer> userIds = new ArrayList<>();
				boolean legalUser = true; // 输入的用户名是否存在
				if (StringUtils.isNotEmpty(username)) {
					User user = userDao.getByUsername(username);
					if (user == null) {
						legalUser = false;
					} else {
						userIds.add(user.getId());
						List<User> userDirectLowers = userDao.getUserDirectLower(user.getId());
						for (User userDirectLower : userDirectLowers) {
							userIds.add(userDirectLower.getId());
						}
					}
				}

				if (!legalUser) {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				} else {
					if (minScale != null) {
						minScale = MathUtil.divide(minScale, 100, 4);
					}
					if (maxScale != null) {
						maxScale = MathUtil.divide(maxScale, 100, 4);
					}

					PageList pList = settleService.search(userIds, sTime, eTime, minScale, maxScale, minValidUser,
							maxValidUser, status, start, limit);
					if (pList != null) {
						json.accumulate("totalCount", pList.getCount());
						json.accumulate("data", pList.getList());
					} else {
						json.accumulate("totalCount", 0);
						json.accumulate("data", "[]");
					}
				}

				json.set(0, "0-3");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_DEL(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_DEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username").trim();

				User uBean = userDao.getByUsername(username);
				if (uBean == null) {
					json.set(2, "2-32");
				} else {
					if (settleService.checkCanDel(json, uBean)) {
						boolean result = settleService.deleteByTeam(username);
						if (result) {
							adminUserCriticalLogJob.logDelDailySettle(uEntity, request, username, actionKey);
							json.set(0, "0-5");
						} else {
							json.set(1, "1-5");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_EDIT_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_EDIT_GET(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_EDIT_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");

				UserDailySettle dailySettle = settleService.getById(id);
				if (dailySettle == null) {
					json.set(1, "1-7");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				User user = userDao.getById(dailySettle.getUserId());
				if (user == null) {
					json.set(1, "2-32");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				if (!settleService.checkCanEdit(json, user)) {
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				UserDailySettle upDailySettle = settleService.getByUserId(user.getUpid());
				if (!uCodePointUtil.isLevel3Proxy(user) && upDailySettle == null) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				double[] minMaxScale = settleService.getMinMaxScale(user);
				double minScale = minMaxScale[0];
				double maxScale = minMaxScale[1];

				double[] minMaxSales = settleService.getMinMaxSales(user);
				double minSales = minMaxSales[0];
				double maxSales = minMaxSales[1];

				double[] minMaxLoss = settleService.getMinMaxLoss(user);
				double minLoss = minMaxLoss[0];
				double maxLoss = minMaxLoss[1];
				
				int[] minMaxUser = settleService.getMinMaxUsers(user);
				int minUser = minMaxUser[0];
				int maxUser = minMaxUser[1];

				List<String> userLevels = uService.getUserLevels(user);
				Map<String, Object> data = new HashMap<>();
				data.put("bean", dailySettle == null ? null : new UserDailySettleVO(dailySettle, dataFactory));
				data.put("upBean", upDailySettle == null ? null : new UserDailySettleVO(upDailySettle, dataFactory));
				data.put("minScale", minScale);
				data.put("maxScale", maxScale);
				data.put("minSales", minSales);
				data.put("maxSales", maxSales);
				data.put("minLoss", minLoss);
				data.put("maxLoss", maxLoss);
				data.put("minUser", minUser);
				data.put("maxUser", maxUser);
				data.put("scaleLevel", dailySettle.getScaleLevel());
				data.put("lossLevel", dailySettle.getLossLevel());
				data.put("salesLevel", dailySettle.getSalesLevel());
				data.put("userLevel", dailySettle.getUserLevel());
				data.put("minValidUser", dataFactory.getDailySettleConfig().getMinValidUserl());
				data.put("maxSignLevel", dataFactory.getDailySettleConfig().getMaxSignLevel());
				data.put("userLevels", userLevels);
				json.accumulate("data", data);
				json.set(0, "0-3");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_EDIT(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String scaleLevel = HttpUtil.getStringParameterTrim(request, "scaleLevel");
				String[] scaleLevels = scaleLevel.split(",");
				String lossLevel = HttpUtil.getStringParameterTrim(request, "lossLevel");
				String[] lossLevels = scaleLevel.split(",");
				String salesLevel = HttpUtil.getStringParameterTrim(request, "salesLevel");
				String[] salesLevels = salesLevel.split(",");
				String userLevel = HttpUtil.getStringParameterTrim(request, "userLevel");
				String[] userLevels = userLevel.split(",");
//				int minValidUser = HttpUtil.getIntParameter(request, "minValidUser");

				double[] scaleCfg = dataFactory.getDailySettleConfig().getLevelsScale();
				double[] salesCfg = dataFactory.getDailySettleConfig().getLevelsSales();
				double[] lossCfg = dataFactory.getDailySettleConfig().getLevelsLoss();
				int[] userCfg = {dataFactory.getDailySettleConfig().getMinValidUserl(), 1000};

				if ((Double.valueOf(scaleLevels[0]) < scaleCfg[0] || Double.valueOf(scaleLevels[scaleLevels.length - 1]) > scaleCfg[1])
						|| (Double.valueOf(salesLevels[0]) < salesCfg[0] || Double.valueOf(salesLevels[salesLevels.length - 1]) > salesCfg[1])
						|| (Double.valueOf(lossLevels[0]) < lossCfg[0] || Double.valueOf(lossLevels[lossLevels.length - 1]) > lossCfg[1])
						|| (Integer.valueOf(userLevels[0]) < userCfg[0] || Integer.valueOf(userLevels[userLevels.length - 1]) > userCfg[1])) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

//				if (minValidUser < dataFactory.getDailySettleConfig().getMinValidUserl() || minValidUser > 1000) {
//					json.set(1, "1-8");
//					HttpUtil.write(response, json.toString(), HttpUtil.json);
//					return;
//				}

				UserDailySettle dailySettle = settleService.getById(id);
				if (dailySettle == null) {
					json.set(1, "1-7");
				} else {
					User uBean = userDao.getById(dailySettle.getUserId());

					if (settleService.checkCanEdit(json, uBean)) {
						boolean result = settleService.update(json, id, scaleLevel, salesLevel, lossLevel,	Integer.valueOf(userLevels[0]), userLevel);
						if (result) {
							adminUserLogJob.logEditDailySettle(uEntity, request, dailySettle, scaleLevel, salesLevel,	lossLevel, userLevel);
							// adminUserCriticalLogJob.logEditDailySettleScale(uEntity, request, dailySettle, dailySettle.getScale(), scale, actionKey);
							json.set(0, "0-5");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_ADD_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_ADD_GET(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_ADD_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				if (StringUtils.isEmpty(username)) {
					json.set(0, "0-3");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				User user = uService.getByUsername(username);
				if (user == null) {
					json.set(2, "2-32");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				if (!settleService.checkCanEdit(json, user)) {
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				UserDailySettle dailySettle = settleService.getByUserId(user.getId());
				if (dailySettle != null) {
					json.set(2, "2-3007");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				UserDailySettle upDailySettle = settleService.getByUserId(user.getUpid());
				if (!uCodePointUtil.isLevel3Proxy(user) && (upDailySettle == null || upDailySettle.getStatus() != Global.DAILY_SETTLE_VALID)) {
					json.set(2, "2-3008");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				double[] minMaxScale = settleService.getMinMaxScale(user);
				double minScale = minMaxScale[0];
				double maxScale = minMaxScale[1];
				
				double[] minMaxSales = settleService.getMinMaxSales(user);
				double minSales = minMaxSales[0];
				double maxSales = minMaxSales[1];
				
				double[] minMaxLoss = settleService.getMinMaxLoss(user);
				double minLoss = minMaxLoss[0];
				double maxLoss = minMaxLoss[1];
				
				int[] minMaxUser = settleService.getMinMaxUsers(user);
				int minUser = minMaxUser[0];
				int maxUser = minMaxUser[1];
				
				List<String> userLevels = uService.getUserLevels(user);
				Map<String, Object> data = new HashMap<>();
				data.put("upBean", upDailySettle == null ? null : new UserDailySettleVO(upDailySettle, dataFactory));
				data.put("minScale", minScale);
				data.put("maxScale", maxScale);
				data.put("minSales", minSales);
				data.put("maxSales", maxSales);
				data.put("minLoss", minLoss);
				data.put("maxLoss", maxLoss);
				data.put("minUser", minUser);
				data.put("maxUser", maxUser);
				data.put("minValidUser", dataFactory.getDailySettleConfig().getMinValidUserl());
				data.put("maxSignLevel", dataFactory.getDailySettleConfig().getMaxSignLevel());
				data.put("userLevels", userLevels);
				json.accumulate("data", data);
				json.set(0, "0-3");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_ADD(HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String scaleLevel = HttpUtil.getStringParameterTrim(request, "scaleLevel");
				String lossLevel = HttpUtil.getStringParameterTrim(request, "lossLevel");
				String salesLevel = HttpUtil.getStringParameterTrim(request, "salesLevel");
				String userLevel = HttpUtil.getStringParameterTrim(request, "userLevel");
//				int minValidUser = HttpUtil.getIntParameter(request, "minValidUser");
				int status = HttpUtil.getIntParameter(request, "status");

				String[] scaleLevels = scaleLevel.split(",");
				String[] lossLevels = lossLevel.split(",");
				String[] salesLevels =salesLevel.split(","); 
				String[] userLevels = userLevel.split(",");

				double[] scaleCfg = dataFactory.getDailySettleConfig().getLevelsScale();
				double[] salesCfg = dataFactory.getDailySettleConfig().getLevelsSales();
				double[] lossCfg = dataFactory.getDailySettleConfig().getLevelsLoss();
				int[] userCfg = {dataFactory.getDailySettleConfig().getMinValidUserl(), 1000};
				
				User user = uService.getByUsername(username);
				if (user == null) {
					json.set(2, "2-32");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				
				if ((Double.valueOf(scaleLevels[0]) < scaleCfg[0] || Double.valueOf(scaleLevels[scaleLevels.length - 1]) > scaleCfg[1])
						|| (Double.valueOf(salesLevels[0]) < salesCfg[0] || Double.valueOf(salesLevels[salesLevels.length - 1]) > salesCfg[1])
						|| (Double.valueOf(lossLevels[0]) < lossCfg[0] || Double.valueOf(lossLevels[lossLevels.length - 1]) > lossCfg[1])
						|| (Integer.valueOf(userLevels[0]) < userCfg[0] || Integer.valueOf(userLevels[userLevels.length - 1]) > userCfg[1])) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
//				
//				if (minValidUser < dataFactory.getDailySettleConfig().getMinValidUserl() || minValidUser > 1000) {
//					json.set(1, "1-8");
//					HttpUtil.write(response, json.toString(), HttpUtil.json);
//					return;
//				}
				
				if (status != Global.DAILY_SETTLE_VALID && status != Global.DAILY_SETTLE_REQUESTED) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				
				boolean result = settleService.add(json, username, scaleLevel, salesLevel, lossLevel, Integer.valueOf(userLevels[0]), status, userLevel);
				if (result) {
					adminUserLogJob.logAddDailySettle(uEntity, request, username, scaleLevel, salesLevel, lossLevel, userLevel, status);
					// adminUserCriticalLogJob.logEditDailySettleScale(uEntity,
					// request, dailySettle, dailySettle.getScale(), scale,
					// actionKey);
					json.set(0, "0-5");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
}