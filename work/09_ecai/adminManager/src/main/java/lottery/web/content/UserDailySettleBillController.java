package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserDailySettleBillService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
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
import java.util.List;

@Controller
public class UserDailySettleBillController extends AbstractActionController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserDailySettleBillService settleBillService;

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@RequestMapping(value = WUC.LOTTERY_USER_DAILY_SETTLE_BILL_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_DAILY_SETTLE_BILL_LIST(HttpSession session, HttpServletRequest request,
												 HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_DAILY_SETTLE_BILL_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String username = request.getParameter("username");
				String sTime = request.getParameter("sTime");
				String eTime = request.getParameter("eTime");
				Double minUserAmount = HttpUtil.getDoubleParameter(request, "minUserAmount");
				Double maxUserAmount = HttpUtil.getDoubleParameter(request, "maxUserAmount");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				Integer status = HttpUtil.getIntParameter(request, "status");
				List<Integer> userIds = new ArrayList<>();
				boolean legalUser = true; // 输入的用户名是否存在
				if (StringUtils.isNotEmpty(username)) {
					User user = userDao.getByUsername(username);
					if (user == null) {
						legalUser = false;
					}
					else {
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
				}
				else {
					PageList pList = settleBillService.search(userIds, sTime, eTime, minUserAmount, maxUserAmount, status, start, limit);
					if(pList != null) {
						json.accumulate("totalCount", pList.getCount());
						json.accumulate("data", pList.getList());
					} else {
						json.accumulate("totalCount", 0);
						json.accumulate("data", "[]");
					}
				}

				json.set(0, "0-3");
			}
			else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
}