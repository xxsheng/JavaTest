package lottery.domains.pool.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import lottery.domains.content.biz.ActivityFirstRechargeConfigService;
import lottery.domains.content.biz.ActivityRedPacketRainTimeService;
import lottery.domains.content.biz.PaymentCardService;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.GameTypeDao;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.dao.LotteryOpenTimeDao;
import lottery.domains.content.dao.LotteryPlayRulesDao;
import lottery.domains.content.dao.LotteryPlayRulesGroupDao;
import lottery.domains.content.dao.LotteryTypeDao;
import lottery.domains.content.dao.PaymentBankDao;
import lottery.domains.content.dao.SysConfigDao;
import lottery.domains.content.dao.SysPlatformDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameAccountDao;
import lottery.domains.content.entity.ActivityFirstRechargeConfig;
import lottery.domains.content.entity.DbServerSync;
import lottery.domains.content.entity.GameType;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.PaymentCard;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.SysConfig;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameAccount;
import lottery.domains.content.entity.activity.ActivityFirstRechargeConfigRule;
import lottery.domains.content.entity.activity.ActivityFirstRechargeConfigVO;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.config.AdminCDNConfig;
import lottery.domains.content.vo.config.AdminGlobalConfig;
import lottery.domains.content.vo.config.AdminGoogleConfig;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.config.DailySettleConfig;
import lottery.domains.content.vo.config.DividendConfig;
import lottery.domains.content.vo.config.DividendConfigRule;
import lottery.domains.content.vo.config.GameDividendConfig;
import lottery.domains.content.vo.config.GameDividendConfigRule;
import lottery.domains.content.vo.config.LotteryConfig;
import lottery.domains.content.vo.config.MailConfig;
import lottery.domains.content.vo.config.PlanConfig;
import lottery.domains.content.vo.config.PortalCDNConfig;
import lottery.domains.content.vo.config.RechargeConfig;
import lottery.domains.content.vo.config.RegistConfig;
import lottery.domains.content.vo.config.VipConfig;
import lottery.domains.content.vo.config.WithdrawConfig;
import lottery.domains.content.vo.payment.PaymentCardVO;
import lottery.domains.content.vo.payment.PaymentChannelSimpleVO;
import lottery.domains.content.vo.payment.PaymentChannelVO;
import lottery.domains.content.vo.user.SysCodeRangeVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import net.sf.json.JSONArray;

@Component
public class LotteryDataFactoryImpl implements LotteryDataFactory, InitializingBean {
	
    @Autowired
    private ActivityFirstRechargeConfigService firstRechargeConfigService;
    private ActivityFirstRechargeConfigVO firstRechargeConfig = null;
	private static final Logger logger = LoggerFactory.getLogger(LotteryDataFactoryImpl.class);

	@Override
	public void init() {
		logger.info("init LotteryDataFactory....start");
		initSysConfig();
		initCDNConfig();
		initSysPlatform();

		initGame();

		initUser();
		initLottery();
		initLotteryType();
		initLotteryOpenTime();
		initLotteryPlayRules();
		initActivityRedPacketRainTimes();
		initPaymentBank();
		initPaymentCard();
		initPaymentChannel();
		initDemoUser();
		initActivityFirstRechargeConfig();
		logger.info("init LotteryDataFactory....done");
	}
	
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
	public void afterPropertiesSet() throws Exception {
		this.init();
	}

	/**
	 * 初始化系统配置
	 */
	@Autowired
	private SysConfigDao sysConfigDao;

	private CodeConfig codeConfig = new CodeConfig();
	private LotteryConfig lotteryConfig = new LotteryConfig();
	private DividendConfig dividendConfig = new DividendConfig();
	private GameDividendConfig gameDividendConfig = new GameDividendConfig();
	private DailySettleConfig dailySettleConfig = new DailySettleConfig();
	private WithdrawConfig withdrawConfig = new WithdrawConfig();
	private RechargeConfig rechargeConfig = new RechargeConfig();
	private VipConfig vipConfig = new VipConfig();
	private PlanConfig planConfig = new PlanConfig();
	private MailConfig mailConfig = new MailConfig();
	private AdminCDNConfig cdnConfig = new AdminCDNConfig();
	private  PortalCDNConfig pcdnConfig = new PortalCDNConfig();
	private AdminGoogleConfig adminGoogleConfig = new AdminGoogleConfig();
	private AdminGlobalConfig adminGlobalConfig = new AdminGlobalConfig();
    private RegistConfig registConfig = new RegistConfig();

