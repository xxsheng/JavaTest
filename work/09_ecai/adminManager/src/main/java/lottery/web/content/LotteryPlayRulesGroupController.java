package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.LotteryPlayRulesGroupService;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupSimpleVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LotteryPlayRulesGroupController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private LotteryPlayRulesGroupService groupService;

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_GROUP_SIMPLE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_GROUP_SIMPLE_LIST(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			int typeId = HttpUtil.getIntParameter(request, "typeId");
			List<LotteryPlayRulesGroupSimpleVO> list = groupService.listSimpleByType(typeId);
			json.accumulate("data", list);
			json.set(0, "0-5");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_GROUP_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_GROUP_LIST(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PLAY_RULES_GROUP_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				List<LotteryPlayRulesGroupVO> list = groupService.list(lotteryId);
				json.accumulate("data", list);
				json.set(0, "0-5");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_GROUP_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_GROUP_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PLAY_RULES_GROUP_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int groupId = HttpUtil.getIntParameter(request, "groupId");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				boolean enable = HttpUtil.getBooleanParameter(request, "enable");
				boolean result = groupService.updateStatus(groupId, lotteryId, enable);
				if (result) {
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

}