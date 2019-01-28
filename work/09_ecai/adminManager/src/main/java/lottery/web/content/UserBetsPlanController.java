package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserBetsPlanController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private UserBetsPlanService uBetsPlanService;
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_PLAN_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_PLAN_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_PLAN_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String keyword = HttpUtil.getStringParameterTrim(request,"keyword");
				String username = HttpUtil.getStringParameterTrim(request,"username");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String expect = HttpUtil.getStringParameterTrim(request,"expect");
				Integer ruleId = HttpUtil.getIntParameter(request,"ruleId");
				String minTime = HttpUtil.getStringParameterTrim(request,"minTime");
				String maxTime = HttpUtil.getStringParameterTrim(request,"maxTime");
				Double minMoney = HttpUtil.getDoubleParameter(request, "minBetsMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxBetsMoney");
				Integer minMultiple = HttpUtil.getIntParameter(request, "minMultiple");
				Integer maxMultiple = HttpUtil.getIntParameter(request, "maxMultiple");
				Integer status = HttpUtil.getIntParameter(request, "status");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBetsPlanService.search(keyword, username, lotteryId, expect, ruleId, minTime, maxTime, minMoney, maxMoney, minMultiple, maxMultiple, status, start, limit);
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