	@Autowired
	private ServletContext servletContext;

	private Map<String, DbServerSync> DbServerSyncMap = new HashMap<>();

	private static volatile boolean isRunningSyncInit = false; // 标识任务是否正在运行
	private static Object syncInitLock = new Object(); // 锁
	@Autowired
	private DbServerSyncDao dbServerSyncDao;

	@Scheduled(cron = "4,14,24,34,44,54 * * * * *")
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
						if (DbServerSyncEnum.SYS_CONFIG.name().equals(key)) {
							logger.debug("有新的同步数据：" + key);
							initSysConfig();
						} else if (DbServerSyncEnum.PAYMENT_CARD.name().equals(key)) {
							logger.debug("有新的同步数据：" + key);
							initPaymentCard();
						} else if (DbServerSyncEnum.PAYMENT_CHANNEL.name().equals(key)) {
							logger.debug("有新的同步数据：" + key);
							initPaymentChannel();
						} else if (DbServerSyncEnum.SYS_PLATFORM.name().equals(key)) {
							initSysPlatform();
						} else if (DbServerSyncEnum.ADMIN_CDN.name().equals(key)) {
							logger.debug("有新的同步数据：" + key);
							initCDNConfig();
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
	public void initSysConfig() {
		try {
			List<SysConfig> list = sysConfigDao.listAll();
			for (SysConfig bean : list) {
				String group = bean.getGroup();
				String key = bean.getKey();
				String value = bean.getValue();
				if ("CODE".equals(group)) {
					if ("SYS_CODE".equals(key)) {
						codeConfig.setSysCode(Integer.parseInt(value));
					}
					if ("SYS_NOT_CREATE_ACCOUNT".equals(key)) {
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
							// for (int i = 0; i < ranges.length; i++) {
							// String[] values = ranges[i].split("~");
							// SysCodeRangeVO range = new SysCodeRangeVO();
							// range.setPoint(Double.parseDouble(values[0]));
							// range.setMinPoint(Double.parseDouble(values[1]));
							// range.setMaxPoint(Double.parseDouble(values[2]));
							// range.setDefaultQuantity(Integer.parseInt(values[3]));
							// rlist.add(range);
							// }
							codeConfig.setSysCodeRange(rlist);
						}
					}
				}
				if ("LOTTERY".equals(group)) {
					if ("NOT_BET_POINT_ACCOUNT".equals(key)) {
						lotteryConfig.setNotBetPointAccount(Integer.valueOf(value));
					}
					if ("NOT_BET_POINT".equals(key)) {
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
					if ("AUTO_HIT_RANKING".equals(key)) {
						lotteryConfig.setAutoHitRanking(Integer.parseInt(value) == 1 ? true : false);
					}
					if ("HIT_RANKING_SIZE".equals(key)) {
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
						withdrawConfig.setStatus(Integer.parseInt(value));
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
					if ("SYSTEM_CONSUMPTION_PERCENT".equals(key)) {
						withdrawConfig.setSystemConsumptionPercent(Double.parseDouble(value));
					}
					if ("TRANSFER_CONSUMPTION_PERCENT".equals(key)) {
						withdrawConfig.setTransferConsumptionPercent(Double.parseDouble(value));
					}
					if ("API_PAY_NOTIFY_URL".equals(key)) {
						withdrawConfig.setApiPayNotifyUrl(value);
					}
				}
				if ("RECHARGE".equals(group)) {
					if ("STATUS".equals(key)) {
						rechargeConfig.setStatus(Integer.parseInt(value));
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
                    if("FICTITIOUS_COUNT".equals(key)){
                    	 registConfig.setFictitiousCount(Integer.parseInt(value));
                    }
                    if("FICTITIOUS_PASSWORD".equals(key)){
                   	 registConfig.setFictitiousPassword(value);
                   }
                    
                    
                    if ("DEMO_LOTTERY_MONEY".equals(key)) {
                        registConfig.setDemoLotteryMoney(Double.parseDouble(value));
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
							dividendConfig.addCJZhaoShangScaleConfig(Double.valueOf(values[0]),
									Double.valueOf(values[1]), Double.valueOf(values[2]));
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
						dailySettleConfig
								.setNeibuZhaoShangScale(StringUtils.isEmpty(value) ? 0 : Double.valueOf(value));
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
							gameDividendConfig.addZhuGuanScaleConfig(Double.valueOf(values[0]),
									Double.valueOf(values[1]), Double.valueOf(values[2]));
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
				if ("ADMIN_GOOGLE".equals(group)) {
					if ("LOGIN_STATUS".equals(key)) {
						adminGoogleConfig.setLoginStatus("1".equals(value));
					}
				}
				if ("ADMIN_GLOBAL".equals(group)) {
					if ("LOGO".equals(key)) {
						adminGlobalConfig.setLogo(value);
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
			List<SysConfig> list = sysConfigDao.listAll();
			for (SysConfig bean : list) {
				String group = bean.getGroup();
				String key = bean.getKey();
				String value = bean.getValue();
				if ("ADMIN_CDN".equals(group)) {
					if ("ADMIN_DOMAIN".equals(key)) {
						if (StringUtils.isEmpty(value)) {
							cdnConfig.setCdnDomain("/");
						} else {
							// 随机选择一个CDN域名
							String[] cdnDomains = value.split(",");
							int randomIndex = RandomUtils.nextInt(cdnDomains.length);
							String cdnDomain = cdnDomains[randomIndex];

							if (!cdnDomain.endsWith("/")) {
								cdnConfig.setCdnDomain(cdnDomain + "/");
							} else {
								cdnConfig.setCdnDomain(cdnDomain);
							}
						}
					}
					if ("ADMIN_VERSION".equals(key)) {
						cdnConfig.setCdnVersion(value);
					}
					if (servletContext != null) {
						servletContext.setAttribute("cdnDomain", cdnConfig.getCdnDomain());
						servletContext.setAttribute("cdnVersion", cdnConfig.getCdnVersion());
					}
				}
				
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
								pcdnConfig.setCdnDomain(cdnDomain + "/");
							}
							else {
								pcdnConfig.setCdnDomain(cdnDomain);
							}
						}
					}
					if ("VERSION".equals(key)) {
						cdnConfig.setCdnVersion(value);
					}
					if (servletContext != null) {
						servletContext.setAttribute("pcdnDomain", cdnConfig.getCdnDomain());
						servletContext.setAttribute("pcdnVersion", cdnConfig.getCdnVersion());
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
	public VipConfig getVipConfig() {
		return vipConfig;
	}

	@Override
	public PlanConfig getPlanConfig() {
		return planConfig;
	}

	@Override
	public DividendConfig getDividendConfig() {
		return dividendConfig;
	}

	@Override
	public GameDividendConfig getGameDividendConfig() {
		return gameDividendConfig;
	}

	@Override
	public GameDividendConfigRule determineGameDividendRule(double loss) {
		return gameDividendConfig.determineZhuGuanRule(loss);
	}

	@Override
	public MailConfig getMailConfig() {
		return mailConfig;
	}

	@Override
	public DividendConfigRule determineZhaoShangDividendRule(double dailyBilling) {
		return dividendConfig.determineZhaoShangRule(dailyBilling);
	}

	@Override
	public DividendConfigRule determineCJZhaoShangDividendRule(double dailyBilling) {
		return dividendConfig.determineCJZhaoShangRule(dailyBilling);
	}

	@Override
	public DailySettleConfig getDailySettleConfig() {
		return dailySettleConfig;
	}

	@Override
	public AdminGoogleConfig getAdminGoogleConfig() {
		return adminGoogleConfig;
	}

	@Override
	public AdminGlobalConfig getAdminGlobalConfig() {
		return adminGlobalConfig;
	}

	/**
	 * 初始化平台列表
	 */
	@Autowired
	private SysPlatformDao sysPlatformDao;

	private List<SysPlatform> sysPlatformList = new LinkedList<>();

	@Override
	public void initSysPlatform() {
		try {
			List<SysPlatform> list = sysPlatformDao.listAll();
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

	/**
	 * 初始化用户基础信息
	 */
	@Autowired
	private UserDao userDao;

	private Map<Integer, UserVO> userMap = new LinkedHashMap<>();

	private Map<String, UserVO> userMapWithUserName = new LinkedHashMap<>();

	@Override
	public void initUser() {
		try {
			List<User> list = userDao.listAll();

			Map<Integer, UserVO> tmpMap = new LinkedHashMap<>();
			for (User user : list) {
				tmpMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
			}
			userMap = tmpMap;

			Map<String, UserVO> tmpMapWithUserName = new LinkedHashMap<>();
			for (User user : list) {
				tmpMapWithUserName.put(user.getUsername(), new UserVO(user.getId(), user.getUsername()));
			}
			userMapWithUserName = tmpMapWithUserName;
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
		User user = userDao.getById(id);
		if (user != null) {
			userMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
			userMapWithUserName.put(user.getUsername(), new UserVO(user.getId(), user.getUsername()));
			return userMap.get(id);
		}
		return null;
	}

	@Override
	public UserVO getUser(String username) {
		if (userMapWithUserName.containsKey(username)) {
			return userMapWithUserName.get(username);
		}
		User user = userDao.getByUsername(username);
		if (user != null) {
			userMapWithUserName.put(user.getUsername(), new UserVO(user.getId(), user.getUsername()));
			userMap.put(user.getId(), new UserVO(user.getId(), user.getUsername()));
			return userMapWithUserName.get(username);
		}
		return null;
	}

	/**
	 * 初始化彩票类型
	 */
	@Autowired
	private LotteryTypeDao lotteryTypeDao;

	private Map<Integer, LotteryType> lotteryTypeMap = new LinkedHashMap<>();

	@Override
	public void initLotteryType() {
		try {
			List<LotteryType> list = lotteryTypeDao.listAll();
			Map<Integer, LotteryType> tmpMap = new LinkedHashMap<>();
			for (LotteryType lotteryType : list) {
				tmpMap.put(lotteryType.getId(), lotteryType);
			}
			if (lotteryTypeMap != null) {
				lotteryTypeMap.clear();
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
			if (lotteryMap != null) {
				lotteryMap.clear();
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
	private LotteryOpenTimeDao lotteryOpenTimeDao;

	private List<LotteryOpenTime> lotteryOpenTimeList = new LinkedList<>();
	private Map<String, List<LotteryOpenTime>> lotteryOpenTimeMap = new HashMap<>();

	@Override
	public void initLotteryOpenTime() {
		try {
			List<LotteryOpenTime> list = lotteryOpenTimeDao.listAll();

			Map<String, List<LotteryOpenTime>> tmpOpenTimeMap = new HashMap<>();
			if (CollectionUtils.isNotEmpty(list)) {
				for (LotteryOpenTime lotteryOpenTime : list) {
					if (tmpOpenTimeMap.containsKey(lotteryOpenTime.getLottery())) {
						tmpOpenTimeMap.get(lotteryOpenTime.getLottery()).add(lotteryOpenTime);
					} else {
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
	private LotteryPlayRulesGroupDao lotteryPlayRulesGroupDao;
	@Autowired
	private LotteryPlayRulesDao lotteryPlayRulesDao;

	private List<LotteryPlayRulesGroup> lotteryPlayRulesGroupList = new LinkedList<>();
	private List<LotteryPlayRules> lotteryPlayRulesList = new LinkedList<>();

	@Override
	public void initLotteryPlayRules() {
		try {
			List<LotteryPlayRulesGroup> groupList = lotteryPlayRulesGroupDao.listAll();
			lotteryPlayRulesGroupList = groupList;

			List<LotteryPlayRules> ruleList = lotteryPlayRulesDao.listAll();
			lotteryPlayRulesList = ruleList;
			logger.info("初始化彩票玩法规则完成！");
		} catch (Exception e) {
			logger.error("初始化彩票玩法规则失败！");
		}
	}

	@Override
	public LotteryPlayRules getLotteryPlayRules(int id) {
		for (LotteryPlayRules rule : lotteryPlayRulesList) {
			if (id == rule.getId()) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public LotteryPlayRulesGroup getLotteryPlayRulesGroup(int id) {
		for (LotteryPlayRulesGroup group : lotteryPlayRulesGroupList) {
			if (id == group.getId()) {
				return group;
			}
		}
		return null;
	}

	@Autowired
	private PaymentBankDao paymentBankDao;

	private List<PaymentBank> paymentBankList = new LinkedList<>();

	@Override
	public void initPaymentBank() {
		try {
			List<PaymentBank> list = paymentBankDao.listAll();
			if (paymentBankList != null) {
				paymentBankList.clear();
			}
			paymentBankList.addAll(list);
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
	private PaymentCardService paymentCardService;

	private Map<Integer, PaymentCardVO> paymentCardVOs = new LinkedHashMap<>();

	@Override
	public void initPaymentCard() {
		try {
			List<PaymentCard> list = paymentCardService.listAll();
			Map<Integer, PaymentCardVO> vos = new LinkedHashMap<>();

			if (CollectionUtils.isNotEmpty(list)) {
				for (PaymentCard paymentCard : list) {
					vos.put(paymentCard.getId(), new PaymentCardVO(paymentCard, this));
				}
			}

			paymentCardVOs = vos;
			logger.info("初始化转账银行卡列表完成！");
		} catch (Exception e) {
			logger.error("初始化转账银行卡列表失败！");
		}
	}

	@Override
	public List<PaymentCardVO> listPaymentCard() {
		return new ArrayList<>(paymentCardVOs.values());
	}

	@Override
	public PaymentCardVO getPaymentCard(int id) {
		return paymentCardVOs.get(id);
	}

	@Autowired
	private PaymentChannelService paymentChannelService;

	private Map<Integer, PaymentChannel> PAYMENT_CHANNEL_FULL_PROPERTY_CACHE = new HashMap<>();
	private Map<Integer, PaymentChannelVO> PAYMENT_CHANNEL_VO_CACHE = new HashMap<>();
	private List<PaymentChannelSimpleVO> PAYMENT_CHANNEL_SIMPLE_LIST = new ArrayList<>();

	@Override
	public void initPaymentChannel() {
		List<PaymentChannel> paymentChannels = paymentChannelService.listAllFullProperties();
		if (CollectionUtils.isEmpty(paymentChannels)) {
			PAYMENT_CHANNEL_FULL_PROPERTY_CACHE.clear();
			PAYMENT_CHANNEL_VO_CACHE.clear();
			PAYMENT_CHANNEL_SIMPLE_LIST.clear();
			return;
		}

		Map<Integer, PaymentChannel> tempPaymentChannelsFullProperty = new HashMap<>();
		Map<Integer, PaymentChannelVO> tempPaymentChannelVOs = new HashMap<>();
		List<PaymentChannelSimpleVO> tempPaymentChannelSimpleList = new ArrayList<>();

		for (PaymentChannel paymentChannel : paymentChannels) {
			tempPaymentChannelsFullProperty.put(paymentChannel.getId(), paymentChannel);
			tempPaymentChannelVOs.put(paymentChannel.getId(), new PaymentChannelVO(paymentChannel));
			tempPaymentChannelSimpleList.add(new PaymentChannelSimpleVO(paymentChannel));
		}

		PAYMENT_CHANNEL_FULL_PROPERTY_CACHE = tempPaymentChannelsFullProperty;
		PAYMENT_CHANNEL_VO_CACHE = tempPaymentChannelVOs;
		PAYMENT_CHANNEL_SIMPLE_LIST = tempPaymentChannelSimpleList;
	}
	
	public void initDemoUser() {
		int demoCount = userDao.getDemoUserCount();
		if(demoCount != registConfig.getDemoCount()){
			for(int i = 0 ; i <registConfig.getDemoCount();i++){
				addDemoUser(registConfig.getDemoPassword());
			}
		}
		
		int fCount = userDao.getFictitiousUserCount();
		if(registConfig.getFictitiousCount() != fCount){
			for(int i = 0 ; i <registConfig.getFictitiousCount();i++){
				addFictitiousUser(registConfig.getFictitiousPassword());
			}
		}
		
	}
	
	private void addDemoUser(String parpassword){
	    String username =  StringUtil.getRandomString(8).toLowerCase();
	    if( userDao.getByUsername(username) !=null){
	    	this.addDemoUser(parpassword);
	    }
	    String password = PasswordUtil.generatePasswordByPlainString(parpassword);
		String nickname = "试玩用户";
		double totalMoney = 0;
		double lotteryMoney = 100000;
		double baccaratMoney = 0;
		double freezeMoney = 0;
		double dividendMoney = 0;
		int codeType = 0;
		double extraPoint = 0;
		String registTime = new Moment().toSimpleTime();
		int AStatus = 0;
		int BStatus = 0;
		int allowEqualCode = 0; // 默认允许同级开号
		int onlineStatus = 0;
		int allowTransfers = 0; //默认允许上下级转账
		int allowPlatformTransfers = 0;//默认允许平台转账 1允许
		int allowWithdraw = 0;////默认允许平台取款 1允许
		int loginValidate = 0; // 默认关闭异地登录验证
		int bindStatus = 0; // 绑定状态
		int vipLevel = 0;
		double integral = 0;
		User entity = new User(username, password, nickname, totalMoney, lotteryMoney, baccaratMoney, freezeMoney, dividendMoney,
				2, 0, 1800, 0, 0, codeType, extraPoint, registTime, AStatus, BStatus, onlineStatus, 
				allowEqualCode, allowTransfers, loginValidate, bindStatus, vipLevel, integral,allowPlatformTransfers,allowWithdraw);
		 userDao.add(entity);
	}
	
	public static void main(String[] args) {
		 String password = PasswordUtil.generatePasswordByPlainString("fpw7107");
		 System.out.println(password);
		 
/*		for(int i = 0;i<=30;i++){
			 String chars = "abcdefghijklmnopqrstuvwxyz";
			 Random random = new Random();
			 int rand = random.nextInt(3)+2;
			 String s = "";
			 for(int j = 0;j<rand;j++){
				 s += String.valueOf(chars.charAt((int)(Math.random() * 26)));
			 }
			 int a = (int)(Math.random()*(9999-1000+1))+1000;
			 System.out.println(s+a);
		}*/

	}
	private void addFictitiousUser(String parpassword){
	    String username =  StringUtil.getRandUserName();
	    if( userDao.getByUsername(username) !=null){
	    	this.addFictitiousUser(parpassword);
	    }
	    String password = PasswordUtil.generatePasswordByPlainString(parpassword);
		String nickname = username;
		double totalMoney = 0;
		double lotteryMoney = 100000;
		double baccaratMoney = 0;
		double freezeMoney = 0;
		double dividendMoney = 0;
		int codeType = 0;
		double extraPoint = 0;
		String registTime = new Moment().toSimpleTime();
		int AStatus = 0;
		int BStatus = 0;
		int allowEqualCode = 0; // 默认允许同级开号
		int onlineStatus = 0;
		int allowTransfers = 0; //默认允许上下级转账
		int allowPlatformTransfers = 0;//默认允许平台转账 1允许
		int allowWithdraw = 0;////默认允许平台取款 1允许
		int loginValidate = 0; // 默认关闭异地登录验证
		int bindStatus = 0; // 绑定状态
		int vipLevel = 0;
		double integral = 0;
		User entity = new User(username, password, nickname, totalMoney, lotteryMoney, baccaratMoney, freezeMoney, dividendMoney,
				4, 0, 1800, 0, 0, codeType, extraPoint, registTime, AStatus, BStatus, onlineStatus, 
				allowEqualCode, allowTransfers, loginValidate, bindStatus, vipLevel, integral,allowPlatformTransfers,allowWithdraw);
		 userDao.add(entity);
	}
	

	@Override
	public List<PaymentChannelVO> listPaymentChannelVOs() {
		return new ArrayList<>(PAYMENT_CHANNEL_VO_CACHE.values());
	}

	@Override
	public List<PaymentChannelSimpleVO> listPaymentChannelVOsSimple() {
		return PAYMENT_CHANNEL_SIMPLE_LIST;
	}

	@Override
	public PaymentChannelVO getPaymentChannelVO(int id) {
		return PAYMENT_CHANNEL_VO_CACHE.get(id);
	}

	@Override
	public PaymentChannelVO getPaymentChannelVO(String channelCode, Integer id) {
		for (PaymentChannelVO channel : PAYMENT_CHANNEL_VO_CACHE.values()) {
			if (channelCode.equals(channel.getChannelCode())) {
				if (id == null) {
					return channel;
				}

				if (id == channel.getId()) {
					return channel;
				}
			}
		}

		return null;
	}

	@Override
	public PaymentChannel getPaymentChannelFullProperty(int id) {
		return PAYMENT_CHANNEL_FULL_PROPERTY_CACHE.get(id);
	}

	@Override
	public PaymentChannel getPaymentChannelFullProperty(String channelCode, Integer id) {
		for (PaymentChannel channel : PAYMENT_CHANNEL_FULL_PROPERTY_CACHE.values()) {
			if (channelCode.equals(channel.getChannelCode())) {
				if (id == null) {
					return channel;
				}

				if (id == channel.getId()) {
					return channel;
				}
			}
		}

		return null;
	}

	@Autowired
	private GameTypeDao gameTypeDao;

	private Map<Integer, GameType> GAME_TYPE_CACHE = new HashMap<>();

	@Scheduled(cron = "* 0/3 * * * *")
	public void initGame() {
		initGameType();
	}

	public void initGameType() {
		List<GameType> types = gameTypeDao.listAll();

		Map<Integer, GameType> tempTypes = new HashMap<>();
		for (GameType type : types) {
			tempTypes.put(type.getId(), type);
		}

		GAME_TYPE_CACHE = tempTypes;
	}

	@Override
	public GameType getGameType(int id) {
		return GAME_TYPE_CACHE.get(id);
	}

	@Autowired
	private UserGameAccountDao uGameAccountDao;

	// platformUsername:<platformId, gameaccount>
	private Map<String, Map<Integer, UserGameAccount>> GAME_ACCOUNT_NAME_CACHE = new HashMap<>();

	@Override
	public UserGameAccount getGameAccount(String platformName, int platformId) {
		if (GAME_ACCOUNT_NAME_CACHE.containsKey(platformName)) {
			Map<Integer, UserGameAccount> platformAccs = GAME_ACCOUNT_NAME_CACHE.get(platformName);
			if (platformAccs.containsKey(platformId)) {
				return platformAccs.get(platformId);
			}
		}

		UserGameAccount gameAccount = uGameAccountDao.get(platformName, platformId);
		if (gameAccount == null) {
			return null;
		}

		if (!GAME_ACCOUNT_NAME_CACHE.containsKey(platformName)) {
			GAME_ACCOUNT_NAME_CACHE.put(platformName, new HashMap<Integer, UserGameAccount>());
		}
		Map<Integer, UserGameAccount> platformAccounts = GAME_ACCOUNT_NAME_CACHE.get(platformName);
		if (!platformAccounts.containsKey(platformId)) {
			platformAccounts.put(platformId, gameAccount);
		}

		return gameAccount;
	}

	@Autowired
	private ActivityRedPacketRainTimeService rainTimeService;

	@Override
	// @Scheduled(cron = "0 0 5 * * *")
	public void initActivityRedPacketRainTimes() {
		try {
			rainTimeService.initTimes(2);
			logger.info("初始化红包雨时间完成！");
		} catch (Exception e) {
			logger.error("初始化红包雨时间失败！", e);
		}
	}

	@Override
	public ActivityFirstRechargeConfigVO getActivityFirstRechargeConfig() {
		return firstRechargeConfig;
	}
}