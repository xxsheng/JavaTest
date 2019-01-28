package lottery.domains.pool.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.*;
import lottery.domains.content.dao.*;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.config.*;
import lottery.domains.content.vo.user.SysCodeRangeVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataFactoryImpl implements DataFactory, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(DataFactoryImpl.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

    @Override
    public void init() {
        log.info("init datafactory....start, current time " + new Moment().toSimpleTime());
        initSysConfig();
        initUser();

        initLottery();
        initLotteryOpenTime();
        initLotteryPlayRules();
        initLotteryOpenCode();

        log.info("init datafactory....done");
    }

    private Map<String, DbServerSync> DbServerSyncMap = new HashMap<>();

    private static volatile boolean isRunningSyncInit = false; // 标识任务是否正在运行
    private static Object syncInitLock = new Object(); // 锁

    @Autowired
    private DbServerSyncDao dbServerSyncDao;

    @Scheduled(cron = "0,10,20,30,40,50 * * * * *")
    public void syncInitJob() {
        synchronized (syncInitLock) {
            if (isRunningSyncInit == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunningSyncInit = true;
        }

        try {
            SyncInit();
        } finally {
            isRunningSyncInit = false;
        }
    }

    private void SyncInit() {
        try {
            List<DbServerSync> list = dbServerSyncDao.listAll();
            for (DbServerSync serverBean : list) {
                String key = serverBean.getKey();
                if (!DbServerSyncMap.containsKey(key)) {
                    DbServerSyncMap.put(key, serverBean);
                }
                DbServerSync thisBean = DbServerSyncMap.get(key);
                // 如果最后修改时间和同步时间不一致，则进行同步
                if (serverBean.getLastModTime() != null) {
                    if (!serverBean.getLastModTime().equals(thisBean.getLastModTime())) {
                        // 开始同步
                        log.debug("有新的同步数据：" + key);
                        if(DbServerSyncEnum.LOTTERY.name().equals(key)) {
                            initLottery();
                        }
                        if(DbServerSyncEnum.LOTTERY_OPEN_TIME.name().equals(key)) {
                            initLotteryOpenTime();
                        }
                        if(DbServerSyncEnum.LOTTERY_PLAY_RULES.name().equals(key)) {
                            initLotteryPlayRules();
                        }
                        if(DbServerSyncEnum.SYS_CONFIG.name().equals(key)) {
                            initSysConfig();
                        }
                        DbServerSyncMap.put(key, serverBean);
                    }
                }
            }
        } catch (Exception e) {
            log.error("同步失败！", e);
        }
    }

    /**
     * 初始化系统配置
     */
    @Autowired
    private SysConfigDao sysConfigDao;
    private CodeConfig codeConfig = new CodeConfig();
    private LotteryConfig lotteryConfig = new LotteryConfig();
    private PlanConfig planConfig = new PlanConfig();
    private MailConfig mailConfig = new MailConfig();
    private SelfLotteryConfig selfLotteryConfig = new SelfLotteryConfig();

    @Override
    public void initSysConfig() {
        try {
            List<SysConfig> list = sysConfigDao.listAll();
            for (SysConfig bean : list) {
                String group = bean.getGroup();
                String key = bean.getKey();
                String value = bean.getValue();
                if ("CODE".equals(group)) {
                	if ("SYS_MIN_POINT".equals(key)) {
						codeConfig.setSysMinLp(Double.valueOf(value));
					}
                	if ("SYS_MIN_CODE".equals(key)) {
                        codeConfig.setSysMinCode(Integer.parseInt(value));
                    }
                    if ("SYS_CODE".equals(key)) {
                        codeConfig.setSysCode(Integer.parseInt(value));
                    }
                    if ("SYS_LOCATE_POINT".equals(key)) {
                        codeConfig.setSysLp(Double.parseDouble(value));
                    }
                    if ("SYS_NOT_LOCATE_POINT".equals(key)) {
                        codeConfig.setSysNlp(Double.parseDouble(value));
                    }
                    if ("SYS_RANGE".equals(key)) {
                        if (StringUtils.isNotEmpty(value)) {
                            String[] ranges = value.split("\\|");
                            List<SysCodeRangeVO> rlist = new ArrayList<>();
                            for (int i = 0; i < ranges.length; i++) {
                                String[] values = ranges[i].split("~");
                                SysCodeRangeVO range = new SysCodeRangeVO();
                                range.setMinPoint(Double.parseDouble(values[0]));
                                range.setMaxPoint(Double.parseDouble(values[1]));
                                rlist.add(range);
                            }
                            codeConfig.setSysCodeRange(rlist);
                        }
                    }
                }
                if ("LOTTERY".equals(group)) {
                    if ("BET_UNIT_MONEY".equals(key)) {
                        lotteryConfig.setbUnitMoney(Integer.parseInt(value));
                    }
                    if ("FEN_MODEL_DOWN_CODE".equals(key)) {
                        lotteryConfig.setFenModelDownCode(Integer.parseInt(value));
                    }
                    if ("LI_MODEL_DOWN_CODE".equals(key)) {
                        lotteryConfig.setLiModelDownCode(Integer.parseInt(value));
                    }
                    if("AUTO_HIT_RANKING".equals(key)) {
                        lotteryConfig.setAutoHitRanking(Integer.parseInt(value) == 1 ? true : false);
                    }
                    if("HIT_RANKING_SIZE".equals(key)) {
                        int hitRankingSize = Integer.parseInt(value);
                        if (hitRankingSize < 0) {
                            hitRankingSize = 10;
                        }
                        if (hitRankingSize > 10) {
                            hitRankingSize = 10;
                        }
                        lotteryConfig.setHitRankingSize(hitRankingSize);
                    }
                }
                if ("PLAN".equals(group)) {
                    if ("MIN_MONEY".equals(key)) {
                        double minMoney = Double.parseDouble(value);
                        planConfig.setMinMoney(minMoney);
                    }
                    if ("TITLE".equals(key)) {
                        JSONArray jsonArray = JSONArray.fromObject(value);
                        List<String> title = new ArrayList<>();
                        for (Object object : jsonArray) {
                            title.add((String) object);
                        }
                        planConfig.setTitle(title);
                    }
                    if ("RATE".equals(key)) {
                        List<Integer> rate = new ArrayList<>();
                        String[] arr = value.split(",");
                        for (String v : arr) {
                            rate.add(Integer.parseInt(v));
                        }
                        planConfig.setRate(rate);
                    }
                    if ("LEVEL".equals(key)) {
                        List<Integer> level = new ArrayList<>();
                        String[] arr = value.split(",");
                        for (String v : arr) {
                            level.add(Integer.parseInt(v));
                        }
                        planConfig.setLevel(level);
                    }
                }
                if ("MAIL".equals(group)) {
                    if ("PASSWORD".equals(key)) {
                        mailConfig.setPassword(value.trim());
                    }
                    if ("PERSONAL".equals(key)) {
                        mailConfig.setPersonal(value.trim());
                    }
                    if ("SMTP_HOST".equals(key)) {
                        mailConfig.setHost(value.trim());
                    }
                    if ("USERNAME".equals(key)) {
                        mailConfig.setUsername(value.trim());
                    }
                    if ("BET".equals(key)) {
                        mailConfig.setBet(Integer.valueOf(value));
                    }
                    if ("OPEN".equals(key)) {
                        mailConfig.setOpen(Integer.valueOf(value));
                    }
                    if ("RECHARGE".equals(key)) {
                        mailConfig.setRecharge(Integer.valueOf(value));
                    }
                    if ("SYS_RECEIVE_MAILS".equals(key)) {
                        mailConfig.getReceiveMails().clear();
                        if (StringUtils.isNotEmpty(value)) {
                            String[] mails = value.split(",");
                            for (String mail : mails) {
                                mailConfig.addReceiveMail(mail.trim());
                            }
                        }
                    }
                }
                if ("SELF_LOTTERY".equals(group)) {
                    if ("CONTROL".equals(key)) {
                        selfLotteryConfig.setControl(Integer.valueOf(value) == 1 ? true : false);
                    }
                    if ("PROBABILITY".equals(key)) {
                        selfLotteryConfig.setProbability(Double.valueOf(value));
                    }
                }
            }
            log.info("初始化系统配置表完成！");
        } catch (Exception e) {
            log.error("初始化系统配置表失败！", e);
        }
    }

    @Override
    public CodeConfig getCodeConfig() {
        return codeConfig;
    }

    @Override
    public LotteryConfig getLotteryConfig() {
        return lotteryConfig;
    }

    @Override
    public PlanConfig getPlanConfig() {
        return planConfig;
    }

    @Override
    public MailConfig getMailConfig() {
        return mailConfig;
    }

    @Override
    public SelfLotteryConfig getSelfLotteryConfig() {
        return selfLotteryConfig;
    }

    /**
     * 初始化彩票
     */
    @Autowired
    private LotteryDao lotteryDao;

    private Map<Integer, Lottery> lotteryMap = new LinkedHashMap<>();

    @Override
    public void initLottery() {
        try {
            List<Lottery> list = lotteryDao.listAll();
            Map<Integer, Lottery> tmpMap = new LinkedHashMap<>();
            for (Lottery lottery : list) {
                tmpMap.put(lottery.getId(), lottery);
            }
            lotteryMap = tmpMap;
            log.debug("初始化彩票信息完成！");
        } catch (Exception e) {
            log.error("初始化彩票信息失败！");
        }
    }

    @Override
    public Lottery getLottery(int id) {
        if (lotteryMap.containsKey(id)) {
            return lotteryMap.get(id);
        }
        return null;
    }

    @Override
    public Lottery getLottery(String shortName) {
        Object[] keys = lotteryMap.keySet().toArray();
        for (Object o : keys) {
            Lottery lottery = lotteryMap.get(o);
            if (lottery.getShortName().equals(shortName)) {
                return lottery;
            }
        }
        return null;
    }

    @Override
    public List<Lottery> listLottery() {
        List<Lottery> list = new LinkedList<Lottery>();
        Object[] keys = lotteryMap.keySet().toArray();
        for (Object o : keys) {
            list.add(lotteryMap.get(o));
        }
        return list;
    }

    @Override
    public List<Lottery> listLottery(int type) {
        List<Lottery> list = new LinkedList<Lottery>();
        Object[] keys = lotteryMap.keySet().toArray();
        for (Object o : keys) {
            Lottery lottery = lotteryMap.get(o);
            if (lottery.getType() == type) {
                list.add(lottery);
            }
        }
        return list;
    }

    @Override
    public List<Lottery> listSelfLottery() {
        List<Lottery> list = new LinkedList<Lottery>();
        Object[] keys = lotteryMap.keySet().toArray();
        for (Object o : keys) {
            Lottery lottery = lotteryMap.get(o);
            if (lottery.getSelf() == 1) {
                list.add(lottery);
            }
        }
        return list;
    }

    /**
     * 初始化开奖时间信息
     */
    @Autowired
    private LotteryOpenTimeDao lotteryOpenTimeDao;

    private List<LotteryOpenTime> lotteryOpenTimeList = new LinkedList<>();

    @Override
    public void initLotteryOpenTime() {
        try {
            List<LotteryOpenTime> list = lotteryOpenTimeDao.listAll();
            if(lotteryOpenTimeList != null) {
                lotteryOpenTimeList.clear();
            }
            lotteryOpenTimeList.addAll(list);
            log.debug("初始化彩票开奖时间信息完成！");
        } catch (Exception e) {
            log.error("初始化彩票开奖时间信息失败！");
        }
    }

    @Override
    public List<LotteryOpenTime> listLotteryOpenTime(String lottery) {
        List<LotteryOpenTime> list = new LinkedList<>();
        for (LotteryOpenTime tmpBean : lotteryOpenTimeList) {
            if(tmpBean.getLottery().equals(lottery)) {
                list.add(tmpBean);
            }
        }
        return list;
    }


    /**
     * 初始化彩票玩法规则
     */
    @Autowired
    private LotteryPlayRulesService playRulesService;
    @Autowired
    private LotteryPlayRulesConfigService playRulesConfigService;
    @Autowired
    private LotteryPlayRulesGroupService playRulesGroupService;
    @Autowired
    private LotteryPlayRulesGroupConfigService playRulesGroupConfigService;

    private Map<Integer, Map<Integer, LotteryPlayRules>> playRulesMap = new LinkedHashMap<>(); // 玩法缓存，<彩票ID, <玩法ID, 玩法>>
    private Map<Integer, Map<Integer, LotteryPlayRulesGroup>> playRulesGroupMap = new LinkedHashMap<>(); // 玩法组缓存，<彩票ID, <玩法组ID, 玩法组>>

    @Override
    public void initLotteryPlayRules() {
        try {
            // 彩票列表
            List<Lottery> lotteries = lotteryDao.listAll();
            if (CollectionUtils.isEmpty(lotteries)) {
                return;
            }

            List<LotteryPlayRules> rules = playRulesService.listAll();
            List<LotteryPlayRulesConfig> rulesConfigs = playRulesConfigService.listAll();
            List<LotteryPlayRulesGroup> groups = playRulesGroupService.listAll();
            List<LotteryPlayRulesGroupConfig> groupConfigs = playRulesGroupConfigService.listAll();

            // 组合玩法数据
            Map<Integer, Map<Integer, LotteryPlayRules>> playRulesMapTmp = groupPlayRulesData(lotteries, rules, rulesConfigs);
            // 组合玩法组数据
            Map<Integer, Map<Integer, LotteryPlayRulesGroup>> playRulesGroupMapTmp = groupPlayRulesGroupData(lotteries, groups, groupConfigs);

            // 替换生效
            playRulesMap = playRulesMapTmp;
            playRulesGroupMap = playRulesGroupMapTmp;
            log.debug("初始化彩票玩法规则完成！");
        } catch (Exception e) {
            log.error("初始化彩票玩法规则失败！");
        }
    }

    @Override
    public LotteryPlayRules getLotteryPlayRules(int lotteryId, int ruleId) {
        Map<Integer, LotteryPlayRules> lotteryRules = playRulesMap.get(lotteryId);
        if (lotteryRules == null) return null;

        return lotteryRules.get(ruleId);
    }

    @Override
    public LotteryPlayRules getLotteryPlayRules(int lotteryId, String code) {
        Map<Integer, LotteryPlayRules> lotteryRules = playRulesMap.get(lotteryId);
        if (lotteryRules == null) return null;

        Collection<LotteryPlayRules> rules = lotteryRules.values();
        for (LotteryPlayRules rule : rules) {
            if (code.equalsIgnoreCase(rule.getCode())) {
                return rule;
            }
        }

        return null;
    }

    @Override
    public LotteryPlayRulesGroup getLotteryPlayRulesGroup(int lotteryId, int groupId) {
        Map<Integer, LotteryPlayRulesGroup> lotteryGroups = playRulesGroupMap.get(lotteryId);
        if (lotteryGroups == null) return null;

        return lotteryGroups.get(groupId);
    }

    private Map<Integer, Map<Integer, LotteryPlayRules>> groupPlayRulesData(List<Lottery> lotteries,
                                                                            List<LotteryPlayRules> rules,
                                                                            List<LotteryPlayRulesConfig> rulesConfigs) {
        Map<Integer, Map<Integer, LotteryPlayRules>> playRulesMapTmp = new LinkedHashMap<>();

        for (Lottery lottery : lotteries) {

            Map<Integer, LotteryPlayRules> lotteryRules = new LinkedHashMap<>();

            for (LotteryPlayRules rule : rules) {
                if (rule.getTypeId() != lottery.getType()) {
                    continue;
                }

                LotteryPlayRules tmpRule = new LotteryPlayRules(rule);

                for (LotteryPlayRulesConfig config : rulesConfigs) {
                    if (config.getRuleId() == rule.getId() && config.getLotteryId() == lottery.getId()) {
                        tmpRule.setMinNum(config.getMinNum());
                        tmpRule.setMaxNum(config.getMaxNum());
                        tmpRule.setStatus(config.getStatus());
                        tmpRule.setPrize(config.getPrize());
                    }
                }

                lotteryRules.put(rule.getId(), tmpRule);
            }

            playRulesMapTmp.put(lottery.getId(), lotteryRules);
        }

        return playRulesMapTmp;
    }

    private Map<Integer, Map<Integer, LotteryPlayRulesGroup>> groupPlayRulesGroupData(List<Lottery> lotteries,
                                                                                      List<LotteryPlayRulesGroup> groups,
                                                                                      List<LotteryPlayRulesGroupConfig> groupsConfigs) {
        Map<Integer, Map<Integer, LotteryPlayRulesGroup>> playRulesGroupMapTmp = new LinkedHashMap<>();

        for (Lottery lottery : lotteries) {

            Map<Integer, LotteryPlayRulesGroup> lotteryGroups = new LinkedHashMap<>();

            for (LotteryPlayRulesGroup group : groups) {
                if (group.getTypeId() != lottery.getType()) {
                    continue;
                }

                LotteryPlayRulesGroup tmpGroup = new LotteryPlayRulesGroup(group);

                for (LotteryPlayRulesGroupConfig config : groupsConfigs) {
                    if (config.getGroupId() == group.getId() && config.getLotteryId() == lottery.getId()) {
                        tmpGroup.setStatus(config.getStatus());
                    }
                }

                lotteryGroups.put(group.getId(), tmpGroup);
            }

            playRulesGroupMapTmp.put(lottery.getId(), lotteryGroups);
        }

        return playRulesGroupMapTmp;
    }


    @Autowired
    private LotteryOpenCodeService lotteryOpenCodeService;

    @Override
    public void initLotteryOpenCode() {
        lotteryOpenCodeService.initLotteryOpenCode();
    }

    /**
     * 初始化用户基础信息
     */
    @Autowired
    private UserDao uDao;

    private Map<Integer, UserVO> userMap = new LinkedHashMap<>();

    @Override
    public void initUser() {
        try {
            List<User> list = uDao.listAll();
            Map<Integer, UserVO> tmpMap = new LinkedHashMap<>();
            for (User user : list) {
                tmpMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
            }
            userMap = tmpMap;
            log.info("初始化用户基础信息完成！");
        } catch (Exception e) {
            log.error("初始化用户基础信息失败！");
        }
    }

    @Override
    public UserVO getUser(int id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        }
        User user = uDao.getById(id);
        if (user != null) {
            userMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
            return userMap.get(id);
        }
        return null;
    }
}