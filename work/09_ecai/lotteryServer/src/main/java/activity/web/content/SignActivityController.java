package activity.web.content;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import activity.domains.content.biz.ActivitySignService;
import activity.domains.content.entity.ActivityRebate;
import activity.domains.content.vo.activity.ActivitySignVO;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;

/**
* <p>Title: SignActivityController</p>  
* <p>Description: 签到活动服务类 </p>  
* @author James  
* @date 2018年2月3日
 */
@Controller
public class SignActivityController extends AbstractActionController {
	
	@Autowired
	private DataFactory dataFactory;
	@Autowired
	private ActivitySignService aSignService;


/**
 * <p>Title: ACTIVITY_SIGN_LOAD</p>  
 * <p>Description: 加载签到活动</p>  
 * @param session
 * @param request
 * @return
 */
	@RequestMapping(value = WUC.ACTIVITY_SIGN_LOAD, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACTIVITY_SIGN_LOAD(HttpSession session, HttpServletRequest request) {
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
		ActivitySignVO signData = aSignService.getSignData(sessionUser.getId());
		if(signData != null) {
			json.data("data", signData);
			json.set(0, "0-1");
		} else {
			json.set(2, "2-4001");
		}
		return json.toJson();
	}
	
	/**
	 * <p>Title: ACTIVITY_SIGN_LOAD</p>  
	 * <p>Description: 用户参与签到活动</p>  
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value = WUC.ACTIVITY_SIGN, method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> ACTIVITY_SIGN(HttpSession session, HttpServletRequest request) {
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
		double amount = aSignService.sign(json, sessionUser.getId());
		// amount < 0 签到失败并返回失败原因
		// amount == 0 签到成功
		// amount > 0 签到成功并成功领取金额
		if(amount >= 0) {
			int amountInt = new BigDecimal(amount).intValue();
			json.data("amount", amountInt);
			json.set(0, "0-1");
		}
		return json.toJson();
	}


}