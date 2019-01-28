package lottery.domains.open.jobs;

import javautils.date.Moment;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBetsSettleService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserBetsOriginalDao;
import lottery.domains.content.dao.UserBetsRiskDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.open.LotteryOpenUtil;
import lottery.domains.utils.open.OpenTime;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 风控注单开奖任务 Created by Nick on 2016/10/19.
 */
@Component
public class LotteryRiskOpenJob {
	private final static Logger log = LoggerFactory.getLogger(LotteryRiskOpenJob.class);
	private static boolean isRuning = false;
	@Autowired
	protected UserBetsDao userBetsDao;
	@Autowired
	protected UserBetsRiskDao userBetsRiskDao;
	@Autowired
	private UserDao uDao;
	@Autowired
	private UserBetsOriginalDao uBetsOriginalDao;

	@Autowired
	protected LotteryOpenCodeService lotteryOpenCodeService;
	@Autowired
	protected UserBetsSettleService userBetsSettleService;
	@Autowired
	private UserBetsService uBetsService;
	@Autowired
	private UserBillService uBillService;
	@Autowired
	protected DataFactory dataFactory;
	@Autowired
	protected LotteryOpenUtil lotteryOpenUtil;

//	@Scheduled(cron = "0/4 * * * * *")
	public void openUserBetsRisk() {
		synchronized (LotteryRiskOpenJob.class) {
			if (isRuning == true) {
				return;
			}
			isRuning = true;
		}

		try {
			long start = System.currentTimeMillis();

			// 开奖
			int total = open();
			long spend = System.currentTimeMillis() - start;

			if (total > 0) {
				log.debug("开奖完成,共计开奖" + total + "条注单,耗时" + spend);
			}
		} catch (Exception e) {
			log.error("开奖异常", e);
		} finally {
			isRuning = false;
		}
	}

	private int open() {
		int total = 0;
		List<Lottery> lotteries = dataFactory.listLottery();
		for (Lottery lottery : lotteries) {
			if (!"jsmmc".equalsIgnoreCase(lottery.getShortName())) {
//				int lotteryTotal = openByLottery(lottery);
//				total += lotteryTotal;
				Map<Integer,List<UserBetsRisk>> result = openByLottery(lottery);
				for (Integer uid : result.keySet()) {
					List<UserBetsRisk> ubRisk = result.get(uid);
					double sumMoney = 0;
					double sumPrize = 0;
					for (UserBetsRisk risk : ubRisk) {
						sumMoney += risk.getMoney();
						sumPrize += risk.getPrizeMoney();
					}
					//如果中奖比投注大，全部退还
					if(sumPrize > sumMoney){
						for (UserBetsRisk risk : ubRisk) {
							updateByHit(risk, risk.getOpenCode(), risk.getPrizeMoney(), risk.getWinNum());
						}
					}else{
						for (UserBetsRisk risk : ubRisk) {
							if(risk.getPrizeMoney() > 0){
								updateByUnHit(risk, risk.getOpenCode(),Global.USER_BETS_STATUS_WIN, risk.getPrizeMoney(), risk.getWinNum());
							}else {
								updateByUnHit(risk, risk.getOpenCode(), Global.USER_BETS_STATUS_OPENED, 0, 0);
							}
						}
					}
				}
			}
		}

		return total;
	}

