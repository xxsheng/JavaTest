package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.domains.jobs.AdminUserCriticalLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDividendVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserDividendController extends AbstractActionController {
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserService uService;

	@Autowired
	private UserDividendService userDividendService;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private LotteryDataFactory dataFactory;

	@Autowired
	private AdminUserLogJob adminUserLogJob;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	
	@Autowired
	private AdminUserCriticalLogJob adminUserCriticalLogJob;
    //
	// @Autowired
	// private DividendJob dividendJob;

	// @RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_MANUAL, method = { RequestMethod.POST, RequestMethod.GET })
	// @ResponseBody
	// public void LOTTERY_USER_DIVIDEND_MANUAL(HttpSession session, HttpServletRequest request,
	// 											 HttpServletResponse response) {
	// 	WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
	// 	AdminUser uEntity = super.getCurrUser(session, request, response);
	// 	if (uEntity != null) {
	// 		dividendJob.schedule();
	// 		json.set(0, "0-5");
	// 	} else {
	// 		json.set(2, "2-6");
	// 	}
	// 	HttpUtil.write(response, json.toString(), HttpUtil.json);
	// }

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_LIST(HttpSession session, HttpServletRequest request,
											   HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_LIST;
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
				Integer fixed = HttpUtil.getIntParameter(request, "fixed");
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

					PageList pList = userDividendService.search(userIds, sTime, eTime, minScale, maxScale,
							minValidUser, maxValidUser, status, fixed, start, limit);
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

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_DEL(HttpSession session, HttpServletRequest request,
											  HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_DEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");

				User uBean = userDao.getByUsername(username);
				if (uBean == null) {
					json.set(2, "2-32");
				}
				else {
					if (userDividendService.checkCanDel(json, uBean)) {
						boolean result = userDividendService.deleteByTeam(username);
						if(result) {
							json.set(0, "0-5");
						} else {
							json.set(1, "1-5");
						}
					}
				}

			}
			else {
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


	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_EDIT_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_EDIT_GET(HttpSession session, HttpServletRequest request,
												   HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_EDIT_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");

				UserDividend dividend = userDividendService.getById(id);
				if (dividend == null) {
					json.set(1, "1-7");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				User user = userDao.getById(dividend.getUserId());
				if (user == null) {
					json.set(1, "2-32");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				if (!userDividendService.checkCanEdit(json, user)) {
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				UserDividend upDividend = userDividendService.getByUserId(user.getUpid());
				if (!uCodePointUtil.isLevel2Proxy(user) && upDividend == null) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;

				}

				double[] minMaxScale = userDividendService.getMinMaxScale(user);
				double minScale = minMaxScale[0];
				double maxScale = minMaxScale[1];
				
				double[] minMaxSales = userDividendService.getMinMaxSales(user);
				double minSales = minMaxSales[0];
				double maxSales = minMaxSales[1];
				
				double[] minMaxLoss = userDividendService.getMinMaxLoss(user);
				double minLoss = minMaxLoss[0];
				double maxLoss = minMaxLoss[1];
				
				int[] minMaxUser = userDividendService.getMinMaxUser(user);
				int minUser = minMaxUser[0];
				int maxUser = minMaxUser[1];
				
				List<String> userLevels = uService.getUserLevels(user);
				Map<String, Object> data = new HashMap<>();
				data.put("bean", dividend == null ? null : new UserDividendVO(dividend, dataFactory));
				data.put("upBean", upDividend == null ? null : new UserDividendVO(upDividend, dataFactory));
				data.put("minScale", minScale);
				data.put("maxScale", maxScale);
				data.put("minSales", minSales);
				data.put("maxSales", maxSales);
				data.put("minLoss", minLoss);
				data.put("maxLoss", maxLoss);
				data.put("minUser", minUser);
				data.put("maxUser", maxUser);
				data.put("scaleLevel", dividend.getScaleLevel());
				data.put("lossLevel", dividend.getLossLevel());
				data.put("salesLevel", dividend.getSalesLevel());
				data.put("userLevel", dividend.getUserLevel());
				data.put("minValidUser", dataFactory.getDividendConfig().getMinValidUserl());
				data.put("maxSignLevel", dataFactory.getDividendConfig().getMaxSignLevel());
				data.put("userLevels", userLevels);
				json.accumulate("data", data);
				json.set(0, "0-3");
			}
			else {
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

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_EDIT(HttpSession session, HttpServletRequest request,
											   HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_EDIT;
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
				String[] salesLevels =salesLevel.split(","); 
				String userLevel = HttpUtil.getStringParameterTrim(request, "userLevel");
				String[] userLevels = userLevel.split(",");
//				int minValidUser = HttpUtil.getIntParameter(request, "minValidUser");

				double[] scaleCfg = dataFactory.getDividendConfig().getLevelsScale();
				double[] salesCfg = dataFactory.getDividendConfig().getLevelsSales();
				double[] lossCfg = dataFactory.getDividendConfig().getLevelsLoss();
				int[] userCfg = {dataFactory.getDividendConfig().getMinValidUserl(), 1000};
				
				if ((Double.valueOf(scaleLevels[0]) < scaleCfg[0] || Double.valueOf(scaleLevels[scaleLevels.length - 1]) > scaleCfg[1])
						|| (Double.valueOf(salesLevels[0]) < salesCfg[0] || Double.valueOf(salesLevels[salesLevels.length - 1]) > salesCfg[1])
						|| (Double.valueOf(lossLevels[0]) < lossCfg[0] || Double.valueOf(lossLevels[lossLevels.length - 1]) > lossCfg[1])
						|| (Integer.valueOf(userLevels[0]) < userCfg[0] || Integer.valueOf(userLevels[userLevels.length - 1]) > userCfg[1])) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				
//				if (minValidUser < dataFactory.getDividendConfig().getMinValidUserl() || minValidUser > 1000) {
//					json.set(1, "1-8");
//					HttpUtil.write(response, json.toString(), HttpUtil.json);
//					return;
//				}

				UserDividend dividend = userDividendService.getById(id);
				if (dividend == null) {
					json.set(1, "1-7");
				}
				else {
					boolean result = userDividendService.update(json, id, scaleLevel, lossLevel, salesLevel, Integer.valueOf(userLevels[0]), userLevel);
					if(result) {
						adminUserLogJob.logEditDividend(uEntity, request, dividend, scaleLevel, lossLevel, salesLevel, userLevel);
						json.set(0, "0-5");
					}
				}
			}
			else {
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


	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_ADD_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_ADD_GET(HttpSession session, HttpServletRequest request,
												  HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_ADD_GET;
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

				if (!userDividendService.checkCanEdit(json, user)) {
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				UserDividend dividend = userDividendService.getByUserId(user.getId());
				if (dividend != null) {
					json.set(2, "2-3010");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				UserDividend upDividend = userDividendService.getByUserId(user.getUpid());
				if (!uCodePointUtil.isLevel2Proxy(user) && upDividend == null) {
					json.set(2, "2-3011");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				double[] minMaxScale = userDividendService.getMinMaxScale(user);
				double minScale = minMaxScale[0];
				double maxScale = minMaxScale[1];
				
				double[] minMaxSales = userDividendService.getMinMaxSales(user);
				double minSales = minMaxSales[0];
				double maxSales = minMaxSales[1];
				
				double[] minMaxLoss = userDividendService.getMinMaxLoss(user);
				double minLoss = minMaxLoss[0];
				double maxLoss = minMaxLoss[1];
				
				int[] minMaxUser = userDividendService.getMinMaxUser(user);
				int minUser = minMaxUser[0];
				int maxUser = minMaxUser[1];
				
				List<String> userLevels = uService.getUserLevels(user);
				Map<String, Object> data = new HashMap<>();
				data.put("upBean", upDividend == null ? null : new UserDividendVO(upDividend, dataFactory));
				data.put("minScale", minScale);
				data.put("maxScale", maxScale);
				data.put("minSales", minSales);
				data.put("maxSales", maxSales);
				data.put("minLoss", minLoss);
				data.put("maxLoss", maxLoss);
				data.put("minUser", minUser);
				data.put("maxUser", maxUser);
				data.put("minValidUser", dataFactory.getDividendConfig().getMinValidUserl());
				data.put("maxSignLevel", dataFactory.getDividendConfig().getMaxSignLevel());
				data.put("userLevels", userLevels);
				json.accumulate("data", data);
				json.set(0, "0-3");
			}
			else {
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

	@RequestMapping(value = WUC.LOTTERY_USER_DIVIDEND_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DIVIDEND_ADD(HttpSession session, HttpServletRequest request,
											  HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DIVIDEND_ADD;
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

				User user = uService.getByUsername(username);
				if (user == null) {
					json.set(2, "2-32");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				
				String[] scaleLevels = scaleLevel.split(",");
				String[] lossLevels = lossLevel.split(",");
				String[] salesLevels =salesLevel.split(","); 
				String[] userLevels = userLevel.split(",");

				double[] scaleCfg = dataFactory.getDividendConfig().getLevelsScale();
				double[] salesCfg = dataFactory.getDividendConfig().getLevelsSales();
				double[] lossCfg = dataFactory.getDividendConfig().getLevelsLoss();
				int[] userCfg = {dataFactory.getDividendConfig().getMinValidUserl(), 1000};
				
				if ((Double.valueOf(scaleLevels[0]) < scaleCfg[0] || Double.valueOf(scaleLevels[scaleLevels.length - 1]) > scaleCfg[1])
						|| (Double.valueOf(salesLevels[0]) < salesCfg[0] || Double.valueOf(salesLevels[salesLevels.length - 1]) > salesCfg[1])
						|| (Double.valueOf(lossLevels[0]) < lossCfg[0] || Double.valueOf(lossLevels[lossLevels.length - 1]) > lossCfg[1])
						|| (Integer.valueOf(userLevels[0]) < userCfg[0] || Integer.valueOf(userLevels[userLevels.length - 1]) > userCfg[1])) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
//				if (minValidUser < dataFactory.getDividendConfig().getMinValidUserl() || minValidUser > 1000) {
//					json.set(1, "1-8");
//					HttpUtil.write(response, json.toString(), HttpUtil.json);
//					return;
//				}
				if (status != Global.DIVIDEND_VALID && status != Global.DIVIDEND_REQUESTED) {
					json.set(1, "1-8");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				boolean result = userDividendService.add(json, username, scaleLevel, lossLevel, salesLevel, Integer.valueOf(userLevels[0]), status, userLevel);
				if(result) {
					adminUserLogJob.logAddDividend(uEntity, request, username, scaleLevel, lossLevel, salesLevel, userLevel, status);
//					 adminUserCriticalLogJob.logEditDailySettleScale(uEntity, request, dailySettle, dailySettle.getScale(), scale, actionKey);
					adminUserCriticalLogJob.logDelDividend(uEntity, request, username, actionKey);
					json.set(0, "0-5");
				}
			}
			else {
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