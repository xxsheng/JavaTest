package lottery.domains.open.jobs;

import javautils.date.Moment;
import javautils.email.SpringMailUtil;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.vo.config.MailConfig;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Nick on 2016/11/11
 */
@Component
public class MailJob {
    private static final Logger log = LoggerFactory.getLogger(MailJob.class);

    private BlockingQueue<String> logQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<String> warningQueue = new LinkedBlockingDeque<>();

    @Autowired
    private DataFactory dataFactory;

    private static boolean isRunning = false;
    private static boolean isRunningWarning = false;
    private static Object warningLock = new Object();

    @Scheduled(cron = "0/10 * * * * *")
    public void run() {
        synchronized (MailJob.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }
        try {
            send();
        } finally {
            isRunning = false;
        }
    }
    @Scheduled(cron = "0/8 * * * * *")
    public void runWarning() {
        synchronized (warningLock) {
            if (isRunningWarning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunningWarning = true;
        }
        try {
            sendWarning();
        } finally {
            isRunningWarning = false;
        }
    }

    private void send() {
        if(logQueue != null && logQueue.size() > 0) {
            try {
                List<String> msgs = new LinkedList<>();
                logQueue.drainTo(msgs, 5);
                if (CollectionUtils.isNotEmpty(msgs)) {
                    send(msgs, null);
                }
            } catch (Exception e) {
                log.error("发送邮件错误", e);
            }
        }
    }

    private void sendWarning() {
        if(warningQueue != null && warningQueue.size() > 0) {
            try {
                List<String> msgs = new LinkedList<>();
                warningQueue.drainTo(msgs, 5);
                if (CollectionUtils.isNotEmpty(msgs)) {
                    send(msgs, "nickathome2020@gmail.com");
                }
            } catch (Exception e) {
                log.error("发送邮件错误", e);
            }
        }
    }

    private void add(String message) {
        logQueue.offer(message);
    }

    public void addWarning(String message) {
        warningQueue.offer(message);
    }

    public void sendOpen(UserBets userBets) {
        try {
            UserVO user = dataFactory.getUser(userBets.getUserId());
            String username = user == null ? "未知" : user.getUsername();
            Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
            String lotteryName = lottery.getShowName();
            int amount = Double.valueOf(userBets.getMoney()).intValue();
            int prizeAmount = Double.valueOf(userBets.getPrizeMoney()).intValue();
            String expect = userBets.getExpect();

            LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
            if (rule == null) return;

            LotteryPlayRulesGroup group = dataFactory.getLotteryPlayRulesGroup(lottery.getId(), rule.getGroupId());
            if (group == null) return;

            String method = group.getName() + rule.getName();
            String model = getModel(userBets.getModel());
            int multiple = userBets.getMultiple();
            int nums = userBets.getNums();
            String time = userBets.getTime();
            String openTime = new Moment().toSimpleTime();
            String billNo = userBets.getBillno();

            Object[] values = {username, amount, prizeAmount, lotteryName, expect, method, model, multiple, nums, time, openTime, billNo};
            String message = String.format("用户彩票中奖提醒；用户名：%s；投注金额：%s；中奖金额：%s；彩种：%s；期号：%s；玩法：%s；模式：%s；倍数：%s；注数：%s；投注时间：%s；开奖时间：%s；注单号：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误", e == null ? "" : e.getMessage());
        }
    }

    public void sendExpectExceedPrize(UserBets userBets, double beforePrize, double afterPrize) {
        try {
            UserVO user = dataFactory.getUser(userBets.getUserId());
            String username = user == null ? "未知" : user.getUsername();
            Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
            String lotteryName = lottery.getShowName();
            int amount = Double.valueOf(userBets.getMoney()).intValue();
            int beforePrizeAmount = Double.valueOf(beforePrize).intValue();
            int afterPrizeAmount = Double.valueOf(afterPrize).intValue();
            String expect = userBets.getExpect();

            LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
            if (rule == null) return;

            LotteryPlayRulesGroup group = dataFactory.getLotteryPlayRulesGroup(lottery.getId(), rule.getGroupId());
            if (group == null) return;

            String method = group.getName() + rule.getName();
            String model = getModel(userBets.getModel());
            int multiple = userBets.getMultiple();
            int nums = userBets.getNums();
            String time = userBets.getTime();
            String openTime = new Moment().toSimpleTime();
            String billNo = userBets.getBillno();

            Object[] values = {username, amount, beforePrizeAmount, afterPrizeAmount, lotteryName, expect, method, model, multiple, nums, time, openTime, billNo};
            String message = String.format("用户彩票奖金超出清除提醒；用户名：%s；投注金额：%s；原中奖金额：%s；清除后中奖金额：%s；彩种：%s；期号：%s；玩法：%s；模式：%s；倍数：%s；注数：%s；投注时间：%s；开奖时间：%s；注单号：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误", e == null ? "" : e.getMessage());
        }
    }

    public static String getModel(String model) {
        if("yuan".equals(model)) return "2元";
        if("jiao".equals(model)) return "2角";
        if("fen".equals(model)) return "2分";
        if("li".equals(model)) return "2厘";
        if("1yuan".equals(model)) return "1元";
        if("1jiao".equals(model)) return "1角";
        if("1fen".equals(model)) return "1分";
        if("1li".equals(model)) return "1厘";
        return "未知";
    }

    private void send(List<String> msgs, String email) {
        List<String> receiveMails;
        if (StringUtils.isEmpty(email)) {
            receiveMails = dataFactory.getMailConfig().getReceiveMails();
            if (CollectionUtils.isEmpty(receiveMails)) {
                return;
            }
        }
        else {
            receiveMails = new ArrayList<>();
            receiveMails.add(email);
        }

        try {
            MailConfig mailConfig = dataFactory.getMailConfig();
            SpringMailUtil mailUtil = new SpringMailUtil(mailConfig.getUsername(), mailConfig.getPersonal(), mailConfig.getPassword(), mailConfig.getHost());
            for (String msg : msgs) {
                for (String receiveMail : receiveMails) {
                    mailUtil.send(receiveMail, "提醒", msg);
                    Thread.sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            log.error("发送邮件错误");
        }
    }
}
