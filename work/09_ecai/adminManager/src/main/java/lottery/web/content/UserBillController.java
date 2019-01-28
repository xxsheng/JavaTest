package lottery.web.content;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.vo.user.HistoryUserBetsVO;
import lottery.domains.content.vo.user.UserBetsVO;

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
public class UserBillController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserBetsService uBetsService;
	
	@RequestMapping(value = WUC.LOTTERY_USER_BILL_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BILL_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BILL_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String keyword = request.getParameter("keyword");
				String username = request.getParameter("username");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer utype = HttpUtil.getIntParameter(request, "utype");
				String minTime = request.getParameter("minTime");
				String maxTime = request.getParameter("maxTime");
				Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBillService.search(keyword, username, utype,type, minTime, maxTime, minMoney, maxMoney, status, start, limit);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_BILL_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BILL_DETAILS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			int id = HttpUtil.getIntParameter(request, "id");
			UserBetsVO result = uBetsService.getById(id);
			json.accumulate("data", result);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
	

	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_BILL_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_BILL_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.HISTORY_LOTTERY_USER_BILL_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String keyword = request.getParameter("keyword");
				String username = request.getParameter("username");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer utype = HttpUtil.getIntParameter(request, "utype");
				String minTime = request.getParameter("minTime");
				String maxTime = request.getParameter("maxTime");
				Double minMoney = HttpUtil.getDoubleParameter(request, "minMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBillService.searchHistory(keyword, username, type, minTime, maxTime, minMoney, maxMoney, status, start, limit);
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
	
	@RequestMapping(value = WUC.HISTORY_LOTTERY_USER_BILL_DETAILS, method = { RequestMethod.POST })
	@ResponseBody
	public void HISTORY_LOTTERY_USER_BILL_DETAILS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			int id = HttpUtil.getIntParameter(request, "id");
			HistoryUserBetsVO result = uBetsService.getHistoryById(id);
			json.accumulate("data", result);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
}
