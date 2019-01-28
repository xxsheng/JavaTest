package admin.web.content;

import admin.domains.content.biz.utils.JSMenuVO;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;
import admin.web.WFC;
import admin.web.helper.AbstractActionController;
import com.alibaba.fastjson.JSON;
import javautils.date.Moment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminPageController extends AbstractActionController {
	
	@RequestMapping(value = WFC.INDEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MAIN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.LOGIN);
		} else {
			Map<String, Object> model = new HashMap<>();
			List<JSMenuVO> mlist = super.listUserMenu(uEntity);
			// JSONArray json = JSONArray.fromObject(mlist);
			model.put("mlist", JSON.toJSONString(mlist));
			return new ModelAndView(WFC.INDEX, model);
		}
	}

	@Autowired
	private AdminUserDao mAdminUserDao;
	
	@RequestMapping(value = WFC.LOGIN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOGIN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		// String clientIpAddr = HttpUtil.getClientIpAddr(request);
		// boolean  isLogin = false;
		// List<AdminUser> listAll = mAdminUserDao.listAll();
		// for (AdminUser adminUser : listAll) {
		// 	String ips = adminUser.getIps();
		// 	if(ips.contains(clientIpAddr)){
		// 		isLogin = true;
		// 	}
		// }
		// if(Global.IPTABLES && !isLogin){
		// 	return new ModelAndView("redirect:" + "https://www.baidu.com/");
		// }
		if (uEntity != null) {
			return new ModelAndView("redirect:" + WFC.INDEX);
		} else {
			return new ModelAndView(WFC.LOGIN);
		}
	}
	
	@RequestMapping(value = WFC.LOGOUT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOGOUT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		super.logOut(session, request, response);
		return new ModelAndView("redirect:" + WFC.MAIN);
	}
	
	@RequestMapping(value = WFC.ACCESS_DENIED, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACCESS_DENIED(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(WFC.ACCESS_DENIED);
	}
	
	@RequestMapping(value = WFC.PAGE_NOT_FOUND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PAGE_NOT_FOUND(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(WFC.PAGE_NOT_FOUND);
	}
	
	@RequestMapping(value = WFC.PAGE_ERROR, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PAGE_ERROR(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(WFC.PAGE_ERROR);
	}
	
	@RequestMapping(value = WFC.PAGE_NOT_LOGIN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView PAGE_NOT_LOGIN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(WFC.PAGE_NOT_LOGIN);
	}
	
	@RequestMapping(value = { WFC.DASHBOARD }, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView DASHBOARD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.DASHBOARD);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_TYPE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_TYPE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_TYPE);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_CRAWLER_STATUS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_CRAWLER_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_CRAWLER_STATUS);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_OPEN_TIME, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_OPEN_TIME(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_OPEN_TIME);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_OPEN_CODE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_OPEN_CODE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_OPEN_CODE);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_OPEN_STATUS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_OPEN_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_OPEN_STATUS);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_PLAY_RULES, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PLAY_RULES(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PLAY_RULES);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_PLAY_RULES_GROUP, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PLAY_RULES_GROUP(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PLAY_RULES_GROUP);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER);
		}
	}
	
	
	@RequestMapping(value = WFC.LOTTERY_USER_ONLINE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_ONLINE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_ONLINE);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_BLACK_LIST, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BLACK_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BLACK_LIST);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_WHITE_LIST, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_WHITE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_WHITE_LIST);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_PROFILE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_PROFILE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_PROFILE);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_CARD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_CARD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_CARD);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_UNBIND_CARD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_UNBIND_CARD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_UNBIND_CARD);
		}
	}
	
	
	@RequestMapping(value = WFC.LOTTERY_USER_SECURITY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_SECURITY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_SECURITY);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_RECHARGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_RECHARGE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_RECHARGE);
		}
	}
	/**
	 * 历史用户充值
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.HISTORY_LOTTERY_USER_RECHARGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_USER_RECHARGE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_LOTTERY_USER_RECHARGE);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_WITHDRAW, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_WITHDRAW(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			Map<String, Object> model = new HashMap<>();
			model.put("LoginUser", uEntity.getUsername());
			return new ModelAndView(WFC.LOTTERY_USER_WITHDRAW, model);
		}
	}
	//历史提现查询
	@RequestMapping(value = WFC.HISTORY_LOTTERY_USER_WITHDRAW, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_USER_WITHDRAW(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			Map<String, Object> model = new HashMap<>();
			model.put("LoginUser", uEntity.getUsername());
			return new ModelAndView(WFC.HISTORY_LOTTERY_USER_WITHDRAW, model);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_WITHDRAW_CHECK, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_WITHDRAW_CHECK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_WITHDRAW_CHECK);
		}
	}
	/**
	 * 历史提现可疑查询
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.HISTORY_LOTTERY_USER_WITHDRAW_CHECK, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_USER_WITHDRAW_CHECK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_LOTTERY_USER_WITHDRAW_CHECK);
		}
	}
	@RequestMapping(value = WFC.LOTTERY_USER_BETS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BETS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BETS);
		}
	}

	/**
	 * 历史投注记录
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.HISTORY_LOTTERY_USER_BETS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_USER_BETS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_LOTTERY_USER_BETS);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_BETS_ORIGINAL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BETS_ORIGINAL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BETS_ORIGINAL);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_BETS_PLAN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BETS_PLAN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BETS_PLAN);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_BETS_BATCH, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BETS_BATCH(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BETS_BATCH);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BILL);
		}
	}
	@RequestMapping(value = WFC.HISTORY_LOTTERY_USER_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_USER_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_LOTTERY_USER_BILL);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_MESSAGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_MESSAGE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_MESSAGE);
		}
	}

	@RequestMapping(value = WFC.USER_HIGH_PRIZE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView USER_HIGH_PRIZE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			Map<String, Object> model = new HashMap<>();
			model.put("LoginUser", uEntity.getUsername());
			return new ModelAndView(WFC.USER_HIGH_PRIZE, model);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_PLATFORM_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PLATFORM_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PLATFORM_BILL);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_DAILY_SETTLE_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_DAILY_SETTLE_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_DAILY_SETTLE_BILL);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_DAILY_SETTLE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_DAILY_SETTLE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_DAILY_SETTLE);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_DIVIDEND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_DIVIDEND(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_DIVIDEND);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_DIVIDEND_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_DIVIDEND_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_DIVIDEND_BILL);
		}
	}

	@RequestMapping(value = WFC.USER_GAME_DIVIDEND_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView USER_GAME_DIVIDEND_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.USER_GAME_DIVIDEND_BILL);
		}
	}

	@RequestMapping(value = WFC.USER_GAME_WATER_BILL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView USER_GAME_WATER_BILL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.USER_GAME_WATER_BILL);
		}
	}

	@RequestMapping(value = WFC.MAIN_REPORT_COMPLEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView MAIN_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.MAIN_REPORT_COMPLEX);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_REPORT_COMPLEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_REPORT_COMPLEX);
		}
	}
	
	/**
	 * 历史彩票综合报表
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.HISTORY_LOTTERY_REPORT_COMPLEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_LOTTERY_REPORT_COMPLEX);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_REPORT_PROFIT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_REPORT_PROFIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_REPORT_PROFIT);
		}
	}
	
	@RequestMapping(value = WFC.GAME_REPORT_COMPLEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView GAME_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.GAME_REPORT_COMPLEX);
		}
	}

	/**
	 * 历史真人彩票体育报表
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.HISTORY_GAME_REPORT_COMPLEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_GAME_REPORT_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_GAME_REPORT_COMPLEX);
		}
	}
	
	@RequestMapping(value = WFC.RECHARGE_WITHDRAW_COMPLEX, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView RECHARGE_WITHDRAW_COMPLEX(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.RECHARGE_WITHDRAW_COMPLEX);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_REPORT_USER_PROFIT_RANKING, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_REPORT_USER_PROFIT_RANKING(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_REPORT_USER_PROFIT_RANKING);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_PAYMENT_BANK, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PAYMENT_BANK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PAYMENT_BANK);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_PAYMENT_CARD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PAYMENT_CARD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PAYMENT_CARD);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_PAYMENT_CHANNEL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PAYMENT_CHANNEL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PAYMENT_CHANNEL);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_PAYMENT_CHANNEL_MOBILESCAN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PAYMENT_CHANNEL_MOBILESCAN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PAYMENT_CHANNEL_MOBILESCAN);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_PAYMENT_CHANNEL_BANK, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_PAYMENT_CHANNEL_BANK(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_PAYMENT_CHANNEL_BANK);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_SYS_CONFIG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_SYS_CONFIG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_SYS_CONFIG);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_SYS_NOTICE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_SYS_NOTICE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_SYS_NOTICE);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_ACTION_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_ACTION_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_ACTION_LOG);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_LOGIN_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_LOGIN_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_LOGIN_LOG);
		}
	}
	/**
	 * 历史登陆日志
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.HISTORY_LOTTERY_USER_LOGIN_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView HISTORY_LOTTERY_USER_LOGIN_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.LOGIN);
		} else {
			return new ModelAndView(WFC.HISTORY_LOTTERY_USER_LOGIN_LOG);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_USER_LOGIN_SAMIP_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_LOGIN_SAMIP_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_LOGIN_SAMIP_LOG);
		}
	}

	@RequestMapping(value = WFC.LOTTERY_USER_BETS_LIMIT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_USER_BETS_LIMIT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_USER_BETS_LIMIT);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_REWARD, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_REWARD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_REWARD);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_SALARY, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_SALARY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_SALARY);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_BIND, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_BIND(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_BIND);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_RECHARGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_RECHARGE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_RECHARGE);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_RECHARGE_LOOP, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_RECHARGE_LOOP(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_RECHARGE_LOOP);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_SIGN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_SIGN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_SIGN);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_GRAB, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_GRAB(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_GRAB);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_PACKET, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_PACKET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_PACKET);
		}
	}
	
	@RequestMapping(value = WFC.ACTIVITY_REBATE_COST, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_COST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_COST);
		}
	}

	@RequestMapping(value = WFC.ACTIVITY_RED_PACKET_RAIN, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_RED_PACKET_RAIN(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_RED_PACKET_RAIN);
		}
	}

	@RequestMapping(value = WFC.ACTIVITY_FIRST_RECHARGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_FIRST_RECHARGE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_FIRST_RECHARGE);
		}
	}

	@RequestMapping(value = WFC.ACTIVITY_REBATE_WHEEL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ACTIVITY_REBATE_WHEEL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ACTIVITY_REBATE_WHEEL);
		}
	}

	@RequestMapping(value = WFC.VIP_UPGRADE_LIST, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView VIP_UPGRADE_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.VIP_UPGRADE_LIST);
		}
	}
	
	@RequestMapping(value = WFC.VIP_UPGRADE_GIFTS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView VIP_UPGRADE_GIFTS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.VIP_UPGRADE_GIFTS);
		}
	}
	
	@RequestMapping(value = WFC.VIP_BIRTHDAY_GIFTS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView VIP_BIRTHDAY_GIFTS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.VIP_BIRTHDAY_GIFTS);
		}
	}
	
	@RequestMapping(value = WFC.VIP_FREE_CHIPS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView VIP_FREE_CHIPS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.VIP_FREE_CHIPS);
		}
	}
	
	@RequestMapping(value = WFC.VIP_INTEGRAL_EXCHANGE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView VIP_INTEGRAL_EXCHANGE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.VIP_INTEGRAL_EXCHANGE);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_INSTANT_STAT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_INSTANT_STAT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			String ServerTime = new Moment().toSimpleTime();
			Map<String, Object> model = new HashMap<>();
			model.put("ServerTime", ServerTime);
			return new ModelAndView(WFC.LOTTERY_INSTANT_STAT, model);
		}
	}

	@RequestMapping(value = WFC.USER_BALANCE_SNAPSHOT, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView USER_BALANCE_SNAPSHOT(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			String ServerTime = new Moment().toSimpleTime();
			Map<String, Object> model = new HashMap<>();
			return new ModelAndView(WFC.USER_BALANCE_SNAPSHOT, model);
		}
	}

	@RequestMapping(value = WFC.ADMIN_USER, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ADMIN_USER);
		}
	}
	
	@RequestMapping(value = WFC.ADMIN_USER_ROLE, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER_ROLE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ADMIN_USER_ROLE);
		}
	}
	
	@RequestMapping(value = WFC.ADMIN_USER_MENU, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER_MENU(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ADMIN_USER_MENU);
		}
	}
	
	@RequestMapping(value = WFC.ADMIN_USER_ACTION, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER_ACTION(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ADMIN_USER_ACTION);
		}
	}
	
	@RequestMapping(value = WFC.ADMIN_USER_ACTION_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER_ACTION_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ADMIN_USER_ACTION_LOG);
		}
	}
	
	@RequestMapping(value = WFC.ADMIN_USER_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.ADMIN_USER_LOG);
		}
	}
	
	/**
	 * 后台操作关键日志
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = WFC.ADMIN_USER_CRITICAL_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView ADMIN_USER_CRITICAL_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.ADMIN_USER_CRITICAL_LOG);
		} else {
			return new ModelAndView(WFC.ADMIN_USER_CRITICAL_LOG);
		}
	}
	
	@RequestMapping(value = WFC.LOTTERY_SYS_CONTROL, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView LOTTERY_SYS_CONTROL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.LOTTERY_SYS_CONTROL);
		}
	}

	@RequestMapping(value = WFC.GAME_LIST, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView GAME_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.GAME_LIST);
		}
	}

	@RequestMapping(value = WFC.GAME_PLATFORM_LIST, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView GAME_PLATFORM_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.GAME_PLATFORM_LIST);
		}
	}

	@RequestMapping(value = WFC.GAME_BETS, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView GAME_BETS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.GAME_BETS);
		}
	}

	@RequestMapping(value = WFC.USER_BETS_HIT_RANKING, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView USER_BETS_HIT_RANKING(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.USER_BETS_HIT_RANKING);
		}
	}

	@RequestMapping(value = WFC.USER_BETS_SAME_IP_LOG, method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView USER_BETS_SAME_IP_LOG(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity == null) {
			return new ModelAndView("redirect:" + WFC.PAGE_NOT_LOGIN);
		} else {
			return new ModelAndView(WFC.USER_BETS_SAME_IP_LOG);
		}
	}

}