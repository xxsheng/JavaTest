package lottery.web.content;

import java.util.ArrayList;
import java.util.List;

import javautils.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lottery.domains.content.biz.LotteryOpenStatusService;
import lottery.domains.content.vo.lottery.LotteryOpenStatusVO;
import lottery.domains.utils.lottery.open.LotteryOpenUtil;
import lottery.domains.utils.lottery.open.OpenTime;

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
public class LotteryOpenStatusController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private LotteryOpenStatusService lotteryOpenStatusService;
	
	@Autowired
	private LotteryOpenUtil lotteryOpenUtil;
	
	@RequestMapping(value = WUC.LOTTERY_OPEN_STATUS_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_STATUS_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_OPEN_STATUS_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String lotteryId = request.getParameter("lotteryId");
				String date = request.getParameter("date");
				if (Integer.valueOf(lotteryId) != 117) {
					List<LotteryOpenStatusVO> list = lotteryOpenStatusService.search(lotteryId, date);
					OpenTime thisOpentime = lotteryOpenUtil.getCurrOpenTime(Integer.parseInt(lotteryId));
					json.accumulate("data", list);
					json.accumulate("thisOpentime", thisOpentime);
				}
				else {
					json.accumulate("data", new ArrayList<>());
					json.accumulate("thisOpentime", null);
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
	
	@RequestMapping(value = WUC.LOTTERY_OPEN_MANUAL_CONTROL, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_MANUAL_CONTROL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_OPEN_MANUAL_CONTROL;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String lottery = request.getParameter("lottery");
				String expect = request.getParameter("expect");
				boolean result = lotteryOpenStatusService.doManualControl(lottery, expect);
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
	
}