package lottery.domains.jobs;

import javautils.array.ArrayUtils;
import javautils.ip.IpUtil;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBetsSameIpLogService;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBetsSameIpLog;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Created by Nick on 2017-10-16.
 */
@Component
public class UserBetsSameIpLogJob {
    private static final Logger log = LoggerFactory.getLogger(UserBetsSameIpLogJob.class);

    private BlockingQueue<UserBets> queue = new LinkedBlockingDeque<>();

    @Autowired
    private DataFactory dataFactory;

    @Autowired
    private UserBetsSameIpLogService uBetsSameIpLogService;

    private static boolean isRunning = false;

    @Scheduled(cron = "0 0/1 * * * *")
    // @Scheduled(cron = "0/10 * * * * *")
    public void run() {
        synchronized (UserBetsSameIpLogJob.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }
        try {
            process();
        } finally {
            isRunning = false;
        }
    }

    private void process() {
        if(queue != null && queue.size() > 0) {
            try {
                List<UserBets> userBetsList = new LinkedList<>();
                queue.drainTo(userBetsList, 500);
                if (CollectionUtils.isNotEmpty(userBetsList)) {
                    process(userBetsList);
                }
            } catch (Exception e) {
                log.error("添加同IP投注日志错误", e);
            }
        }
    }

    private void process(List<UserBets> userBetsList) {
        // 先将所有IP数据分组
        Map<String, LinkedList<UserBets>> groupByIp = groupByIp(userBetsList);

        Set<String> ips = groupByIp.keySet();
        for (String ip : ips) {
            List<UserBets> ipUserBetsList = groupByIp.get(ip);

            addByIp(ip, ipUserBetsList);
        }
    }

    private void addByIp(String ip, List<UserBets> ipUserBetsList) {
        UserBetsSameIpLog oldLog = uBetsSameIpLogService.getByIP(ip);
        if (oldLog == null) {
            saveAsNew(ip, ipUserBetsList);
        }
        else {
            saveAsOld(oldLog, ipUserBetsList);
        }
    }

    private void saveAsNew(String ip, List<UserBets> ipUserBetsList) {
        UserBetsSameIpLog log = transData(ip, ipUserBetsList);
        uBetsSameIpLogService.add(log);
    }

    private void saveAsOld(UserBetsSameIpLog oldLog, List<UserBets> ipUserBetsList) {
        UserBetsSameIpLog newLog = transData(oldLog.getIp(), ipUserBetsList);

        oldLog.setAddress(newLog.getAddress());

        // 把新旧用户合并
        Set<String> userList = new HashSet<>();

        String[] oldUsersArr = oldLog.getUsers().split(",");
        String[] newUsersArr = newLog.getUsers().split(",");

        for (String oldUser : oldUsersArr) {
            if (!userList.contains(oldUser)) {
                userList.add(oldUser);
            }
        }

        for (String newUser : newUsersArr) {
            if (!userList.contains(newUser)) {
                userList.add(newUser);
            }
        }

        String users = ArrayUtils.toStringFromSet(userList);

        oldLog.setUsers(users);
        oldLog.setUsersCount(userList.size());
        oldLog.setLastTime(newLog.getLastTime());
        oldLog.setLastUser(newLog.getLastUser());
        oldLog.setLastUserBetsId(newLog.getLastUserBetsId());
        oldLog.setTimes(newLog.getTimes() + oldLog.getTimes());
        double amount = MathUtil.add(newLog.getAmount(), oldLog.getAmount());
        oldLog.setAmount(amount);
        uBetsSameIpLogService.update(oldLog);
    }

    private UserBetsSameIpLog transData(String ip, List<UserBets> ipUserBetsList) {
        UserBetsSameIpLog oldLog = new UserBetsSameIpLog();

        HashSet<String> userList = new HashSet<>();
        double amount = 0;
        for (int i = 0; i < ipUserBetsList.size(); i++) {
            UserBets userBets = ipUserBetsList.get(i);

            amount = MathUtil.add(amount, userBets.getMoney());

            UserVO user = dataFactory.getUser(userBets.getUserId());
            if (user != null) {
                if (!userList.contains(user.getUsername())) {
                    userList.add("[" + user.getUsername() + "]");
                }
            }
        }

        String users = ArrayUtils.toStringFromSet(userList);

        UserBets lastUserBets = ipUserBetsList.get(ipUserBetsList.size() - 1);
        String lastTime = lastUserBets.getTime();
        UserVO user = dataFactory.getUser(lastUserBets.getUserId());
        String lastUser = user == null ? null : user.getUsername();
        int lastUserBetsId = lastUserBets.getId();

        int times = ipUserBetsList.size();

        String[] infos = IpUtil.find(ip);
        String address = Arrays.toString(infos);

        oldLog.setIp(ip);
        oldLog.setAddress(address);
        oldLog.setUsers(users);
        oldLog.setUsersCount(userList.size());
        oldLog.setLastTime(lastTime);
        oldLog.setLastUser(lastUser);
        oldLog.setLastUserBetsId(lastUserBetsId);
        oldLog.setTimes(times);
        oldLog.setAmount(amount);

        return oldLog;
    }

    private Map<String, LinkedList<UserBets>> groupByIp(List<UserBets> userBetsList) {
        Map<String, LinkedList<UserBets>> groupByIp = new LinkedHashMap<>();

        for (UserBets userBets : userBetsList) {
            String ip = userBets.getIp();
            if (!groupByIp.containsKey(ip)) {
                groupByIp.put(ip, new LinkedList<UserBets>());
            }

            groupByIp.get(ip).add(userBets);
        }

        return groupByIp;
    }

    public void add(UserBets userBets) {
        try {
            if (StringUtils.isEmpty(userBets.getIp())) {
                log.warn("添加同IP投注日志时IP字段为空，本次不处理");
                return;
            }

            userBets.setCodes(null);
            queue.offer(userBets);
        } catch (Exception e) {
            log.error("添加同IP投注日志出错", e);
        }
    }
}
