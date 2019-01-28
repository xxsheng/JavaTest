package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.domains.jobs.AdminUserLogJob;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.http.HttpUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.pt.PTAPI;
import lottery.domains.content.biz.GameBetsService;
import lottery.domains.content.biz.GameService;
import lottery.domains.content.biz.SysPlatformService;
import lottery.domains.content.dao.UserGameAccountDao;
import lottery.domains.content.entity.Game;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.vo.user.GameBetsVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2016/12/24.
 */
@Controller
public class GameController extends AbstractActionController {
    @Autowired
    private GameService gameService;

    @Autowired
    private GameBetsService gameBetsService;

    @Autowired
    private UserGameAccountDao uGameAccountDao;

    @Autowired
    private SysPlatformService sysPlatformService;

    @Autowired
    private AdminUserActionLogJob adminUserActionLogJob;

    @Autowired
    private AdminUserLogJob adminUserLogJob;

    @Autowired
    private PTAPI ptAPI;
    @Autowired
    private AGAPI agAPI;

    @RequestMapping(value = WUC.GAME_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String gameName = request.getParameter("gameName");
                String gameCode = request.getParameter("gameCode");
                Integer typeId = HttpUtil.getIntParameter(request, "typeId");
                Integer platformId = HttpUtil.getIntParameter(request, "platformId");
                Integer display = HttpUtil.getIntParameter(request, "display");
                Integer flashSupport = HttpUtil.getIntParameter(request, "flashSupport");
                Integer h5Support = HttpUtil.getIntParameter(request, "h5Support");
                int start = HttpUtil.getIntParameter(request, "start");
                int limit = HttpUtil.getIntParameter(request, "limit");
                PageList pList = gameService.search(gameName, gameCode, typeId, platformId, display, flashSupport, h5Support, start, limit);
                if(pList != null) {
                    json.accumulate("totalCount", pList.getCount());
                    json.accumulate("data", pList.getList());
                } else {
                    json.accumulate("totalCount", 0);
                    json.accumulate("data", "[]");
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

    @RequestMapping(value = WUC.GAME_GET, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_GET;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");
                Game game = gameService.getById(id);
                if(game != null) {
                    json.accumulate("data", game);
                } else {
                    json.accumulate("data", "{}");
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

    @RequestMapping(value = WUC.GAME_ADD, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_ADD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_ADD;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String gameName = request.getParameter("gameName");
                String gameCode = request.getParameter("gameCode");
                Integer platformId = HttpUtil.getIntParameter(request, "platformId");
                Integer typeId = HttpUtil.getIntParameter(request, "typeId");
                String imgUrl = request.getParameter("imgUrl");
                int sequence = HttpUtil.getIntParameter(request, "sequence");
                int display = HttpUtil.getIntParameter(request, "display");
                Integer flashSupport = HttpUtil.getIntParameter(request, "flashSupport");
                Integer h5Support = HttpUtil.getIntParameter(request, "h5Support");
                Integer progressiveSupport = HttpUtil.getIntParameter(request, "progressiveSupport");
                String progressiveCode = request.getParameter("progressiveCode");

                boolean result = gameService.add(gameName, gameCode, typeId, platformId, imgUrl, sequence, display, flashSupport, h5Support, progressiveSupport, progressiveCode);
                if(result) {
                    adminUserLogJob.logAddGame(uEntity, request, gameName);
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

    @RequestMapping(value = WUC.GAME_MOD, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_MOD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_MOD;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");
                String gameName = request.getParameter("gameName");
                String gameCode = request.getParameter("gameCode");
                Integer typeId = HttpUtil.getIntParameter(request, "typeId");
                Integer platformId = HttpUtil.getIntParameter(request, "platformId");
                String imgUrl = request.getParameter("imgUrl");
                Integer sequence = HttpUtil.getIntParameter(request, "sequence");
                Integer display = HttpUtil.getIntParameter(request, "display");
                Integer flashSupport = HttpUtil.getIntParameter(request, "flashSupport");
                Integer h5Support = HttpUtil.getIntParameter(request, "h5Support");
                Integer progressiveSupport = HttpUtil.getIntParameter(request, "progressiveSupport");
                String progressiveCode = request.getParameter("progressiveCode");

                boolean result = gameService.update(id, gameName, gameCode, typeId, platformId, imgUrl, sequence, display, flashSupport, h5Support, progressiveSupport, progressiveCode);
                if(result) {
                    adminUserLogJob.logUpdateGame(uEntity, request, gameName);
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

    @RequestMapping(value = WUC.GAME_DEL, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_DEL(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_DEL;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");

                Game game = gameService.getById(id);
                if (game == null) {
                    json.set(1, "2-3001");
                }
                else {
                    boolean result = gameService.deleteById(id);
                    if(result) {
                        adminUserLogJob.logDelGame(uEntity, request, game.getGameName());
                        json.set(0, "0-5");
                    } else {
                        json.set(1, "1-5");
                    }
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

    @RequestMapping(value = WUC.GAME_MOD_DISPLAY, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_MOD_DISPLAY(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_MOD_DISPLAY;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");
                int display = HttpUtil.getIntParameter(request, "display");

                Game game = gameService.getById(id);
                if (game == null) {
                    json.set(1, "2-3001");
                }
                else {
                    boolean result = gameService.updateDisplay(id, display);
                    if(result) {
                        adminUserLogJob.logUpdateGameDisplay(uEntity, request, game.getGameName(), display);
                        json.set(0, "0-5");
                    } else {
                        json.set(1, "1-5");
                    }
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

    @RequestMapping(value = WUC.GAME_CHECK_GAMENAME_EXIST, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_CHECK_GAMENAME_EXIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String gameName = request.getParameter("gameName");
        Integer id = HttpUtil.getIntParameter(request, "id");

        Game game = gameService.getByGameName(gameName);

        String isExist;
        if (game == null) {
            isExist = "true";
        }
        else {
            if (id != null && game.getId() == id) {
                isExist = "true";
            }
            else {
                isExist = "false";
            }
        }

        HttpUtil.write(response, isExist, HttpUtil.json);
    }

    @RequestMapping(value = WUC.GAME_CHECK_GAMECODE_EXIST, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_CHECK_GAMECODE_EXIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String gameCode = request.getParameter("gameCode");
        Integer id = HttpUtil.getIntParameter(request, "id");

        Game game = gameService.getByGameCode(gameCode);

        String isExist;
        if (game == null) {
            isExist = "true";
        }
        else {
            if (id != null && game.getId() == id) {
                isExist = "true";
            }
            else {
                isExist = "false";
            }
        }

        HttpUtil.write(response, isExist, HttpUtil.json);
    }

    @RequestMapping(value = WUC.GAME_PLATFORM_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_PLATFORM_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_PLATFORM_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                List<SysPlatform> sysPlatforms = sysPlatformService.listAll();

                Iterator<SysPlatform> iterator = sysPlatforms.iterator();
                while(iterator.hasNext()) {
                    SysPlatform next = iterator.next();
                    if (next.getId() != 4 && next.getId() != 11 && next.getId() != 12 && next.getId() != 13) {
                        iterator.remove();
                    }
                }

                if(CollectionUtils.isNotEmpty(sysPlatforms)) {
                    json.accumulate("data", sysPlatforms);
                } else {
                    json.accumulate("data", "[]");
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

    @RequestMapping(value = WUC.GAME_PLATFORM_MOD_STATUS, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_PLATFORM_MOD_STATUS(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_PLATFORM_MOD_STATUS;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                int id = HttpUtil.getIntParameter(request, "id");
                int status = HttpUtil.getIntParameter(request, "status");

                boolean result = sysPlatformService.updateStatus(id, status);
                if(result) {
                    adminUserLogJob.logPlatformModStatus(uEntity, request, id, status);
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

    @RequestMapping(value = WUC.GAME_BALANCE, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_BALANCE(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_BALANCE;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            int platformId = HttpUtil.getIntParameter(request, "platformId");
            int userId = HttpUtil.getIntParameter(request, "userId");
            UserGameAccount account = uGameAccountDao.get(userId, platformId);
            Map<String, Object> data = new HashMap<>();
            if (account == null) {
                data.put("balance", 0);
                json.accumulate("data", data);
                json.set(0, "0-3");
            }
            else {
                Double balance = null;
                if (account != null) {
                    if (platformId == 11) {
                        balance = ptAPI.playerBalance(json, account.getUsername());

                    }
                    else if (platformId == 4) {
                        balance = agAPI.getBalance(json, account.getUsername(), account.getPassword());
                    }
                }

                if (balance != null) {
                    data.put("balance", balance);
                    json.accumulate("data", data);
                    json.set(0, "0-3");
                }
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

    @RequestMapping(value = WUC.GAME_BETS_LIST, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_BETS_LIST(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_BETS_LIST;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            if (super.hasAccess(uEntity, actionKey)) {
                String keyword = request.getParameter("keyword");
                String username = request.getParameter("username");
                Integer platformId = HttpUtil.getIntParameter(request, "platformId");
                String minTime = request.getParameter("minTime");
                if (StringUtil.isNotNull(minTime)) {
                    minTime += " 00:00:00";
                }
                String maxTime = request.getParameter("maxTime");
                if (StringUtil.isNotNull(maxTime)) {
                    maxTime += " 00:00:00";
                }
                Double minMoney = HttpUtil.getDoubleParameter(request, "minBetsMoney");
                Double maxMoney = HttpUtil.getDoubleParameter(request, "maxBetsMoney");
                Double minPrizeMoney = HttpUtil.getDoubleParameter(request, "minPrizeMoney");
                Double maxPrizeMoney = HttpUtil.getDoubleParameter(request, "maxPrizeMoney");
                String gameCode = request.getParameter("gameCode");
                String gameType = request.getParameter("gameType");
                String gameName = request.getParameter("gameName");
                int start = HttpUtil.getIntParameter(request, "start");
                int limit = HttpUtil.getIntParameter(request, "limit");
                PageList pList = gameBetsService.search(keyword, username, platformId, minTime, maxTime, minMoney, maxMoney,
                        minPrizeMoney, maxPrizeMoney, gameCode, gameType, gameName, start, limit);
                if(pList != null) {
                    double[] totalMoney = gameBetsService.getTotalMoney(keyword, username, platformId, minTime, maxTime, minMoney, maxMoney,
                            minPrizeMoney, maxPrizeMoney, gameCode, gameType, gameName);
                    json.accumulate("totalMoney", totalMoney[0]);
                    json.accumulate("totalPrizeMoney", totalMoney[1]);
                    json.accumulate("totalCount", pList.getCount());
                    json.accumulate("data", pList.getList());
                } else {
                    json.accumulate("totalMoney", 0);
                    json.accumulate("totalPrizeMoney", 0);
                    json.accumulate("totalCount", 0);
                    json.accumulate("data", "[]");
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

    @RequestMapping(value = WUC.GAME_BETS_GET, method = { RequestMethod.POST })
    @ResponseBody
    public void GAME_BETS_GET(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String actionKey = WUC.GAME_BETS_GET;
        long t1 = System.currentTimeMillis();
        WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
        AdminUser uEntity = super.getCurrUser(session, request, response);
        if (uEntity != null) {
            int id = HttpUtil.getIntParameter(request, "id");
            GameBetsVO gameBetsVO = gameBetsService.getById(id);
            if(gameBetsVO != null) {
                json.accumulate("data", gameBetsVO);
            } else {
                json.accumulate("data", "{}");
            }
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
}
