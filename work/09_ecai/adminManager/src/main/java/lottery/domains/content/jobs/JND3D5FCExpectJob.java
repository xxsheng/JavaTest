// package lottery.domains.content.jobs;
//
// import javautils.date.Moment;
// import lottery.domains.content.biz.LotteryOpenCodeService;
// import lottery.domains.content.biz.LotteryOpenTimeService;
// import lottery.domains.content.biz.LotteryService;
// import lottery.domains.content.entity.Lottery;
// import lottery.domains.content.entity.LotteryOpenCode;
// import lottery.domains.content.entity.LotteryOpenTime;
// import lottery.domains.pool.LotteryDataFactory;
// import lottery.domains.utils.lottery.open.LotteryOpenUtil;
// import lottery.domains.utils.lottery.open.OpenTime;
// import org.apache.commons.collections.CollectionUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.util.List;
//
// /**
//  * 加拿大3.5分彩期数任务
//  * 每天凌晨4点开始设置最新的期数，必须要在
//  * 每10秒检查一次当前期号是否小于最后采集期号
//  * Created by Nick on 2017-09-04.
//  */
// @Component
// public class JND3D5FCExpectJob {
//     private static final Logger log = LoggerFactory.getLogger(JND3D5FCExpectJob.class);
//     private static volatile boolean isRunning = false; // 标识任务是否正在运行
//
//     @Autowired
//     private LotteryOpenCodeService lotteryOpenCodeService;
//     @Autowired
//     private LotteryService lotteryService;
//     @Autowired
//     private LotteryOpenTimeService lotteryOpenTimeService;
//     @Autowired
//     private LotteryOpenUtil lotteryOpenUtil;
//     @Autowired
//     private LotteryDataFactory dataFactory;
//
//     @Scheduled(cron = "0 0 4 * * *")
//     public void scheduleDaily (){
//         synchronized (JND3D5FCExpectJob.class) {
//             try {
//                 log.info("加拿大3.5分彩期数设置任务开始");
//
//                 checkForDaily();
//
//                 log.info("加拿大3.5分彩期数设置任务完成");
//             } catch (Exception e) {
//                 log.error("加拿大3.5分彩期数设置任务出错", e);
//             } finally {
//                 isRunning = false;
//             }
//         }
//     }
//
//     @Scheduled(cron = "0,20,40 * * * * *")
//     public void scheduleReal (){
//         synchronized (JND3D5FCExpectJob.class) {
//             if (isRunning == true) {
//                 // 任务正在运行，本次中断
//                 return;
//             }
//             isRunning = true;
//         }
//
//         synchronized (JND3D5FCExpectJob.class) {
//             try {
//                 // log.info("加拿大3.5分彩期数自动校正任务开始");
//                 checkForReal();
//                 // log.info("加拿大3.5分彩期数自动校正任务结束");
//             } catch (Exception e) {
//                 log.error("加拿大3.5分彩期数自动校正任务出错", e);
//             } finally {
//                 isRunning = false;
//             }
//         }
//     }
//
//     private void checkForDaily() {
//         Lottery lottery = dataFactory.getLottery("jnd3d5fc");
//         if (lottery.getSelf() == 1) {
//             return;
//         }
//
//         String yesterday = new Moment().subtract(1, "days").toSimpleDate();
//         String yesterdayStart = yesterday + " 00:00:00";
//         String todayStart = new Moment().toSimpleDate() + " 00:00:00";
//
//         // 昨天共有多少期
//         int yesterdayTotal = lotteryOpenCodeService.countByInterfaceTime(lottery.getShortName(), yesterdayStart, todayStart);
//         if (yesterdayTotal <= 0) {
//             return;
//         }
//         if (yesterdayTotal <= 370 || yesterdayTotal >= 400) {
//             return;
//         }
//
//         // 昨天第一期
//         LotteryOpenCode yesterdayFirstCode = lotteryOpenCodeService.getFirstExpectByInterfaceTime(lottery.getShortName(), yesterdayStart, todayStart);
//         if (yesterdayFirstCode == null) {
//             return;
//         }
//
//         // 设置彩票期数
//         lotteryService.updateTimes(lottery.getId(), yesterdayTotal);
//
//         // 设置昨天第一期
//         String yesterdayFirstEexpect = yesterdayFirstCode.getExpect();
//         String newExpect = (Integer.valueOf(yesterdayFirstEexpect) - 1) + "";
//         LotteryOpenTime Jnd3d5fcRef = lotteryOpenTimeService.getByLottery(lottery.getShortName()+"_ref");
//         Jnd3d5fcRef.setStartTime(yesterday);
//         Jnd3d5fcRef.setStopTime(yesterday);
//         Jnd3d5fcRef.setOpenTime(yesterday);
//         Jnd3d5fcRef.setExpect(newExpect);
//         lotteryOpenTimeService.update(Jnd3d5fcRef);
//     }
//
//     /**
//      * 实时检查当前期号，如果最近10期抓取时间间隔都在3-3.5分钟之内，则调整为最近的期数
//      */
//     private void checkForReal() {
//         Lottery lottery = dataFactory.getLottery("jnd3d5fc");
//         if (lottery.getSelf() == 1) {
//             return;
//         }
//
//         // 获取当前期号及时间
//         OpenTime currOpenTime = lotteryOpenUtil.getCurrOpenTime(lottery.getId());
//         if (currOpenTime == null) {
//             return;
//         }
//
//         Moment now = new Moment();
//         Moment stopMoment = new Moment().fromTime(currOpenTime.getStopTime());
//         int surplusSeconds = stopMoment.difference(now, "second");
//         // if (surplusSeconds < 180) {
//         //     return;
//         // }
//
//         // 最近抓取
//         List<LotteryOpenCode> latestCodes = lotteryOpenCodeService.getLatest(lottery.getShortName(), 10);
//         if (CollectionUtils.isEmpty(latestCodes) || latestCodes.size() != 10) {
//             return;
//         }
//
//         // 如果最后一期抓取时间超过5分钟以上，则不再自动设置
//         Moment lastCaptureMoment = new Moment().fromTime(latestCodes.get(0).getTime());
//         int lastCaptureDiffSecs = now.difference(lastCaptureMoment, "second");
//         if (lastCaptureDiffSecs >= 300) {
//             return;
//         }
//
//         int normalCount = 0;
//         for (int i = 0; i < latestCodes.size() - 1; i++) {
//             LotteryOpenCode newer = latestCodes.get(i);
//             LotteryOpenCode older = latestCodes.get(i + 1);
//
//             Moment newerCapture = new Moment().fromTime(newer.getTime());
//             Moment olderCapture = new Moment().fromTime(older.getTime());
//
//             int captureDiffSecs = newerCapture.difference(olderCapture, "second");
//
//             if (captureDiffSecs >= 190 && captureDiffSecs <= 250) {
//                 normalCount++;
//             }
//         }
//
//         if (normalCount != latestCodes.size() -1) {
//             return;
//         }
//
//         int currentExpectInt = Integer.valueOf(currOpenTime.getExpect());
//         int lastExpectInt = Integer.valueOf(latestCodes.get(0).getExpect());
//
//         int diffExpect = currentExpectInt - lastExpectInt;
//
//         if (diffExpect == 1) {
//             // if (surplusSeconds >= 180 && lastCaptureDiffSecs >= 180) {
//             //     int addCount = 1;
//             //     log.info(lottery.getShowName()  + "期号校正，自动加{}，当前期：{}，最后抓取期：{}", addCount, currentExpectInt, lastExpectInt);
//             //     updateRefExpect(addCount, lottery);
//             // }
//             return;
//         }
//         if (diffExpect == 2) {
//             if (lastCaptureDiffSecs >= 120) {
//                 // int addCount = -1;
//                 // log.info(lottery.getShowName()  + "期号校正，自动加{}，当前期：{}，最后抓取期：{}", addCount, currentExpectInt, lastExpectInt);
//                 // updateRefExpect(addCount, lottery);
//                 return;
//             }
//         }
//
//         int addCount = -diffExpect + 1;
//         log.info(lottery.getShowName()  + "期号校正，自动加{}，当前期：{}，最后抓取期：{}", addCount, currentExpectInt, lastExpectInt);
//         updateRefExpect(addCount, lottery);
//     }
//
//     private void updateRefExpect(int addCount, Lottery lottery) {
//         LotteryOpenTime Jnd3d5fcRef = lotteryOpenTimeService.getByLottery(lottery.getShortName()+"_ref");
//
//         int currentExpecInt = Integer.valueOf(Jnd3d5fcRef.getExpect());
//
//         String newExpect = (currentExpecInt + addCount) + "";
//
//         Jnd3d5fcRef.setExpect(newExpect);
//
//         lotteryOpenTimeService.update(Jnd3d5fcRef);
//     }
// }
