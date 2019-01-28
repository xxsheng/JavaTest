package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bets.UserBetsAutoCancelAdapter;
import lottery.domains.content.vo.bets.UserBetsCancelAdapter;
import lottery.domains.open.jobs.MailJob;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class UserBetsServiceImpl implements UserBetsService {
	public static final String USER_BETS_UNOPEN_RECENT_KEY = "USER:BETS:UNOPEN:RECENT:%s"; // 用户最近未结算彩票投注ID  用户ID
	public static final String USER_BETS_OPENED_RECENT_KEY = "USER:BETS:OPENED:RECENT:%s"; // 用户最近已结算彩票投注ID  用户ID

	private static final Logger log = LoggerFactory.getLogger(UserBetsServiceImpl.class);
	private BlockingQueue<UserBetsCancelAdapter> cancelChaseQueue = new LinkedBlockingDeque<>();

	private ConcurrentHashMap<String, Boolean> cancelChaseMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Boolean> cancelChaseHisMap = new ConcurrentHashMap<>();

	private BlockingQueue<UserBetsAutoCancelAdapter> cancelTXFFXInvalidQueue = new LinkedBlockingDeque<>();
	private Set<String> cancelTXFFXInvalidSet = Collections.synchronizedSet(new HashSet<String>());

	private BlockingQueue<UserBetsAutoCancelAdapter> cancelTXLHDInvalidQueue = new LinkedBlockingDeque<>();
	private Set<String> cancelTXLHDInvalidSet = Collections.synchronizedSet(new HashSet<String>());

	private static boolean isRunning = false;
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserBetsDao uBetsDao;

	@Autowired
	private LotteryOpenCodeService lotteryOpenCodeService;

	@Autowired
	private MailJob mailJob;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Scheduled(cron = "0/3 * * * * *")
	public void run() {

		synchronized (UserBetsServiceImpl.class) {
			if (isRunning == true) {
				// 任务正在运行，本次中断
				return;
			}
			isRunning = true;
		}
		try {
			cancelChase(); // 撤单追号
			cancelTXFFC(); // 撤单腾讯分分彩
			cancelTXLHD(); // 撤单腾讯龙虎斗
		} finally {
			isRunning = false;
		}
	}

	private void cancelChase() {
		if(cancelChaseQueue != null && cancelChaseQueue.size() > 0) {
			try {
				List<UserBetsCancelAdapter> cancels = new LinkedList<>();
				cancelChaseQueue.drainTo(cancels, 200); // 每次取200条
				if (CollectionUtils.isNotEmpty(cancels)) {
					cancelChase(cancels);
				}
			} catch (Exception e) {
				log.error("撤销用户追单失败", e);
			}
		}
	}

	private void cancelChase(List<UserBetsCancelAdapter> cancels) {
		log.info("正在中奖撤销追号订单，共计{}条追号单需要撤销", cancels.size());

		long start = System.currentTimeMillis();

		int canceledCount = 0;
		for (UserBetsCancelAdapter cancel : cancels) {
			List<UserBets> list = uBetsDao.getByChaseBillno(cancel.getChaseBillno(), cancel.getUserId(), cancel.getWinExpect());
			for (UserBets bBean : list) {
				if(bBean.getStatus() == 0) {
					doCancelOrderByChase(bBean);
					canceledCount++;
				}
			}
		}

		long spent = System.currentTimeMillis() - start;
		log.info("完成中奖撤销追号订单，共计{}条追号单{}条注单,耗时{}", cancels.size(), canceledCount, spent);

		if (spent >= 30000) {
			String warningMsg = String.format("中奖撤销追号订单耗时告警；撤销%s条追号单%s条注单时耗时达到%s", cancels.size(), canceledCount, spent);
			log.warn(warningMsg);
			mailJob.addWarning(warningMsg);
		}
	}
	
	@Override
	public boolean cancelChase(String chaseBillno, int userId, String winExpect) {
		if (!cancelChaseMap.containsKey(chaseBillno) && !cancelChaseHisMap.contains(chaseBillno)) {
			cancelChaseMap.put(chaseBillno, true);
			cancelChaseQueue.offer(new UserBetsCancelAdapter(chaseBillno, userId, winExpect));
		}
		return true;
	}

	@Override
	public boolean isCancelChase(String chaseBillno) {
		return cancelChaseMap.containsKey(chaseBillno) || cancelChaseHisMap.contains(chaseBillno);
	}

	private boolean doCancelOrderByChase(UserBets bBean) {
		try {
			if(bBean.getType() == Global.USER_BETS_TYPE_CHASE && bBean.getStatus() == 0 && bBean.getChaseStop() == 1) {
				boolean canceled = uBetsDao.cancel(bBean.getId());
				if(canceled) {
					boolean added = uBillService.addCancelOrderBill(bBean, "中奖后停止追号返款");
					if(added) {
						cacheUserBetsId(bBean);
						return uDao.updateLotteryMoney(bBean.getUserId(), bBean.getMoney(), -bBean.getMoney());
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error("中奖停止追号出错,注单号：" + bBean.getId() + ",追单号：" + bBean.getChaseBillno(), e);
		}

		return false;
	}



	private void cancelTXFFC() {
		if(cancelTXFFXInvalidQueue != null && cancelTXFFXInvalidQueue.size() > 0) {
			try {
				List<UserBetsAutoCancelAdapter> cancels = new LinkedList<>();
				cancelTXFFXInvalidQueue.drainTo(cancels, 200); // 每次取200条
				if (CollectionUtils.isNotEmpty(cancels)) {
					cancelTXFFC(cancels);
				}
			} catch (Exception e) {
				log.error("撤销腾讯分分彩失败", e);
			}
		}
	}

	private void cancelTXFFC(List<UserBetsAutoCancelAdapter> cancels) {
		long start = System.currentTimeMillis();

		int canceledCount = 0;
		for (UserBetsAutoCancelAdapter cancel : cancels) {
			List<UserBets> list = cancel.getUserBetsList();

			log.info("正在撤销腾讯分分彩无效开奖号码，期号{}", cancel.getLotteryOpenCode().getExpect());

			// 撤销注单
			for (UserBets bBean : list) {
				if(bBean.getStatus() == 0) {
					doCancelOrderByTXFFC(bBean, cancel.getLotteryOpenCode().getCode());
					canceledCount++;
				}
			}

			// 修改开奖号码状态为无效已撤单(2)
			lotteryOpenCodeService.updateCancelled(cancel.getLotteryOpenCode());

			cancelTXFFXInvalidSet.remove(cancel.getLotteryOpenCode().getExpect());
		}

		long spent = System.currentTimeMillis() - start;
		log.info("完成撤销腾讯分分彩无效开奖号码，共计{}期{}条注单,耗时{}", cancels.size(), canceledCount, spent);

		if (spent >= 30000) {
			String warningMsg = String.format("撤销腾讯分分彩无效开奖号码耗时告警；撤销%s期%s条注单时耗时达到%s", cancels.size(), canceledCount, spent);
			log.warn(warningMsg);
			mailJob.addWarning(warningMsg);
		}
	}

	@Override
	public void cancelByTXFFCInvalid(LotteryOpenCode lotteryOpenCode, List<UserBets> userBetsList) {
		if (cancelTXFFXInvalidSet.contains(lotteryOpenCode.getExpect())) {
			return;
		}
		if (!"txffc".equalsIgnoreCase(lotteryOpenCode.getLottery())) {
			return;
		}

		cancelTXFFXInvalidSet.add(lotteryOpenCode.getExpect());
		cancelTXFFXInvalidQueue.offer(new UserBetsAutoCancelAdapter(lotteryOpenCode, userBetsList));
	}

	private boolean doCancelOrderByTXFFC(UserBets bBean, String openCode) {
		try {
			Moment thisTime = new Moment();
			Moment stopTime = new Moment().fromTime(bBean.getStopTime());
			if (bBean.getStatus() == 0 && thisTime.ge(stopTime)) {
				boolean canceled = uBetsDao.cancelAndSetOpenCode(bBean.getId(), openCode);
				if(canceled) {
					boolean added = uBillService.addCancelOrderBill(bBean, "腾讯分分彩重复开奖号码撤单");
					if(added) {
						cacheUserBetsId(bBean);
						return uDao.updateLotteryMoney(bBean.getUserId(), bBean.getMoney(), -bBean.getMoney());
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error("腾讯分分彩无效开奖号码撤单出错,注单号：" + bBean.getId(), e);
		}

		return false;
	}


	private void cancelTXLHD() {
		if(cancelTXLHDInvalidQueue != null && cancelTXLHDInvalidQueue.size() > 0) {
			try {
				List<UserBetsAutoCancelAdapter> cancels = new LinkedList<>();
				cancelTXLHDInvalidQueue.drainTo(cancels, 200); // 每次取200条
				if (CollectionUtils.isNotEmpty(cancels)) {
					cancelTXLHD(cancels);
				}
			} catch (Exception e) {
				log.error("撤销腾讯龙虎斗失败", e);
			}
		}
	}

	private void cancelTXLHD(List<UserBetsAutoCancelAdapter> cancels) {
		long start = System.currentTimeMillis();

		int canceledCount = 0;
		for (UserBetsAutoCancelAdapter cancel : cancels) {
			List<UserBets> list = cancel.getUserBetsList();

			log.info("正在撤销腾讯龙虎斗无效开奖号码，期号{}", cancel.getLotteryOpenCode().getExpect());

			// 撤销注单
			for (UserBets bBean : list) {
				if(bBean.getStatus() == 0) {
					doCancelOrderByTXLHD(bBean, cancel.getLotteryOpenCode().getCode());
					canceledCount++;
				}
			}

			// 修改开奖号码状态为无效已撤单(2)
			lotteryOpenCodeService.updateCancelled(cancel.getLotteryOpenCode());

			cancelTXLHDInvalidSet.remove(cancel.getLotteryOpenCode().getExpect());
		}

		long spent = System.currentTimeMillis() - start;
		log.info("完成撤销腾讯龙虎斗无效开奖号码，共计{}期{}条注单,耗时{}", cancels.size(), canceledCount, spent);

		if (spent >= 30000) {
			String warningMsg = String.format("撤销腾讯龙虎斗无效开奖号码耗时告警；撤销%s期%s条注单时耗时达到%s", cancels.size(), canceledCount, spent);
			log.warn(warningMsg);
			mailJob.addWarning(warningMsg);
		}
	}

	@Override
	public void cancelByTXLHDInvalid(LotteryOpenCode lotteryOpenCode, List<UserBets> userBetsList) {
		if (cancelTXLHDInvalidSet.contains(lotteryOpenCode.getExpect())) {
			return;
		}
		if (!"txlhd".equalsIgnoreCase(lotteryOpenCode.getLottery())) {
			return;
		}

		cancelTXLHDInvalidSet.add(lotteryOpenCode.getExpect());
		cancelTXLHDInvalidQueue.offer(new UserBetsAutoCancelAdapter(lotteryOpenCode, userBetsList));
	}

	private boolean doCancelOrderByTXLHD(UserBets bBean, String openCode) {
		try {
			Moment thisTime = new Moment();
			Moment stopTime = new Moment().fromTime(bBean.getStopTime());
			if (bBean.getStatus() == 0 && thisTime.ge(stopTime)) {
				boolean canceled = uBetsDao.cancelAndSetOpenCode(bBean.getId(), openCode);
				if(canceled) {
					boolean added = uBillService.addCancelOrderBill(bBean, "腾讯龙虎斗重复开奖号码撤单");
					if(added) {
						cacheUserBetsId(bBean);
						return uDao.updateLotteryMoney(bBean.getUserId(), bBean.getMoney(), -bBean.getMoney());
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.error("腾讯龙虎斗无效开奖号码撤单出错,注单号：" + bBean.getId(), e);
		}

		return false;
	}

	@Scheduled(cron = "0 30 5 * * *")
	public void clear() {
		cancelChaseHisMap.clear();
		cancelChaseHisMap.putAll(cancelChaseMap);
		cancelChaseMap.clear();
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
}