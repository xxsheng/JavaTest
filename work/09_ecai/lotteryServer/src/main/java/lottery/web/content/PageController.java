package lottery.web.content;

import javautils.http.HttpUtil;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.im.IMAPI;
import lottery.domains.content.api.sb.Win88SBAPI;
import lottery.domains.content.biz.GameService;
import lottery.domains.content.biz.LotteryPlayRulesService;
import lottery.domains.content.biz.UserGameAccountService;
import lottery.domains.content.biz.UserInfoService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.SysLotteryConfigVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;
import lottery.domains.content.vo.user.UserCodeVO;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.payment.utils.RequestUtils;
import lottery.web.WFC;
import lottery.web.WebJSON;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController extends AbstractActionController {
	/**
	 * Service
	 */
	@Autowired
	private UserInfoService uInfoService;

	@Autowired
	private LotteryPlayRulesService lPlayRulesService;

	@Autowired
	private UserGameAccountService uGameAccountService;

	@Autowired
	private UserReadService uReadService;

	@Autowired
	private AGAPI agAPI;

	@Autowired
	private Win88SBAPI win88SBAPI;

	@Autowired
	private IMAPI imApi;

	@Autowired
	private GameService gameService;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@RequestMapping(value = WFC.LOGIN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOGIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser != null) {
			return new ModelAndView("redirect:"+WFC.MAIN);
		}
		else {
			return new ModelAndView(WFC.LOGIN);
		}
	}
	
	@RequestMapping(value = WFC.LOGOUT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOGOUT(HttpSession session, HttpServletRequest request) {
		super.logOut(session, request);
		return new ModelAndView("redirect:" + WFC.LOGIN);
	}
	
	@RequestMapping(value = WFC.PAGE_NOT_FOUND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PAGE_NOT_FOUND(HttpSession session, HttpServletRequest request) {
		return new ModelAndView(WFC.PAGE_NOT_FOUND);
	}
	
	@RequestMapping(value = WFC.PAGE_ERROR, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PAGE_ERROR(HttpSession session, HttpServletRequest request) {
		return new ModelAndView(WFC.PAGE_ERROR);
	}
	
	@RequestMapping(value = { WFC.DOMAIN, WFC.MAIN}, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MAIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser != null) {
			return new ModelAndView(WFC.MAIN);
		} else {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
	}

	@RequestMapping(value = WFC.INDEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView INDEX(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser != null) {
			return new ModelAndView(WFC.INDEX);
		} else {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
	}

	@RequestMapping(value = WFC.CENTER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView CENTER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser != null) {
			return new ModelAndView(WFC.CENTER);
		} else {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
	}

	@RequestMapping(value = WFC.PAY, method = { RequestMethod.POST })
	@ResponseBody
	public ModelAndView PAY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		String link = request.getParameter("link");
		String pid = request.getParameter("pid");
		String billno = request.getParameter("billno");
		String amount = request.getParameter("amount");
		String bankco = request.getParameter("bankco");
		String Signature = request.getParameter("Signature");
		Map<String, Object> model = new HashMap<>();
		model.put("link", link);
		model.put("pid", pid);
		model.put("billno", billno);
		model.put("amount", amount);
		model.put("bankco", bankco);
		model.put("Signature", Signature);
		return new ModelAndView(WFC.PAY, model);
	}
	
	// @RequestMapping(value = WFC.GAME, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView GAME(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.GAME,model);
	// }
	//
	// @RequestMapping(value = WFC.GAME_HAPPY, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView GAME_HAPPY(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.GAME_HAPPY,model);
	// }
	
	@RequestMapping(value = WFC.BRAND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView BRAND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		} 
		Map<String, Object> model = new HashMap<>();
		boolean isLogin = sessionUser != null ? true : false;
		model.put("isLogin", String.valueOf(isLogin));
		return new ModelAndView(WFC.BRAND, model);
	}
	
	// @RequestMapping(value = WFC.COUPON, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView COUPON(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
    //
	// 	User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
    //
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = uEntity != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	boolean isDaiLi = false;
	// 	boolean isZhuGuan = false;
	// 	boolean isZhiShu = false;
	// 	boolean isZongDai = false;
	// 	if(isLogin) {
	// 		isDaiLi = uEntity.getType() == Global.USER_TYPE_PROXY || uEntity.getType() == Global.USER_TYPE_RELATED;
	// 		if(isDaiLi) {
	// 			isZhuGuan = uCodePointUtil.isZhuGuan(uEntity);
	// 			if(!isZhuGuan) {
	// 				isZhiShu = uCodePointUtil.isZhiShu(uEntity);
	// 				if(!isZongDai) {
	// 					isZongDai = uCodePointUtil.isSpecialZhiShu(uEntity);
	// 				}
	// 			}
	// 		}
	// 	}
	// 	model.put("isDaiLi", isDaiLi);
	// 	model.put("isZhuGuan", isZhuGuan);
	// 	model.put("isZhiShu", isZhiShu);
	// 	model.put("isSpecialZhiShu", isZongDai);
	// 	return new ModelAndView(WFC.COUPON, model);
	// }
	
	@RequestMapping(value = WFC.ACTIVITY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.ACTIVITY, model);
	}
	
	// @RequestMapping(value = WFC.VIP_CLUB, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView VIP_CLUB(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.VIP_CLUB, model);
	// }
	//
	// @RequestMapping(value = WFC.JOIN_US, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView JOIN_US(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.JOIN_US, model);
	// }
	//
	@RequestMapping(value = WFC.NOTICE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView NOTICE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.NOTICE);
	}
	//
	// @RequestMapping(value = WFC.LICENSE, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView LICENSE(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.LICENSE, model);
	// }
	
	@RequestMapping(value = WFC.REGISTER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REGIST(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		Map<String, Object> model = new HashMap<>();
		boolean isLogin = sessionUser != null ? true : false;
		model.put("isLogin", String.valueOf(isLogin));
		return new ModelAndView(WFC.REGISTER, model);
	}
	//
	// @RequestMapping(value = WFC.QUESTION, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView QUESTION(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.QUESTION, model);
	// }
	//
	// @RequestMapping(value = WFC.GAME_CENTER, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView GAME_CENTER(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	UserInfo uInfo = uInfoService.get(sessionUser.getId());
	// 	if(uInfo == null) {
	// 		uInfo = new UserInfo();
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	model.put("navigate", uInfo.getNavigate());
	// 	return new ModelAndView(WFC.GAME_CENTER, model);
	// }
	//
	// @RequestMapping(value = WFC.GAME_LOTTERY, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView GAME_LOTTERY(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.GAME_LOTTERY);
	// }
	//
	@RequestMapping(value = WFC.REAL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REAL(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		Map<String, Object> model = new HashMap<>();
		boolean isLogin = sessionUser != null ? true : false;
		model.put("isLogin", String.valueOf(isLogin));
		return new ModelAndView(WFC.REAL);
	}
	//
	// @RequestMapping(value = WFC.GAME_ATHLETIC, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView GAME_ATHLETIC(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	boolean isLogin = sessionUser != null ? true : false;
	// 	model.put("isLogin", String.valueOf(isLogin));
	// 	return new ModelAndView(WFC.GAME_ATHLETIC);
	// }
	
	@RequestMapping(value = WFC.GAME, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView GAME(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		Map<String, Object> model = new HashMap<>();
		boolean isLogin = sessionUser != null ? true : false;
		model.put("isLogin", String.valueOf(isLogin));
		return new ModelAndView(WFC.GAME);
	}

	@RequestMapping(value = WFC.SPORT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView SPORT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.SPORT);
	}

	@RequestMapping(value = WFC.LOTTERY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY(HttpSession session, HttpServletRequest request, @PathVariable("name") String name) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		} else {
			Lottery lottery = dataFactory.getLottery(name);

			if(lottery != null) {
				User uEntity = uReadService.getByIdFromRead(sessionUser.getId());
				UserCodeVO UserData = new UserCodeVO(uEntity);
				SysLotteryConfigVO Config = new SysLotteryConfigVO(dataFactory);
				Map<String, LotteryPlayRulesGroupVO> groups = dataFactory.listLotteryPlayRulesGroupVOs(lottery.getId());
				Map<String, LotteryPlayRulesVO> rules = dataFactory.listLotteryPlayRulesVOs(lottery.getId());

//				// 快三、福彩3D、快乐8默认降点30
//				if(lottery.getType() == 3 || lottery.getType() == 4 || lottery.getType() == 5) {
//					final int lotteryDownCode = 30; // 默认降点30
//					UserData.setCode(UserData.getCode() - lotteryDownCode);
//				}
				// // 福彩3D、快乐8默认降点30
				// if(lottery.getType() == 4 || lottery.getType() == 5) {
				// 	final int lotteryDownCode = 30; // 默认降点30
				// 	UserData.setCode(UserData.getCode() - lotteryDownCode);
				// }

				Map<String, Object> model = new HashMap<>();
				model.put("Lottery", JSONObject.fromObject(lottery));
				model.put("UserData", JSONObject.fromObject(UserData));
				model.put("Config", JSONObject.fromObject(Config));
				model.put("PlayRulesGroup", JSONObject.fromObject(groups));
				model.put("PlayRules", JSONObject.fromObject(rules));

				if ("jsmmc".equals(lottery.getShortName())) {
					return new ModelAndView(WFC.LOTTERY_JSMMC, model);
				}
				if(lottery.getType() == 1) {
					return new ModelAndView(WFC.LOTTERY_SSC, model);
				}
				if(lottery.getType() == 2) {
					return new ModelAndView(WFC.LOTTERY_11X5, model);
				}
				if(lottery.getType() == 3) {
					return new ModelAndView(WFC.LOTTERY_K3, model);
				}
				if(lottery.getType() == 4) {
					return new ModelAndView(WFC.LOTTERY_3D, model);
				}
				if(lottery.getType() == 5) {
					return new ModelAndView(WFC.LOTTERY_KL8, model);
				}
				if(lottery.getType() == 6) {
					return new ModelAndView(WFC.LOTTERY_PK10, model);
				}
				if(lottery.getType() == 7) {
					return new ModelAndView(WFC.LOTTERY_LHD, model);
				}
			}
		}
//		return new ModelAndView(WFC.PAGE_NOT_FOUND);
		return new ModelAndView(WFC.LOTTERT_NOT_FOUND);
	}
	
	// @RequestMapping(value = WFC.LOTTERY_PLAN, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView LOTTERY_PLAN(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	} else {
	// 		String l = request.getParameter("l");
	// 		Lottery lottery = dataFactory.getLottery(l);
	// 		if(lottery != null) {
	// 			Map<String, Object> model = new HashMap<>();
	// 			model.put("Lottery", JSONObject.fromObject(lottery));
	// 			return new ModelAndView(WFC.LOTTERY_PLAN, model);
	// 		}
	// 	}
	// 	return new ModelAndView(WFC.PAGE_NOT_FOUND);
	// }
	
	@RequestMapping(value = WFC.LOTTERY_TREND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_TREND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		} else {
			String l = request.getParameter("l");
			Lottery lottery = dataFactory.getLottery(l);
			if(lottery != null) {
				Map<String, Object> model = new HashMap<>();
				model.put("Lottery", JSONObject.fromObject(lottery));
				return new ModelAndView(WFC.LOTTERY_TREND, model);
			}
		}
		return new ModelAndView(WFC.PAGE_NOT_FOUND);
	}
	
	@RequestMapping(value = WFC.MANAGER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MANAGER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.MANAGER);
	}

	@RequestMapping(value = WFC.ACCOUNT_MANAGER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACCOUNT_MANAGER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.ACCOUNT_MANAGER);
	}

	@RequestMapping(value = WFC.ACCOUNT_CARD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACCOUNT_CARD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.ACCOUNT_CARD);
	}

	@RequestMapping(value = WFC.ACCOUNT_LOGIN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACCOUNT_LOGIN(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.ACCOUNT_LOGIN);
	}

	@RequestMapping(value = WFC.FUND_RECHARGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView FUND_RECHARGE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.FUND_RECHARGE);
	}

	@RequestMapping(value = WFC.FUND_WITHDRAW, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView FUND_WITHDRAW(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.FUND_WITHDRAW);
	}

	@RequestMapping(value = WFC.FUND_TRANSFER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView FUND_TRANSFER(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.FUND_TRANSFER);
	}
	/**
	　* @Description: 转账中心
	　* @param [session, request]
	　* @return org.springframework.web.servlet.ModelAndView
	　* @throws
	　* @author SunJiang
	　* @date 2018/08/21,021 21:56:40
	　*/
	@RequestMapping(value = WFC.TRANSFER_ACCOUNT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView TRANSFER_ACCOUNT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.TRANSFER_ACCOUNT);
	}

	@RequestMapping(value = WFC.FUND_RECHARGE_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView FUND_RECHARGE_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.FUND_RECHARGE_RECORD);
	}

	@RequestMapping(value = WFC.FUND_WITHDRAW_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView FUND_WITHDRAW_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.FUND_WITHDRAW_RECORD);
	}

	@RequestMapping(value = WFC.FUND_TRANSFER_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView FUND_TRANSFER_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.FUND_TRANSFER_RECORD);
	}

	@RequestMapping(value = WFC.REPORT_MAIN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_REPORT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_MAIN);
	}

	@RequestMapping(value = WFC.REPORT_LOTTERY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_LOTTERY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_LOTTERY);
	}

	@RequestMapping(value = WFC.REPORT_GAME, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_GAME(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_GAME);
	}

	@RequestMapping(value = WFC.REPORT_LOTTERY_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_LOTTERY_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_LOTTERY_RECORD);
	}

	@RequestMapping(value = WFC.REPORT_GAME_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_GAME_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_GAME_RECORD);
	}

	@RequestMapping(value = WFC.REPORT_CHASE_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_CHASE_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_CHASE_RECORD);
	}

	@RequestMapping(value = WFC.REPORT_BILL_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView REPORT_BILL_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.REPORT_BILL_RECORD);
	}

	@RequestMapping(value = WFC.PROXY_INDEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_INDEX(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_INDEX, model);
	}

	@RequestMapping(value = WFC.PROXY_ACCOUNT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_ACCOUNT(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_ACCOUNT, model);
	}

	@RequestMapping(value = WFC.PROXY_TEAM, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_TEAM(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_TEAM, model);
	}

	@RequestMapping(value = WFC.PROXY_ONLINE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_ONLINE(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_ONLINE, model);
	}

	@RequestMapping(value = WFC.PROXY_LOTTERY_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_LOTTERY_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_LOTTERY_RECORD, model);
	}

	@RequestMapping(value = WFC.PROXY_GAME_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_GAME_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_GAME_RECORD, model);
	}

	@RequestMapping(value = WFC.PROXY_BILL_RECORD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_BILL_RECORD(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_BILL_RECORD, model);
	}

	@RequestMapping(value = WFC.PROXY_SALARY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_SALARY(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_SALARY, model);
	}

	@RequestMapping(value = WFC.PROXY_DIVIDEND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PROXY_DIVIDEND(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(WFC.PROXY_DIVIDEND, model);
	}

	@RequestMapping(value = WFC.MESSAGE_INBOX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MESSAGE_INBOX(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.MESSAGE_INBOX);
	}

	@RequestMapping(value = WFC.MESSAGE_OUTBOX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MESSAGE_OUTBOX(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.MESSAGE_OUTBOX);
	}

	@RequestMapping(value = WFC.MESSAGE_SYS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MESSAGE_SYS(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.MESSAGE_SYS);
	}

	@RequestMapping(value = WFC.MESSAGE_NEW, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MESSAGE_NEW(HttpSession session, HttpServletRequest request) {
		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}
		return new ModelAndView(WFC.MESSAGE_NEW);
	}

	// @RequestMapping(value = WFC.MANAGER_FUNDS, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView MANAGER_FUNDS(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	return new ModelAndView(WFC.MANAGER_FUNDS);
	// }
	//
	// @RequestMapping(value = WFC.MANAGER_REPORT, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView MANAGER_REPORT(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	return new ModelAndView(WFC.MANAGER_REPORT);
	// }
	//
	// @RequestMapping(value = WFC.MANAGER_ACCOUNT, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView MANAGER_ACCOUNT(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
    //
	// 	Map<String, Object> model = new HashMap<>();
	// 	return new ModelAndView(WFC.MANAGER_ACCOUNT, model);
	// }
	//
	// @RequestMapping(value = WFC.MANAGER_PROXY, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView MANAGER_PROXY(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
    //
	// 	if (sessionUser.getType() != Global.USER_TYPE_PROXY && sessionUser.getType() != Global.USER_TYPE_RELATED) {
	// 		Map<String, Object> model = new HashMap<>();
	// 		return new ModelAndView(WFC.PAGE_NOT_FOUND, model);
	// 	}
	// 	Map<String, Object> model = new HashMap<>();
	// 	return new ModelAndView(WFC.MANAGER_PROXY, model);
	// }
	//
	//
	// @RequestMapping(value = WFC.MANAGER_MESSAGE, method = { RequestMethod.GET })
	// @ResponseBody
	// public ModelAndView MANAGER_MESSAGE(HttpSession session, HttpServletRequest request) {
	// 	WebJSON json = new WebJSON(dataFactory);
	// 	SessionUser sessionUser = super.getSessionUser(json, session, request);
	// 	if (sessionUser == null) {
	// 		return new ModelAndView("redirect:"+WFC.LOGIN);
	// 	}
	// 	return new ModelAndView(WFC.MANAGER_MESSAGE);
	// }
	
	// @RequestMapping(value = WFC.LINE_DETECTION, method = { RequestMethod.GET,RequestMethod.POST })
	// @ResponseBody
	// public ModelAndView LINE_DETECTION(HttpSession session, HttpServletRequest request) {
	// 	return new ModelAndView(WFC.LINE_DETECTION);
	// }

	@RequestMapping(value = WFC.GAME_LAUNCHER, method = { RequestMethod.GET})
	@ResponseBody
	public ModelAndView GAME_LAUNCHER(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		int platformId = HttpUtil.getIntParameter(request, "platformId");
		SysPlatform platform = dataFactory.getSysPlatform(platformId);
		if (platform == null || platform.getStatus() != 0) {
			request.setAttribute("code", "2-7010");
			request.setAttribute("message", "该平台暂未开放！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}

		if (platformId == Global.BILL_ACCOUNT_PT) { // PT
			String gameCode = request.getParameter("gameCode");
			String h5 = request.getParameter("h5");
			if (StringUtils.isEmpty(gameCode)) {
				request.setAttribute("code", "2-7008");
				request.setAttribute("message", "参数不完整！");
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			UserGameAccount account = uGameAccountService.createIfNoAccount(json, sessionUser.getId(), sessionUser.getUsername(), platformId, 1);

			// 创建游戏账号失败
			if (account == null) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			String redirectUrl = gameService.getPTRedirectUrl(sessionUser.getId());
			if (StringUtils.isEmpty(redirectUrl)) {
				request.setAttribute("code", "1-1");
				request.setAttribute("message", "未获取到跳转地址，请稍候再试");
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			return new ModelAndView("redirect:"+redirectUrl+"&platformId=" + platformId + "&gameCode=" + gameCode);
		}
		else if (platformId == Global.BILL_ACCOUNT_IM) { // IM
			UserGameAccount account = uGameAccountService.createIfNoAccount(json, sessionUser.getId(), sessionUser.getUsername(), platformId, 1);

			// 创建游戏账号失败
			if (account == null) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			// 进入游戏链接
			String gameUrl = imApi.loginUrl(json, account.getUsername(), uGameAccountService.decryptPwd(account.getPassword()));
			if (StringUtils.isEmpty(gameUrl)) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}
			else {
				request.setAttribute("gameUrl", gameUrl);
				return new ModelAndView(WFC.GAME_LAUNCHER);
			}
		}
		else if (platformId == Global.BILL_ACCOUNT_AG) { // AG
			String lobby = HttpUtil.getStringParameterTrim(request, "lobby"); // 大厅
			if (StringUtils.isEmpty(lobby)) {
				lobby = "1"; // 默认旗舰
			}
			String model = HttpUtil.getStringParameterTrim(request, "model"); // 真钱or试玩
			if (StringUtils.isEmpty(model)) {
				model = "1"; // 默认真钱
			}

//			UserGameAccount account = uGameAccountService.createIfNoAccount(json, sessionUser.getId(), sessionUser.getUsername(), platformId, Integer.valueOf(model));
//			// 创建游戏账号失败
//			if (account == null) {
//				request.setAttribute("code", json.getCode());
//				request.setAttribute("message", json.getMessage());
//				return new ModelAndView(WFC.PAGE_ERROR);
//			}
//			String website = RequestUtils.getReferer(request);
//			String gameUrl = agAPI.forwardGame(account.getUsername(), uGameAccountService.decryptPwd(account.getPassword()), website, lobby, model);

			// 进入游戏链接
			String gameUrl = uGameAccountService.getGameUrlForword(json, sessionUser.getUsername(), Integer.valueOf(model));
			request.setAttribute("gameUrl", gameUrl);
			return new ModelAndView(WFC.GAME_LAUNCHER);
		}
		else if (platformId == Global.BILL_ACCOUNT_SB) { // SB
			UserGameAccount account = uGameAccountService.createIfNoAccount(json, sessionUser.getId(), sessionUser.getUsername(), Global.BILL_ACCOUNT_SB, 1);

			// 创建游戏账号失败
			if (account == null) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			String gameUrl = win88SBAPI.pcLoginUrl(json, account.getUsername());

			if (StringUtils.isEmpty(gameUrl)) {
				// 返回空地址，转向错误页面
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}
			else {
				// 获取游戏地址成功，设置cookie，沙巴要求
				String sessionToken = gameUrl.substring(gameUrl.lastIndexOf("g=")+2);
				Cookie sessionCookie = new Cookie("g", sessionToken);
				response.addCookie(sessionCookie);
			}

			request.setAttribute("gameUrl", gameUrl);
			return new ModelAndView(WFC.GAME_LAUNCHER);
		}
		else {
			request.setAttribute("code", "2-7007");
			request.setAttribute("message", "暂不支持的平台！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}
	}

	@RequestMapping(value = WFC.MOBILE_LAUNCHER, method = { RequestMethod.GET})
	@ResponseBody
	public ModelAndView MOBILE_LAUNCHER(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		WebJSON json = new WebJSON(dataFactory);
		SessionUser sessionUser = super.getSessionUser(json, session, request);
		if (sessionUser == null) {
			return new ModelAndView("redirect:"+WFC.LOGIN);
		}

		int platformId = HttpUtil.getIntParameter(request, "platformId");
		SysPlatform platform = dataFactory.getSysPlatform(platformId);
		if (platform == null || platform.getStatus() != 0) {
			request.setAttribute("code", "2-7010");
			request.setAttribute("message", "该平台暂未开放！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}

		if (platformId == Global.BILL_ACCOUNT_SB) { // SB
			UserGameAccount account = uGameAccountService.createIfNoAccount(json, sessionUser.getId(), sessionUser.getUsername(), Global.BILL_ACCOUNT_SB, 1);

			// 创建游戏账号失败
			if (account == null) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			// 进入游戏链接
			String gameUrl = win88SBAPI.mobileLoginUrl(json, account.getUsername());
			if (StringUtils.isEmpty(gameUrl)) {
				// 返回空地址，转向错误页面
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			// 转向游戏页面
			request.setAttribute("gameUrl", gameUrl);
			return new ModelAndView(WFC.GAME_LAUNCHER);
		}
		else {
			request.setAttribute("code", "2-7007");
			request.setAttribute("message", "暂不支持的平台！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}
	}

	@RequestMapping(value = WFC.GAME_REDIRECT, method = { RequestMethod.GET})
	@ResponseBody
	public ModelAndView GAME_REDIRECT(HttpServletRequest request) {
		Integer platformId = HttpUtil.getIntParameter(request, "platformId");
		if (platformId == null) {
			request.setAttribute("code", "1-1");
			request.setAttribute("message", "platformId参数不能为空！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}
		String param = request.getParameter("param");
		if (StringUtils.isEmpty(param)) {
			request.setAttribute("code", "1-1");
			request.setAttribute("message", "param参数不能为空！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}

		SysPlatform platform = dataFactory.getSysPlatform(platformId);
		if (platform == null || platform.getStatus() != 0) {
			request.setAttribute("code", "2-7010");
			request.setAttribute("message", "该平台暂未开放！");
			return new ModelAndView(WFC.PAGE_ERROR);
		}

		if (platformId == 11) {
			String gameCode = request.getParameter("gameCode");
			if (StringUtils.isEmpty(gameCode)) {
				request.setAttribute("code", "1-1");
				request.setAttribute("message", "gameCode参数不能为空！");
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			WebJSON json = new WebJSON(dataFactory);
			String ip = HttpUtil.getRealIp(json, request);
			if (ip == null) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			UserGameAccount account = gameService.decryptPTParam(json, ip, param);
			if (account == null) {
				request.setAttribute("code", json.getCode());
				request.setAttribute("message", json.getMessage());
				return new ModelAndView(WFC.PAGE_ERROR);
			}

			request.setAttribute("username", account.getUsername());
			request.setAttribute("password", uGameAccountService.decryptPwd(account.getPassword()));
			request.setAttribute("gameCode", gameCode);
			return new ModelAndView(WFC.PT_LAUNCHER);
		}

		request.setAttribute("code", "2-7010");
		request.setAttribute("message", "该平台暂未开放！");
		return new ModelAndView(WFC.PAGE_ERROR);
	}
}