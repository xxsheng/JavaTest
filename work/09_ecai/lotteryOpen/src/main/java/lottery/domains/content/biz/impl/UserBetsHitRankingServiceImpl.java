package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserBetsHitRankingService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.UserBetsHitRankingDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBetsHitRanking;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Nick on 2017/2/27.
 */
@Service
public class UserBetsHitRankingServiceImpl implements UserBetsHitRankingService {
    private static final Logger log = LoggerFactory.getLogger(UserBetsHitRankingServiceImpl.class);

    private BlockingQueue<UserBets> userBetsQueue = new LinkedBlockingDeque<>();

    @Autowired
    private UserBetsHitRankingDao rankingDao;

    @Autowired
    private DataFactory dataFactory;

    @Autowired
    private UserDao uDao;

    @Autowired
    private DbServerSyncDao dbServerSyncDao;

    private static boolean isRunning = false;

    @Scheduled(cron = "0 0/5 * * * *")
    // @Scheduled(cron = "0/10 * * * * *")
    public void run() {
        synchronized (UserBetsHitRankingServiceImpl.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }
        try {
            add();
        } finally {
            isRunning = false;
        }
    }

    private void add() {
        if(userBetsQueue != null && userBetsQueue.size() > 0) {
            try {
                List<UserBets> adds = new LinkedList<>();
                userBetsQueue.drainTo(adds, 50);
                if (CollectionUtils.isNotEmpty(adds)) {
                    add(adds);
                }
            } catch (Exception e) {
                log.error("添加中奖排行榜错误", e);
            }
        }
    }

    private void add(List<UserBets> adds) {
        if (!dataFactory.getLotteryConfig().isAutoHitRanking()) {
            return;
        }
        int hitRankingSize = dataFactory.getLotteryConfig().getHitRankingSize();

        // 把奖金从大到小排序
        Collections.sort(adds, new Comparator<UserBets>() {
            @Override
            public int compare(UserBets o1, UserBets o2) {
                return o2.getPrizeTime().compareTo(o1.getPrizeTime());
            }
        });

        String today = new Moment().toSimpleDate() + " 00:00:00";
        String tomorrow = new Moment().add(1, "days").toSimpleDate() + " 00:00:00";

        // 先查看当天的数据是否满了10条
        List<Integer> todayIds = rankingDao.getIds(Global.BILL_ACCOUNT_LOTTERY, today, tomorrow);

        // 没有满10条，把本次的数据由大到小加到数据库，但不超过最大条数
        int currentSize = todayIds.size();
        if (currentSize < hitRankingSize) {
            for (int i = 0; i < adds.size(); i++) {
                if (currentSize >= hitRankingSize) {
                    break;
                }
                else {
                    UserBets userBets = adds.get(i);
                    UserBetsHitRanking ranking = convert(userBets);
                    rankingDao.add(ranking);
                    currentSize++;
                }
            }

            // 重新调整以往的数据
            List<Integer> idsByTimeDesc = rankingDao.getIdsByTimeDesc(hitRankingSize, Global.BILL_ACCOUNT_LOTTERY);
            if (CollectionUtils.isNotEmpty(idsByTimeDesc)) {
                rankingDao.delNotInIds(idsByTimeDesc, Global.BILL_ACCOUNT_LOTTERY);
            }

            dbServerSyncDao.update(DbServerSyncEnum.HIT_RANKING);

            log.info("中奖排行榜更新完成");
        }
        else {
            // 如果大于今天最小的奖金，那么加入该数据
            UserBetsHitRanking minRanking = rankingDao.getMinRanking(Global.BILL_ACCOUNT_LOTTERY, today, tomorrow);
            int addedCount = 0;
            for (UserBets add : adds) {
                boolean allowAdd = false;
                if (minRanking == null) {
                    allowAdd = true;
                }
                else {
                    BigDecimal prize = BigDecimal.valueOf(add.getPrizeMoney());
                    int prizeMoney = prize.intValue();
                    if (prizeMoney > minRanking.getPrizeMoney()) {
                        allowAdd = true;
                    }
                }

                if (allowAdd) {
                    UserBetsHitRanking ranking = convert(add);
                    rankingDao.add(ranking);
                    addedCount++;
                }
            }

            // 重新调整今天的数据
            if (addedCount > 0) {
                List<Integer> totalIds = rankingDao.getTotalIds(hitRankingSize, Global.BILL_ACCOUNT_LOTTERY, today, tomorrow);
                if (CollectionUtils.isNotEmpty(totalIds)) {
                    rankingDao.delNotInIds(totalIds, Global.BILL_ACCOUNT_LOTTERY, today, tomorrow);
                }

                // 重新调整以往的数据
                List<Integer> idsByTimeDesc = rankingDao.getIdsByTimeDesc(hitRankingSize, Global.BILL_ACCOUNT_LOTTERY);
                if (CollectionUtils.isNotEmpty(idsByTimeDesc)) {
                    rankingDao.delNotInIds(idsByTimeDesc, Global.BILL_ACCOUNT_LOTTERY);
                }

                dbServerSyncDao.update(DbServerSyncEnum.HIT_RANKING);

                log.info("中奖排行榜更新完成");
            }
        }
    }

    private UserBetsHitRanking convert(UserBets userBets) {

        Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
        if (lottery == null) {
            return null;
        }

        User user = uDao.getById(userBets.getUserId());
        if (user == null) {
            return null;
        }

        UserBetsHitRanking ranking = new UserBetsHitRanking();
        ranking.setName(lottery.getShowName());
        ranking.setUsername(user.getUsername());
        BigDecimal prize = BigDecimal.valueOf(userBets.getPrizeMoney());
        ranking.setPrizeMoney(prize.intValue());
        ranking.setTime(userBets.getPrizeTime());
        ranking.setPlatform(Global.BILL_ACCOUNT_LOTTERY);
        ranking.setCode(lottery.getShortName());
        ranking.setType(lottery.getType()+"");

        return ranking;
    }

    @Override
    public void addIfNecessary(UserBets userBets) {
        if (userBets.getPrizeMoney() != null && userBets.getPrizeMoney() >= 50000) {
            userBetsQueue.offer(userBets);
        }
    }
}
