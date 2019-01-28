package lottery.domains.content.jobs;

import javautils.ObjectUtil;
import javautils.date.Moment;
import lottery.domains.content.biz.UserBalanceSnapshotService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.UserBalanceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Nick on 2017-07-03.
 */
@Component
public class UserBalanceSnapshotJob {
    private static final Logger log = LoggerFactory.getLogger(UserBalanceSnapshotJob.class);
    private static volatile boolean isRunning = false; // 标识任务是否正在运行

    @Autowired
    private UserDao uDao;
    @Autowired
    private UserBalanceSnapshotService uBalanceSnapshotService;

    @Scheduled(cron = "59 59 23 * * ?") // 每天零点零分零秒
    // @PostConstruct
    public void syncOrder() {
        synchronized (UserBalanceSnapshotJob.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }
        try {
            log.debug("开始执行平台余额快照");

            // 开始同步注单
            start();

            log.debug("完成执行平台余额快照");
        } catch (Exception e) {
            log.error("执行平台余额快照出错", e);
        } finally {
            isRunning = false;
        }
    }

    private void start() {
        // 统计此时平台余额
        Object[] balance = uDao.getTotalMoney();
        double totalMoney = ObjectUtil.toDouble(balance[0]);
        double lotteryMoney = ObjectUtil.toDouble(balance[1]);

        // 获取当前日期，将当前时间减去1小时，保证是昨天
        String time = new Moment().toSimpleTime();

        // 保存数据
        save(totalMoney, lotteryMoney, time);
    }

    private void save(double totalMoney, double lotteryMoney, String time) {
        UserBalanceSnapshot snapshot = new UserBalanceSnapshot();
        snapshot.setTotalMoney(totalMoney);
        snapshot.setLotteryMoney(lotteryMoney);
        snapshot.setTime(time);
        uBalanceSnapshotService.add(snapshot);
    }
}
