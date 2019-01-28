package lottery.domains.open.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javautils.array.ArrayUtils;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.map.MapUtil;
import javautils.math.MathUtil;
import javautils.random.RandomUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsSettleService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.open.utils.ssc.LotteryCodeUtils;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.open.LotteryOpenUtil;

/**
 * Created by Nick on 2016/11/24.
 */
public abstract class AbstractSelfLotteryOpenCodeJob implements InitializingBean{
    private static final Logger log = LoggerFactory.getLogger("probability");
    private static final String ADMIN_OPEN_CODE_KEY = "ADMIN_OPEN_CODE:%s";
    private static final double DEFAULT_PROBABILITY = 0.15; // 默认杀率

    private static List<String> ALL_SSC_CODES = new ArrayList<>(); // 所有时时彩开奖号码
    private static List<String> ALL_11X5_CODES = new ArrayList<>(); // 所有11选5开奖号码
    private static List<String> ALL_K3_CODES = new ArrayList<>(); // 所有快3开奖号码
    private static List<String> ALL_3D_CODES = new ArrayList<>(); // 所有3D开奖号码
    private static List<String> ALL_PK10_CODES = new ArrayList<>(); // 所有PK10开奖号码

    private static Map<Integer, Integer> THREAD_COUNTS = new HashMap<>(); // 使用多少个线程计算开奖结果,必须能被100000整除

    @Autowired
    protected DataFactory dataFactory;
    @Autowired
    protected LotteryOpenUtil lotteryOpenUtil;
    @Autowired
    protected LotteryOpenCodeService lotteryOpenCodeService;
    @Autowired
    protected UserBetsSettleService userBetsSettleService;
    @Autowired
    protected UserBetsDao userBetsDao;
    @Autowired
    protected LotteryCodeUtils lotteryCodeUtils;
    @Autowired
    protected MailJob mailJob;
    @Autowired
	private JedisTemplate jedisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        ALL_SSC_CODES = new ArrayList<>(lotteryCodeUtils.getSSCCodes());
        ALL_11X5_CODES = new ArrayList<>(lotteryCodeUtils.get11X5Codes());
        ALL_K3_CODES = new ArrayList<>(lotteryCodeUtils.getK3Codes());
        ALL_3D_CODES = new ArrayList<>(lotteryCodeUtils.get3DCodes());
        ALL_PK10_CODES = new ArrayList<>(lotteryCodeUtils.getpk10Codes());

