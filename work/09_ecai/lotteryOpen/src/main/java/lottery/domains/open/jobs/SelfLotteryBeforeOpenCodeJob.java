package lottery.domains.open.jobs;

import javautils.date.Moment;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.utils.open.OpenTime;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 自主彩往期开奖号码，期终开上期之前未生成开奖号码的，每10分钟一次，始终只检查过去2天加今天一共3天的期数数据
 * Created by Nick on 2016/11/22.
 */
@Component
public class SelfLotteryBeforeOpenCodeJob extends AbstractSelfLotteryOpenCodeJob{
    private final static Logger log = LoggerFactory.getLogger(SelfLotteryBeforeOpenCodeJob.class);
    private static boolean isRuning = false;
    private static final int CHECK_DAYS = 1; // 检查过去几天的

    @Autowired
    private LotteryOpenCodeDao openCodeDao;

    @Scheduled(cron = "0 0/10 * * * ?")
    // @PostConstruct
    public void generate() {
        synchronized (SelfLotteryBeforeOpenCodeJob.class) {
            if (isRuning == true) {
                return;
            }
            isRuning = true;
        }

        try {
            // 生成开奖号码
            List<Lottery> selfLotteries = getSelfLotteries();
            generate(selfLotteries);
        } catch (Exception e) {
            log.error("自主彩生成开奖号码异常", e);
        } finally {
            isRuning = false;
        }
    }

    private void generate(List<Lottery> selfLotteries) {
        if (CollectionUtils.isEmpty(selfLotteries)) {
            return;
        }

        for (Lottery selfLottery : selfLotteries) {
            generateBefore(selfLottery);
        }
    }

    /**
     * 往期
     */
    private void generateBefore(Lottery selfLottery) {
        OpenTime lastOpenTime = lotteryOpenUtil.getLastOpenTime(selfLottery.getId());
        if (lastOpenTime == null) {
            return;
        }

        String lastExpect = lastOpenTime.getExpect();

        // 之前n期期号
        HashSet<String> beforeExpects = new HashSet<>();

        if (selfLottery.getType() == 4) {
            // 3D类只补开昨天一天
            OpenTime openTime = lotteryOpenUtil.substractOneExpect(selfLottery.getId(), lastExpect);
            if (openTime != null) {
                beforeExpects.add(openTime.getExpect());
            }
        }
        else {

            for (int i = 0; i < CHECK_DAYS+1; i++) {
                Moment moment = new Moment().add(-i, "days");

                List<OpenTime> openTimeList = lotteryOpenUtil.getOpenDateList(selfLottery.getId(), moment.toSimpleDate());
                for (OpenTime openTime : openTimeList) {

                    if (openTime.getExpect().compareTo(lastExpect) < 0) {
                        // 必须小于上期
                        beforeExpects.add(openTime.getExpect());
                    }
                }
            }
        }

        // 找出最小的那一期
        Object[] obj = beforeExpects.toArray();
        Arrays.sort(obj);
        String minExpect = obj[0].toString();

        // 之前已开期数
        List<LotteryOpenCode> beforeOpenCodes = openCodeDao.listAfter(selfLottery.getShortName(), minExpect);
        // 之前已开期数列表
        HashSet<String> savedExpects = new HashSet<>();
        for (LotteryOpenCode beforeOpenCode : beforeOpenCodes) {
            savedExpects.add(beforeOpenCode.getExpect());
        }

        // 去重复后就是没有生成的
        beforeExpects.removeAll(savedExpects);

        for (String beforeExpect : beforeExpects) {
            long start = System.currentTimeMillis();
            // 生成开奖号码
            LotteryOpenCode openCode = generateOpenCode(selfLottery, beforeExpect, null);
            if (openCode != null) {
                long spend = System.currentTimeMillis() - start;
                log.debug("补开{}第{}期开奖号码{},耗时{}", selfLottery.getShowName(), beforeExpect, openCode.getCode(), spend);
            }
        }

    }


}
