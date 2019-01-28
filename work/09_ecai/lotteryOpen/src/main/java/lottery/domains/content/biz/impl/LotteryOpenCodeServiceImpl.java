package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.open.jobs.MailJob;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.play.HConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

@Service
public class LotteryOpenCodeServiceImpl implements LotteryOpenCodeService {
	private static final Logger log = LoggerFactory.getLogger(LotteryOpenCodeServiceImpl.class);
	private static final String OPEN_CODE_KEY = "OPEN_CODE:%s";
	private static final int OPEN_CODE_MOST_EXPECT = 50; // 保留最近50期开奖号码

	@Autowired
	private LotteryOpenCodeDao lotteryOpenCodeDao;

	@Autowired
	private LotteryDao lotteryDao;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Autowired
	private MailJob mailJob;

	@Autowired
	private DataFactory dataFactory;

	@Override
	public boolean updateOpened(LotteryOpenCode openCode) {
		String updateTime = new Moment().toSimpleTime();
		boolean updated = lotteryOpenCodeDao.updateOpened(openCode.getId(), updateTime);
		return updated;
	}

	@Override
	public boolean updateCancelled(LotteryOpenCode openCode) {
		String updateTime = new Moment().toSimpleTime();
		boolean updated = lotteryOpenCodeDao.updateCancelled(openCode.getId(), updateTime);
		return updated;
	}

	@Override
	public List<LotteryOpenCode> getBeforeNotOpen(String lottery, int count) {
		return lotteryOpenCodeDao.getBeforeNotOpen(lottery, count);
	}

	@Override
	public LotteryOpenCode getByExcept(String lottery, String except) {
		return lotteryOpenCodeDao.getByExcept(lottery, except);
	}

	@Override
	public LotteryOpenCode getByExceptAndUserId(String lottery, int userId, String except) {
		return lotteryOpenCodeDao.getByExceptAndUserId(lottery, userId, except);
	}

	@Override
	public boolean add(LotteryOpenCode openCode) {
		if (!"jsmmc".equals(openCode.getLottery())) {
			boolean hasCaptured = hasCaptured(openCode.getLottery(), openCode.getExpect());
			if (hasCaptured) {
				return false;
			}
		}

		boolean added = lotteryOpenCodeDao.add(openCode);
		if (!added) {
			return false;
		}

		addedToRedis(openCode);

		return added;
	}

	@Override
	public boolean hasCaptured(String lotteryName, String expect) {
		String key = String.format(OPEN_CODE_KEY, lotteryName);

		return jedisTemplate.hexists(key, expect);
	}

	private void addedToRedis(LotteryOpenCode entity) {
		// 操作redis
		String key = String.format(OPEN_CODE_KEY, entity.getLottery());

		if ("jsmmc".equals(entity.getLottery())) {
			// 急速秒秒彩直接存
			String field = entity.getExpect() + ":" + entity.getUserId();
			jedisTemplate.hset(key, field, entity.getCode());
		}
		else {
			// 删除redis中多余的期数，始终保持50期
			Set<String> hkeys = jedisTemplate.hkeys(key);
			if (CollectionUtils.isEmpty(hkeys)) {
				// 最新的设置进去
				jedisTemplate.hset(key, entity.getExpect(), entity.getCode());
			}
			else {
				TreeSet<String> sortHKeys = new TreeSet<>(hkeys);

				// 先设置进去
				String[] expects = sortHKeys.toArray(new String[]{});
				String firstExpect = expects[0];
				if (entity.getExpect().compareTo(firstExpect) > 0) {
					jedisTemplate.hset(key, entity.getExpect(), entity.getCode());
					sortHKeys.add(entity.getExpect());
				}

				if (CollectionUtils.isNotEmpty(sortHKeys) && sortHKeys.size() > OPEN_CODE_MOST_EXPECT) {
					int exceedSize = sortHKeys.size() - OPEN_CODE_MOST_EXPECT;

					Iterator<String> iterator = sortHKeys.iterator();
					int count = 0;
					List<String> delFields = new ArrayList<>();
					while (iterator.hasNext()) {
						if (count >= exceedSize) {
							break;
						}

						delFields.add(iterator.next());
						iterator.remove();
						count++;
					}

					// 删除多余的
					jedisTemplate.hdel(key, delFields.toArray(new String[]{}));
				}
			}
		}
	}

	@Override
	public void initLotteryOpenCode() {
		// 加载所有彩票
		List<Lottery> lotteries = lotteryDao.listAll();
		for (Lottery lottery : lotteries) {
			if (lottery.getId() == 117) continue; // 急速秒秒彩不设置

			initLotteryOpenCodeByLottery(lottery);
			// continue; // 只针对自主彩
		}
	}

	private void initLotteryOpenCodeByLottery(final Lottery lottery) {
		final List<LotteryOpenCode> openCodes = lotteryOpenCodeDao.getLatest(lottery.getShortName(), OPEN_CODE_MOST_EXPECT);
		if (CollectionUtils.isEmpty(openCodes)) {
			return;
		}

		final String key = String.format(OPEN_CODE_KEY, lottery.getShortName());

		// 批量设置值
		final TreeMap<String, String> values = new TreeMap<>();
		for (LotteryOpenCode openCode : openCodes) {
			values.put(openCode.getExpect(), openCode.getCode());
		}
		//
		// // 倒序
		// final NavigableMap<String, String> descendingMap = values.descendingMap();

		// 执行pipeline是为保证最大程度减少redis中没有值的情况，比如删除后重新设置前，那么redis此时处于真空状态
		jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				Pipeline pipelined = jedis.pipelined();

				// 删除原开奖号码
				pipelined.del(key);

				// 重新设置到redis
				for (String expect : values.keySet()) {
					pipelined.hset(key, expect, values.get(expect));
				}

				// 执行
				pipelined.sync();
			}
		});
	}
}