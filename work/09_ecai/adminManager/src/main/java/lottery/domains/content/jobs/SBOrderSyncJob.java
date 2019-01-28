// package lottery.domains.content.jobs;
//
// import admin.domains.jobs.MailJob;
// import com.alibaba.fastjson.JSON;
// import javautils.date.Moment;
// import lottery.domains.content.api.sb.Win88SBAPI;
// import lottery.domains.content.api.sb.Win88SBSportBetLogResult;
// import lottery.domains.content.biz.UserGameReportService;
// import lottery.domains.content.biz.UserHighPrizeService;
// import lottery.domains.content.dao.GameBetsDao;
// import lottery.domains.content.dao.SysConfigDao;
// import lottery.domains.content.entity.GameBets;
// import lottery.domains.content.entity.SysConfig;
// import lottery.domains.content.entity.SysPlatform;
// import lottery.domains.content.entity.UserGameAccount;
// import lottery.domains.content.global.Global;
// import lottery.domains.pool.LotteryDataFactory;
// import org.apache.commons.collections.CollectionUtils;
// import org.apache.commons.lang.StringUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
//
// /**
//  * Created by Nick on 2016/12/27.
//  */
// @Component
// public class SBOrderSyncJob {
//     private static final Logger log = LoggerFactory.getLogger(SBOrderSyncJob.class);
//     private static volatile boolean isRunning = false; // 标识任务是否正在运行
//     private static final String SYS_CONFIG_KEY = "SB_LAST_VERSION_KEY";
//     private static final String SYS_CONFIG_GROUP = "GAME";
//
//     @Autowired
//     private SysConfigDao sysConfigDao;
//     @Autowired
//     private Win88SBAPI win88SBAPI;
//     @Autowired
//     private GameBetsDao gameBetsDao;
//     @Autowired
//     private UserGameReportService uGameReportService;
//     @Autowired
//     private UserHighPrizeService highTimesService;
//     @Autowired
//     private MailJob mailJob;
//     @Autowired
//     private LotteryDataFactory dataFactory;
//
//     @Scheduled(cron = "0 2,7,12,17,22,27,32,37,42,47,52,57 * * * ?")
//     // @Scheduled(cron = "0/10 * * * * ?")
//     public void syncOrder() {
//         synchronized (SBOrderSyncJob.class) {
//             if (isRunning == true) {
//                 // 任务正在运行，本次中断
//                 return;
//             }
//             isRunning = true;
//         }
//         try {
//             SysPlatform sysPlatform = dataFactory.getSysPlatform(Global.BILL_ACCOUNT_SB);
//             if (sysPlatform == null || sysPlatform.getStatus() != 0) {
//                 return;
//             }
//
//             log.debug("开始同步沙巴注单");
//
//             // 开始同步注单
//             startSync();
//
//             log.debug("同步沙巴注单完成");
//         } catch (Exception e) {
//             log.error("同步沙巴注单出错", e);
//         } finally {
//             isRunning = false;
//         }
//     }
//
//     private void startSync() throws Exception {
//         String lastVersionKey = getLastVersionKey();
//
//         // 获取注单
//         log.debug("获取沙巴注单,请求版本号{}", lastVersionKey);
//         Win88SBSportBetLogResult win88SBSportBetLogResult = win88SBAPI.sportBetLog(lastVersionKey);
//         if (win88SBSportBetLogResult == null || StringUtils.isEmpty(win88SBSportBetLogResult.getLastVersionKey())) {
//             log.error("获取沙巴注单时，返回记录为空或没有版本号，本次不作任何操作，请求版本号：" + lastVersionKey);
//             return;
//         }
//
//         if (CollectionUtils.isEmpty(win88SBSportBetLogResult.getData())) {
//             log.debug("获取沙巴注单记录为空,请求版本号{}，返回版本号{}", lastVersionKey, win88SBSportBetLogResult.getLastVersionKey());
//             saveLastVersionKey(win88SBSportBetLogResult.getLastVersionKey());
//             return;
//         }
//
//         log.debug("获取沙巴注单记录,时间段记录共计{}条,请求版本号{},返回版本号{}", win88SBSportBetLogResult.getData().size(), lastVersionKey, win88SBSportBetLogResult.getLastVersionKey());
//         log.debug("获取沙巴注单记录：{}", JSON.toJSONString(win88SBSportBetLogResult));
//
//         // List<GameBets> gameBets = transferResults(win88SBSportBetLogResult);
//         // if (CollectionUtils.isNotEmpty(gameBets)) {
//         //     for (GameBets gameBet : gameBets) {
//         //         log.debug("沙巴注单：{}", JSON.toJSONString(gameBet));
//         //     }
//         // }
//
//         // 保存注单
//         List<GameBets> gameBets = transferResults(win88SBSportBetLogResult);
//         if (CollectionUtils.isNotEmpty(gameBets)) {
//             saveOrders(gameBets);
//         }
//         saveLastVersionKey(win88SBSportBetLogResult.getLastVersionKey());
//     }
//
//     private void saveOrders(List<GameBets> gameBets) {
//         // 保存注单并更新报表
//         Iterator<GameBets> iterator = gameBets.iterator();
//         while (iterator.hasNext()) {
//             GameBets newGameBets = iterator.next();
//             if (newGameBets.getMoney() <=0 && newGameBets.getPrizeMoney() <= 0 && newGameBets.getProgressiveMoney() <= 0 && newGameBets.getProgressivePrize() <= 0) {
//                 continue;
//             }
//
//             boolean set;
//             GameBets oldGameBets = gameBetsDao.get(newGameBets.getUserId(), newGameBets.getPlatformId(), newGameBets.getBetsId(), newGameBets.getGameCode());
//             int oldStatus = -1;
//             if (oldGameBets == null) {
//                 // 保存注单
//                 gameBetsDao.save(newGameBets);
//                 set = true;
//             }
//             else {
//                 // 修改注单
//                 oldStatus = oldGameBets.getStatus();
//                 set = setOldValues(newGameBets, oldGameBets);
//                 if (set) {
//                     gameBetsDao.update(oldGameBets);
//                 }
//             }
//
//             if (set) {
//                 // 状态，-1：未知；1：完成；2：等待中；3：进行中；4：赢；5：输；6：平局；7：拒绝；8：退钱；9：取消；10：上半场赢；11：上半场输；12：和；
//
//                 // 更新报表，并添加大额中奖记录
//                 // 没有旧的注单，且新注单是完成状态，则更新报表
//                 if (oldGameBets == null && isCompletedStatus(newGameBets.getStatus())) {
//                     addReportAndAddHighPrize(newGameBets);
//                 }
//                 // 旧的注单是进行中的状态，且新注单是完成状态，则更新报表
//                 else if (oldGameBets != null && isCompletedStatus(oldStatus) == false && isCompletedStatus(newGameBets.getStatus())) {
//                     addReportAndAddHighPrize(newGameBets);
//                 }
//                 // 旧的注单是完成的状态，且新注单出现撤单，那么减去报表
//                 // else if (oldGameBets != null && isCompletedStatus(oldGameBets.getStatus()) && isCancelledStatus(newGameBets.getStatus())) {
//                 //     reduceOldReport(oldGameBets);
//                 // }
//             }
//         }
//     }
//
//     private boolean isCompletedStatus(int status) {
//         // 状态，-1：未知；1：完成；2：等待中；3：进行中；4：赢；5：输；6：平局；7：拒绝；8：退钱；9：取消；10：上半场赢；11：上半场输；12：和；
//         switch (status) {
//             case 4:
//             case 5:
//             case 6:
//             case 10:
//             case 11:
//                 return true;
//             default:
//                 return false;
//         }
//     }
//
//     private boolean isCancelledStatus(int status) {
//         // 状态，-1：未知；1：完成；2：等待中；3：进行中；4：赢；5：输；6：平局；7：拒绝；8：退钱；9：取消；10：上半场赢；11：上半场输；12：和；
//         switch (status) {
//             case 7:
//             case 8:
//             case 9:
//                 return true;
//             default:
//                 return false;
//         }
//     }
//
//     private void addReportAndAddHighPrize(GameBets newGameBets) {
//         // 更新报表，并添加大额中奖记录
//         String date = new Moment().toSimpleDate(); // 使用当前时间更新报表
//         double money = newGameBets.getMoney();
//         double prize = newGameBets.getPrizeMoney();
//         uGameReportService.update(newGameBets.getUserId(), newGameBets.getPlatformId(), money, prize, 0, 0, date);
//
//         try {
//             // 尝试添加大额中奖记录
//             highTimesService.addIfNecessary(newGameBets);
//         } catch (Exception e) {
//             log.error("添加大额中奖记录出错", e);
//         }
//
//         if (newGameBets.getPrizeMoney() >= dataFactory.getMailConfig().getOpen()) {
//             mailJob.sendOpen(dataFactory.getUser(newGameBets.getUserId()).getUsername(), newGameBets);
//         }
//     }
//
//     private boolean setOldValues(GameBets newGameBets, GameBets oldGameBets) {
//         // 不是同一个注单，返回
//         if (!oldGameBets.getBetsId().equalsIgnoreCase(newGameBets.getBetsId())) {
//             return false;
//         }
//         // 两个注单相同
//         if (newGameBets.equals(oldGameBets)) {
//             return false;
//         }
//
//         oldGameBets.setGameCode(newGameBets.getGameCode());
//         oldGameBets.setGameType(newGameBets.getGameType());
//         oldGameBets.setGameName(newGameBets.getGameName());
//         oldGameBets.setMoney(newGameBets.getMoney());
//         oldGameBets.setPrizeMoney(newGameBets.getPrizeMoney());
//         oldGameBets.setStatus(newGameBets.getStatus());
//         oldGameBets.setTime(newGameBets.getTime());
//         oldGameBets.setPrizeTime(newGameBets.getPrizeTime());
//         return true;
//     }
//
//     private void saveLastVersionKey(String lastVersionKey) {
//         // 修改上次时间
//         SysConfig config = new SysConfig();
//         config.setGroup(SYS_CONFIG_GROUP);
//         config.setKey(SYS_CONFIG_KEY);
//         config.setValue(lastVersionKey);
//         config.setDescription("沙巴上次获取记录的版本号");
//         sysConfigDao.update(config);
//     }
//
//     private List<GameBets> transferResults(Win88SBSportBetLogResult result) {
//         if (CollectionUtils.isEmpty(result.getData())) {
//             return null;
//         }
//         List<Win88SBSportBetLogResult.Data> datas = result.getData();
//
//         List<GameBets> gameBetsList = new ArrayList<>();
//
//         for (Win88SBSportBetLogResult.Data data : datas) {
//             UserGameAccount account = dataFactory.getGameAccount(data.getPlayerName(), Global.BILL_ACCOUNT_SB);
//             if (account == null) {
//                 log.error("发现未知注单，沙巴平台方账号" + data.getPlayerName() + ",沙巴注单号：" + data.getTransId());
//             }
//             else {
//                 GameBets bet = new GameBets(data, account);
//                 if (bet.getStatus() == -1) {
//                     log.error("发现未知注单状态，沙巴平台方账号" + data.getPlayerName() + ",沙巴注单号:" + data.getTransId() + ",沙巴注单状态：" + data.getTicketStatus());
//                 }
//                 else if (StringUtils.isEmpty(bet.getBetsId())) {
//                     log.error("发现未知注单ID：" + JSON.toJSONString(data));
//                 }
//                 else {
//                     if (bet.getStatus() != 2 && bet.getStatus() != 3) {
//                         // 沙巴数据的winLostDateTime不正确，这里设置系统当前时间
//                         bet.setPrizeTime(new Moment().toSimpleTime());
//                     }
//                     gameBetsList.add(bet);
//                 }
//             }
//         }
//
//         return gameBetsList;
//     }
//
//     private String getLastVersionKey() {
//         SysConfig sysConfig = sysConfigDao.get(SYS_CONFIG_GROUP, SYS_CONFIG_KEY);
//
//         if (sysConfig == null) {
//             sysConfig = new SysConfig();
//             sysConfig.setGroup(SYS_CONFIG_GROUP);
//             sysConfig.setKey(SYS_CONFIG_KEY);
//             sysConfig.setValue("0");
//             sysConfig.setDescription("沙巴上次获取记录的版本号");
//             sysConfigDao.save(sysConfig);
//         }
//
//         return sysConfig.getValue();
//     }
// }
