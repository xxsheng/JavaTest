package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsSameIpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserBetsSameIpLogController extends AbstractActionController {
	@Autowired
	private UserBetsSameIpLogService uBetsSameIpLogService;

	@RequestMapping(value = WUC.USER_BETS_SAME_IP_LOG_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void USER_BETS_SAME_IP_LOG_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.USER_BETS_SAME_IP_LOG_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String ip = HttpUtil.getStringParameterTrim(request, "ip");
				String username = HttpUtil.getStringParameterTrim(request, "username");
				String sortColumn = HttpUtil.getStringParameterTrim(request, "sortColumn");
				String sortType = HttpUtil.getStringParameterTrim(request, "sortType");

				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBetsSameIpLogService.search(ip, username, sortColumn, sortType, start, limit);
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
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
}
