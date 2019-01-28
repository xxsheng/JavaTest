package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsOriginalService;
import lottery.domains.content.vo.user.UserBetsOriginalVO;
import lottery.web.content.utils.UserCodePointUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserBetsOriginalController extends AbstractActionController {
	@Autowired
	private UserBetsOriginalService uBetsOriginalService;

	@Autowired
	private UserCodePointUtil uCodePointUtil;
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_ORIGINAL_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_ORIGINAL_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_ORIGINAL_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String keyword = HttpUtil.getStringParameterTrim(request,"keyword");
				String username = HttpUtil.getStringParameterTrim(request,"username");
				Integer type = HttpUtil.getIntParameter(request, "type");
				Integer utype = HttpUtil.getIntParameter(request, "utype");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String expect = HttpUtil.getStringParameterTrim(request,"expect");
				Integer ruleId = HttpUtil.getIntParameter(request,"ruleId");
				String minTime = HttpUtil.getStringParameterTrim(request,"minTime");
				if (StringUtil.isNotNull(minTime)) {
					minTime += " 00:00:00";
				}
				String maxTime = HttpUtil.getStringParameterTrim(request,"maxTime");
				if (StringUtil.isNotNull(maxTime)) {
					maxTime += " 00:00:00";
				}
				String minPrizeTime = HttpUtil.getStringParameterTrim(request,"minPrizeTime");
				if (StringUtil.isNotNull(minPrizeTime)) {
					minPrizeTime += " 00:00:00";
				}
				String maxPrizeTime = HttpUtil.getStringParameterTrim(request,"maxPrizeTime");
				if (StringUtil.isNotNull(maxPrizeTime)) {
					maxPrizeTime += " 00:00:00";
				}
				Double minMoney = HttpUtil.getDoubleParameter(request, "minBetsMoney");
				Double maxMoney = HttpUtil.getDoubleParameter(request, "maxBetsMoney");
				Integer minMultiple = HttpUtil.getIntParameter(request, "minMultiple");
				Integer maxMultiple = HttpUtil.getIntParameter(request, "maxMultiple");
				Double minPrizeMoney = HttpUtil.getDoubleParameter(request, "minPrizeMoney");
				Double maxPrizeMoney = HttpUtil.getDoubleParameter(request, "maxPrizeMoney");
				Integer status = HttpUtil.getIntParameter(request, "status");
				
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = uBetsOriginalService.search(keyword, username,utype, type, lotteryId, expect, ruleId, minTime, maxTime, minPrizeTime, maxPrizeTime,
						minMoney, maxMoney, minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney, status, start, limit);
				if(pList != null) {
					double[] totalMoney = uBetsOriginalService.getTotalMoney(keyword, username,utype, type, lotteryId, expect, ruleId, minTime, maxTime,minPrizeTime, maxPrizeTime,
							minMoney, maxMoney, minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney, status);
					json.accumulate("totalMoney", totalMoney[0]);
					json.accumulate("totalPrizeMoney", totalMoney[1]);
					json.accumulate("totalCount", pList.getCount());
					json.accumulate("data", pList.getList());
				} else {
					json.accumulate("totalMoney", 0);
					json.accumulate("totalPrizeMoney", 0);
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
	
	@RequestMapping(value = WUC.LOTTERY_USER_BETS_ORIGINAL_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_BETS_ORIGINAL_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_USER_BETS_ORIGINAL_GET;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				UserBetsOriginalVO result = uBetsOriginalService.getById(id);
				json.accumulate("data", result);
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
