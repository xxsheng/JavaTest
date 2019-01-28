//package lottery.domains.content.jobs;
//
//import admin.domains.jobs.MailJob;
//import javautils.date.DateUtil;
//import javautils.date.Moment;
//import lottery.domains.content.api.pt.PTAPI;
//import lottery.domains.content.api.pt.PTPlayerGame;
//import lottery.domains.content.api.pt.PTPlayerGameResult;
//import lottery.domains.content.biz.UserGameReportService;
//import lottery.domains.content.biz.UserHighPrizeService;
//import lottery.domains.content.dao.GameBetsDao;
//import lottery.domains.content.dao.SysConfigDao;
//import lottery.domains.content.entity.GameBets;
//import lottery.domains.content.entity.SysConfig;
//import lottery.domains.content.entity.SysPlatform;
//import lottery.domains.content.entity.UserGameAccount;
//import lottery.domains.pool.LotteryDataFactory;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by Nick on 2016/12/27.
// */
//@Component
//public class PTOrderSyncJob {
//    private static final Logger log = LoggerFactory.getLogger(PTOrderSyncJob.class);
//    private static volatile boolean isRunning = false; // 标识任务是否正在运行
//    private static final String SYS_CONFIG_KEY = "PT_LAST_TIME";
//    private static final String SYS_CONFIG_GROUP = "GAME";
//
//    @Autowired
//    private SysConfigDao sysConfigDao;
//    @Autowired
//    private PTAPI ptAPI;
//    @Autowired
//    private GameBetsDao gameBetsDao;
//    @Autowired
//    private UserGameReportService uGameReportService;
//    @Autowired
//    private UserHighPrizeService highTimesService;
//    @Autowired
//    private MailJob mailJob;
//    @Autowired
//    private LotteryDataFactory dataFactory;
//
//    @Scheduled(cron = "0 0/3 * * * ?")
//    public void syncOrder() {
//        synchronized (PTOrderSyncJob.class) {
//            if (isRunning == true) {
//                // 任务正在运行，本次中断
//                return;
//            }
//            isRunning = true;
//        }
//
//        try {
//            SysPlatform sysPlatform = dataFactory.getSysPlatform(11);
//            if (sysPlatform == null || sysPlatform.getStatus() != 0) {
//                return;
//            }
//
//            log.debug("开始同步PT注单");
//
//            // 开始同步注单
//            startSync();
//
//            log.debug("完成同步PT注单");
//        } catch (Exception e){
//            log.error("同步PT注单出错", e);
//        } finally {
//            isRunning = false;
//        }
//    }
//
//    private void startSync() throws Exception{
//        List<String[]> times = getTimes();
//        if (CollectionUtils.isEmpty(times)) {
//            return;
//        }
//
//        for (String[] time : times) {
//            String startTime = time[0];
//            String endTime = time[1];
//
//            // 获取注单
//            log.debug("获取PT注单{}~{}时间段记录", startTime, endTime);
//            PTPlayerGame playerGame = ptAPI.playerGames(startTime, endTime);
//            if (playerGame == null) {
//                return;
//            }
//            if (playerGame.getPagination().getTotalCount() <= 0) {
//                log.debug("获取PT注单{}~{}时间段记录为空", startTime, endTime);
//                saveLastModTime(startTime);
//                continue;
//            }
//
//            // 保存注单
//            log.debug("获取PT注单{}~{}时间段记录共计{}条", startTime, endTime, playerGame.getPagination().getTotalCount());
//            List<PTPlayerGameResult> results = playerGame.getResult();
//            saveOrders(results);
//            saveLastModTime(startTime);
//        }
//    }
//
//    private void saveOrders(List<PTPlayerGameResult> results) {
//        if (CollectionUtils.isNotEmpty(results)) {
//            // 转换注单
//            List<GameBets> gameBets = new ArrayList<>();
//            for (PTPlayerGameResult result : results) {
//                // 转换结果
//                GameBets bet = transferResult(result);
//                if (bet == null) {
//                    continue;
//                }
//
//                gameBets.add(bet);
//            }
//
//            // 保存注单并更新报表
//            Iterator<GameBets> iterator = gameBets.iterator();
//            while (iterator.hasNext()) {
//                GameBets next = iterator.next();
//                if (next.getMoney() <=0 && next.getPrizeMoney() <= 0 && next.getProgressiveMoney() <= 0 && next.getProgressivePrize() <= 0) {
//                    continue;
//                }
//
//                GameBets oldBets = gameBetsDao.get(next.getUserId(), next.getPlatformId(), next.getBetsId(), next.getGameCode());
//                if (oldBets != null) {
//                    iterator.remove();
//                }
//                else {
//                    // 保存注单
//                    gameBetsDao.save(next);
//                    // 更新报表
//                    String betTime = new Moment().fromTime(next.getTime()).toSimpleDate();
//                    double money = next.getMoney() + next.getProgressiveMoney();
//                    double prize = next.getPrizeMoney() + next.getProgressivePrize();
//                    uGameReportService.update(next.getUserId(), next.getPlatformId(), money, prize, 0, 0, betTime);
//
//                    try {
//                        // 尝试添加大额中奖记录
//                        highTimesService.addIfNecessary(next);
//                    } catch (Exception e) {
//                        log.error("添加大额中奖信息出错", e);
//                    }
//
//                    if (next.getPrizeMoney() >= dataFactory.getMailConfig().getOpen()) {
//                        mailJob.sendOpen(dataFactory.getUser(next.getUserId()).getUsername(), next);
//                    }
//                }
//            }
//        }
//    }
//
//    private void saveLastModTime(String time) {
//        // 修改上次时间
//        SysConfig config = new SysConfig();
//        config.setGroup(SYS_CONFIG_GROUP);
//        config.setKey(SYS_CONFIG_KEY);
//        config.setValue(time);
//        sysConfigDao.update(config);
//    }
//
//    private GameBets transferResult(PTPlayerGameResult result) {
//        UserGameAccount account = dataFactory.getGameAccount(result.getPLAYERNAME(), 11); // 11是PT
//        if (account == null) {
//            log.error("发现未知投注，PT平台方账号" + result.getPLAYERNAME());
//            return null;
//        }
//
//        GameBets bet = new GameBets(result, account);
//        return bet;
//    }
//
//    /**
//     * 第一位为起始时间
//     * 第二位为结束时间
//     */
//    private List<String[]> getTimes() {
//        SysConfig sysConfig = sysConfigDao.get(SYS_CONFIG_GROUP, SYS_CONFIG_KEY);
//
//        Moment ptLastTime = new Moment().fromTime(sysConfig.getValue());
//
//        Moment startMoment = ptLastTime.add(-2, "minutes");
//        String endTime = new Moment().toSimpleTime();
//
//        if (new Moment().difference(ptLastTime, "hour") > 1) {
//            String msg = "PT注单同步已经超过1个小时未获取到数据,请重启后台,上次同步时间:" + ptLastTime.toSimpleTime() + ",当时时间：" + new Moment().toSimpleTime();
//            log.warn(msg);
//            mailJob.addWarning(msg);
//        }
//
//        // 上次同步时间和现在相差多少毫秒
//        long mills = DateUtil.calcDate(endTime, startMoment.toSimpleTime());
//
//        // 计算相差多少个5分钟
//        long minutes = mills / 1000 / 60 / 5;
//        if (minutes <= 0) {
//            minutes = 1; // 至少要有1个
//        }
//
//        List<String[]> times = new ArrayList<>();
//
//        for (int i = 0; i < minutes; i++) {
//            // 每段查询间隔5分钟
//            String _startTime = startMoment.toSimpleTime();
//            String _endTime = startMoment.add(5, "minutes").toSimpleTime();
//
//            String[] time = {_startTime, _endTime};
//            times.add(time);
//        }
//
//        return times;
//    }
//}
