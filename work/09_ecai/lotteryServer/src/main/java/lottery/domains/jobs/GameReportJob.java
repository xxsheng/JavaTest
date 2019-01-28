package lottery.domains.jobs;

import com.alibaba.fastjson.JSON;
import javautils.date.DateUtil;
import javautils.http.HttpClientUtil;
import javautils.http.UrlParamUtils;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.ag.AGResult;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameReportDao;
import lottery.domains.content.dao.read.GameBetsReadDao;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameReport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameReportJob {

    private static final Logger log = LoggerFactory.getLogger(GameReportJob.class);

    private static final String AG_KEY_SEPARATOR_NEW = "&";
    @Autowired
    private AGAPI agAPI;

    @Autowired
    private UserDao uDao;

    @Autowired
    private UserGameReportDao uGameReportDao;

    @Autowired
    private GameBetsReadDao gameBetsReadDao;

    @Value("${ag.api_name}")
    private String api_name;
    @Value("${ag.api_pass}")
    private String api_pass;
    @Value("${ag.private_key}")
    private String private_key;
    @Value("${ag.giurl}")
    private String giurl;


    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {

        HashMap<String, String> params = new HashMap<>();
        params.put("api_name", api_name);
        params.put("api_pass", api_pass);
        params.put("private_key", private_key);

        String FromDate = DateUtil.getBeforeTimeByMinute(1).replaceAll(" ", "T");
        String ToDate = DateUtil.getCurrentTime().replaceAll(" ", "T");
//        String FromDate = "2018-08-29T11:29:00";
//        String ToDate = "2018-08-29T11:40:00";
        log.info(FromDate);
        log.info(ToDate);
        params.put("FromDate", FromDate);
        params.put("ToDate", ToDate);
        //游戏类型
        //live = 视讯
        //egame = 电子
        params.put("game_type", "live");

        AGResult result = postAg(params, "GetBetRecord");
        String info = result.getInfo();
        log.info("AG投注记录："+info);
        AgBean agBean = JSON.parseObject(info, AgBean.class);
        Integer result2 = agBean.getResult();
        //1：成功，0：失败
        if (result2.intValue() == 1) {

            String data = agBean.getData();
            HashMap hashMap = JSON.parseObject(data, HashMap.class);
            Object bet_data = hashMap.get("bet_data");

            List<AgBeanData> dataList = JSON.parseArray(bet_data.toString(), AgBeanData.class);
            for (AgBeanData dataLive : dataList) {


                String playName1 = dataLive.getPlayName();
                if(StringUtils.isEmpty(playName1)){
                    continue;
                }
                String playName = playName1.replaceAll(api_name,"");

                User user = uDao.getByUsername(playName);
                int userId = user.getId();
                int platformId = 4;
                String betAmount = dataLive.getBetAmount();//下注金额
                String betTime = dataLive.getBetTime();//下注时间
                String netAmount = dataLive.getNetAmount();//派彩结果（输赢结果）

                String time = DateUtil.dateToString(DateUtil.stringToDate(betTime));

                String billNo = dataLive.getBillNo();
                String gameType = dataLive.getGameType();
                String round = dataLive.getRound();
                String recalcuTime = dataLive.getRecalcuTime();

                GameBets gameBets = new GameBets();
                gameBets.setUserId(userId);
                gameBets.setPlatformId(platformId);
                gameBets.setBetsId(billNo);
                gameBets.setGameCode(round);
                gameBets.setGameType(gameType);
                gameBets.setGameName(gameType);
                gameBets.setMoney(Double.valueOf(betAmount));
                gameBets.setPrizeMoney(Double.valueOf(netAmount));
                gameBets.setStatus(1);
                gameBets.setTime(betTime);
                gameBets.setPrizeTime(recalcuTime);

                try {
                    gameBetsReadDao.save(gameBets);
                } catch (Exception e) {
                    log.info("订单号重复："+billNo);
                    continue;
                }
                log.info("订单号："+billNo);
                UserGameReport userGameReport1 = uGameReportDao.get(userId, platformId, time);
                if(userGameReport1==null){
                    UserGameReport userGameReport = new UserGameReport();
                    userGameReport.setUserId(userId);
                    userGameReport.setPlatformId(platformId);
                    userGameReport.setBillingOrder(Double.valueOf(betAmount));
                    userGameReport.setPrize(Double.valueOf(netAmount));
                    userGameReport.setTime(DateUtil.dateToString(DateUtil.stringToDate(betTime)));
                    uGameReportDao.save(userGameReport);
                }else{
                    userGameReport1.setBillingOrder(Double.valueOf(betAmount));
                    userGameReport1.setPrize(Double.valueOf(netAmount));
                    userGameReport1.setTransIn(0);
                    userGameReport1.setTransOut(0);
                    userGameReport1.setWaterReturn(0);
                    userGameReport1.setProxyReturn(0);
                    uGameReportDao.update(userGameReport1);
                }



            }
        }


    }

    private AGResult postAg(HashMap<String, String> params, String paramUrl) {
        try {
            // 请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "text/xml; utf-8=;charset=UTF-8");
            // 请求数据
            String paramsStr = UrlParamUtils.toUrlParam(params, AG_KEY_SEPARATOR_NEW, false);
            // 请求
            String url = giurl + "/" + paramUrl + "?" + paramsStr;
            String xml = HttpClientUtil.doPost(url);

            AGResult result = new AGResult();
            result.setInfo(xml);
            return result;
        } catch (Exception e) {
            log.error("连接AG发生异常，请求参数：" + JSON.toJSONString(params), e);
            return null;
        }
    }
}
