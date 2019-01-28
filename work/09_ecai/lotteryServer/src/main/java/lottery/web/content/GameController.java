package lottery.web.content;

import com.alibaba.fastjson.JSON;
import javautils.StringUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.api.ag.AGValidationResult;
import lottery.domains.content.api.im.IMValidationResult;
import lottery.domains.content.biz.UserGameAccountService;
import lottery.domains.content.entity.Game;
import lottery.domains.content.entity.GameType;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.pool.DataFactory;
import lottery.web.WUC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserValidate;
import lottery.web.helper.AbstractActionController;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2016/12/25.
 */
@Controller
public class GameController extends AbstractActionController {
    /**
     * Service
     */
    @Autowired
    private UserGameAccountService uGameAccountService;

    /**
     * Validate
     */
    @Autowired
    private UserValidate uValidate;

    /**
     * Util
     */

    /**
     * DataFactory
     */
    @Autowired
    private DataFactory dataFactory;

    @RequestMapping(value = WUC.GAME_TYPE_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> GAME_TYPE_LIST(HttpSession session, HttpServletRequest request) {
        WebJSON json = new WebJSON(dataFactory);
        SessionUser sessionUser = super.getSessionUser(json, session, request);
        if (sessionUser == null) {
            return json.toJson();
        }

        int platformId = HttpUtil.getIntParameter(request, "platformId");
        List<GameType> types = dataFactory.listGameTypeByPlatform(platformId);
        if (types == null) {
            types = new ArrayList<>();
        }
        json.data("data", types);
        json.set(0, "0-1");
        return json.toJson();
    }

    @RequestMapping(value = WUC.GAME_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> GAME_LIST(HttpSession session, HttpServletRequest request) {
        WebJSON json = new WebJSON(dataFactory);
        SessionUser sessionUser = super.getSessionUser(json, session, request);
        if (sessionUser == null) {
            return json.toJson();
        }

        int platformId = HttpUtil.getIntParameter(request, "platformId");
        Integer typeId = HttpUtil.getIntParameter(request, "typeId");
        String name = HttpUtil.getStringParameterTrim(request, "name");

        List<Game> games;
        if (typeId == null) {
            games = dataFactory.listGameByPlatform(platformId);
        }
        else {
            games = dataFactory.listGameByType(typeId);
        }

        if (games == null) {
            games = new ArrayList<>();
        }

        if (StringUtil.isNotNull(name)) {
            List<Game> searGames = new ArrayList<>();
            for (Game game : games) {
                if (game.getGameName().contains(name)) {
                    searGames.add(game);
                }
            }
            json.data("data", searGames);
        }
        else {
            json.data("data", games);
        }

        json.set(0, "0-1");
        return json.toJson();
    }

    // @RequestMapping(value = WUC.PT_USER_INFO, method = { RequestMethod.POST })
    // @ResponseBody
    // public Map<String, Object> PT_USER_INFO(HttpSession session, HttpServletRequest request) {
    //     WebJSON json = new WebJSON(dataFactory);
    //     SessionUser sessionUser = super.getSessionUser(json, session, request);
    //     if (sessionUser == null) {
    //         return json.toJson();
    //     }
    //
    //     UserGameAccount account = dataFactory.getGameAccount(sessionUser.getId(), 11, 1); // 11：PT
    //
    //     if (account != null) {
    //         Map<String, String> userInfo = new HashMap<>();
    //         userInfo.put("username", account.getUsername());
    //         userInfo.put("password", uGameAccountService.decryptPwd(account.getPassword()));
    //         json.data("data", userInfo);
    //         json.set(0, "0-1");
    //     }
    //     else {
    //         json.data("data", "{}");
    //     }
    //     return json.toJson();
    // }

    // @RequestMapping(value = WUC.PT_MOD_PWD, method = { RequestMethod.POST })
    // @ResponseBody
    // public Map<String, Object> PT_MOD_PWD(HttpSession session, HttpServletRequest request) {
    //     WebJSON json = new WebJSON(dataFactory);
    //     SessionUser sessionUser = super.getSessionUser(json, session, request);
    //     if (sessionUser == null) {
    //         return json.toJson();
    //     }
    //
    //     UserGameAccount account = dataFactory.getGameAccount(sessionUser.getId(), 11, 1); // 11：PT
    //     if (account == null) {
    //         json.set(2, "2-8010"); // 需首次登录游戏
    //     }
    //     else {
    //         String password = request.getParameter("password");
    //         if (StringUtils.isEmpty(password)) {
    //             json.set(2, "2-6"); // 请求数据错误
    //         }
    //         else {
    //             if (password.length() < 6 || password.length() > 16) {
    //                 json.set(2, "2-8011"); // 密码长度为6-16位
    //             }
    //             else {
    //                 boolean succeed = uGameAccountService.modPwd(json, sessionUser.getId(), 11, password);
    //                 if (succeed) {
    //                     json.set(0, "0-1"); // 成功
    //                 }
    //             }
    //         }
    //     }
    //     return json.toJson();
    // }

    // @RequestMapping(value = WUC.AG_USER_INFO, method = { RequestMethod.POST })
    // @ResponseBody
    // public Map<String, Object> AG_USER_INFO(HttpSession session, HttpServletRequest request) {
    //     WebJSON json = new WebJSON(dataFactory);
    //     SessionUser sessionUser = super.getSessionUser(json, session, request);
    //     if (sessionUser == null) {
    //         return json.toJson();
    //     }
    //
    //     UserGameAccount account = dataFactory.getGameAccount(sessionUser.getId(), 4, 1); // 4：AG
    //
    //     if (account != null) {
    //         Map<String, String> userInfo = new HashMap<>();
    //         userInfo.put("username", account.getUsername());
    //         userInfo.put("password", uGameAccountService.decryptPwd(account.getPassword()));
    //         json.data("data", userInfo);
    //         json.set(0, "0-1");
    //     }
    //     else {
    //         json.data("data", "{}");
    //     }
    //     return json.toJson();
    // }

    // @RequestMapping(value = WUC.AG_VALIDATION, method = {RequestMethod.POST, RequestMethod.GET}, produces = MediaType.APPLICATION_XML_VALUE)
    // @ResponseBody
    // public String agValidation(@RequestBody String body, HttpServletRequest request) {
    //     String domain = HttpUtil.getHost(request);
    //     String ip = HttpUtil.getRealIp(null, request);
    //
    //     // 验证
    //     AGValidationResult result = uValidate.validateAG(body, domain, ip);
    //     if (result == null) {
    //         return null;
    //     }
    //
    //     // // 转账
    //     // if (result.isSuccess()) {
    //     //     // 自动将用户彩票账户的钱转到AG
    //     //     User user = result.getUser();
    //     //     double lotteryMoney = user.getLotteryMoney();
    //     //     WebJSON json = new WebJSON(dataFactory);
    //     //     if (lotteryMoney > 0) {
    //     //         lotteryMoney = MathUtil.doubleFormat(lotteryMoney, 0);
    //     //         uTransfersService.transfersToSelf(json, user.getId(), Global.BILL_ACCOUNT_AG, Global.BILL_ACCOUNT_LOTTERY, lotteryMoney);
    //     //     }
    //     //
    //     //     // 自动将用户主账户的钱转到AG
    //     //     double totalMoney = user.getTotalMoney();
    //     //     if (totalMoney > 0) {
    //     //         totalMoney = MathUtil.doubleFormat(totalMoney, 0);
    //     //         uTransfersService.transfersToSelf(json, user.getId(), Global.BILL_ACCOUNT_AG, Global.BILL_ACCOUNT_MAIN, totalMoney);
    //     //     }
    //     // }
    //
    //     return result.getXml();
    // }
    //
    // @RequestMapping(value = WUC.IM_VALIDATION, method = {RequestMethod.POST, RequestMethod.GET})
    // @ResponseBody
    // public String imValidation(HttpServletRequest request) {
    //     String userName = request.getParameter("userName");
    //     String password = request.getParameter("password");
    //     String ip = HttpUtil.getRealIp(null, request);
    //
    //     // 验证
    //     IMValidationResult result = uValidate.validateIM(userName, password, ip);
    //     if (result == null) {
    //         return null;
    //     }
    //
    //     return JSON.toJSONString(request);
    // }
}
