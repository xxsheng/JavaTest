package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.encrypt.PasswordUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.PaymentCardService;
import lottery.domains.content.entity.PaymentCard;
import lottery.domains.content.vo.payment.PaymentCardVO;
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
public class PaymentCardController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private PaymentCardService paymentCardService;

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_LIST, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_LIST;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				List<PaymentCardVO> list = new ArrayList<>();
				List<PaymentCard> clist = paymentCardService.listAll();
				for (PaymentCard tmpBean : clist) {
					list.add(new PaymentCardVO(tmpBean, super.getLotteryDataFactory()));
				}
				json.set(0, "0-3");
				json.accumulate("data", list);
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

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_GET, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_GET;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {

				int id = HttpUtil.getIntParameter(request, "id");
				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							PaymentCard bean = paymentCardService.getById(id);
							json.accumulate("data", bean);
							json.set(0, "0-3");
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
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

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_ADD, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_ADD;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {

				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (isUnlockedWithdrawPwd(session)) {
						if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
							int bankId = HttpUtil.getIntParameter(request, "bankId");
							String branchName = request.getParameter("branchName");
							String cardName = request.getParameter("cardName");
							String cardId = request.getParameter("cardId");
							double totalCredits = HttpUtil.getDoubleParameter(request, "totalCredits");
							double minTotalRecharge = HttpUtil.getDoubleParameter(request, "minTotalRecharge");
							double maxTotalRecharge = HttpUtil.getDoubleParameter(request, "maxTotalRecharge");
							String startTime = request.getParameter("sTime");
							String endTime = request.getParameter("eTime");
							double minUnitRecharge = HttpUtil.getDoubleParameter(request, "minUnitRecharge");
							double maxUnitRecharge = HttpUtil.getDoubleParameter(request, "maxUnitRecharge");
							boolean result = paymentCardService.add(bankId, branchName, cardName, cardId, totalCredits, minTotalRecharge, maxTotalRecharge, startTime, endTime, minUnitRecharge, maxUnitRecharge);
							if(result) {
								json.set(0, "0-6");
							} else {
								json.set(1, "1-6");
							}
						}
						else {
							json.set(2, "2-41");
						}
					}
					else {
						json.set(2, "2-43");
					}
				} else {
					json.set(2, "2-12");
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

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_EDIT, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_EDIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_EDIT;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {

				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							int id = HttpUtil.getIntParameter(request, "id");
							int bankId = HttpUtil.getIntParameter(request, "bankId");
							String branchName = request.getParameter("branchName");
							String cardName = request.getParameter("cardName");
							String cardId = request.getParameter("cardId");
							double totalCredits = HttpUtil.getDoubleParameter(request, "totalCredits");
							double minTotalRecharge = HttpUtil.getDoubleParameter(request, "minTotalRecharge");
							double maxTotalRecharge = HttpUtil.getDoubleParameter(request, "maxTotalRecharge");
							String startTime = request.getParameter("sTime");
							String endTime = request.getParameter("eTime");
							double minUnitRecharge = HttpUtil.getDoubleParameter(request, "minUnitRecharge");
							double maxUnitRecharge = HttpUtil.getDoubleParameter(request, "maxUnitRecharge");
							boolean result = paymentCardService.edit(id, bankId, branchName, cardName, cardId, totalCredits, minTotalRecharge, maxTotalRecharge, startTime, endTime, minUnitRecharge, maxUnitRecharge);
							if(result) {
								json.set(0, "0-6");
							} else {
								json.set(1, "1-6");
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
					json.set(2, "2-12");
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

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_UPDATE_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_UPDATE_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_UPDATE_STATUS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							int id = HttpUtil.getIntParameter(request, "id");
							int status = HttpUtil.getIntParameter(request, "status");
							boolean result = paymentCardService.updateStatus(id, status);
							if(result) {
								json.set(0, "0-5");
							} else {
								json.set(1, "1-5");
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
					json.set(2, "2-12");
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

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_RESET_CREDITS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_RESET_CREDITS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_RESET_CREDITS;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							int id = HttpUtil.getIntParameter(request, "id");
							boolean result = paymentCardService.resetCredits(id);
							if(result) {
								json.set(0, "0-5");
							} else {
								json.set(1, "1-5");
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
					json.set(2, "2-12");
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

	@RequestMapping(value = WUC.LOTTERY_PAYMENT_CARD_DELETE, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_PAYMENT_CARD_DELETE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_PAYMENT_CARD_DELETE;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			if (super.hasAccess(uEntity, actionKey)) {
				String withdrawPwd = request.getParameter("withdrawPwd");
				String token = getDisposableToken(session, request);

				if(PasswordUtil.validatePassword(uEntity.getWithdrawPwd(), token, withdrawPwd)) {
					if (!PasswordUtil.isSimplePassword(uEntity.getWithdrawPwd())) {
						if (isUnlockedWithdrawPwd(session)) {
							int id = HttpUtil.getIntParameter(request, "id");
							boolean result = paymentCardService.delete(id);
							if(result) {
								json.set(0, "0-5");
							} else {
								json.set(1, "1-5");
							}
						}
						else {
							json.set(2, "2-43");
						}
					}
					else {
						json.set(2, "2-41");
					}
				} else {
					json.set(2, "2-12");
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