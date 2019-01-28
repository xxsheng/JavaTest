package lottery.web.content;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.VipFreeChipsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
public class VipFreeChipsController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private VipFreeChipsService vFreeChipsService;
	
	@RequestMapping(value = WUC.VIP_FREE_CHIPS_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void VIP_FREE_CHIPS_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.VIP_FREE_CHIPS_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				Integer level = HttpUtil.getIntParameter(request, "level");
				String date = request.getParameter("date");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = vFreeChipsService.search(username, level, date, status, start, limit);
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
	
	@RequestMapping(value = WUC.VIP_FREE_CHIPS_CALCULATE, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void VIP_FREE_CHIPS_CALCULATE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.VIP_FREE_CHIPS_CALCULATE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				boolean result = vFreeChipsService.calculate();
				if(result) {
					json.set(0, "0-3");
				} else {
					json.set(1, "1-3");
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
	
	@RequestMapping(value = WUC.VIP_FREE_CHIPS_CONFIRM, method = { RequestMethod.POST,RequestMethod.GET })
	@ResponseBody
	public void VIP_FREE_CHIPS_CONFIRM(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.VIP_FREE_CHIPS_CONFIRM;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				boolean result = vFreeChipsService.agreeAll();
				if(result) {
					json.set(0, "0-3");
				} else {
					json.set(1, "1-3");
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