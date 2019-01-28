package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.jdbc.PageList;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.HistoryUserBets;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.vo.user.HistoryUserBetsVO;
import lottery.domains.content.vo.user.UserBetsVO;
import lottery.domains.pool.LotteryDataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserBetsServiceImpl implements UserBetsService {
	private static final Logger log = LoggerFactory.getLogger(UserBetsServiceImpl.class);

	public static final String USER_BETS_UNOPEN_RECENT_KEY = "USER:BETS:UNOPEN:RECENT:%s"; // 用户最近未结算彩票投注ID
																							// 用户ID
	public static final String USER_BETS_OPENED_RECENT_KEY = "USER:BETS:OPENED:RECENT:%s"; // 用户最近已结算彩票投注ID
																							// 用户ID

	@Autowired
	private UserDao uDao;

	@Autowired
	private UserBetsDao uBetsDao;

	@Autowired
	private UserBillService uBillService;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Override
	public PageList search(String keyword, String username, Integer uype, Integer type, Integer lotteryId,
			String expect, Integer ruleId, String minTime, String maxTime, String minPrizeTime, String maxPrizeTime,
			Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple, Double minPrizeMoney,
			Double maxPrizeMoney, Integer status, Integer locked, String ip, int start, int limit) {
		
		StringBuilder querySql = new StringBuilder();
		boolean isSearch = true;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				querySql.append(" and b.user_id = ").append(user.getId());
			} else {
				isSearch = false;
			}
		}

		if (StringUtil.isNotNull(keyword)) {
			if (StringUtil.isInteger(keyword)) {
				querySql.append(" and b.id = " + Integer.parseInt(keyword));
			} else {
				querySql.append(" and b.billno =").append("'" + keyword + "'");
			}
		}

		if (lotteryId != null) {
			querySql.append(" and  b.lottery_id = ").append(lotteryId.intValue());
		}
		if (StringUtil.isNotNull(expect)) {
			querySql.append(" and  b.expect = ").append(expect);
		}
		if (ruleId != null) {
			querySql.append(" and b.rule_id = ").append(ruleId);

		}

		if (type != null) {
			querySql.append(" and b.type = ").append(type);

		}
		if (StringUtil.isNotNull(minTime)) {
			querySql.append(" and b.time > ").append("'" + minTime + "'");
		}
		if (StringUtil.isNotNull(maxTime)) {
			querySql.append(" and b.time < ").append("'" + maxTime + "'");
		}
		if (StringUtil.isNotNull(minPrizeTime)) {
			querySql.append(" and b.prize_time > ").append("'" + minPrizeTime + "'");
		}
		if (StringUtil.isNotNull(maxPrizeTime)) {
			querySql.append(" and b.prize_time < ").append("'" + maxPrizeTime + "'");
		}
		if (minMoney != null) {
			querySql.append(" and b.money >= ").append(minMoney.doubleValue());
		}
		if (maxMoney != null) {
			querySql.append(" and b.money <= ").append(maxMoney.doubleValue());
		}
		if (minMultiple != null) {
			querySql.append(" and b.multiple >= ").append(minMultiple.intValue());
		}
		if (maxMultiple != null) {
			querySql.append(" and b.multiple <= ").append(maxMultiple.intValue());
		}

		if (minPrizeMoney != null) {
			querySql.append(" and b.prize_money >= ").append(minPrizeMoney.doubleValue());
		}
		if (maxPrizeMoney != null) {
			querySql.append(" and b.prize_money <= ").append(maxPrizeMoney.doubleValue());
		}
		if (status != null) {
			querySql.append(" and b.status = ").append(status.intValue());
		}
		if (locked != null) {
			querySql.append(" and b.locked = ").append(locked.intValue());
		}
		if (StringUtil.isNotNull(ip)) {
			querySql.append("  and b.ip = ").append(ip);
		}
		querySql.append(" and b.id > ").append(0);
		if (uype != null) {
			querySql.append("  and u.type = ").append(uype);
		} else {
			querySql.append("  and u.upid != ").append(0);
		}

		querySql.append("  ORDER BY b.time DESC ");

		if (isSearch) {
			List<UserBetsVO> list = new ArrayList<>();
			PageList pList = uBetsDao.find(querySql.toString(), start, limit, false);
			for (Object tmpBean : pList.getList()) {
				UserBetsVO tmpVO = new UserBetsVO((UserBets) tmpBean, lotteryDataFactory, false);
				list.add(tmpVO);
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public PageList searchHistory(String keyword, String username, Integer uypeu, Integer type, Integer lotteryId,
			String expect, Integer ruleId, String minTime, String maxTime, String minPrizeTime, String maxPrizeTime,
			Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple, Double minPrizeMoney,
			Double maxPrizeMoney, Integer status, Integer locked, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if (StringUtil.isNotNull(keyword)) {
			Disjunction disjunction = Restrictions.or();
			if (StringUtil.isInteger(keyword)) {
				disjunction.add(Restrictions.eq("id", Integer.parseInt(keyword)));
			}
			disjunction.add(Restrictions.eq("billno", keyword));
			criterions.add(disjunction);
		}
		if (type != null) {
			criterions.add(Restrictions.eq("type", type.intValue()));
		}
		if (lotteryId != null) {
			criterions.add(Restrictions.eq("lotteryId", lotteryId.intValue()));
		}
		if (StringUtil.isNotNull(expect)) {
			criterions.add(Restrictions.eq("expect", expect));
		}
		if (ruleId != null) {
			criterions.add(Restrictions.eq("ruleId", ruleId));
		}
		if (StringUtil.isNotNull(minTime)) {
			criterions.add(Restrictions.gt("time", minTime));
		}
		if (StringUtil.isNotNull(maxTime)) {
			criterions.add(Restrictions.lt("time", maxTime));
		}
		if (StringUtil.isNotNull(minPrizeTime)) {
			criterions.add(Restrictions.gt("prizeTime", minPrizeTime));
		}
		if (StringUtil.isNotNull(maxPrizeTime)) {
			criterions.add(Restrictions.lt("prizeTime", maxPrizeTime));
		}
		if (minMoney != null) {
			criterions.add(Restrictions.ge("money", minMoney.doubleValue()));
		}
		if (maxMoney != null) {
			criterions.add(Restrictions.le("money", maxMoney.doubleValue()));
		}
		if (minMultiple != null) {
			criterions.add(Restrictions.ge("multiple", minMultiple.intValue()));
		}
		if (maxMultiple != null) {
			criterions.add(Restrictions.le("multiple", maxMultiple.intValue()));
		}
		if (minPrizeMoney != null) {
			criterions.add(Restrictions.ge("prizeMoney", minPrizeMoney.doubleValue()));
		}
		if (maxPrizeMoney != null) {
			criterions.add(Restrictions.le("prizeMoney", maxPrizeMoney.doubleValue()));
		}
		if (status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
		if (locked != null) {
			criterions.add(Restrictions.eq("locked", locked.intValue()));
		}
		criterions.add(Restrictions.gt("id", 0));
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		if (isSearch) {
			List<HistoryUserBetsVO> list = new ArrayList<>();
			PageList pList = uBetsDao.findHistory(criterions, orders, start, limit, false);
			for (Object tmpBean : pList.getList()) {
				HistoryUserBetsVO tmpVO = new HistoryUserBetsVO((HistoryUserBets) tmpBean, lotteryDataFactory, false);
				list.add(tmpVO);
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public List<UserBets> notOpened(int lotteryId, Integer ruleId, String expect, String match) {
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("lotteryId", lotteryId));
		if (ruleId != null) {
			criterions.add(Restrictions.eq("ruleId", ruleId));
		}
		if (StringUtil.isNotNull(expect)) {
			if ("eq".equals(match)) {
				criterions.add(Restrictions.eq("expect", expect));
			}
			if ("le".equals(match)) {
				criterions.add(Restrictions.le("expect", expect));
			}
			if ("ge".equals(match)) {
				criterions.add(Restrictions.ge("expect", expect));
			}
		}
		criterions.add(Restrictions.eq("status", 0));
		List<Order> orders = new ArrayList<>();
		return uBetsDao.find(criterions, orders, false);
	}

	@Override
	public UserBetsVO getById(int id) {
		UserBets entity = uBetsDao.getById(id);
		if (entity != null) {
			UserBetsVO bean = new UserBetsVO(entity, lotteryDataFactory, true);
			return bean;
		}
		return null;
	}

	@Override
	public HistoryUserBetsVO getHistoryById(int id) {
		HistoryUserBets entity = uBetsDao.getHistoryById(id);
		if (entity != null) {
			HistoryUserBetsVO bean = new HistoryUserBetsVO(entity, lotteryDataFactory, true);
			return bean;
		}
		return null;
	}

	@Override
	public boolean cancel(int id) {
		UserBets bBean = uBetsDao.getById(id);
		if (bBean != null) {
			return doCancelOrder(bBean);
		}
		return false;
	}

	@Override
	public boolean cancel(int lotteryId, Integer ruleId, String expect, String match) {
		List<UserBets> list = notOpened(lotteryId, ruleId, expect, match);
		for (UserBets bBean : list) {
			doCancelOrder(bBean);
		}
		return true;
	}

	/**
	 * 取消订单
	 */
	private synchronized boolean doCancelOrder(UserBets bBean) {
		if (bBean.getStatus() == 0) {
			boolean cFlag = uBetsDao.cancel(bBean.getId());
			if (cFlag) {
				User uBean = uDao.getById(bBean.getUserId());
				if (uBean != null) {
					boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), bBean.getMoney(), -bBean.getMoney());
					if (uFlag) {
						cacheUserBetsId(bBean);
						uBillService.addCancelOrderBill(bBean, uBean);
					}
				}
			}
			return cFlag;
		}
		return false;
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

	@Override
	public List<UserBetsVO> getSuspiciousOrder(int userId, int multiple) {
		List<UserBetsVO> formatList = new ArrayList<>();
		List<UserBets> list = uBetsDao.getSuspiciousOrder(userId, multiple, false);
		for (UserBets tmpBean : list) {
			formatList.add(new UserBetsVO(tmpBean, lotteryDataFactory, false));
		}
		return formatList;
	}

	@Override
	public int countUserOnline(Date time) {
		String stime = DateUtil.calcDateByTime(DateUtil.dateToString(time), -600);
		return uBetsDao.countUserOnline(stime);
	}

	@Override
	public double[] getTotalMoney(String keyword, String username, Integer uype, Integer type, Integer lotteryId,
			String expect, Integer ruleId, String minTime, String maxTime, String minPrizeTime, String maxPrizeTime,
			Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple, Double minPrizeMoney,
			Double maxPrizeMoney, Integer status, Integer locked, String ip) {
		Integer userId = null;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
		}
		
		return uBetsDao.getTotalMoney(keyword, userId, uype, type, lotteryId, expect, ruleId, minTime, maxTime,
				minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney,
				status, locked, ip);
	}

	@Override
	public double[] getHistoryTotalMoney(String keyword, String username, Integer utype, Integer type,
			Integer lotteryId, String expect, Integer ruleId, String minTime, String maxTime, String minPrizeTime,
			String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple,
			Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked) {
		Integer userId = null;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
		}
		return uBetsDao.getHistoryTotalMoney(keyword, userId, utype, type, lotteryId, expect, ruleId, minTime, maxTime,
				minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney,
				status, locked);
	}

	@Override
	@Transactional(readOnly = true)
	public double getBillingOrder(int userId, String startTime, String endTime) {
		return uBetsDao.getBillingOrder(userId, startTime, endTime);
	}
	
	@Override
	public UserBets getBetsById(int id) {
		return uBetsDao.getById(id);
	}

}