	private Map<Integer,List<UserBetsRisk>> openByLottery(Lottery lottery) {
		int total = 0;
		Map<Integer,List<UserBetsRisk>> rsMap = new HashMap<Integer,List<UserBetsRisk>>();
		try {
			// 获取注单
			List<UserBetsRisk> userBetses = getUserBetsMonitoring(lottery.getId());
			if (CollectionUtils.isEmpty(userBetses)) {
				return rsMap;
			}

			// 组装开奖号码
			LinkedHashMap<LotteryOpenCode, List<UserBetsRisk>> groupByOpenCodes = groupUserBetsByOpenCode(lottery,
					userBetses);
			if (groupByOpenCodes.isEmpty()) {
				return rsMap;
			}

			// 按照开奖期号进行分组
			Map<String, LotteryOpenCode> openCodeMap = new HashMap<>();
			for (LotteryOpenCode lotteryOpenCode : groupByOpenCodes.keySet()) {
				openCodeMap.put(lotteryOpenCode.getExpect(), lotteryOpenCode);
			}

			
			for (Map.Entry<LotteryOpenCode, List<UserBetsRisk>> groupByOpenCode : groupByOpenCodes.entrySet()) {
				LotteryOpenCode openCode = groupByOpenCode.getKey(); // 开奖号码
				List<UserBetsRisk> expectUserBets = groupByOpenCode.getValue(); // 当前期注单列表

				// 腾讯分分彩自动撤单
				if ("txffc".equalsIgnoreCase(openCode.getLottery())
						&& openCode.getOpenStatus() == Global.LOTTERY_OPEN_CODE_STATUS_UNCANCEL) {
					updateByCancel(expectUserBets, openCode.getCode());
					continue;
				}

				// 腾讯龙虎斗自动撤单
				if ("txlhd".equalsIgnoreCase(openCode.getLottery())
						&& openCode.getOpenStatus() == Global.LOTTERY_OPEN_CODE_STATUS_UNCANCEL) {
					updateByCancel(expectUserBets, openCode.getCode());
					continue;
				}

				// 计算是否中奖
				for (UserBetsRisk expectUserBet : expectUserBets) {
					UserBets userBets = expectUserBet.toUserBets();

					Object[] results = userBetsSettleService.testUsersBets(userBets, openCode.getCode(), lottery,
							false);

					if (results == null) {
						updateByHit(expectUserBet, openCode.getCode(), 0, 0);
					} else {
						int winNum = (Integer) results[0];
						double prize = (Double) results[1];
						expectUserBet.setPrizeMoney(prize);
						expectUserBet.setWinNum(winNum);
						expectUserBet.setOpenCode(openCode.getCode());
						
						//按用户分组存储风控结果
						if(rsMap.containsKey(expectUserBet.getUserId())){
							List<UserBetsRisk> urisks = rsMap.get(expectUserBet.getUserId());
							urisks.add(expectUserBet);
							rsMap.put(expectUserBet.getUserId(), urisks);
						}else{
							List<UserBetsRisk> urisks = new ArrayList<UserBetsRisk>();
							urisks.add(expectUserBet);
							rsMap.put(expectUserBet.getUserId(), urisks);
						}
						
						//判断投入是否大于中奖
						/*if (prize > 0 && prize > userBets.getMoney()) {
							updateByHit(expectUserBet, openCode.getCode(), prize, winNum);
						}else if(prize > 0){
							updateByUnHit(expectUserBet, openCode.getCode(),Global.USER_BETS_STATUS_WIN, prize, winNum);
						}else {
							updateByUnHit(expectUserBet, openCode.getCode(), Global.USER_BETS_STATUS_OPENED, 0, 0);
						}*/
					}
				}
			}
		} catch (Exception e) {
			log.error("开奖异常:" + lottery.getShowName(), e);
		}
		return rsMap;
	}

	/**
	 * 注单中奖，注单修改为系统返还，并退还用户金额
	 */
	private void updateByHit(UserBetsRisk userBetsRisk, String lotteryOpenCode, double prizeMoney,
			int winNum) {
		// 修改风控投注单
		boolean updatedRiskOrder = updateStatus(userBetsRisk, lotteryOpenCode, Global.USER_BETS_STATUS_SYS_RETURNED,
				prizeMoney, winNum);
		if (updatedRiskOrder) {
			int userId = userBetsRisk.getUserId();
			double money = userBetsRisk.getMoney();

			uDao.updateLotteryMoney(userId, money);
		}
	}

	/**
	 * 注单撤单（腾讯分分彩、腾讯龙虎斗等相同开奖号码的注单），将数据转移到正常投注记录表、生成账单、增加原始投注记录
	 */
	private void updateByCancel(List<UserBetsRisk> userBetsRiskList, String lotteryOpenCode) {
		for (UserBetsRisk userBetsRisk : userBetsRiskList) {
			// 修改风控投注单
			boolean updatedRiskOrder = updateStatus(userBetsRisk, lotteryOpenCode, Global.USER_BETS_STATUS_CANCELED, 0,
					0);
			if (updatedRiskOrder) {
				moveToRealData(userBetsRisk);
			}
		}
	}

	/**
	 * 注单未中奖，将数据转移到正常投注记录表、生成账单、增加原始投注记录
	 */
	private void updateByUnHit(UserBetsRisk userBetsRisk, String lotteryOpenCode, int status, double prizeMoney, int winNum ) {
		// 修改风控投注单
		boolean updatedRiskOrder = updateStatus(userBetsRisk, lotteryOpenCode, status, prizeMoney, winNum);
		if (updatedRiskOrder) {
			moveToRealData(userBetsRisk);
		}
	}

