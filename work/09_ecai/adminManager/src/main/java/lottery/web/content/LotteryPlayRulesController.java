package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.LotteryPlayRulesService;
import lottery.domains.content.vo.lottery.LotteryPlayRulesSimpleVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;
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
public class LotteryPlayRulesController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private LotteryPlayRulesService playRulesService;

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_LIST(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PLAY_RULES_LIST;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				Integer groupId = HttpUtil.getIntParameter(request, "groupId");
				List<LotteryPlayRulesVO> list = playRulesService.list(lotteryId, groupId);
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

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_SIMPLE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_SIMPLE_LIST(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			int typeId = HttpUtil.getIntParameter(request, "typeId");
			Integer groupId = HttpUtil.getIntParameter(request, "groupId");
			List<LotteryPlayRulesSimpleVO> list = playRulesService.listSimple(typeId, groupId);
			json.accumulate("data", list);
			json.set(0, "0-5");
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PLAY_RULES_GET;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				int ruleId = HttpUtil.getIntParameter(request, "ruleId");
				LotteryPlayRulesVO rulesVO = playRulesService.get(lotteryId, ruleId);
				json.accumulate("data", rulesVO);
				json.set(0, "0-5");
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PLAY_RULES_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int ruleId = HttpUtil.getIntParameter(request, "ruleId");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				String minNum = HttpUtil.getStringParameterTrim(request, "minNum");
				String maxNum = HttpUtil.getStringParameterTrim(request, "maxNum");
				boolean result = playRulesService.edit(ruleId, lotteryId, minNum, maxNum);
				if (result) {
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

	@RequestMapping(value = WUC.LOTTERY_PLAY_RULES_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PLAY_RULES_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PLAY_RULES_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int ruleId = HttpUtil.getIntParameter(request, "ruleId");
				Integer lotteryId = HttpUtil.getIntParameter(request, "lotteryId");
				boolean enable = HttpUtil.getBooleanParameter(request, "enable");
				boolean result = playRulesService.updateStatus(ruleId, lotteryId, enable);
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