package admin.domains.jobs;

import javautils.date.Moment;
import javautils.email.SpringMailUtil;
import javautils.math.MathUtil;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.MailConfig;
import lottery.domains.pool.LotteryDataFactory;
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
    private LotteryDataFactory dataFactory;

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
                    send(msgs, "999wudi@gmail.com");
                }
            } catch (Exception e) {
                log.error("发送邮件错误", e);
            }
        }
    }

    private void add(String message) {
        logQueue.offer(message);
    }

    public void sendRecharge(String username, String paymentThird, UserRecharge userRecharge) {
        try {
            int amount = Double.valueOf(userRecharge.getMoney()).intValue();
            String payTime = userRecharge.getPayTime();
            String billNo = userRecharge.getBillno();

            Object[] values = {username, amount, payTime, paymentThird, billNo};
            String message = String.format("用户充值提醒；用户名：%s；金额：%s；时间：%s；渠道：%s；充值单号：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendRecharge", e == null ? "" : e.getMessage());
        }
    }

    public void sendSystemRecharge(String username, String operator, int type, int account, double amount, String remarks) {
        try {
            int amountInt = Double.valueOf(amount).intValue();
            String typeStr = getSystemRechargeType(type);
            String accountStr = getAccount(account);
            String time = new Moment().toSimpleTime();

            Object[] values = {username, amountInt, typeStr, operator, accountStr, time, remarks};
            String message = String.format("管理员加减钱提醒；用户名：%s；金额：%s；类型：%s；操作人：%s；账户：%s；操作时间：%s；备注：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendSystemRecharge", e == null ? "" : e.getMessage());
        }
    }

    public void sendLockUser(User user, String operator, int status, String remarks) {
        try {
            String username = user.getUsername();
            int totalMoney = Double.valueOf(user.getTotalMoney()).intValue();
            int lotteryMoney = Double.valueOf(user.getLotteryMoney()).intValue();
            String statusStr = getLockUserStatus(status);
            String time = new Moment().toSimpleTime();

            Object[] values = {username, statusStr, totalMoney, lotteryMoney, operator, time, remarks};
            String message = String.format("管理员冻结用户提醒；用户名：%s；状态：%s；主账户：%s；彩票账户：%s；操作人：%s；操作时间：%s；备注：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }

    public void sendUnLockUser(User user, String operator) {
        try {
            String username = user.getUsername();
            int totalMoney = Double.valueOf(user.getTotalMoney()).intValue();
            int lotteryMoney = Double.valueOf(user.getLotteryMoney()).intValue();
            String time = new Moment().toSimpleTime();

            Object[] values = {username, totalMoney, lotteryMoney, operator, time};
            String message = String.format("管理员解冻用户提醒；用户名：%s；主账户：%s；彩票账户：%s；操作人：%s；操作时间：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendUnLockUser", e == null ? "" : e.getMessage());
        }
    }

    public void sendLockTeam(String username, String operator, String remarks) {
        try {
            String time = new Moment().toSimpleTime();

            Object[] values = {username, operator, time, remarks};
            String message = String.format("管理员冻结团队提醒；用户名：%s；操作人：%s；操作时间：%s；备注：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }

    public void sendUnLockTeam(String username, String operator, String remarks) {
        try {
            String time = new Moment().toSimpleTime();

            Object[] values = {username, operator, time, remarks};
            String message = String.format("管理员解冻团队提醒；用户名：%s；操作人：%s；操作时间：%s；备注：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }

    public void sendProhibitTeamWithdraw(String username, String operator) {
        try {
            String time = new Moment().toSimpleTime();
            Object[] values = {username, operator, time};
            String message = String.format("管理员禁止团队取款提醒；用户名：%s；操作人：%s；操作时间：%s", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }
    
    public void sendAllowTeamWithdraw(String username, String operator) {
        try {
            String time = new Moment().toSimpleTime();
            Object[] values = {username, operator, time};
            String message = String.format("管理员开启团队取款提醒；用户名：%s；操作人：%s；操作时间：%s", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }
    
    public void sendAllowTeamTransfers(String username, String operator) {
        try {
            String time = new Moment().toSimpleTime();
            Object[] values = {username, operator, time};
            String message = String.format("管理员开启团队上下级转账提醒；用户名：%s；操作人：%s；操作时间：%s", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }
    
    
    
    public void sendProhibitTeamTransfers(String username, String operator) {
        try {
            String time = new Moment().toSimpleTime();
            Object[] values = {username, operator, time};
            String message = String.format("管理员禁止团队上下级转账提醒；用户名：%s；操作人：%s；操作时间：%s", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }
    
    public void sendAllowTeamPlatformTransfers(String username, String operator) {
        try {
            String time = new Moment().toSimpleTime();
            Object[] values = {username, operator, time};
            String message = String.format("管理员开启团队平台转账提醒；用户名：%s；操作人：%s；操作时间：%s", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }
    
    public void sendProhibitTeamPlatformTransfers(String username, String operator) {
        try {
            String time = new Moment().toSimpleTime();
            Object[] values = {username, operator, time};
            String message = String.format("管理员禁止团队平台转账提醒；用户名：%s；操作人：%s；操作时间：%s", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendLockUser", e == null ? "" : e.getMessage());
        }
    }
    
    public void sendRecoverUser(User user, String operator) {
        try {
            String username = user.getUsername();
            int totalMoney = Double.valueOf(user.getTotalMoney()).intValue();
            int lotteryMoney = Double.valueOf(user.getLotteryMoney()).intValue();
            String time = new Moment().toSimpleTime();

            Object[] values = {username, totalMoney, lotteryMoney, operator, time};
            String message = String.format("管理员回收用户提醒；用户名：%s；主账户：%s；彩票账户：%s；操作人：%s；操作时间：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendRecoverUser", e == null ? "" : e.getMessage());
        }
    }

    public void sendDailySettle(String eTime, double billingOrder, double totalAmount) {
        try {
            int billingOrderInt = Double.valueOf(billingOrder).intValue();
            int totalAmountInt = Double.valueOf(totalAmount).intValue();
            String time = new Moment().toSimpleTime();

            Object[] values = {eTime, totalAmountInt, billingOrderInt, time};
            String message = String.format("契约日结已派发；日期：%s；总金额：%s；总销量：%s；系统派发时间：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendDailySettle", e == null ? "" : e.getMessage());
        }
    }

    public void sendDividend(String sTime, String eTime, double billingOrder, double totalLoss, double platformTotalLoss, double platformTotalAmount) {
        try {
            int billingOrderInt = Double.valueOf(billingOrder).intValue();
            int totalLossInt = Double.valueOf(totalLoss).intValue();
            int platformTotalLossInt = Double.valueOf(platformTotalLoss).intValue();
            int platformTotalAmountInt = Double.valueOf(platformTotalAmount).intValue();
            String time = new Moment().toSimpleTime();

            Object[] values = {sTime, eTime, billingOrderInt, totalLossInt, platformTotalLossInt, platformTotalAmountInt, time};
            String message = String.format("契约分红已计算；周期：%s~%s；报表销量：%s；报表亏损：%s；平台发放层级总亏损：%s；平台总发放：%s；系统计算时间：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendDividend", e == null ? "" : e.getMessage());
        }
    }

    public void sendGameDividend(String sTime, String eTime, double billingOrder, double totalLoss, double totalAmount) {
        try {
            int billingOrderInt = Double.valueOf(billingOrder).intValue();
            int totalLossInt = Double.valueOf(totalLoss).intValue();
            int totalAmountInt = Double.valueOf(totalAmount).intValue();
            String time = new Moment().toSimpleTime();

            Object[] values = {sTime, eTime, billingOrderInt, totalLossInt, totalAmountInt, time};
            String message = String.format("老虎机真人体育分红已计算；周期：%s~%s；总销量：%s；总亏损：%s；总发放：%s；系统计算时间：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误sendGameDividend", e == null ? "" : e.getMessage());
        }
    }

    public void sendOpen(String username, GameBets gameBets) {
        try {
            SysPlatform sysPlatform = dataFactory.getSysPlatform(gameBets.getPlatformId());
            String platformName = sysPlatform.getName();
            int amount = Double.valueOf(gameBets.getMoney()).intValue();
            int prizeAmount = Double.valueOf(gameBets.getPrizeMoney()).intValue();
            String type = gameBets.getGameType();
            String name = gameBets.getGameName();
            String time = gameBets.getTime();
            String prizeTime = gameBets.getPrizeTime();
            int id = gameBets.getId();

            Object[] values = {platformName, username, amount, prizeAmount, type, name, time, prizeTime, id};
            String message = String.format("用户%s中奖提醒；用户名：%s；投注金额：%s；中奖金额：%s；游戏类型：%s；游戏名称：%s；下单时间：%s；派奖时间：%s；注单ID：%s；", values);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误", e == null ? "" : e.getMessage());
        }
    }

    public void sendUserTransfer(String aUser, String bUser, double money,String remarks) {
        try {
            String moneyStr = MathUtil.doubleToStringDown(money, 1);
            String message = String.format("管理员操作用户转账提醒；待转会员：%s；目标会员：%s；金额：%s；备注：%s；", aUser, bUser, moneyStr,remarks);
            add(message);
        } catch (Exception e) {
            log.error("发送邮件发生错误", e == null ? "" : e.getMessage());
        }
    }

    private String getSystemRechargeType(int subType) {
        String typeStr;
        switch (subType) {
            case 1 :
                typeStr = "充值未到账"; break;
            case 2 :
                typeStr = "活动补贴"; break;
            case 3 :
                typeStr = "修改资金（增）"; break;
            case 4 :
                typeStr = "修改资金（减）"; break;
            default:
                typeStr = "未知";
        }
        return typeStr;
    }

    private String getAccount(int account) {
        String accountStr;
        switch (account) {
            case 1 :
                accountStr = "主账户"; break;
            case 2 :
                accountStr = "彩票账户"; break;
            case 3 :
                accountStr = "百家乐账户"; break;
            default:
                accountStr = "未知";
        }
        return accountStr;
    }

    private String getLockUserStatus(int status) {
        String statusStr;
        switch (status) {
            case 0 :
                statusStr = "正常"; break;
            case -1 :
                statusStr = "冻结"; break;
            case -2 :
                statusStr = "永久冻结"; break;
            case -3:
                statusStr = "禁用"; break;
            default:
                statusStr = "未知";
        }
        return statusStr;
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

    public void addWarning(String message) {
        warningQueue.offer(message);
    }
}
