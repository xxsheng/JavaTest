package activity.web.content;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.entity.ActivityRebate;
import activity.domains.content.vo.activity.ActivityWheelVO;
import javautils.http.HttpUtil;
import javautils.math.MathUtil;
import lottery.domains.content.biz.ActivityRebateWheelService;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;

/**
 * 幸运大转盘活动服务类
* <p>Title: ActivityController</p>  
* <p>Description: </p>  
* @author James  
* @date 2018年2月3日
 */
@Controller
public class WheelActivityController extends AbstractActionController {

	@Autowired
	private ActivityRebateWheelService wheelService;

	@Autowired
	private DataFactory dataFactory;
	
	@Autowired
	private ActivityRebateDao aRebateDao;
	
 /**
  * <p>Title: ACTIVITY_WHEEL_LOAD</p>  
  * <p>Description:加载幸运大转盘活动信息 </p>  
  * @param session
  * @param request
  * @return
  */
	@RequestMapping(value = WUC.ACTIVITY_WHEEL_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACTIVITY_WHEEL_LOAD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}
		if (sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13");
			return json.toJson();
		}
		/*
		if (sessionUser.getUpid() != 0) {
			json.set(2, "2-13");
			return json.toJson();
		}
		*/
		ActivityWheelVO todayData = wheelService.getTodayData(sessionUser.getId());
		ActivityRebate rebate = aRebateDao.getById(Global.ACTIVITY_REBATE_WHEEL);
		if(todayData != null) {
			boolean enabled = true;
			if(rebate.getStatus() != 0) {
				enabled = false;
			}
			todayData.setEnabled(enabled);
			json.data("data", todayData);
			json.set(0, "0-1");
		} else {
			json.set(2, "2-4001");
		}
		return json.toJson();
	}
	
	
	 /**
	  * <p>Title: ACTIVITY_WHEEL_LOAD</p>  
	  * <p>Description:e用户参与幸运大转盘活动 </p>  
	  * @param session
	  * @param request
	  * @return
	  */
	@RequestMapping(value = WUC.ACTIVITY_WHEEL_DRAW, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACTIVITY_WHEEL_DRAW(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return json.toJson();
		}

		if (sessionUser.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-13");
			return json.toJson();
		}
		/*
		if (sessionUser.getUpid() != 0) {
			json.set(2, "2-13");
			return json.toJson();
		}
		*/
		String ip = HttpUtil.getRealIp(json, request);
		if (ip == null) {
			return json.toJson();
		}

		Double amount = wheelService.draw(json, sessionUser.getId(), ip);
		// amount == null || amount <= 0 活动参与失败并返回失败原因
		// amount > 0 活动参与成功并返回金额
		if(amount != null && amount > 0) {
			String amountStr = MathUtil.doubleUpTOString(amount);
			json.data("amount", amountStr);
			json.set(0, "0-1");
		}
		return json.toJson();
	}

}