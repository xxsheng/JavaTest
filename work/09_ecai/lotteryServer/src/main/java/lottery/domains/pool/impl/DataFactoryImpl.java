package lottery.domains.pool.impl;

import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.entity.ActivityRebate;
import com.alibaba.fastjson.JSON;
import javautils.StringUtil;
import javautils.date.Moment;
import javautils.ip.IpUtil;
import lottery.domains.content.biz.*;
import lottery.domains.content.biz.read.DbServerSyncReadService;
import lottery.domains.content.biz.read.UserBetsHitRankingReadService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.global.Global;
import lottery.domains.content.global.UserBetsHitRankingDefault;
import lottery.domains.content.vo.SysNoticeVO;
import lottery.domains.content.vo.activity.*;
import lottery.domains.content.vo.config.*;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesGroupVO;
import lottery.domains.content.vo.lottery.LotteryPlayRulesVO;
import lottery.domains.content.vo.user.SysCodeRangeVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WSC;
import lottery.web.websocket.WebSocketMsgSender;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Component
public class DataFactoryImpl implements DataFactory, InitializingBean, ServletContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DataFactoryImpl.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

    @Override
    public void init() {
        logger.info("init datafactory....start");
        initSysConfig();
        initCDNConfig();
        initSysMessage();
        initSysPlatform();
        initSysCodeAmount();
        initIpData();

        initUser();
        initLottery();
        initLotteryOpenTime();
        initLotteryPlayRules();
        initLotteryType();
        initUserBetsHitRankings();
        initLotteryOpenCodeJob();

        initPaymentChannel();
        initPaymentBank();
        initPaymentChannelBank();
        initGames();
        initSysNotices();
        // initActivityRedPacketRainTimes(); 红包雨活动
        initActivityFirstRechargeConfig(); // 首充活动
        initActivityRebate(); // 活动列表
        logger.info("init datafactory....done");
    }

    private Map<String, DbServerSync> DbServerSyncMap = new HashMap<>();

    private static volatile boolean isRunningSyncInit = false; // 标识任务是否正在运行
    private static Object syncInitLock = new Object(); // 锁

    @Autowired
    private DbServerSyncReadService dbServerSyncReadService;

    @Scheduled(cron = "2,12,22,32,42,52 * * * * *")
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
            List<DbServerSync> list = dbServerSyncReadService.listAll();
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
                        logger.debug("有新的同步数据：" + key);
                        if (DbServerSyncEnum.LOTTERY.name().equals(key)) {
                            initLottery();
                        }
                        else if (DbServerSyncEnum.LOTTERY_OPEN_TIME.name().equals(key)) {
                            initLotteryOpenTime();
                        }
                        else if (DbServerSyncEnum.LOTTERY_PLAY_RULES.name().equals(key)) {
                            initLotteryPlayRules();
                        }
                        else if (DbServerSyncEnum.LOTTERY_TYPE.name().equals(key)) {
                            initLotteryType();
                        }
                        else if (DbServerSyncEnum.SYS_CONFIG.name().equals(key)) {
                            initSysConfig();
                        }
                        else if (DbServerSyncEnum.SYS_NOTICE.name().equals(key)) {
                            initSysNotices();
                        }
                        else if (DbServerSyncEnum.ACTIVITY.name().equals(key)) {
                            initActivityFirstRechargeConfig(); // 首冲活动
                            initActivityRebate(); // 初始化活动配置
                            // initActivityRedPacketRainTimes(); // 红包雨活动
                        }
                        else if (DbServerSyncEnum.CDN.name().equals(key)) {
                            initCDNConfig();
                        }
                        else if (DbServerSyncEnum.PAYMENT_CHANNEL.name().equals(key)) {
                            initPaymentChannel();
                        }
                        else if (DbServerSyncEnum.PAYMENT_CHANNEL_BANK.name().equals(key)) {
                            initPaymentChannelBank();
                        }
                        else if (DbServerSyncEnum.SYS_CODE_AMOUNT.name().equals(key)) {
                            initSysCodeAmount();
                        }
                        else if (DbServerSyncEnum.SYS_PLATFORM.name().equals(key)) {
                            initSysPlatform();
                        }
                        else if (DbServerSyncEnum.HIT_RANKING.name().equals(key)) {
                            initUserBetsHitRankings();
                        }
                        DbServerSyncMap.put(key, serverBean);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步失败！", e);
        }
    }

    @Override
    public void setServletContext(ServletContext context) {
        WSC.PROJECT_PATH = context.getRealPath("").replace("\\", "/");
        logger.info("Project Path:" + WSC.PROJECT_PATH);
    }

    /**
     * 初始化ip数据库
     */
    private void initIpData() {
        try {
            File file = ResourceUtils.getFile("classpath:config/ip/17monipdb.dat");
            IpUtil.load(file.getPath());
            logger.info("初始化ip数据库完成！");
        } catch (Exception e) {
            logger.error("初始化ip数据库失败！");
        }
    }

    @Override
    public String getFileAsString(String classpath) {
        try {
            File file = ResourceUtils.getFile(classpath);
            if (file != null) {
                InputStream inputStream = new FileInputStream(file);
                return StringUtil.fromInputStream(inputStream);
            }
        } catch (Exception e) {
            logger.error("获取文件失败！", e);
        }
        return null;
    }

    /**
     * 初始化语言文件
     */
    private Properties sysMessage;

    @Override
    public void initSysMessage() {
        try {
            String fileClassPath = "classpath:config/message/language.cn.xml";
            File file = ResourceUtils.getFile(fileClassPath);
            if (file != null) {
                Properties properties = new Properties();
                InputStream inputStream = new FileInputStream(file);
                properties.loadFromXML(inputStream);
                inputStream.close();
                sysMessage = properties;
                logger.info("初始化语言文件完成。");
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception e) {
            logger.error("加载语言文件失败！", e);
        }
    }

    @Override
    public String getSysMessage(String key) {
        return sysMessage.getProperty(key);
    }

    /**
     * 初始化系统配置
     */
    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private ServletContext servletContext;

    private CodeConfig codeConfig = new CodeConfig();
    private LotteryConfig lotteryConfig = new LotteryConfig();
    private DividendConfig dividendConfig = new DividendConfig();
    private GameDividendConfig gameDividendConfig = new GameDividendConfig();
    private DailySettleConfig dailySettleConfig = new DailySettleConfig();
    private WithdrawConfig withdrawConfig = new WithdrawConfig();
    private RechargeConfig rechargeConfig = new RechargeConfig();
    private ServiceConfig serviceConfig = new ServiceConfig();
    private VipConfig vipConfig = new VipConfig();
    private DoMainConfig doMainConfig = new DoMainConfig();
    private PlanConfig planConfig = new PlanConfig();
    private CDNConfig cdnConfig = new CDNConfig();
    private MailConfig mailConfig = new MailConfig();
    private RegistConfig registConfig = new RegistConfig();

    @Override
    public void initSysConfig() {
        try {
            List<SysConfig> list = sysConfigService.listAll();
            for (SysConfig bean : list) {
                String group = bean.getGroup();
                String key = bean.getKey();
                String value = bean.getValue();
                if ("CODE".equals(group)) {
                    if ("SYS_CODE".equals(key)) {
                        codeConfig.setSysCode(Integer.parseInt(value));
                    }
                    if("SYS_NOT_CREATE_ACCOUNT".equals(key)){
						codeConfig.setNotCreateAccount(Integer.parseInt(value));
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

                                int accountCode = "*".equals(values[0]) ? -1 : Integer.valueOf(values[0]);
                                int allocateCode = Integer.valueOf(values[1]);
                                int quantity = Integer.valueOf(values[2]);

                                SysCodeRangeVO range = new SysCodeRangeVO(accountCode, allocateCode, quantity);
                                rlist.add(range);
                            }
                            codeConfig.setSysCodeRange(rlist);
                        }
                    }
                }
                if ("LOTTERY".equals(group)) {
                	if("NOT_BET_POINT_ACCOUNT".equals(key)){
                		lotteryConfig.setNotBetPointAccount(Integer.valueOf(value));
                	}
                	if("NOT_BET_POINT".equals(key)){
                		lotteryConfig.setNotBetPoint(Integer.valueOf(value));
                	}
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
                if ("WITHDRAW".equals(group)) {
                    if ("STATUS".equals(key)) {
                        withdrawConfig.setEnable(Integer.parseInt(value) == 1 ? true : false);
                    }
                    if ("SERVICE_TIME".equals(key)) {
                        withdrawConfig.setServiceTime(value);
                    }
                    if ("SERVICE_MSG".equals(key)) {
                        withdrawConfig.setServiceMsg(value);
                    }
                    if ("MIN_AMOUNT".equals(key)) {
                        withdrawConfig.setMinAmount(Double.parseDouble(value));
                    }
                    
                    if ("REGISTER_HOURS".equals(key)) {
                        withdrawConfig.setRegisterHours(Integer.parseInt(value));
                    }
                    if ("MAX_AMOUNT".equals(key)) {
                        withdrawConfig.setMaxAmount(Double.parseDouble(value));
                    }
                    if ("MAX_TIMES".equals(key)) {
                        withdrawConfig.setMaxTimes(Integer.parseInt(value));
                    }
                    if ("FREE_TIMES".equals(key)) {
                        withdrawConfig.setFreeTimes(Integer.parseInt(value));
                    }
                    if ("FEE".equals(key)) {
                        withdrawConfig.setFee(Double.parseDouble(value));
                    }
                    if ("MAX_FEE".equals(key)) {
                        withdrawConfig.setMaxFee(Double.parseDouble(value));
                    }
                    if("SYSTEM_CONSUMPTION_PERCENT".equals(key)) {
                        withdrawConfig.setSystemConsumptionPercent(Double.parseDouble(value));
                    }
                    if("TRANSFER_CONSUMPTION_PERCENT".equals(key)){
                        withdrawConfig.setTransferConsumptionPercent(Double.parseDouble(value));
                    }
                }
                if ("RECHARGE".equals(group)) {
                    if ("STATUS".equals(key)) {
                        rechargeConfig.setEnable(Integer.parseInt(value) == 1 ? true : false);
                    }
                    if ("SERVICE_TIME".equals(key)) {
                        rechargeConfig.setServiceTime(value);
                    }
                    if ("FEE_PERCENT".equals(key)) {
                        if (NumberUtils.isNumber(value)) {
                            rechargeConfig.setFeePercent(Double.valueOf(value));
                        }
                    }
                }
                if ("SERVICE".equals(group)) {
                    if ("URL".equals(key)) {
                        serviceConfig.setUrl(value);
                    }
                }
                if ("VIP".equals(group)) {
                    if ("BIRTHDAY_GIFTS".equals(key)) {
                        String[] arr = value.split(",");
                        double[] birthdayGifts = new double[arr.length];
                        for (int i = 0; i < arr.length; i++) {
                            birthdayGifts[i] = Double.parseDouble(arr[i]);
                        }
                        vipConfig.setBirthdayGifts(birthdayGifts);
                    }
                    if ("FREE_CHIPS".equals(key)) {
                        String[] arr = value.split(",");
                        double[] freeChips = new double[arr.length];
                        for (int i = 0; i < arr.length; i++) {
                            freeChips[i] = Double.parseDouble(arr[i]);
                        }
                        vipConfig.setFreeChips(freeChips);
                    }
                    if ("UPGRADE_GIFTS".equals(key)) {
                        String[] arr = value.split(",");
                        double[] upgradeGifts = new double[arr.length];
                        for (int i = 0; i < arr.length; i++) {
                            upgradeGifts[i] = Double.parseDouble(arr[i]);
                        }
                        vipConfig.setUpgradeGifts(upgradeGifts);
                    }
                    if ("WITHDRAW".equals(key)) {
                        String[] arr = value.split(",");
                        double[] withdraw = new double[arr.length];
                        for (int i = 0; i < arr.length; i++) {
                            withdraw[i] = Double.parseDouble(arr[i]);
                        }
                        vipConfig.setWithdraw(withdraw);
                    }
                    if ("EXCHANGE_RATE".equals(key)) {
                        vipConfig.setExchangeRate(Integer.parseInt(value));
                    }
                    if ("MAX_EXCHANGE_MULTIPLE".equals(key)) {
                        vipConfig.setMaxExchangeMultiple(Integer.parseInt(value));
                    }
                    if ("MAX_EXCHANGE_TIMES".equals(key)) {
                        vipConfig.setMaxExchangeTimes(Integer.parseInt(value));
                    }
                }
                if ("DOMAIN".equals(group)) {
                    if ("URLS".equals(key)) {
                        List<String> urls = new ArrayList<>();
                        String[] arr = value.split(",");
                        for (String url : arr) {
                            urls.add(url);
                        }
                        doMainConfig.setUrls(urls);
                    }
                    if ("MOBILE_REGISTER_URL".equals(key)) {
                        doMainConfig.setMobileRegisterUrl(value);
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
                if ("DIVIDEND".equals(group)) {
                	if ("FIXED_TYPE".equals(key)) {
						dividendConfig.setFixedType(Integer.valueOf(value));
					}
                	if ("MIN_VALID_USER".equals(key)) {
						dividendConfig.setMinValidUserl(Integer.valueOf(value));
					}
					if ("IS_CHECK_LOSS".equals(key)) {
						dividendConfig.setCheckLoss(Integer.valueOf(value) == 1 ? true : false);
					}
					if ("LEVELS_LOSS".equals(key)) {
						String[] arrs = value.split("~");
						double[] values = {Double.valueOf(arrs[0]),Double.valueOf(arrs[1])};
						dividendConfig.setLevelsLoss(values);
					}
					if ("LEVELS_SALES".equals(key)) {
						String[] arrs = value.split("~");
						double[] values = {Double.valueOf(arrs[0]),Double.valueOf(arrs[1])};
						dividendConfig.setLevelsSales(values);
					}
					if ("LEVELS_SCALE".equals(key)) {
						String[] arrs = value.split("~");
						double[] values = {Integer.valueOf(arrs[0]),Integer.valueOf(arrs[1])};
						dividendConfig.setLevelsScale(values);
					}
					
                	if ("MAX_SIGN_LEVEL".equals(key)) {
						dividendConfig.setMaxSignLevel(Integer.valueOf(value));
					}
                	if ("START_LEVEL".equals(key)) {
						dividendConfig.setStartLevel(Integer.valueOf(value));
					}
                    if ("ZHAO_SHANG_SCALE".equals(key)) {
                    	String[] values = value.split("~");
						dividendConfig.setZhaoShangSalesLevels(values[0]);
						dividendConfig.setZhaoShangLossLevels(values[1]);
						dividendConfig.setZhaoShangScaleLevels(values[2]);
                    }
                    if ("CJ_ZHAO_SHANG_SCALE".equals(key)) {
                        String[] arr = value.split("\\|");
                        for (String ruleStr : arr) {
                            String[] values = ruleStr.split("~");
                            dividendConfig.addCJZhaoShangScaleConfig(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]));
                        }
                    }
                    if ("ZHAO_SHANG_MIN_VALID_USER".equals(key)) {
                        dividendConfig.setZhaoShangMinValidUser(Integer.valueOf(value));
                    }
                    if ("ZHAO_SHANG_BELOW_SCALE".equals(key)) {
                        String[] values = value.split("~");
                        dividendConfig.setZhaoShangBelowMinScale(Double.valueOf(values[0]));
                        dividendConfig.setZhaoShangBelowMaxScale(Double.valueOf(values[1]));
                    }
                    if ("STATUS".equals(key)) {
                        dividendConfig.setEnable(Integer.valueOf(value) == 1 ? true : false);
                    }
                }
                if ("DAILY_SETTLE".equals(group)) {
                	if ("MIN_VALID_USER".equals(key)) {
						dailySettleConfig.setMinValidUserl(Integer.valueOf(value));
					}
					if ("IS_CHECK_LOSS".equals(key)) {
						dailySettleConfig.setCheckLoss(Integer.valueOf(value) == 1 ? true : false);
					}
					if ("LEVELS_LOSS".equals(key)) {
						String[] arrs = value.split("~");
						double[] values = {Double.valueOf(arrs[0]),Double.valueOf(arrs[1])};
						dailySettleConfig.setLevelsLoss(values);
					}
					if ("LEVELS_SALES".equals(key)) {
						String[] arrs = value.split("~");
						double[] values = {Double.valueOf(arrs[0]),Double.valueOf(arrs[1])};
						dailySettleConfig.setLevelsSales(values);
					}
					if ("LEVELS_SCALE".equals(key)) {
						String[] arrs = value.split("~");
						double[] values = {Double.valueOf(arrs[0]),Double.valueOf(arrs[1])};
						dailySettleConfig.setLevelsScale(values);
					}

					if ("MAX_SIGN_LEVEL".equals(key)) {
						dailySettleConfig.setMaxSignLevel(Integer.valueOf(value));
					}
					
                    if ("NEIBU_ZHAO_SHANG_SCALE".equals(key)) {
                        dailySettleConfig.setNeibuZhaoShangScale(StringUtils.isEmpty(value) ? 0 : Double.valueOf(value));
                    }
                    if ("NEIBU_ZHAO_SHANG_MIN_VALID_USER".equals(key)) {
                        dailySettleConfig.setNeibuZhaoShangMinValidUser(Integer.valueOf(value));
                    }
                    if ("ZHAO_SHANG_SCALE".equals(key)) {
                        dailySettleConfig.setZhaoShangScale(StringUtils.isEmpty(value) ? 0 : Double.valueOf(value));
                    }
                    if ("CJ_ZHAO_SHANG_SCALE".equals(key)) {
                        dailySettleConfig.setCjZhaoShangScale(StringUtils.isEmpty(value) ? 0 : Double.valueOf(value));
                    }
                    if ("ZHAO_SHANG_MIN_VALID_USER".equals(key)) {
                        dailySettleConfig.setZhaoShangMinValidUser(Integer.valueOf(value));
                    }
                    if ("STATUS".equals(key)) {
                        dailySettleConfig.setEnable(Integer.valueOf(value) == 1 ? true : false);
                    }
                }
                if ("SETTLE".equals(group)) {
                    if ("MIN_BILLING_ORDER".equals(key)) {
                        Double minBillingOrder = Double.valueOf(value);
                        dailySettleConfig.setMinBillingOrder(minBillingOrder);
                        dividendConfig.setMinBillingOrder(minBillingOrder);
                    }
                }
                if ("GAME_DIVIDEND".equals(group)) {
                    if ("ZHU_GUAN_SCALE".equals(key)) {
                        String[] arr = value.split("\\|");
                        for (String ruleStr : arr) {
                            String[] values = ruleStr.split("~");
                            gameDividendConfig.addZhuGuanScaleConfig(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]));
                        }
                    }
                    if ("ZHU_GUAN_MIN_VALID_USER".equals(key)) {
                        gameDividendConfig.setZhuGuanMinValidUser(Integer.valueOf(value));
                    }
                    if ("ZHU_GUAN_BELOW_SCALE".equals(key)) {
                        String[] values = value.split("~");
                        gameDividendConfig.setZhuGuanBelowMinScale(Double.valueOf(values[0]));
                        gameDividendConfig.setZhuGuanBelowMaxScale(Double.valueOf(values[1]));
                    }
                    if ("STATUS".equals(key)) {
                        gameDividendConfig.setEnable(Integer.valueOf(value) == 1 ? true : false);
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
                if ("REGIST".equals(group)) {
                    if ("REGIST_CODE".equals(key)) {
                        registConfig.setDefaultCode(Integer.valueOf(value));
                    }
                    if ("REGIST_STATUS".equals(key)) {
                        registConfig.setEnable(Integer.valueOf(value) == 1 ? true : false);
                    }
                    if ("DEMO_COUNT".equals(key)) {
                        registConfig.setDemoCount(Integer.parseInt(value));
                    }

                    if ("DEMO_PASSWORD".equals(key)) {
                        registConfig.setDemoPassword(value);
                    }
                    if ("DEMO_LOTTERY_MONEY".equals(key)) {
                        registConfig.setDemoLotteryMoney(Double.parseDouble(value));
                    }
                    
                }
            }
            logger.info("初始化系统配置表完成！");
        } catch (Exception e) {
            logger.error("初始化系统配置表失败！", e);
        }
    }

    @Override
    public void initCDNConfig() {
        try {
            List<SysConfig> list = sysConfigService.listAll();
            for (SysConfig bean : list) {
                String group = bean.getGroup();
                String key = bean.getKey();
                String value = bean.getValue();
                if ("CDN".equals(group)) {
                    if ("DOMAIN".equals(key)) {
                        if (StringUtils.isEmpty(value)) {
                            cdnConfig.setCdnDomain("/");
                        }
                        else {
                            // 随机选择一个CDN域名
                            String[] cdnDomains = value.split(",");
                            int randomIndex = RandomUtils.nextInt(cdnDomains.length);
                            String cdnDomain = cdnDomains[randomIndex];

                            if (!cdnDomain.endsWith("/")) {
                                cdnConfig.setCdnDomain(cdnDomain + "/");
                            }
                            else {
                                cdnConfig.setCdnDomain(cdnDomain);
                            }
                        }
                    }
                    if ("VERSION".equals(key)) {
                        cdnConfig.setCdnVersion(value);
                    }
                    if (servletContext != null) {
                        servletContext.setAttribute("cdnName", cdnConfig.getCdnDomain());
                        System.out.println(cdnConfig.getCdnDomain());
                        servletContext.setAttribute("cdnVersion", cdnConfig.getCdnVersion());
                    }
                }
            }
            logger.info("初始化系统配置表完成！");
        } catch (Exception e) {
            logger.error("初始化系统配置表失败！", e);
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
    public WithdrawConfig getWithdrawConfig() {
        return withdrawConfig;
    }

    @Override
    public RechargeConfig getRechargeConfig() {
        return rechargeConfig;
    }

    @Override
    public ServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    @Override
    public VipConfig getVipConfig() {
        return vipConfig;
    }

    @Override
    public DoMainConfig getDoMainConfig() {
        return doMainConfig;
    }

    @Override
    public PlanConfig getPlanConfig() {
        return planConfig;
    }

    @Override
    public CDNConfig getCDNConfig() {
        return cdnConfig;
    }

    @Override
    public MailConfig getMailConfig() {
        return mailConfig;
    }

    @Override
    public RegistConfig getRegistConfig() {
        return registConfig;
    }

    @Override
    public DividendConfig getDividendConfig() {
        return dividendConfig;
    }

    @Override
    public DailySettleConfig getDailySettleConfig() {
        return dailySettleConfig;
    }

    /**
     * 初始化平台列表
     */
    @Autowired
    private SysPlatformService sysPlatformService;

    private List<SysPlatform> sysPlatformList = new LinkedList<>();

    @Override
    public void initSysPlatform() {
        try {
            List<SysPlatform> list = sysPlatformService.listAll();
            sysPlatformList = list;
            logger.info("初始化平台列表完成！");
        } catch (Exception e) {
            logger.error("初始化平台列表失败！");
        }
    }

    @Override
    public List<SysPlatform> listSysPlatform() {
        return sysPlatformList;
    }

    @Override
    public SysPlatform getSysPlatform(int id) {
        for (SysPlatform tmpBean : sysPlatformList) {
            if (tmpBean.getId() == id) {
                return tmpBean;
            }
        }
        return null;
    }

    @Override
    public SysPlatform getSysPlatform(String name) {
        for (SysPlatform tmpBean : sysPlatformList) {
            if (tmpBean.getName().equals(name)) {
                return tmpBean;
            }
        }
        return null;
    }

    @Autowired
    private SysCodeAmountService sysCodeAmountService;

    private List<SysCodeAmount> sysCodeAmountList = new LinkedList<>();

    @Override
    public void initSysCodeAmount() {
        try {
            List<SysCodeAmount> list = sysCodeAmountService.listAll();
            sysCodeAmountList = list;
            logger.info("初始化按量升点配置完成！");
        } catch (Exception e) {
            logger.error("初始化按量升点配置失败！");
        }
    }

    @Override
    public List<SysCodeAmount> listSysCodeAmount() {
        return sysCodeAmountList;
    }

    /**
     * 初始化用户基础信息
     */
    @Autowired
    private UserReadService uReadService;

    private Map<Integer, UserVO> userMap = new LinkedHashMap<>();

    @Override
    public void initUser() {
        try {
            List<User> list = uReadService.listAllFromRead();
            Map<Integer, UserVO> tmpMap = new LinkedHashMap<>();
            for (User user : list) {
                tmpMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
            }
            userMap = tmpMap;
            logger.info("初始化用户基础信息完成！");
        } catch (Exception e) {
            logger.error("初始化用户基础信息失败！");
        }
    }

    @Override
    public UserVO getUser(int id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        }
        User user = uReadService.getByIdFromRead(id);
        if (user != null) {
            userMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
            return userMap.get(id);
        }
        return null;
    }

    @Override
    public void setUser(User user) {
        userMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
    }

    /**
     * 初始化彩票类型
     */
    @Autowired
    private LotteryTypeService lotteryTypeService;

    private Map<Integer, LotteryType> lotteryTypeMap = new LinkedHashMap<>();

    @Override
    public void initLotteryType() {
        try {
            List<LotteryType> list = lotteryTypeService.listAll();
            Map<Integer, LotteryType> tmpMap = new LinkedHashMap<>();
            for (LotteryType lotteryType : list) {
                tmpMap.put(lotteryType.getId(), lotteryType);
            }
            lotteryTypeMap = tmpMap;
            logger.info("初始化彩票类型完成！");
        } catch (Exception e) {
            logger.error("初始化彩票类型失败！");
        }
    }

    @Override
    public LotteryType getLotteryType(int id) {
        if (lotteryTypeMap.containsKey(id)) {
            return lotteryTypeMap.get(id);
        }
        return null;
    }

    @Override
    public List<LotteryType> listLotteryType() {
        List<LotteryType> list = new LinkedList<>();
        Object[] keys = lotteryTypeMap.keySet().toArray();
        for (Object o : keys) {
            list.add(lotteryTypeMap.get(o));
        }
        return list;
    }

    /**
     * 初始化彩票
     */
    @Autowired
    private LotteryService lotteryService;

    private Map<Integer, Lottery> lotteryMap = new LinkedHashMap<>();

    @Override
    public void initLottery() {
        try {
            List<Lottery> list = lotteryService.listAll();
            Map<Integer, Lottery> tmpMap = new LinkedHashMap<>();
            for (Lottery lottery : list) {
                tmpMap.put(lottery.getId(), lottery);
            }
            lotteryMap = tmpMap;
            logger.info("初始化彩票信息完成！");
        } catch (Exception e) {
            logger.error("初始化彩票信息失败！");
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
    public List<Lottery> listLotteryByStatus(int status) {
        List<Lottery> list = new LinkedList<Lottery>();
        Object[] keys = lotteryMap.keySet().toArray();
        for (Object o : keys) {
            Lottery lottery = lotteryMap.get(o);
            if (lottery.getStatus() == status) {
                list.add(lottery);
            }
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

    /**
     * 初始化开奖时间信息
     */
    @Autowired
    private LotteryOpenTimeService lotteryOpenTimeService;

    private Map<String, List<LotteryOpenTime>> lotteryOpenTimeMap = new HashMap<>();

    @Override
    public void initLotteryOpenTime() {
        try {
            List<LotteryOpenTime> list = lotteryOpenTimeService.listAll();

            Map<String, List<LotteryOpenTime>> tmpOpenTimeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list)) {
                for (LotteryOpenTime lotteryOpenTime : list) {
                    if (tmpOpenTimeMap.containsKey(lotteryOpenTime.getLottery())) {
                        tmpOpenTimeMap.get(lotteryOpenTime.getLottery()).add(lotteryOpenTime);
                    }
                    else {
                        LinkedList<LotteryOpenTime> openTimes = new LinkedList<>();
                        openTimes.add(lotteryOpenTime);
                        tmpOpenTimeMap.put(lotteryOpenTime.getLottery(), openTimes);
                    }
                }
            }

            lotteryOpenTimeMap = tmpOpenTimeMap;
            logger.info("初始化彩票开奖时间信息完成！");
        } catch (Exception e) {
            logger.error("初始化彩票开奖时间信息失败！");
        }
    }

    @Override
    public List<LotteryOpenTime> listLotteryOpenTime(String lottery) {
        return lotteryOpenTimeMap.get(lottery);
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
    private Map<Integer, Map<String, LotteryPlayRulesGroupVO>> playRulesGroupVOMap = new LinkedHashMap<>(); // 前台缓存，<彩票ID, <玩法组编码, 玩法组>>
    private Map<Integer, Map<String, LotteryPlayRulesVO>> playRulesVOMap = new LinkedHashMap<>(); // 前台缓存，<彩票ID, <玩法编码, 玩法>>

    @Override
    public void initLotteryPlayRules() {
        try {
            // 彩票列表
            List<Lottery> lotteries = lotteryService.listAll();
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
            // 组合前台玩法组缓存数据
            Map<Integer, Map<String, LotteryPlayRulesGroupVO>> playRulesGroupVOMapTmp = groupPlayRulesGroupVOData(lotteries, playRulesGroupMapTmp);
            // 组合前台玩法缓存数据
            Map<Integer, Map<String, LotteryPlayRulesVO>> playRulesVOMapTmp = groupPlayRulesVOData(lotteries, playRulesMapTmp, playRulesGroupMapTmp);

            // 替换生效
            playRulesMap = playRulesMapTmp;
            playRulesGroupMap = playRulesGroupMapTmp;
            playRulesGroupVOMap = playRulesGroupVOMapTmp;
            playRulesVOMap = playRulesVOMapTmp;
            logger.info("初始化彩票玩法规则完成！");
        } catch (Exception e) {
            logger.error("初始化彩票玩法规则失败！");
        }
    }

    @Override
    public LotteryPlayRules getLotteryPlayRules(int lotteryId, int ruleId) {
        Map<Integer, LotteryPlayRules> lotteryRules = playRulesMap.get(lotteryId);
        if (lotteryRules == null) return null;

        return lotteryRules.get(ruleId);
    }

    @Override
    public LotteryPlayRulesGroup getLotteryPlayRulesGroup(int lotteryId, int groupId) {
        Map<Integer, LotteryPlayRulesGroup> lotteryGroups = playRulesGroupMap.get(lotteryId);
        if (lotteryGroups == null) return null;

        return lotteryGroups.get(groupId);
    }

    @Override
    public Map<String, LotteryPlayRulesGroupVO> listLotteryPlayRulesGroupVOs(int lotteryId) {
        return playRulesGroupVOMap.get(lotteryId);
    }

    @Override
    public Map<String, LotteryPlayRulesVO> listLotteryPlayRulesVOs(int lotteryId) {
        return playRulesVOMap.get(lotteryId);
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

    private Map<Integer, Map<String, LotteryPlayRulesGroupVO>> groupPlayRulesGroupVOData(List<Lottery> lotteries,
                                                                                  Map<Integer, Map<Integer, LotteryPlayRulesGroup>> playRulesGroupMapTmp) {
        Map<Integer, Map<String, LotteryPlayRulesGroupVO>> playRulesGroupVOMapTmp = new LinkedHashMap<>();

        for (Lottery lottery : lotteries) {

            Map<Integer, LotteryPlayRulesGroup> playRulesGroups = playRulesGroupMapTmp.get(lottery.getId());
            if (playRulesGroups == null || playRulesGroups.isEmpty()) {
                continue;
            }

            Map<String, LotteryPlayRulesGroupVO> playRulesGroupVOs = new LinkedHashMap<>();

            Collection<LotteryPlayRulesGroup> groups = playRulesGroups.values();

            for (LotteryPlayRulesGroup group : groups) {
                if (group.getStatus() == -1) {
                    continue;
                }
                playRulesGroupVOs.put(group.getCode(), new LotteryPlayRulesGroupVO(group));
            }

            playRulesGroupVOMapTmp.put(lottery.getId(), playRulesGroupVOs);
        }

        return playRulesGroupVOMapTmp;
    }

    private Map<Integer, Map<String, LotteryPlayRulesVO>> groupPlayRulesVOData(List<Lottery> lotteries,
                                                                                    Map<Integer, Map<Integer, LotteryPlayRules>> playRulesMapTmp,
                                                                                    Map<Integer, Map<Integer, LotteryPlayRulesGroup>> playRulesGroupMapTmp) {
        Map<Integer, Map<String, LotteryPlayRulesVO>> playRulesVOMapTmp = new LinkedHashMap<>();

        for (Lottery lottery : lotteries) {

            Map<Integer, LotteryPlayRules> playRules = playRulesMapTmp.get(lottery.getId());
            if (playRules == null || playRules.isEmpty()) {
                continue;
            }

            Map<String, LotteryPlayRulesVO> playRulesVOs = new LinkedHashMap<>();

            Collection<LotteryPlayRules> rules = playRules.values();

            for (LotteryPlayRules rule : rules) {
                if (rule.getStatus() == -1) {
                    continue;
                }

                LotteryPlayRulesGroup group = null;
                Map<Integer, LotteryPlayRulesGroup> groupMap = playRulesGroupMapTmp.get(lottery.getId());
                if (groupMap != null && !groupMap.isEmpty()) {
                    group = groupMap.get(rule.getGroupId());
                }

                playRulesVOs.put(rule.getCode(), new LotteryPlayRulesVO(rule, null, group));
            }

            playRulesVOMapTmp.put(lottery.getId(), playRulesVOs);
        }

        return playRulesVOMapTmp;
    }


    @Autowired
    private UserBetsHitRankingReadService uBetsHitRankingReadService;
    private static volatile boolean isRunningInitUserBetsHitRanking = false; // 标识任务是否正在运行
    private static Object syncInitUserBetsHitRankingLock = new Object(); // 锁

    private List<UserBetsHitRanking> userBetsHitRankings = new ArrayList<>();

    @Override
    public void initUserBetsHitRankings() {
        synchronized (syncInitUserBetsHitRankingLock) {
            if (isRunningInitUserBetsHitRanking == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunningInitUserBetsHitRanking = true;
        }

        try {
            doInitUserBetsHitRanking();
        } finally {
            isRunningInitUserBetsHitRanking = false;
        }
    }

    private void doInitUserBetsHitRanking() {
        int hitRankingSize = getLotteryConfig().getHitRankingSize();
        List<UserBetsHitRanking> rankings = uBetsHitRankingReadService.listAll(hitRankingSize);

        if (CollectionUtils.isEmpty(rankings)) {
            rankings = UserBetsHitRankingDefault.DEFAULT_LIST;

            // 排序
            Collections.sort(rankings);

            // 截取
            rankings = rankings.subList(0, hitRankingSize);
        }
        else {
            if (rankings.size() < hitRankingSize) {
                // 注单中奖排行榜 + 默认排行榜，按奖金降序排序，并只取10条
                rankings.addAll(UserBetsHitRankingDefault.DEFAULT_LIST);

                // 排序
                Collections.sort(rankings);

                // 截取
                rankings = rankings.subList(0, hitRankingSize);
            }
        }

        // 马赛克用户名
        mosaicUsername(rankings);

        // 更新
        userBetsHitRankings = rankings;
    }

    private void mosaicUsername(List<UserBetsHitRanking> rankings) {
        for (UserBetsHitRanking ranking : rankings) {
            String username = ranking.getUsername();
            if (username.length() >= 3) {
                username = username.substring(0, 3) + "****";
            }
            else {
                username = username.substring(0, 1) + "****";
            }
            ranking.setUsername(username);
        }
    }

    @Override
    public List<UserBetsHitRanking> listUserBetsHitRanking() {
        return userBetsHitRankings;
    }

    @Autowired
    private PaymentChannelService paymentChannelService;
    private List<PaymentChannel> paymentChannelList = new ArrayList<>();
    private Map<Integer, PaymentChannel> paymentChannelMap = new HashMap<>();

    private void initPaymentChannel() {
        List<PaymentChannel> paymentChannels = paymentChannelService.listAll();

        Map<Integer, PaymentChannel> tmpPaymentChannelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(paymentChannels)) {

            for (PaymentChannel paymentChannel : paymentChannels) {
                tmpPaymentChannelMap.put(paymentChannel.getId(), paymentChannel);
            }
        }

        paymentChannelList = paymentChannels;
        paymentChannelMap = tmpPaymentChannelMap;
    }

    @Override
    public List<PaymentChannel> listPaymentChannel() {
        return paymentChannelList;
    }

    @Override
    public PaymentChannel getPaymentChannel(int id) {
        return paymentChannelMap.get(id);
    }

    @Autowired
    private PaymentBankService paymentBankService;

    private List<PaymentBank> paymentBankList = new LinkedList<>();

    @Override
    public void initPaymentBank() {
        try {
            List<PaymentBank> list = paymentBankService.listAll();
            paymentBankList = list;
            logger.info("初始化银行列表完成！");
        } catch (Exception e) {
            logger.error("初始化银行列表失败！");
        }
    }

    @Override
    public List<PaymentBank> listPaymentBank() {
        return paymentBankList;
    }

    @Override
    public PaymentBank getPaymentBank(int id) {
        for (PaymentBank tmpBean : paymentBankList) {
            if (tmpBean.getId() == id) {
                return tmpBean;
            }
        }
        return null;
    }

    @Autowired
    private PaymentChannelBankService paymentChannelBankService;

    private List<PaymentChannelBank> paymentChannelBankList = new LinkedList<>();

    @Override
    public void initPaymentChannelBank() {
        try {
            List<PaymentChannelBank> list = paymentChannelBankService.listAll(0);
            paymentChannelBankList = list;
            logger.info("初始化充值通道银行列表完成！");
        } catch (Exception e) {
            logger.error("初始化充值通道银行列表失败！");
        }
    }

    @Override
    public List<PaymentChannelBank> listPaymentChannelBank(String channelCode) {
        List<PaymentChannelBank> list = new ArrayList<>();
        for (PaymentChannelBank tmpBean : paymentChannelBankList) {
            if (channelCode.equals(tmpBean.getChannelCode())) {
                list.add(tmpBean);
            }
        }
        return list;
    }

    /**
     * 初始化数据获取方法
     */
    @Autowired
    private LotteryOpenCodeService lotteryOpenCodeService;

    @Autowired
    private WebSocketMsgSender msgSender;

    // 开奖号码数据
    private Map<Integer, List<LotteryOpenCodeVO>> lotteryOpenCodeMap = new LinkedHashMap<>();
    // 上次抓取期号
    private Map<Integer, String> lastOpenExpectMap = new HashMap<>();

    private static volatile boolean isRunningInitLotteryOpenCode = false; // 标识任务是否正在运行
    private static Object initLotteryOpenCodeLock = new Object(); // 锁

    @Scheduled(cron = "0/2 * * * * *")
    public void initLotteryOpenCodeJob() {
        synchronized (initLotteryOpenCodeLock) {
            if (isRunningInitLotteryOpenCode == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunningInitLotteryOpenCode = true;
        }

        try {
            initLotteryOpenCode();
        } finally {
            isRunningInitLotteryOpenCode = false;
        }
    }

    private void initLotteryOpenCode() {
        // 抓取号码
        List<String> lotteryNames = new ArrayList<>();
        List<Lottery> lotteries = listLottery();
        for (Lottery lottery : lotteries) {
            lotteryNames.add(lottery.getShortName());
        }

        // 批量获取所有开奖号码数据
        List<LotteryOpenCodeVO> allCodes = lotteryOpenCodeService.getLatestFromRedis(lotteryNames);

        // 重新封装数据
        Map<Integer, List<LotteryOpenCodeVO>> tmpMap = new LinkedHashMap<>();
        for (LotteryOpenCodeVO code : allCodes) {
            if (!tmpMap.containsKey(code.getLotteryId())) {
                tmpMap.put(code.getLotteryId(), new ArrayList<LotteryOpenCodeVO>());
            }

            tmpMap.get(code.getLotteryId()).add(code);
        }

        // 设置到最新值，并推送最新开奖号码
        for (Lottery lottery : lotteries) {
            List<LotteryOpenCodeVO> openCodeVOs = tmpMap.get(lottery.getId());

            if (CollectionUtils.isNotEmpty(openCodeVOs)) {
                if (lottery.getId() == 117) {
                    // 推送给用户急速秒秒彩开奖结果
                    for (LotteryOpenCodeVO openCodeVO : openCodeVOs) {
                        msgSender.sendUserJSMMCOpenCodeMsg(openCodeVO.getUserId(), openCodeVO.getCode());
                        lotteryOpenCodeService.delJSMMCOpenCodeFromRedis(openCodeVO.getExpect(), openCodeVO.getUserId());
                    }
                }
                else {
                    lotteryOpenCodeMap.put(lottery.getId(), openCodeVOs);
                    // 查看是否是最新期数
                    LotteryOpenCodeVO thisOpenCodeVO = openCodeVOs.get(0);
                    String thisExpect = thisOpenCodeVO.getExpect();
                    String lastExpect = lastOpenExpectMap.get(lottery.getId());
                    lastOpenExpectMap.put(lottery.getId(), thisExpect);

                    if (lastExpect != null && thisExpect.compareTo(lastExpect) > 0) {
                        // 推送给所有该彩种用户开奖号码
                        msgSender.sendLotteryOpenCodeMsg(lottery.getId(), thisExpect, thisOpenCodeVO.getCode());
                    }
                }
            }
        }
    }

    @Override
    public List<LotteryOpenCodeVO> listLotteryOpenCode(int lotteryId, int count, Integer userId) {
        if (lotteryId == 117) {
            // 急速秒秒彩直接从数据库查
            List<LotteryOpenCodeVO> openCodeVOs = new ArrayList<>();

            List<LotteryOpenCode> latest = lotteryOpenCodeService.getLatest("jsmmc", count, userId);
            if (CollectionUtils.isNotEmpty(latest)) {
                for (LotteryOpenCode openCode : latest) {
                    LotteryOpenCodeVO openCodeVO = new LotteryOpenCodeVO(openCode, this);
                    openCodeVOs.add(openCodeVO);
                }
            }

            return openCodeVOs;
        }
        else {
            if (lotteryOpenCodeMap.containsKey(lotteryId)) {
                List<LotteryOpenCodeVO> openCodeVOs = lotteryOpenCodeMap.get(lotteryId);
                if (CollectionUtils.isEmpty(openCodeVOs)) {
                    return new ArrayList<>();
                }

                if (count >= openCodeVOs.size()) {
                    return openCodeVOs;
                }
                else {
                    return openCodeVOs.subList(0, count);
                }

            }
        }
        return null;
    }

    private static volatile boolean isRunningBetsNotice = false; // 标识任务是否正在运行
    private static Object betsNoticeLock = new Object(); // 锁

    @Autowired
    private UserBetsNoticeService userBetsNoticeService;

    @Scheduled(cron = "0/3 * * * * *")
    public void betsNoticesJob() {
        synchronized (betsNoticeLock) {
            if (isRunningBetsNotice == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunningBetsNotice = true;
        }

        try {
            // 给用户发送注单通知
            betsNotices();
        } finally {
            isRunningBetsNotice = false;
        }
    }

    private void betsNotices() {
        Map<Integer, Map<String, String>> allBetsNotices = userBetsNoticeService.getAllBetsNotices();
        if (MapUtils.isEmpty(allBetsNotices)) {
            return;
        }
        Iterator<Map.Entry<Integer, Map<String, String>>> iterator = allBetsNotices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Map<String, String>> next = iterator.next();

            int userId = next.getKey();
            Map<String, String> notices = next.getValue();

            Iterator<Map.Entry<String, String>> noticeIterator = notices.entrySet().iterator();
            while (noticeIterator.hasNext()) {
                Map.Entry<String, String> notice = noticeIterator.next();
                String field = notice.getKey();
                String value = notice.getValue();

                msgSender.sendUserMsg(userId, value);
                userBetsNoticeService.delBetsNotice(field);
            }
        }
    }

    @Autowired
    private GameService gameService;
    @Autowired
    private GameTypeService gameTypeService;
    @Autowired
    private UserGameAccountService uGameAccountService;

    // typeId:<gameId:game>
    private Map<Integer, Map<Integer, Game>> GAME_CACHE = new HashMap<>();
    // platformId:<typeId, type>
    private Map<Integer, Map<Integer, GameType>> GAME_TYPE_CACHE = new HashMap<>();

    @Scheduled(cron = "0 0/5 * * * *")
    public void initGames() {
        initGame();
        initGameType();
    }

    public void initGame() {
        List<Game> games = gameService.listAll();

        Map<Integer, Map<Integer, Game>> tempGames = new HashMap<>();
        for (Game game : games) {
            if (game.getDisplay() == 0) {
                continue;
            }

            int typeId = game.getTypeId();

            if (!tempGames.containsKey(typeId)) {
                tempGames.put(typeId, new HashMap<Integer, Game>());
            }

            tempGames.get(typeId).put(game.getId(), game);
        }

        GAME_CACHE = tempGames;
    }

    public void initGameType() {
        List<GameType> gameTypes = gameTypeService.listAll();

        Map<Integer, Map<Integer, GameType>> tempGameTypes = new HashMap<>();
        for (GameType gameType : gameTypes) {
            if (gameType.getDisplay() == 0) {
                continue;
            }

            int platformId = gameType.getPlatformId();

            if (!tempGameTypes.containsKey(platformId)) {
                tempGameTypes.put(platformId, new HashMap<Integer, GameType>());
            }

            tempGameTypes.get(platformId).put(gameType.getId(), gameType);
        }

        GAME_TYPE_CACHE = tempGameTypes;
    }

    @Override
    public List<Game> listGameByType(int typeId) {
        // 根据类型查找游戏
        Map<Integer, Game> games = GAME_CACHE.get(typeId);
        if (games != null) {
            Collection<Game> values = games.values();
            return new ArrayList<>(values);
        }
        return null;
    }

    @Override
    public List<Game> listGameByPlatform(int platformId) {
        // 根据平台ID查找游戏
        List<Game> games = new ArrayList<>();
        Map<Integer, GameType> types = GAME_TYPE_CACHE.get(platformId);
        if (types == null) {
            return null;
        }

        Set<Integer> typeIds = types.keySet();
        for (Integer typeId : typeIds) {
            Map<Integer, Game> typeGames = GAME_CACHE.get(typeId);
            if (typeGames != null) {
                games.addAll(typeGames.values());
            }
        }

        return games;
    }

    @Override
    public List<GameType> listGameTypeByPlatform(int platformId) {
        Map<Integer, GameType> types = GAME_TYPE_CACHE.get(platformId);
        if (types != null) {
            Collection<GameType> values = types.values();
            return new ArrayList<>(values);
        }
        return null;
    }

    // <userId:<platformId, <model, gameAccount>>>
    private Map<Integer, Map<Integer, Map<Integer, UserGameAccount>>> GAME_ACCOUNT_CACHE = new HashMap<>();

    @Override
    public UserGameAccount getGameAccount(int userId, int platformId, int model) {
        if (GAME_ACCOUNT_CACHE.containsKey(userId)) {
            Map<Integer, Map<Integer, UserGameAccount>> platformAccs = GAME_ACCOUNT_CACHE.get(userId);
            if (platformAccs.containsKey(platformId)) {
                Map<Integer, UserGameAccount> modelAccs = platformAccs.get(platformId);
                if (modelAccs.containsKey(model)) {
                    return modelAccs.get(model);
                }
            }
        }

        UserGameAccount gameAccount = uGameAccountService.get(userId, platformId, model);
        if (gameAccount == null) {
            return null;
        }


        if (!GAME_ACCOUNT_CACHE.containsKey(userId)) {
            GAME_ACCOUNT_CACHE.put(userId, new HashMap<Integer, Map<Integer, UserGameAccount>>());
        }

        Map<Integer, Map<Integer, UserGameAccount>> platformAccs = GAME_ACCOUNT_CACHE.get(userId);
        if (!platformAccs.containsKey(platformId)) {
            platformAccs.put(platformId, new HashMap<Integer, UserGameAccount>());
        }


        Map<Integer, UserGameAccount> modelAccounts = GAME_ACCOUNT_CACHE.get(userId).get(platformId);
        if (!modelAccounts.containsKey(model)) {
            modelAccounts.put(model, gameAccount);
        }

        return gameAccount;
    }

    @Autowired
    private SysNoticeService sysNoticeService;
    private List<SysNoticeVO> sysNoticeVOs = new ArrayList<>();
    private List<SysNoticeVO> sysNoticeSimples = new ArrayList<>();

    private void initSysNotices() {
        List<SysNotice> sysNotices = sysNoticeService.get(30);

        List<SysNoticeVO> sysNoticeVOs = new ArrayList<>();
        List<SysNoticeVO> sysNoticeSimples = new ArrayList<>();
        for (SysNotice sysNotice : sysNotices) {
            sysNoticeVOs.add(new SysNoticeVO(sysNotice.getId(), sysNotice.getTitle(), sysNotice.getDate(), sysNotice.getContent(), sysNotice.getSimpleContent()));
            sysNoticeSimples.add(new SysNoticeVO(sysNotice.getId(), sysNotice.getTitle(), sysNotice.getDate(), sysNotice.getSimpleContent()));
        }

        this.sysNoticeVOs = sysNoticeVOs;
        this.sysNoticeSimples = sysNoticeSimples;
    }

    @Override
    public List<SysNoticeVO> listSysNotices() {
        return sysNoticeVOs;
    }

    @Override
    public List<SysNoticeVO> listSysNoticeSimples() {
        return sysNoticeSimples;
    }

    @Override
    public SysNoticeVO getSysNotice(int id) {
        for (SysNoticeVO sysNoticeVO : sysNoticeVOs) {
            if (sysNoticeVO.getId() == id) {
                return sysNoticeVO;
            }
        }

        return null;
    }

    @Autowired
    private ActivityRedPacketRainTimeService rainTimeService;
    @Autowired
    private ActivityRedPacketRainConfigService rainConfigService;
    private List<ActivityRedPacketRainTime> rainTimes = new ArrayList<>();
    private ActivityRedPacketRainConfigVO rainConfig = null;

    @Override
    // @Scheduled(cron = "0 0 4 * * *") // 初始化红包雨时间
    public synchronized void initActivityRedPacketRainTimes() {
        ActivityRedPacketRainConfig config = rainConfigService.getConfig();
        rainConfig = covertRainConfigVO(config);
        if (config == null || config.getStatus() == 0) {
            rainTimes.clear();
        }
        else {
            String sDate = new Moment().toSimpleDate();
            String eDate = new Moment().fromDate(sDate).add(1, "days").toSimpleDate();
            List<ActivityRedPacketRainTime> times = rainTimeService.listByDate(sDate, eDate);
            rainTimes = times;
        }
    }

    private ActivityRedPacketRainConfigVO covertRainConfigVO(ActivityRedPacketRainConfig config) {
        List<ActivityRedPacketRainConfigRule> rules = JSON.parseArray(config.getRules(), ActivityRedPacketRainConfigRule.class);
        if (CollectionUtils.isEmpty(rules)) {
            return null;
        }

        ActivityRedPacketRainConfigVO configVO = new ActivityRedPacketRainConfigVO();
        configVO.setId(config.getId());
        configVO.setDurationMinutes(config.getDurationMinutes());
        configVO.setHours(config.getHours());
        configVO.setRules(config.getRules());
        configVO.setStatus(config.getStatus());
        configVO.setRuleVOs(rules);

        double minCost = 0;
        for (ActivityRedPacketRainConfigRule rule : rules) {
            if (minCost <= 0) {
                minCost = rule.getMinCost();
            }
            else {
                if (rule.getMinCost() < minCost) {
                    minCost = rule.getMinCost();
                }
            }
        }
        configVO.setMinCost(minCost);
        return configVO;
    }

    @Override
    public List<ActivityRedPacketRainTime> getActivityRedPacketRainTimes() {
        return rainTimes;
    }

    @Override
    public ActivityRedPacketRainTime getCurrentActivityRedPacketRainTime() {
        if (CollectionUtils.isEmpty(rainTimes)) {
            return null;
        }

        if (rainConfig.getStatus() == 0) {
            // 活动未开启
            return null;
        }

        Moment now = new Moment();
        for (ActivityRedPacketRainTime rainTime : rainTimes) {
            // 红包雨开始
            Moment startMoment = new Moment().fromTime(rainTime.getStartTime());
            Moment endMoment = new Moment().fromTime(rainTime.getEndTime()).subtract(2, "seconds"); // 如果距离活动结束时间只剩下2秒,那就不算了
            if (now.between(startMoment, endMoment)) {
                return rainTime;
            }
        }

        return null;
    }

    @Override
    public ActivityRedPacketRainTime getNextActivityRedPacketRainTime(ActivityRedPacketRainTime currentTime) {
        if (CollectionUtils.isEmpty(rainTimes)) {
            return null;
        }

        if (rainConfig.getStatus() == 0) {
            // 活动未开启
            return null;
        }

        if (currentTime == null) {
            Moment now = new Moment();
            for (ActivityRedPacketRainTime rainTime : rainTimes) {
                // 红包雨开始
                Moment startMoment = new Moment().fromTime(rainTime.getStartTime());
                if (startMoment.gt(now)) {
                    return rainTime;
                }
            }
        }
        else {
            for (int i = 0; i < rainTimes.size(); i++) {
                ActivityRedPacketRainTime time = rainTimes.get(i);
                if (time.equalsByTime(currentTime)) {
                    if (i >= rainTimes.size() - 1) {
                        logger.error("红包雨时间出错,请检查");
                    }
                    else {
                        return rainTimes.get(i + 1);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public ActivityRedPacketRainConfigVO getActivityRedPacketRainConfig() {
        return rainConfig;
    }

    @Autowired
    private ActivityFirstRechargeConfigService firstRechargeConfigService;
    private ActivityFirstRechargeConfigVO firstRechargeConfig = null;

    private void initActivityFirstRechargeConfig(){
        ActivityFirstRechargeConfig config = firstRechargeConfigService.getConfig();
        firstRechargeConfig = covertFirstRechargeConfigVO(config);
    }

    private ActivityFirstRechargeConfigVO covertFirstRechargeConfigVO(ActivityFirstRechargeConfig config) {
        List<ActivityFirstRechargeConfigRule> rules = JSON.parseArray(config.getRules(), ActivityFirstRechargeConfigRule.class);
        ActivityFirstRechargeConfigVO configVO = new ActivityFirstRechargeConfigVO();
        configVO.setId(config.getId());
        configVO.setRules(config.getRules());
        configVO.setStatus(config.getStatus());
        configVO.setRuleVOs(rules);

        double minRecharge = 0;
        for (ActivityFirstRechargeConfigRule rule : rules) {
            if (minRecharge <= 0) {
                minRecharge = rule.getMinRecharge();
            }
            else {
                if (rule.getMinRecharge() < minRecharge) {
                    minRecharge = rule.getMinRecharge();
                }
            }
        }
        configVO.setMinRecharge(minRecharge);
        return configVO;
    }

    @Override
    public ActivityFirstRechargeConfigVO getActivityFirstRechargeConfig() {
        return firstRechargeConfig;
    }

    @Autowired
    private ActivityRebateDao aRebateDao;
    private ActivitySignConfigVO activitySignConfigVO;

    private void initActivityRebate() {
        initActivityRebateSign(); // 签到活动
        initActivityRebateWheel(); // 幸运大转盘
    }
    private void initActivityRebateSign() {
        ActivityRebate rebate = aRebateDao.getById(Global.ACTIVITY_REBATE_SIGN);
        String rules = rebate.getRules();
        ActivitySignConfigVO config = JSON.parseObject(rules, ActivitySignConfigVO.class);

        if (config == null) {
            activitySignConfigVO = new ActivitySignConfigVO();
            activitySignConfigVO.setStatus(-1);
        }
        else {
            activitySignConfigVO = config;
            activitySignConfigVO.setStatus(rebate.getStatus());
        }
    }

    @Override
    public ActivitySignConfigVO getActivitySignConfig() {
        return activitySignConfigVO;
    }

    /* 幸运大转盘 */
    private ActivityRebateWheelConfigVO activityRebateWheelConfigVO;

    private void initActivityRebateWheel() {
        ActivityRebate rebate = aRebateDao.getById(Global.ACTIVITY_REBATE_WHEEL);
        String rules = rebate.getRules();
        ActivityRebateWheelConfigVO config = JSON.parseObject(rules, ActivityRebateWheelConfigVO.class);

        if (config == null) {
            activityRebateWheelConfigVO = new ActivityRebateWheelConfigVO();
            activityRebateWheelConfigVO.setStatus(-1);
        }
        else {
            activityRebateWheelConfigVO = config;
            activitySignConfigVO.setStatus(rebate.getStatus());

            if (config.getRules().size() > 0) {
                double minCost = config.getRules().get(0).getMinCost();

                if (config.getRules().size() > 1) {
                    for (int i = 1; i < config.getRules().size(); i++) {
                        if (config.getRules().get(i).getMinCost() < minCost) {
                            minCost = config.getRules().get(i).getMinCost();
                        }
                    }
                }

                activityRebateWheelConfigVO.setMinCost(minCost);
            }
        }
    }

    @Override
    public ActivityRebateWheelConfigVO getActivityRebateWheelConfig() {
        return activityRebateWheelConfigVO;
    }
}