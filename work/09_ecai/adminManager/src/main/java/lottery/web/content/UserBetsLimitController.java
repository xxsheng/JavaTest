package lottery.web.content;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.UserBetsLimitService;
import lottery.domains.content.vo.user.UserBetsLimitVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;

@Controller
public class UserBetsLimitController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
//	@Autowired
//	private AdminUserLogJob adminUserLogJob;
	
	@Autowired
	private UserBetsLimitService uBetsLimitService;
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_LIMIT_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_LIMIT_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_LIMIT_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				int start =  HttpUtil.getIntParameter(request, "start");
				int limit =  HttpUtil.getIntParameter(request, "limit");
				boolean queryGobalSetting =  HttpUtil.getBooleanParameter(request, "queryGobalSetting");
				PageList pList = uBetsLimitService.search(username, start, limit, queryGobalSetting);
				if(pList != null) {
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalCount", 0);
					json.accumulate("data", "[]");
				}
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_LIMIT_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_LIMIT_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_LIMIT_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserBetsLimitVO result = uBetsLimitService.getById(id);
				json.accumulate("data", result);
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
	
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_LIMIT_DELETE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_LIMIT_DELETE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_LIMIT_DELETE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				boolean result = uBetsLimitService.deleteUserBetsLimit(id);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_LIMIT_ADD_UPDATE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_LIMIT_ADD_UPDATE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_LIMIT_ADD_UPDATE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				Integer id = HttpUtil.getIntParameter(request, "id");
				String username = request.getParameter("username");
				String source = request.getParameter("source");
				int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				double maxBet = HttpUtil.getDoubleParameter(request, "maxBet");
				double maxPrize = HttpUtil.getDoubleParameter(request, "maxPrize");
				try {
					boolean result = uBetsLimitService.addOrUpdate(id, username, lotteryId, maxBet, source, maxPrize);
					if(result) {
						json.set(0, "0-5");
					} else {
						json.set(1, "1-5");
					}
				} catch (Exception e) {
						json.set(2, "2-2200");
					if(e instanceof IllegalArgumentException){
						json.set(2, "2-2201");
					}
					e.printStackTrace();
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
