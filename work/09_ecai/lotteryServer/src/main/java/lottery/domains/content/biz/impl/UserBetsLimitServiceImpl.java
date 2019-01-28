package lottery.domains.content.biz.impl;

import com.alibaba.fastjson.JSON;
import javautils.math.MathUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserBetsLimitService;
import lottery.domains.content.dao.UserBetsLimitDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.entity.UserBetsLimit;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.prize.PrizeUtils;
import lottery.web.WSC;
import lottery.web.content.validate.UserBetsValidate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBetsLimitServiceImpl implements UserBetsLimitService {
	private static final int SECS_24_HOURS = 60 * 60 * 24;

	@Autowired
	private UserBetsLimitDao userBetsLimitDao;
	
	@Autowired
	private JedisTemplate jedisTemplate;

	@Autowired
	private DataFactory dataFactory;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserBetsValidate.class);
	


	@Override
	@Transactional(readOnly = true)
	public List<UserBetsLimit> getGlobalSetting() {
		String value = jedisTemplate.get(WSC.USER_LIMIT_KEY + 0);
		if (StringUtils.isNotEmpty(value)) {
			List<UserBetsLimit> cache = JSON.parseArray(value, UserBetsLimit.class);
			if(cache != null){
				LOGGER.debug("从缓存中查询系统投注限额设置,是否为空:{}", cache.isEmpty());
				return cache;
			}
		}

		// List<UserBetsLimit> dbData = userBetsLimitDao.getByUserId(0);
		// jedisTemplate.set(CACHE_USER_BETS_LIMIT + 0, JSON.toJSONString(dbData));
		// return dbData;
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBetsLimit> getByUserId(int userId) {
		String value = jedisTemplate.get(WSC.USER_LIMIT_KEY + userId);
		if (StringUtils.isNotEmpty(value)) {
			List<UserBetsLimit> cache = JSON.parseArray(value, UserBetsLimit.class);
			if(cache != null){
				LOGGER.debug("从缓存中查询系统投注限额设置,是否为空:{}", cache.isEmpty());
				return cache;
			}
		}

		// List<UserBetsLimit> dbData = userBetsLimitDao.getByUserId(userId);
		// jedisTemplate.setex(CACHE_USER_BETS_LIMIT + userId, JSON.toJSONString(dbData), SECS_3_HOURS);
		// return dbData;

		return null;
	}

	/**
	 * 先从用户配置找，如果没有就从全局配置找
	 */
	@Override
	@Transactional(readOnly = true)
	public UserBetsLimit get(int userId, int lotteryId) {
		List<UserBetsLimit> limitConfig =  this.getByUserId(userId);
		List<UserBetsLimit> systemConfig =  this.getGlobalSetting();
		
		if(limitConfig == null || limitConfig.isEmpty()){
			if(systemConfig == null || systemConfig.isEmpty()){
				return null;
			}else {
				LOGGER.debug("用户配置为空，从全局配置匹配");
				for (UserBetsLimit userBetsLimit : systemConfig) {
					if(userBetsLimit.getLotteryId() == lotteryId){
						return userBetsLimit;
					}
				}
			}
			
		}
		
		for (UserBetsLimit userBetsLimit : limitConfig) {
			if(userBetsLimit.getLotteryId() == lotteryId){
				LOGGER.debug("用户投注限额匹配成功!");
				return userBetsLimit;
			}
		}
		
		LOGGER.debug("用户配置没有有效的配置，从全局配置匹配");
		for (UserBetsLimit userBetsLimit : systemConfig) {
			if(userBetsLimit.getLotteryId() == lotteryId){
				return userBetsLimit;
			}
		}
		
		LOGGER.debug("没有匹配到有效的投注限额!");
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public double getMaxPrizeOneExcept(int userId, int lotteryId, String except, double currentMaxPrize) {
		Double total = jedisTemplate.getAsDouble(WSC.USER_CURRENT_PRIZE_KEY + lotteryId + ":" + except + ":" + userId);
		total = total == null ? 0 : total.doubleValue();
		total += currentMaxPrize;
		LOGGER.debug("用户ID :{} 彩种:{} 期号:{} 的已投注最大奖金为:{}", userId, lotteryId, except, MathUtil.doubleToString(total, 4));
		return total;
	}

	@Override
	public void setMaxPrizeOneExcept(int userId, int lotteryId, String except, double maxPrize, boolean isChase) {
		// 设置用户本期奖金
		String userKey = WSC.USER_CURRENT_PRIZE_KEY + lotteryId + ":" + except + ":" + userId;
		double userTotal = jedisTemplate.incrByFloat(userKey, maxPrize);
		if (userTotal == maxPrize) {
			jedisTemplate.expire(userKey, SECS_24_HOURS);
		}

		// 增加本期总奖金
		String expectKey = WSC.LOTTERY_CURRENT_PRIZE_KEY + lotteryId + ":" + except;
		double expectTotal = jedisTemplate.incrByFloat(expectKey, maxPrize);
		if (expectTotal == maxPrize) {
			jedisTemplate.expire(expectKey, SECS_24_HOURS);
		}
	}

	@Override
	public void deleteLimitAfterCancelOder(UserBets userBet) {
		// 减少用户本期总奖金
		Lottery lottery = dataFactory.getLottery(userBet.getLotteryId());
		LotteryPlayRules rule = dataFactory.getLotteryPlayRules(lottery.getId(), userBet.getRuleId());
		double maxUnitPrize = PrizeUtils.getMoney(rule, userBet.getModel() , dataFactory.getLotteryConfig().getbUnitMoney(), userBet.getCode());
		double maxPrize = (maxUnitPrize * userBet.getMultiple());

		String userKey = WSC.USER_CURRENT_PRIZE_KEY + userBet.getLotteryId() + ":" + userBet.getExpect() + ":" + userBet.getUserId();
		Double userTotal = jedisTemplate.getAsDouble(userKey);
		if (userTotal != null) {
			if (maxPrize >= userTotal) {
				jedisTemplate.del(userKey);
			}
			else {
				jedisTemplate.incrByFloat(userKey, -maxPrize);
			}
		}

		// 减少本期总奖金
		String expectKey = WSC.LOTTERY_CURRENT_PRIZE_KEY + userBet.getLotteryId() + ":" + userBet.getExpect();
		Double expectTotal = jedisTemplate.getAsDouble(expectKey);
		if (expectTotal != null) {
			if (maxPrize >= expectTotal) {
				jedisTemplate.del(expectKey);
			}
			else {
				jedisTemplate.incrByFloat(expectKey, -maxPrize);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public double getMaxPrizeOneExcept(int lotteryId, String except) {
		Double total = jedisTemplate.getAsDouble(WSC.LOTTERY_CURRENT_PRIZE_KEY + lotteryId + ":" + except);
		total = total == null ? 0 : total.doubleValue();
		return total;
	}
	
}