	/**
	 * 注单转移到正式数据，进行正常开奖
	 */
	private boolean moveToRealData(UserBetsRisk userBetsRisk) {
		UserBets userBets = userBetsRisk.toUserBets();
		userBets.setStatus(Global.USER_BETS_STATUS_NOT_OPEN);
		// 转移到正常投注记录表
		boolean addedUserBets = userBetsDao.add(userBets);

		if (addedUserBets) {
			// 生成投注账单
			boolean addedBill = uBillService.addSpendBill(userBets);

			if (addedBill) {
				// 增加原始投注记录
				return addOriginalUserBets(userBets);
			}
		}

		return addedUserBets;
	}

	private boolean updateStatus(UserBetsRisk userBetsRisk, String openCode, int status, double prizeMoney, int winNum) {
		int id = userBetsRisk.getId();

		int fromStatus = Global.USER_BETS_STATUS_MONITORING;
		int toStatus = status;
//		String openCode = lotteryOpenCode.getCode();
		String prizeTime = new Moment().toSimpleTime();

		return userBetsRiskDao.updateStatus(id, fromStatus, toStatus, openCode, prizeMoney, prizeTime, winNum);
	}

	private boolean addOriginalUserBets(UserBets userBets) {
		try {
			UserBetsOriginal original = new UserBetsOriginal(userBets);
			return uBetsOriginalDao.add(original); // 增加原始注单记录
		} catch (Exception e) {
			log.error("增加原始注单时发生异常", e);
		}

		return false;
	}

	private LinkedHashMap<LotteryOpenCode, List<UserBetsRisk>> groupUserBetsByOpenCode(Lottery lottery,
			List<UserBetsRisk> userBetses) {

		// 按期数进行分数
		LinkedHashMap<String, List<UserBetsRisk>> groupByExpect = new LinkedHashMap<>();
		for (UserBetsRisk userBet : userBetses) {
			String key = userBet.getExpect();

			if (!groupByExpect.containsKey(key)) {
				groupByExpect.put(key, new LinkedList<UserBetsRisk>());
			}

			groupByExpect.get(key).add(userBet);
		}

		LinkedHashMap<LotteryOpenCode, List<UserBetsRisk>> groupByCode = new LinkedHashMap<>(); // LotteryOpenCode:UserBetsRisk
		LinkedHashMap<String, LotteryOpenCode> expectCodes = new LinkedHashMap<>(); // expect:LotteryOpenCode
		for (Map.Entry<String, List<UserBetsRisk>> stringListEntry : groupByExpect.entrySet()) {
			String expect = stringListEntry.getKey();

			// 查找开奖号码
			if (!expectCodes.containsKey(expect)) {
				// 查找开奖号码
				LotteryOpenCode lotteryOpenCode = lotteryOpenCodeService.getByExcept(lottery.getShortName(), expect);
				if (lotteryOpenCode == null || StringUtils.isEmpty(lotteryOpenCode.getCode())) {
					expectCodes.put(expect, new LotteryOpenCode()); // 实际为空，只是标识一下
				} else {
					expectCodes.put(expect, lotteryOpenCode);
				}
			}

			LotteryOpenCode openCode = expectCodes.get(expect);
			if (openCode == null || StringUtils.isEmpty(openCode.getCode())) {
				continue;
			}

			if (!groupByCode.containsKey(openCode)) {
				groupByCode.put(openCode, new LinkedList<UserBetsRisk>());
			}

			groupByCode.get(openCode).addAll(stringListEntry.getValue());
		}

		return groupByCode;
	}

	private List<UserBetsRisk> getUserBetsMonitoring(int lotteryId) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("status", Global.USER_BETS_STATUS_MONITORING));// 风控中
		criterions.add(Restrictions.eq("lotteryId", lotteryId));
		criterions.add(Restrictions.gt("id", 0));

		OpenTime currOpenTime;
		try {
			currOpenTime = lotteryOpenUtil.getCurrOpenTime(lotteryId);
			if (currOpenTime == null) {
				log.error("开奖获取当前期数时为空,彩票ID：" + lotteryId);
				return null;
			}
		} catch (Exception e) {
			log.error("开奖获取当前期数时异常,彩票ID：" + lotteryId, e);
			return null;
		}

		criterions.add(Restrictions.lt("expect", currOpenTime.getExpect())); // 小于当前期

		// 查询注单
		List<UserBetsRisk> betsList = userBetsRiskDao.list(criterions, null);

		return betsList;
	}
}
