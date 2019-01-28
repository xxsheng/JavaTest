package admin.web.content;

import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import net.sf.json.JSONArray;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.biz.AdminUserCriticalLogService;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserCriticalLog;
import admin.domains.content.vo.AdminUserActionVO;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;

@Controller
public class AdminUserCriticalLogController extends AbstractActionController {

	@Autowired
	private AdminUserCriticalLogService adminUserCriticalLogService;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@RequestMapping(value = WUC.ADMIN_USER_CRITICAL_LOG_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void ADMIN_USER_CRITICAL_LOG_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.ADMIN_USER_CRITICAL_LOG_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String ip = HttpUtil.getStringParameterTrim(request, "ip");
				String keyword = HttpUtil.getStringParameterTrim(request, "keyword");
				String sDate = HttpUtil.getStringParameterTrim(request, "sDate");
				String eDate = HttpUtil.getStringParameterTrim(request, "eDate");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				Integer actionId = HttpUtil.getIntParameter(request, "actionId");
				PageList pList = adminUserCriticalLogService.search(actionId,username, ip, keyword, sDate, eDate, start, limit);
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

}