        // TODO 每个彩种使用线程计算的数量，如果总注数不能被指定的线程数整除，那需要修改代码
        THREAD_COUNTS.put(1, 10); // 时时彩  10万注使用10个线程
        THREAD_COUNTS.put(2, 10); // 11选5  55440注使用10个线程
        THREAD_COUNTS.put(3, 1); // 快3   216注使用1个线程
        THREAD_COUNTS.put(4, 1); // 3D  1000注使用1个线程
        THREAD_COUNTS.put(6, 1); // PK10  1728注使用1个线程
    }

    /**
     * 生成开奖号码
     */
    protected LotteryOpenCode generateOpenCode(Lottery lottery, String expect, Integer userId) {
        LotteryOpenCode openCode = generate(lottery, dataFactory.getSelfLotteryConfig().isControl(),
                dataFactory.getSelfLotteryConfig().getProbability(), expect, userId);
        return openCode;
    }

    private LotteryOpenCode generate(Lottery lottery, boolean control, double probability, String expect, Integer userId) {
        String code;
        
        //检查是否redis 已经设置当前期开奖号码
        String key = String.format(ADMIN_OPEN_CODE_KEY, lottery.getShortName());
        boolean iskey = jedisTemplate.hexists(key, expect);
        if(iskey){
        	code = jedisTemplate.hget(key, expect);
        	log.debug("{}第{}期，获取指定开奖号码为{}", lottery.getShowName(), expect, code);
        	long del = jedisTemplate.hdel(key, expect);
        	log.debug("{}第{}期，删除redis开奖号码，结果为{}",lottery.getShowName(), expect, del);
        	return save(lottery, expect, code, userId);
        }
        
        if (!control) {
            // 随机开奖
            code = randomGenerate(lottery);
        }
        else {
            // 杀率开奖
            code = probabilityGenerate(lottery, probability, expect, userId);
        }

        if (StringUtils.isNotEmpty(code)) {
            return save(lottery, expect, code, userId);
        }
        return null;
    }

    private String randomGenerate(Lottery lottery) {
        if (lottery.getType() == 1) {
            return lotteryCodeUtils.randomSSC();
        }
        if (lottery.getType() == 2) {
            return lotteryCodeUtils.random11X5();
        }
        if (lottery.getType() == 3) {
            return lotteryCodeUtils.randomK3();
        }
        if (lottery.getType() == 4) {
            return lotteryCodeUtils.random3D();
        }
        if (lottery.getType() == 6) {
            return lotteryCodeUtils.randompk10();
        }

        return null;
    }

    private List<String> getAllCodes(Lottery lottery) {
        if (lottery.getType() == 1) {
            return ALL_SSC_CODES;
        }
        if (lottery.getType() == 2) {
            return ALL_11X5_CODES;
        }
        if (lottery.getType() == 3) {
            return ALL_K3_CODES;
        }
        if (lottery.getType() == 4) {
            return ALL_3D_CODES;
        }
        if (lottery.getType() == 6) {
            return ALL_PK10_CODES;
        }

        return null;
    }

    private String probabilityGenerate(Lottery lottery, double probability, String expect, Integer userId) {
        List<UserBets> userBets = getUserBetsByExpect(lottery, expect, userId);

        if (CollectionUtils.isEmpty(userBets)) {
            return randomGenerate(lottery);
        }

        double maxPrize = getMaxPrize(lottery.getId(), probability, userBets);

        return open(lottery, userBets, maxPrize);
    }

    private List<UserBets> getUserBetsByExpect(Lottery lottery, String expect, Integer userId) {
    	String hql = "select b  from UserBets  b, User  u  where   u.id = b.userId   and    b.lotteryId = ?0 and  b.status = ?1  and b.expect = ?2 and b.id > 0 and u.upid !=?3 and u.utype=?4";
		Object[] values = { lottery.getId(), Global.USER_BETS_STATUS_NOT_OPEN, expect, 0, 0};
		if (userId != null) {
			hql += "  and b.userId =  " + userId;
		}
		return userBetsDao.getNoDemoUserBetsByExpect(hql, values);
    }

    /**
     * 获取当前最大派奖金额
     */
    private double getMaxPrize(int lotteryId, double probability, List<UserBets> userBets) {

        // String currentDate = DateUtil.getCurrentDate();
        // String startTime = currentDate + " 00:00:00";
        // String endTime = currentDate + " 23:59:59";
        //
        // double[] doubles = userBetsDao.sumProfit(lotteryId, startTime, endTime);
        //
        // // 最大派奖百分比，显示为1以下数值
        // double maxPercent = 1 - probability;
        // if (maxPercent <= 0 || maxPercent >= 1) {
        //     maxPercent = DEFAULT_PROBABILITY;
        // }
        //
        // double betMoney = doubles[0];
        // double prizeMoney = doubles[1];
        //
        // // 最大派奖金额
        // double maxPrize = MathUtil.multiply(betMoney, maxPercent);
        // maxPrize -= prizeMoney;
        // maxPrize = maxPrize <= 0 ? 0 : maxPrize;
        //
        // return maxPrize;


        HashSet<Integer> userIds = new HashSet<>();
        for (UserBets userBet : userBets) {
            if (!userIds.contains(userBet.getUserId())) {
                userIds.add(userBet.getUserId());
            }
        }

        List<?> userBetsList = sumUserProfitGroupByUserId(lotteryId, new ArrayList<>(userIds));
        if (CollectionUtils.isEmpty(userBetsList) || userIds.size() != userBetsList.size()) {
            return 0;
        }

        double maxPercent = 1 - probability;

        if (maxPercent <= 0 || maxPercent >= 1) {
            maxPercent = DEFAULT_PROBABILITY;
        }

        Map<Integer, Double> userMaxPrizes = new HashMap<>();
        for (Object o : userBetsList) {
            Object[] values = (Object[]) o;

            int userId = Integer.valueOf(values[0].toString());
            double money = Double.valueOf(values[1].toString());
            double prizeMoney = Double.valueOf(values[2].toString());

            if (prizeMoney >= money) {
                userMaxPrizes.put(userId, 0d);
            }
            else {
                double maxPrize = MathUtil.multiply(money, maxPercent);
                maxPrize -= prizeMoney;
                maxPrize = maxPrize <= 0 ? 0 : maxPrize;

                userMaxPrizes.put(userId, maxPrize);
            }
        }

        double maxPrize = 0;

        if (!userMaxPrizes.isEmpty()) {
            Map<Integer, Double> sortedPrizeAsc = MapUtil.sortByValueAsc(userMaxPrizes);
            Integer userId = sortedPrizeAsc.keySet().iterator().next();
            return userMaxPrizes.get(userId);
        }

        return maxPrize;
    }

    protected List<Lottery> getSelfLotteries() {
        List<Lottery> lotteries = dataFactory.listSelfLottery();
        if (CollectionUtils.isEmpty(lotteries)) {
            return null;
        }

        List<Lottery> selfLotteries = new ArrayList<>();
        for (Lottery lottery : lotteries) {
            if (lottery.getStatus() != Global.LOTTERY_STATUS_OPEN) {
                continue;
            }
            if (lottery.getId() != 117) {
                selfLotteries.add(lottery);
            }
        }

        return selfLotteries;
    }

    private LotteryOpenCode save(Lottery lottery, String expect, String code, Integer userId) {
        LotteryOpenCode bean = new LotteryOpenCode();
        bean.setUserId(userId);
        bean.setCode(code);
        bean.setOpenStatus(Global.LOTTERY_OPEN_CODE_STATUS_NOT_OPEN);
        bean.setExpect(expect);
        bean.setLottery(lottery.getShortName());
        bean.setTime(new Moment().toSimpleTime());
        bean.setInterfaceTime(new Moment().toSimpleTime());
        lotteryOpenCodeService.add(bean);
        return bean;
    }

    private List<?> sumUserProfitGroupByUserId(int lotteryId, List<Integer> userIds) {
        String currentDate = DateUtil.getCurrentDate();
        String startTime = currentDate + " 00:00:00";
        String endTime = currentDate + " 23:59:59";

        return userBetsDao.sumUserProfitGroupByUserId(lotteryId, startTime, endTime, userIds);
    }

    private String open(final Lottery lottery, final List<UserBets> userBets, final double maxPrize) {
        long start = System.currentTimeMillis();


        int threadCount = THREAD_COUNTS.get(lottery.getType());
        final List<String> allCodes = getAllCodes(lottery);

        int eachCount = allCodes.size() / threadCount;

        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);

        List<Integer> indexes = RandomUtil.randomUniqueNumbers(0, allCodes.size()-1, allCodes.size());

        final AtomicReference<String> randomCode = new AtomicReference();
        final Object codeLock = new Object();

        final ConcurrentHashMap<String, Double> prizeCodes = new ConcurrentHashMap<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                int startSize = i * eachCount;
                int endSize = startSize + eachCount;
                final List<Integer> threadIndexes = indexes.subList(startSize, endSize);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Iterator<Integer> iterator = threadIndexes.iterator();
                            while (iterator.hasNext()) {
                                if (randomCode.get() != null) {
                                    break;
                                }

                                String code = allCodes.get(iterator.next());

                                double totalPrize = 0;
                                for (UserBets userBet : userBets) {

                                    Object[] objects = userBetsSettleService.testUsersBets(userBet, code, lottery, false);
                                    if (objects != null) {
                                        double prize = (Double)objects[1];
                                        totalPrize += prize;
                                    }
                                }

                                if (totalPrize <= maxPrize) {
                                    synchronized (codeLock) {
                                        if (randomCode.get() == null) {
                                            randomCode.set(code);
                                            break;
                                        }
                                    }
                                }
                                else {
                                    prizeCodes.put(code, totalPrize);
                                }
                            }
                        } catch (Exception e) {
                            log.error("杀率计算开奖号码时异常", e);
                        } finally {
                            latch.countDown();
                        }
                    }
                };
                service.submit(runnable);
            }

        } catch (Exception e) {
            log.error("杀率计算开奖号码时异常", e);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("杀率计算开奖号码时阑珊异常", e);
        } finally {
            service.shutdown();
        }

        String code = null;
        if (randomCode.get() != null) {
            code = randomCode.get();
        }
        else {
            if (!prizeCodes.isEmpty()) {
                code = tryRandomGenerate(lottery, userBets, prizeCodes);
            }
        }

        if (StringUtils.isEmpty(code)) {
            code = randomGenerate(lottery);
        }

        long end = System.currentTimeMillis();
        long spend = end - start;

