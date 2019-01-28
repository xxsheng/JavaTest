package lottery.domains.content.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.math.MathUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsHitRankingService;
import lottery.domains.content.biz.UserBetsPointService;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBetsSettleService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserHighPrizeService;
import lottery.domains.content.biz.UserLotteryDetailsReportService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.content.global.NoticeSendType;
import lottery.domains.content.vo.config.LotteryConfig;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.open.jobs.MailJob;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.play.HConstants;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.MultipleResult;
import lottery.domains.pool.play.TicketPlayHandlerContext;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.play.impl.SSCRX3HHZXPlayHanlder;
import lottery.domains.utils.prize.PrizeUtils;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

/**
 *
 * Created by Nick on 2016/8/29.
 */
@Service
public class UserBetsSettleServiceImpl implements UserBetsSettleService {
    private static final Logger log = LoggerFactory.getLogger(UserBetsSettleServiceImpl.class);
    private static final String USER_CURRENT_PRIZE_KEY = "USER:CURRENT_PRIZE:";
    private static final String LOTTERY_CURRENT_PRIZE_KEY = "LOTTERY:CURRENT_PRIZE:";
    private static final String USER_BETS_NOTICE_KEY = "USER:BETS_NOTICE";

    /**
     * 域：用户ID:注单ID:失效时间
     * 值：json，彩票ID，彩票名称，彩票期号，注单ID，注单状态，投注金额，奖金，玩法名称，通知类型
     */
    public static final String USER_BETS_UNOPEN_RECENT_KEY = "USER:BETS:UNOPEN:RECENT:%s"; // 用户最近未结算彩票投注ID  用户ID
    public static final String USER_BETS_OPENED_RECENT_KEY = "USER:BETS:OPENED:RECENT:%s"; // 用户最近已结算彩票投注ID  用户ID

    private static final int USER_BETS_NOTICE_EXPIRE_HOURS = 20; // 20小时后失效

    //region services
    @Autowired
    private UserBetsDao userBetsDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBillService userBillService;
    @Autowired
    private UserBetsService userBetsService;
    @Autowired
    private UserLotteryReportService userLotteryReportService;
    @Autowired
    private UserLotteryDetailsReportService userLotteryDetailsReportService;
    @Autowired
    private DataFactory dataFactory;
    @Autowired
    private LotteryOpenCodeService lotteryOpenCodeService;
    @Autowired
    private UserBetsHitRankingService userBetsHitRankingService;
    @Autowired
    private UserHighPrizeService highPrizeService;
    @Autowired
    private UserBetsPointService userBetsPointService;
    @Autowired
    private MailJob mailJob;
    @Autowired
    private JedisTemplate jedisTemplate;
    //endregion

    //region 单挑、单期奖金限制
    private static ConcurrentHashMap<String, Double> expectDantiaoPrize = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Double> expectDantiaoUnCachePrize = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Double> userExpectPrize = new ConcurrentHashMap<>();
    //endregion

    @Scheduled(cron = "0 0 6 * * *")
    public void clearCache() {
        expectDantiaoPrize.clear();
        userExpectPrize.clear();
        expectDantiaoUnCachePrize.clear();
    }

