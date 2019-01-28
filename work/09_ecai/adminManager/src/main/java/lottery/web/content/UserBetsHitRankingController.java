package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsHitRankingService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.entity.UserBetsHitRanking;
import lottery.domains.content.global.Global;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserBetsHitRankingController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private UserBetsHitRankingService uBetsHitRankingService;
	
	@Autowired
	private UserSysMessageService mUserSysMessageService;
	
	@RequestMapping(value = WUC.USER_BETS_HIT_RANKING_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_BETS_HIT_RANKING_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.USER_BETS_HIT_RANKING_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBetsHitRankingService.search(start, limit);
				json.accumulate("totalCount", pList.getCount());
				json.accumulate("data", pList.getList());
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
	
	@RequestMapping(value = WUC.USER_BETS_HIT_RANKING_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_BETS_HIT_RANKING_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.USER_BETS_HIT_RANKING_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String name = request.getParameter("name");
				String username = request.getParameter("username");
				int prizeMoney = HttpUtil.getIntParameter(request, "prizeMoney");
				String time = request.getParameter("time");
				String code = request.getParameter("code");
				String type = request.getParameter("type");
				int platform = HttpUtil.getIntParameter(request, "platform");
				if (StringUtils.isEmpty(name)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(username)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (prizeMoney < 0 || prizeMoney > 99999999) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(time)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(code)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(type)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (platform != Global.BILL_ACCOUNT_LOTTERY && platform != Global.BILL_ACCOUNT_AG && platform != Global.BILL_ACCOUNT_AG) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}

				boolean result = uBetsHitRankingService.add(name, username, prizeMoney, time, code, type, platform);
				if(result) {
					json.set(0, "0-6");
				} else {
					json.set(1, "1-6");
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
	
	@RequestMapping(value = WUC.USER_BETS_HIT_RANKING_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_BETS_HIT_RANKING_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.USER_BETS_HIT_RANKING_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				String name = request.getParameter("name");
				String username = request.getParameter("username");
				int prizeMoney = HttpUtil.getIntParameter(request, "prizeMoney");
				String time = request.getParameter("time");
				String code = request.getParameter("code");
				String type = request.getParameter("type");
				int platform = HttpUtil.getIntParameter(request, "platform");
				if (StringUtils.isEmpty(name)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(username)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (prizeMoney < 0 || prizeMoney > 99999999) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(time)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(code)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (StringUtils.isEmpty(type)) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				if (platform != Global.BILL_ACCOUNT_LOTTERY && platform != Global.BILL_ACCOUNT_AG && platform != Global.BILL_ACCOUNT_AG) {
					json.set(2, "2-2");
					HttpUtil.write(response, json.toString(), HttpUtil.json);
					return;
				}
				boolean result = uBetsHitRankingService.edit(id, name, username, prizeMoney, time, code, type, platform);
				if(result) {
					json.set(0, "0-6");
				} else {
					json.set(1, "1-6");
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
	
	@RequestMapping(value = WUC.USER_BETS_HIT_RANKING_DEL, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_BETS_HIT_RANKING_DEL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.USER_BETS_HIT_RANKING_DEL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				boolean result = uBetsHitRankingService.delete(id);
				if(result) {
					json.set(0, "0-5");
				} else {
					json.set(1, "1-5");
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
	
	@RequestMapping(value = WUC.USER_BETS_HIT_RANKING_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_BETS_HIT_RANKING_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		int id = HttpUtil.getIntParameter(request, "id");
		UserBetsHitRanking bean = uBetsHitRankingService.getById(id);
		JSONObject json = JSONObject.fromObject(bean);
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	
}