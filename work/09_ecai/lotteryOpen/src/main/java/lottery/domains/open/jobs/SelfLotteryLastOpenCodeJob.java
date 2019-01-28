package lottery.domains.open.jobs;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.utils.open.OpenTime;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自主彩上期开奖号码，期终开上次的开号结果，每30秒一次
 * Created by Nick on 2016/11/22.
 */
@Component
public class SelfLotteryLastOpenCodeJob extends AbstractSelfLotteryOpenCodeJob{
    private final static Logger log = LoggerFactory.getLogger(SelfLotteryLastOpenCodeJob.class);

    private static boolean isRuning = false;

    @Scheduled(cron = "5,35 * * * * *")
    public void generate() {
        synchronized (SelfLotteryLastOpenCodeJob.class) {
            if (isRuning == true) {
                return;
            }
            isRuning = true;
        }

        try {
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
            generateLast(selfLottery);
        }
    }

    /**
     * 上期
     */
    private void generateLast(Lottery selfLottery) {
        OpenTime lastOpenTime = lotteryOpenUtil.getLastOpenTime(selfLottery.getId());
        if (lastOpenTime == null) {
            return;
        }
        String lastExpect = lastOpenTime.getExpect();

        // 看上期是否已经开奖
        LotteryOpenCode lastOpenCode = lotteryOpenCodeService.getByExcept(selfLottery.getShortName(), lastExpect);

        if (lastOpenCode != null) {
            return;
        }

        long start = System.currentTimeMillis();
        // 生成开奖号码
        LotteryOpenCode openCode = generateOpenCode(selfLottery, lastExpect, null);
        if (openCode != null) {
            long spend = System.currentTimeMillis() - start;
            log.debug("{}第{}期生成开奖号码为{},耗时{}", selfLottery.getShowName(), lastExpect, openCode.getCode(), spend);
        }
        else {
            log.error("{}第{}期生成开奖号码为失败", selfLottery.getShowName(), lastExpect);
        }
    }
}