    @Override
    public void settleUserBets(List<UserBets> userBetsList, LotteryOpenCode openCode, Lottery lottery) {
        try {
            // 结算注单
            for (UserBets userBets : userBetsList) {
                if (userBets.getTime().compareTo(openCode.getTime()) >= 0) {
                    // 如果投注时间大于抓取时间，不开奖
                    continue;
                }
                else {
                    try {

                        if(userBets.getType() == Global.USER_BETS_TYPE_CHASE
                                && userBets.getChaseStop() == 1){
                            boolean cancelChase = userBetsService.isCancelChase(userBets.getChaseBillno());
                            if (!cancelChase) {
                                calculateUsersBets(userBets, openCode.getCode(), lottery);
                            }
                        }
                        else {
                            calculateUsersBets(userBets, openCode.getCode(), lottery);
                        }
                    } catch (Exception e) {
                        log.error("开奖发生错误", e);
                    }
                }
            }

            // 更新开奖状态
            lotteryOpenCodeService.updateOpened(openCode);
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 计算用户注单
     */
    private void calculateUsersBets(UserBets userBets, String openCode, Lottery lottery){
        long start = System.currentTimeMillis();

        // 计算注单
        Object[] results = testUsersBets(userBets, openCode, lottery, true);

        long spend = System.currentTimeMillis() - start;
        if (spend >= 500) {
            String warningMsg = String.format("开奖耗时告警；计算注单耗时较多；计算注单%s时耗时达到%s", userBets.getId(), spend);
            log.warn(warningMsg);
            mailJob.addWarning(warningMsg);
        }

        if (results == null || results.length <= 0) {
            return;
        }

        int winNum = (Integer) results[0];
        double prize = (Double) results[1];


        start = System.currentTimeMillis();

        // 计算结果
        calculateResult(lottery, userBets, openCode, prize, winNum);

        spend = System.currentTimeMillis() - start;
        if (spend >= 1000) {
            String warningMsg = String.format("开奖耗时告警；处理注单耗时较多；处理注单%s时耗时达到%s", userBets.getId(), spend);
            log.warn(warningMsg);
            mailJob.addWarning(warningMsg);
        }
    }


    public double getWinMoneyByCode(WinResult result,UserBets userBets, int bUnitMoney,
                                    String [] prizeCodes){
        LotteryPlayRules rules = dataFactory.getLotteryPlayRules(userBets.getLotteryId(), userBets.getRuleId());
        String tempPrize = disposeDsPlayPrize(rules,prizeCodes,result.getWinCode());
        double prize = 0;
        if(!tempPrize.equals("")){
            //单注奖金 * 中奖注数 * 倍数
            prize =  MathUtil.multiply(
                    MathUtil.multiply(PrizeUtils.getWinMoneyByCode(rules.getFixed(),
                            userBets.getModel(), bUnitMoney,
                            userBets.getCode(),Double.parseDouble(tempPrize)),
                            result.getWinNum()),
                    userBets.getMultiple());
        }
        return prize;
    }


    public LotteryPlayRules[] getZhWinLev(int winNum, Lottery lottery){
        LotteryPlayRules zuheRules1 = dataFactory.getLotteryPlayRules(lottery.getId(), "dw");//一星奖金配置
        if(zuheRules1 == null){
            return null;
        }
        if(winNum == 1){
            LotteryPlayRules [] res = new LotteryPlayRules[]{zuheRules1};
            return res;
        }
        else if(winNum == 2){
            LotteryPlayRules zuheRules2 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "exzhixdsq");
            LotteryPlayRules [] res = new LotteryPlayRules[]{zuheRules1,zuheRules2};
            return res;
        }
        else if(winNum == 3){
            LotteryPlayRules zuheRules2 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "exzhixdsq");
            LotteryPlayRules zuheRules3 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "sxzhixdsq");
            LotteryPlayRules [] res = new LotteryPlayRules[]{zuheRules1,zuheRules2,zuheRules3};
            return res;
        }
        else if(winNum == 4){
            LotteryPlayRules zuheRules2 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "exzhixdsq");
            LotteryPlayRules zuheRules3 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "sxzhixdsq");
            LotteryPlayRules zuheRules4 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "sixzhixzhq");
            LotteryPlayRules [] res = new LotteryPlayRules[]{zuheRules1,zuheRules2,zuheRules3,zuheRules4};
            return res;
        }
        else if(winNum == 5){
            LotteryPlayRules zuheRules2 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "exzhixdsq");
            LotteryPlayRules zuheRules3 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "sxzhixdsq");
            LotteryPlayRules zuheRules4 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "sixzhixzhq");
            LotteryPlayRules zuheRules5 = dataFactory.getLotteryPlayRules(lottery.getId(),
                    "wxzhixzh");
            LotteryPlayRules [] res = new LotteryPlayRules[]{zuheRules1,zuheRules2,zuheRules3,
                    zuheRules4,zuheRules5};
            return res;
        }
        return null;
    }

    public String disposeDsPlayPrize(LotteryPlayRules rule,String [] codeArr,String winCode){
        String [] pr = rule.getPrize().split(",");
        String res = "";
        for (int i = 0; i < pr.length; i++) {
            String [] valuearr = codeArr[i].split(",");
            for (int j = 0; j < valuearr.length; j++) {
                if(valuearr[j].trim().equals(winCode.trim())){
                    res = pr[i];
                }
            }
        }

        return res;
    }

    public void calculateResult(Lottery lottery, UserBets userBets, String openCode, double prize, int winNum) {
        userBets.setOpenCode(openCode);
        String prizeTime = new Moment().toSimpleTime();
        userBets.setPrizeTime(prizeTime);

        boolean succeed = false;
        if(winNum >0 && prize > 0){
            userBets.setPrizeMoney(prize);
            userBets.setStatus(Global.USER_BETS_STATUS_WIN);

            // 生成赢账单并更新中奖报表
            succeed = userBillService.addUserWinBill(userBets, prize, "派奖");

            if (succeed) {
                // 更新注单状态
                userBetsDao.updateStatus(userBets.getId(), Global.USER_BETS_STATUS_WIN, openCode, prize, prizeTime);

                // 修改用户金额并解冻金额
                userDao.updateLotteryMoney(userBets.getUserId(), prize, -userBets.getMoney());
            }
        }else{
            userBets.setPrizeMoney(0d);
            // 更新注单状态
            if (winNum > 0) {
                userBets.setStatus(Global.USER_BETS_STATUS_WIN);
                userBetsDao.updateStatus(userBets.getId(), Global.USER_BETS_STATUS_WIN, openCode, 0, prizeTime);
            }
            else {
                userBets.setStatus(Global.USER_BETS_STATUS_OPENED);
                userBetsDao.updateStatus(userBets.getId(), Global.USER_BETS_STATUS_OPENED, openCode, 0, prizeTime);
            }

            // 解冻金额
            userDao.updateFreezeMoney(userBets.getUserId(), -userBets.getMoney());
            succeed = true;
        }

        if (succeed == false) {
            String warningMsg = "结算订单出错；ID：" + userBets.getId() + "，新增赢账单时错误";
            log.error(warningMsg);
            mailJob.addWarning(warningMsg);
            return;
        }

        // 更新消费报表
        String time = DateUtil.formatTime(userBets.getTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
        userLotteryReportService.update(userBets.getUserId(), Global.BILL_TYPE_BILLING_ORDER, userBets.getMoney(), time);
        userLotteryDetailsReportService.update(userBets.getUserId(), userBets.getLotteryId(), userBets.getRuleId(), Global.BILL_TYPE_BILLING_ORDER, userBets.getMoney(), time);

        // 返点
        userBetsPointService.add(userBets); // 异步进行

        // 中奖停止撤单
        if(winNum > 0){
            if(userBets.getType() == Global.USER_BETS_TYPE_CHASE
                    && userBets.getChaseStop() == 1){
                userBetsService.cancelChase(userBets.getChaseBillno(), userBets.getUserId(), userBets.getExpect()); // 异步进行
            }
        }

        try {
            // 发送邮件通知
            if (prize >= dataFactory.getMailConfig().getOpen()) {
                userBets.setPrizeMoney(prize);
                mailJob.sendOpen(userBets);
            }

            // 发送自主彩邮件通知
            if (lottery.getSelf() == 1) {
                if (prize >= 200) {
                    UserVO user = dataFactory.getUser(userBets.getUserId());
                    String username = user == null ? "未知" : user.getUsername();
                    mailJob.addWarning("用户"+username+"在自主彩"+lottery.getShowName()+"第"+userBets.getExpect()+"中奖" + prize + "元");
                }
            }
        }
        catch (Exception e) {
            log.error("发送邮件告警错误", e);
        }

        // // 删除用户奖金设置
        // delCurrentPrize(userBets);

        try {
            // 新增中奖排行榜
            if (dataFactory.getLotteryConfig().isAutoHitRanking()) {
                userBetsHitRankingService.addIfNecessary(userBets);
            }

            // 通知用户
             notifyUser(userBets);

            // 移除未结算订单ID，设置已结算订单ID
            cacheUserBetsId(userBets);

            // 尝试添加大额中奖记录
            highPrizeService.addIfNecessary(userBets);
        } catch (Exception e) {
            log.error("添加中奖通知信息时异常", e);
        }
    }

    private void cacheUserBetsId(UserBets userBets) {

        final String unOpenCacheKey = String.format(USER_BETS_UNOPEN_RECENT_KEY, userBets.getUserId());
        final String openedCacheKey = String.format(USER_BETS_OPENED_RECENT_KEY, userBets.getUserId());
        final String userBetsId = userBets.getId() + "";

        try {
            jedisTemplate.execute(new JedisTemplate.PipelineActionNoResult() {
                @Override
                public void action(Pipeline pipeline) {
                    pipeline.lrem(unOpenCacheKey, 1, userBetsId); // 移除未结算订单ID
                    pipeline.lpush(openedCacheKey, userBetsId); // 设置已结算订单ID
                    pipeline.ltrim(openedCacheKey, 0, 10); // 最多设置10条
                    pipeline.expire(openedCacheKey, 60 * 60 * 12); // 12小时过期
                    pipeline.sync();
                }
            });
        } catch (JedisException e) {
            log.error("执行Redis缓存注单ID时出错", e);
        }
    }

     private void notifyUser(UserBets userBets) {
         // 域：用户ID:注单ID:失效时间
         // 值：json，彩票ID，彩票名称，彩票期号，注单ID，注单状态，投注金额，奖金，玩法名称，通知类型
    
         long expireAt = new Moment().add(USER_BETS_NOTICE_EXPIRE_HOURS, "hours").toDate().getTime(); // 20小时后失效
    
         Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
         String field = userBets.getUserId() + ":" + userBets.getId() + ":" + expireAt; // 用户ID:注单ID:过期时间
         Map<String, Object> value = new HashMap<>();
    
         value.put("lotteryId", lottery.getId()); // 彩票ID
         value.put("lottery", lottery.getShowName()); // 彩票名称
         value.put("expect", userBets.getExpect()); // 彩票期号
         value.put("id", userBets.getId()); // 注单ID
         value.put("status", userBets.getStatus()); // 注单状态
         value.put("money", userBets.getMoney()); // 投注金额
         value.put("prizeMoney", userBets.getPrizeMoney()); // 奖金
    
         String mname = "";
         LotteryPlayRules playRules = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
         LotteryPlayRulesGroup group = dataFactory.getLotteryPlayRulesGroup(lottery.getId(), playRules.getGroupId());
         if(playRules != null) {
             mname = "[" + group.getName() + "_" + playRules.getName() + "]";
         }
    
         value.put("mname", mname); // 玩法名称
         value.put("type", NoticeSendType.USER_BETS_NOTICE); // 彩票注单通知类型
    
         jedisTemplate.hset(USER_BETS_NOTICE_KEY, field, JSON.toJSONString(value));
     }

    /**
     * 计算该比投注会中多少钱
     */
    public Object[] testUsersBets(UserBets userBets, String openCode, Lottery lottery, boolean cache){
        LotteryConfig config = dataFactory.getLotteryConfig();

        String codes = userBets.getCodes();

        // 时时彩任三混合组选
        LotteryPlayRules userBetsPlayRules = dataFactory.getLotteryPlayRules(userBets.getLotteryId(), userBets.getRuleId());
        if (lottery.getType() == 1 && userBetsPlayRules.getCode().equals("rx3hhzx")) {
            return rx3hhzxTestUsersBets(userBets, openCode, lottery, cache);
        }

        // 获取玩法对应的处理类
        ITicketPlayHandler handler = TicketPlayHandlerContext.getHandler(lottery.getType(), userBetsPlayRules.getCode());
        if (handler == null) {
            log.error("注单{}未找到对应的处理类", userBets.getId());
            return null;
        }

        // 计算中奖结果
        WinResult result = handler.calculateWinNum(userBets.getId(), codes, openCode);

        /**
         * 计算奖金需要注意的地方：
         *时时彩：
         *1、二星前、后 、大小单双玩法， 根据中奖注数计算中奖金额  ( prize * 注数) ----------
         *3、不定位  前、中、三 一码不定 最多可中三注 ( prize * 注数) ----------
         *4、所有任选玩法   根据中奖注数计算中奖金额  ( prize * 注数) ----------
         *5、三星 混合组选在计算奖金的时候需要根据返回的对象玩法ID（playId）判断金额  -----------
         *6、四、五星组合玩法 需要根据中奖注数 来判断中奖金额 （中奖注数等几星中奖） 1/2/3/4/5  中奖数字 = 对应的金额 --------
         *7、定位胆  = ( prize * 注数) ----------
         *11X5：
         * 1、前三/前二“组选单式” 因为投注时没有去重判断，所有在中奖时需要计算注数 ----------
         * 2、不 定位玩法中奖需要计算注数 ---------
         * 3、定 位胆玩法中奖需要计算注数 ----------
         * 4、 趣味猜中位中奖需要根据中奖号码计算奖金：winCode:为0不需要处理，----------
         *        3和9 =?奖金，4和8 =?奖金，5和7=?奖金,6 =?奖金  返回03 or 09 ……
         * 5、 趣味定单双中奖需要根据中奖号码计算奖金：winCode:为0不需要处理，-----------
         *     5单0双 = ?奖金，4单1双 =?奖金 …… 返回 5单0双，4单1双……
         * 6、任选单式、复式 X中X 都需要根据中奖注数计算奖金 -----------
         *
         *福彩3D、排列3：
         *1、 前三混合组选 需要根据玩法ID（playId）判断奖金 ------------
         *2、定位胆需要根据中奖注数计算奖金 ----------
         *3、不定位需要根据中奖注数计算奖金 -----------
         *
         *PK10：
         *1、定位胆需要根据中奖注数来计算奖金 ------------
         *
         *K3：
         *1、二不同号标准选号计算  “单注奖金 乘以中奖注数” --------
         *2、二不同号手动选号计算  “单注奖金 乘以中奖注数”（投注时没有去不同位置重复号） ---------
         *3、二不同号 胆拖计算 “单注奖金 乘以中奖注数” -----------
         *4、二同号手动选号  “单注奖金 乘以中奖注数” -----------
         *5、快3和值  和值为3或18一等奖；4或17二等奖；5或16三等奖；6或15四等奖；7或14五等奖；8或13六等奖；9或12七等奖；10或11八等奖 ----
         *
         *kl8:
         * 1、趣味和值大小玩法 根据中奖号码来判断奖金
         * 2、趣味 奇偶盘玩法 根据中奖号码来判断奖金
         * 3、趣味 上下盘 根据中奖号码来判断奖金
         * 4、五行玩法 根据中奖号码来判断奖金
         */
        int winNum = 0;
        double prize = 0;
        if(result != null  && (result.getWinNum() >0 || CollectionUtils.isNotEmpty(result.getMultipleResults()))){
            switch (result.getPlayId()) {
                case "1_wxzhixzh": case "1_sixzhixzhh": case "1_sixzhixzhq": // 时时彩 四/五星 组合
                    LotteryPlayRules[] levNum = getZhWinLev(result.getWinNum(),lottery);
                    double zhprize = 0;
                    for (int i = 0; i < levNum.length; i++) {
                        zhprize += PrizeUtils.getBetWinMoney(levNum[i],userBets.getModel(),
                                config.getbUnitMoney(),userBets.getCode());
                    }
                    //单注奖金  * 倍数
                    prize = MathUtil.multiply(zhprize, userBets.getMultiple());
                    //赋值状态
                    winNum = result.getWinNum();
                    break;
                case "1_sxzuxzsh":case "1_sxzuxzlh":case "1_sxzuxzsz":
                case "1_sxzuxzlz":case "1_sxzuxzsq":case "1_sxzuxzlq": // 时时彩混合组选
                case "4_sanxzs":case "4_sanxzl": // 3D 混合组选
                case "1_sxzuxhzh":case "1_sxzuxhzz":case "1_sxzuxhzq":
                    String playId = result.getPlayId().substring(result.getPlayId().indexOf("_")+1,
                            result.getPlayId().length());
                    LotteryPlayRules rulezx = dataFactory.getLotteryPlayRules(lottery.getId(),playId);
                    double prizehh = PrizeUtils.getMoney(rulezx, userBets.getModel(), config.getbUnitMoney(), userBets.getCode());
                    //单注奖金 * 中奖注数 * 倍数
                    prize =  MathUtil.multiply(MathUtil.multiply(prizehh,result.getWinNum()),
                            userBets.getMultiple());
                    //赋值状态
                    winNum = result.getWinNum();
                    break;
                case "1_longhuhewq": case "1_longhuhewb": case "1_longhuhews": case "1_longhuhewg": // 时时彩龙虎和
                case "1_longhuheqb": case "1_longhuheqs": case "1_longhuheqg":
                case "1_longhuhebs": case "1_longhuhebg":
                case "1_longhuhesg":

                case "7_lhd_longhuhewq": case "7_lhd_longhuhewb": case "7_lhd_longhuhews": case "7_lhd_longhuhewg": // 龙虎斗
                case "7_lhd_longhuheqb": case "7_lhd_longhuheqs": case "7_lhd_longhuheqg":
                case "7_lhd_longhuhebs": case "7_lhd_longhuhebg":
                case "7_lhd_longhuhesg":
                	winNum = result.getWinNum();
                    LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
                    String calPrize = rule.getPrize().split(",")[result.getPrizeIndex()];

                    LotteryPlayRules calRule = new LotteryPlayRules();
                    calRule.setFixed(rule.getFixed());
                    calRule.setPrize(calPrize);

                    double prizelh = PrizeUtils.getBetWinMoney(calRule, userBets.getModel(),
                            config.getbUnitMoney(), userBets.getCode());
                    //单注奖金 * 中奖注数 * 倍数
                    prize = MathUtil.multiply(MathUtil.multiply(prizelh,result.getWinNum()),
                            userBets.getMultiple());
                    break;
                case "2_dds": //11x5 定单双
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.dds11x5);
                    break;
                case "2_czw": //11X5猜中位
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.czw11x5);
                    break;
                case "3_hezhi":// 快3和值
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.k3hezhi);
                    break;
                case "5_hezhidx": //快乐8 和值大小
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.kl8hezhidx);
                    break;
                case "5_jopan": //快乐8 和值 奇偶
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.kl8jopan);
                    break;
                case "5_sxpan": //快乐8 和值 上下
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.kl8sxpan);
                    break;
                case "5_hezhiwx": //快乐8 和值 五行
                    winNum = result.getWinNum();
                    prize = getWinMoneyByCode(result,userBets,config.getbUnitMoney(),
                            HConstants.playPrize.kl8wx);
                    break;
                case "6_pk10_dxdsgyhz":// PK10 大小单双冠亚和值
                case "6_pk10_hzgyhz":// PK10 和值冠亚和值
                case "6_pk10_hzqshz":// PK10 和值前三和值
                    if (CollectionUtils.isNotEmpty(result.getMultipleResults())) {
                        winNum = result.getWinNum();

                        LotteryPlayRules ruleDefault = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
                        String[] prizeArr = ruleDefault.getPrize().split(",");
                        double[] prizeArrDouble = new double[prizeArr.length];
                        for (int i = 0; i < prizeArr.length; i++) {
                            prizeArrDouble[i] = Double.valueOf(prizeArr[i]);
                        }

                        List<MultipleResult> multipleResults = result.getMultipleResults();

                        for (MultipleResult multipleResult : multipleResults) {
                            if (multipleResult.getNums() > 0) {
                                // 赔率
                                double odds = prizeArrDouble[multipleResult.getOddsIndex()];

                                // 赔率金额
                                double multiplePrize = PrizeUtils.getPrize(ruleDefault.getFixed(), userBets.getModel(), config.getbUnitMoney(), userBets.getCode(), odds);

                                //单注奖金 * 中奖注数 * 倍数
                                multiplePrize = MathUtil.multiply(MathUtil.multiply(multiplePrize, multipleResult.getNums()),userBets.getMultiple());

                                prize += multiplePrize;
                            }
                        }
                    }
                    break;
                default:
                    if (CollectionUtils.isNotEmpty(result.getMultipleResults())) {
                        winNum = result.getWinNum();

                        LotteryPlayRules ruleDefault = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
                        String[] prizeArr = ruleDefault.getPrize().split(",");
                        double[] prizeArrDouble = new double[prizeArr.length];
                        for (int i = 0; i < prizeArr.length; i++) {
                            prizeArrDouble[i] = Double.valueOf(prizeArr[i]);
                        }

                        List<MultipleResult> multipleResults = result.getMultipleResults();

                        for (MultipleResult multipleResult : multipleResults) {
                            if (multipleResult.getNums() > 0) {
                                // 赔率
                                double odds = prizeArrDouble[multipleResult.getOddsIndex()];

                                // 赔率金额
                                double multiplePrize = PrizeUtils.getPrize(ruleDefault.getFixed(), userBets.getModel(), config.getbUnitMoney(), userBets.getCode(), odds);

                                //单注奖金 * 中奖注数 * 倍数
                                multiplePrize = MathUtil.multiply(MathUtil.multiply(multiplePrize, multipleResult.getNums()),userBets.getMultiple());

                                prize += multiplePrize;
                            }
                        }
                    }
                    else {
                        LotteryPlayRules ruleDefault = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
                        double prizedefailt = PrizeUtils.getBetWinMoney(ruleDefault, userBets.getModel(), config.getbUnitMoney(), userBets.getCode());
                        //单注奖金 * 中奖注数 * 倍数
                        prize = MathUtil.multiply(MathUtil.multiply(prizedefailt,result.getWinNum()), userBets.getMultiple());
                        //赋值状态
                        winNum = result.getWinNum();
                    }
                    break;
            }
        }

        if (winNum > 0 && prize > 0) {
            // 判定是否单挑模式
            prize = assertDantiao(lottery, userBets, prize, cache);

            // 判断是否已经超出本期奖金
            prize = assertExpectPrize(lottery, userBets, prize, cache);
        }

        return new Object[]{winNum, prize};
    }

    /**
     * 时时彩任三混合组选
     */
    public Object[] rx3hhzxTestUsersBets(UserBets userBets, String openCode, Lottery lottery, boolean cache) {
        LotteryConfig config = dataFactory.getLotteryConfig();

        String codes = userBets.getCodes();

        SSCRX3HHZXPlayHanlder hanlder = new SSCRX3HHZXPlayHanlder();
        List<WinResult> results = hanlder.calculateWinNum(userBets.getId(), codes, openCode);
        if (results == null || results.size() <= 0) {
            return new Object[]{0, 0.0};
        }
        else {
            double totalPrize = 0;
            for (WinResult winResult : results) {
                LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), winResult.getPlayId());
                double rulePrize = PrizeUtils.getBetWinMoney(rule, userBets.getModel(), config.getbUnitMoney(),userBets.getCode());

                // 乘注数
                totalPrize += MathUtil.multiply(rulePrize, winResult.getWinNum());
            }
            //单注奖金  * 倍数
            totalPrize = MathUtil.multiply(totalPrize, userBets.getMultiple());

            // 判定是否单挑模式
            totalPrize = assertDantiao(lottery, userBets, totalPrize, cache);

            // 判断是否已经超出本期奖金
            totalPrize = assertExpectPrize(lottery, userBets, totalPrize, cache);

            return new Object[]{1, totalPrize};
        }
    }


    /**
     * 判断单挑
     * 单期同用户多注单共享额度
     */
    private double assertDantiao(Lottery lottery, UserBets userBets, double prize, boolean cacheDantiao) {
        // 判断单挑
        double finalPrize = prize;
        if (lottery.getDantiaoMaxPrize() > 0) {
            LotteryPlayRules playRules = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
            if (StringUtils.isNotEmpty(playRules.getDantiao())) {
                int dantiaoNum = getDantiaoNum(userBets, playRules);

                if (dantiaoNum > 0 && userBets.getNums() <= dantiaoNum) {

                    // String expectKey = userBets.getLotteryId() + "_" + userBets.getExpect();
                    String expectKey = userBets.getLotteryId() + "_" + userBets.getExpect() + "_" + userBets.getUserId();

                    Double beforeTotalPrize = cacheDantiao ? expectDantiaoPrize.get(expectKey) : expectDantiaoUnCachePrize.get(expectKey); // 当期的累计单挑总奖金

                    if (beforeTotalPrize == null) beforeTotalPrize = 0d;

                    if (beforeTotalPrize >= lottery.getDantiaoMaxPrize()) {
                        finalPrize = 0;
                    }
                    else {
                        double expectPrize = beforeTotalPrize + prize;

                        if (expectPrize > lottery.getDantiaoMaxPrize()) {
                            finalPrize = MathUtil.subtract(lottery.getDantiaoMaxPrize(), beforeTotalPrize);

                            if (finalPrize < 0) {
                                finalPrize = 0;
                            }
                        }
                        if (cacheDantiao) {
                            expectDantiaoPrize.put(expectKey, (beforeTotalPrize + finalPrize));
                        }
                        else {
                            expectDantiaoUnCachePrize.put(expectKey, (beforeTotalPrize + finalPrize));
                        }
                    }
                }
            }
        }

        if (cacheDantiao && prize != finalPrize) {
            String warningMsg = lottery.getShowName() + "第" + userBets.getExpect() + ",用户注单" + userBets.getBillno() + "派奖时属于单挑模式，原奖金：" + prize + ",清除后奖金：" + finalPrize;
            log.warn(warningMsg);
            mailJob.addWarning(warningMsg);
        }

        return finalPrize;
    }

    private int getDantiaoNum(UserBets userBets, LotteryPlayRules playRules) {
        if (playRules.getTypeId() != 1) {
            return Integer.valueOf(playRules.getDantiao());
        }

        int totalPosition = getSSCBetTotalPosition(userBets);

        int minPosition;
        switch (playRules.getCode()) {
            case "rx2fs":
            case "rx2ds":
            case "rx2zx":
                minPosition = 2;
                break;
            case "rx3fs":
            case "rx3ds":
            case "rx3z3":
            case "rx3z6":
            case "rx3hhzx":
                minPosition = 3;
                break;
            case "rx4fs":
            case "rx4ds":
                minPosition = 4;
                break;
            // 时时彩中龙虎和玩法投注和号码就算单挑，不论注数
            case "longhuhewq":
            case "longhuhewb":
            case "longhuhews":
            case "longhuhewg":
            case "longhuheqb":
            case "longhuheqs":
            case "longhuheqg":
            case "longhuhebs":
            case "longhuhebg":
            case "longhuhesg":
                if (userBets.getCodes().contains("和")) {
                    return Integer.valueOf(playRules.getDantiao());
                }
                else {
                    return 0;
                }
            // 时时彩中牛牛玩法投注“五条”、“炸弹”、“葫芦”号码就算单挑，不论注数
            case "sscniuniu":
                if (userBets.getCodes().contains("五条")) {
                    return Integer.valueOf(playRules.getDantiao().split(",")[0]);
                }
                else if (userBets.getCodes().contains("炸弹")) {
                    return Integer.valueOf(playRules.getDantiao().split(",")[1]);
                }
                else if (userBets.getCodes().contains("葫芦")) {
                    return Integer.valueOf(playRules.getDantiao().split(",")[2]);
                }
                else {
                    return 0;
                }
            default:
                return Integer.valueOf(playRules.getDantiao());
        }

        if (minPosition > totalPosition) {
            log.error("玩法{}配置错误,单挑规则配置错误[{}]", playRules.getName(), playRules.getDantiao());
            return 0;
        }

        String[] dantiaoNums = playRules.getDantiao().split(",");
        return Integer.valueOf(dantiaoNums[totalPosition - minPosition]);
    }

    private int getSSCBetTotalPosition(UserBets userBets) {
        LotteryPlayRules playRules = dataFactory.getLotteryPlayRules(userBets.getLotteryId(), userBets.getRuleId());
        int totalPosition = 0;
        String[] codes;
        String format;
        switch (playRules.getCode()) {
            case "rx2fs":
            case "rx3fs":
            case "rx4fs":
                codes = userBets.getCodes().split(",");
                for (String code : codes) {
                    if (NumberUtils.isDigits(code)) {
                        totalPosition++;
                    }
                }
                return totalPosition;
            case "rx2ds":
            case "rx3ds":
            case "rx4ds":
            case "rx3z3":
            case "rx3z6":
            case "rx3hhzx":
            case "rx2zx":
                format = StringUtil.substring(userBets.getCodes(), "[", "]", false);
                return StringUtils.countMatches(format, "√");
            default:
                return 0;
        }
    }

    /**
     * 判断用户当期奖金是否超额，超出则清零,cacheExpect如果为false，则此处逻辑无效，直接返回参数prize
     * 单期同用户多注单共享额度
     */
    private double assertExpectPrize(Lottery lottery, UserBets userBets, double prize, boolean cachePrize) {
        if (cachePrize == false || lottery.getExpectMaxPrize() <= 0) {
            return prize;
        }

        String prizeKey = userBets.getLotteryId() + "_" + userBets.getExpect() + "_" + userBets.getUserId();
        Double beforeTotalPrize = userExpectPrize.get(prizeKey);
        if (beforeTotalPrize == null) beforeTotalPrize = 0d;

        double expectTotalPrize = beforeTotalPrize + prize;

        double finalPrize = prize;

        if (expectTotalPrize > lottery.getExpectMaxPrize()) {
            finalPrize = MathUtil.subtract(lottery.getExpectMaxPrize(), beforeTotalPrize);

            if (finalPrize < 0) {
                finalPrize = 0;
            }
        }
        if (prize != finalPrize) {
            mailJob.sendExpectExceedPrize(userBets, prize, finalPrize);
        }

        userExpectPrize.put(prizeKey, (beforeTotalPrize + finalPrize));

        return finalPrize;
    }
}