//        if (spend >= 5000) {
//            String warningMsg = "杀率生成" + lottery.getShowName() + userBets.get(0).getExpect() + "开奖号码时耗时缓慢,达到"+spend+"ms,请检查程序";
//            log.error(warningMsg);
//            mailJob.addWarning(warningMsg);
//        }

        prizeCodes.clear();
        indexes.clear();

        return code;
    }

    private String tryRandomGenerate(Lottery lottery, List<UserBets> userBets, ConcurrentHashMap<String, Double> prizeCodes) {
        int firstUserId = userBets.get(0).getUserId();
        boolean isOnlyOnePerson = true;
        for (UserBets userBet : userBets) {
            if (userBet.getUserId() != firstUserId) {
                isOnlyOnePerson = false;
                break;
            }
        }

        if (isOnlyOnePerson) {
            HashSet<Double> uniqueValues = new HashSet<>(prizeCodes.values());
            Double[] prizes = uniqueValues.toArray(new Double[]{});
            ArrayUtils.quickSortDouble(prizes);
            double minPrize = prizes[0];

            LinkedList<String> allMinCodes = new LinkedList<>();
            Iterator<Map.Entry<String, Double>> iterator = prizeCodes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Double> next = iterator.next();
                String randomMinCode = next.getKey();
                double prize = next.getValue();
                if (prize == minPrize) {
                    allMinCodes.add(randomMinCode);
                }
            }

            Random random = new Random();
            int randomIndex = random.nextInt(allMinCodes.size());
            return allMinCodes.get(randomIndex);
        }
        else {
            return randomGenerate(lottery);
        }
    }
}
