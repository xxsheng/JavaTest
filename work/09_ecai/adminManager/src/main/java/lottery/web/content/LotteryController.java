package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.LotteryService;
import lottery.domains.content.vo.lottery.LotteryVO;
import net.sf.json.JSONArray;
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
public class LotteryController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;
	
	@Autowired
	private LotteryService lotteryService;
	
	@RequestMapping(value = WUC.LOTTERY_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String type = request.getParameter("type");
			List<LotteryVO> list = lotteryService.list(type);
			JSONArray json = JSONArray.fromObject(list);
			HttpUtil.write(response, json.toString(), HttpUtil.json);
		}
		else {
			HttpUtil.write(response, "[]", HttpUtil.json);
		}
	}
	
	@RequestMapping(value = WUC.LOTTERY_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_USER_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				int id = HttpUtil.getIntParameter(request, "id");
				int status = HttpUtil.getIntParameter(request, "status");
				boolean result = lotteryService.updateStatus(id, status);
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
