package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.web.content.validate.CodeValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LotteryOpenCodeController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private LotteryOpenCodeService lotteryOpenCodeService;

	@Autowired
	private CodeValidate codeValidate;

	@RequestMapping(value = WUC.LOTTERY_OPEN_CODE_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_CODE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_OPEN_CODE_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String lottery = request.getParameter("lottery");
				String expect = request.getParameter("expect");
				int start = HttpUtil.getIntParameter(request, "start");
				int limit = HttpUtil.getIntParameter(request, "limit");
				PageList pList = lotteryOpenCodeService.search(lottery, expect, start, limit);
				json.accumulate("totalCount", pList.getCount());
				json.accumulate("data", pList.getList());
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

	@RequestMapping(value = WUC.LOTTERY_OPEN_CODE_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_CODE_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_OPEN_CODE_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String lottery = request.getParameter("lottery");
			String expect = request.getParameter("expect");
			LotteryOpenCodeVO entity = lotteryOpenCodeService.get(lottery, expect);
			json.accumulate("data", entity);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_OPEN_CODE_DELETE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_CODE_DELETE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_OPEN_CODE_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String lottery = request.getParameter("lottery");
			String expect = request.getParameter("expect");
			LotteryOpenCodeVO entity = lotteryOpenCodeService.get(lottery, expect);
			LotteryOpenCode bean = entity.getBean();
			lotteryOpenCodeService.delete(bean);
			json.set(0, "0-3");
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2 - t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}


	@RequestMapping(value = WUC.LOTTERY_OPEN_CODE_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_CODE_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		// 必须有手动开奖权限才能添加号码
		String actionKey = WUC.LOTTERY_OPEN_MANUAL_CONTROL;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String lottery = request.getParameter("lottery");
				String expect = request.getParameter("expect");
				String code = request.getParameter("code");
				// 号码验证
				if (codeValidate.validateCode(json, lottery, code)) {
					// 期号验证，不能大于当前投注期号
					if (codeValidate.validateExpect(json, lottery, expect)) {
						boolean result = lotteryOpenCodeService.add(json, lottery, expect, code, uEntity.getUsername());
						if (result) {
							json.set(0, "0-5");
						}
					}
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_OPEN_CODE_CORRECT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_OPEN_CODE_CORRECT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		// 必须有手动开奖权限才能添加号码
		String actionKey = WUC.LOTTERY_OPEN_MANUAL_CONTROL;
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String lottery = request.getParameter("lottery");
				String expect = request.getParameter("expect");
				String code = request.getParameter("code");
				String moneyPwd = request.getParameter("moneyPwd");
				String token = getDisposableToken(session, request);
				// 号码验证
				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, moneyPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							if (codeValidate.validateCode(json, lottery, code)) {
								// 期号验证，不能大于当前投注期号
								boolean result = lotteryOpenCodeService.add(json, lottery, expect, code, "手动修正号码");
								if (result) {
									json.set(0, "0-5");
								}
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				}else {
					json.set(2, "2-12");
				}
			} else {
				json.set(2, "2-4");
			}
		} else {
			json.set(2, "2-6");
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}
}