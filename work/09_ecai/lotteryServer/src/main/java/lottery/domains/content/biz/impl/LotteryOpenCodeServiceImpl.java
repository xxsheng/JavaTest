package lottery.domains.content.biz.impl;

import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WSC;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;

@Service
public class LotteryOpenCodeServiceImpl implements LotteryOpenCodeService {
	@Autowired
	private LotteryOpenCodeDao lotteryOpenCodeDao;
	@Autowired
	private DataFactory dataFactory;
	@Autowired
	private JedisTemplate jedisTemplate;

	@Override
	@Transactional(readOnly = true)
	public List<LotteryOpenCodeVO> getLatestFromRedis(String lotteryName) {
		String key = String.format(WSC.OPEN_CODE_KEY, lotteryName);

		Map<String, String> expectsCodes = jedisTemplate.hgetAll(key);

		// 倒序排序
		TreeMap<String, String> sortMap = new TreeMap<>(expectsCodes);
		NavigableMap<String, String> navigableMap = sortMap.descendingMap();

		Lottery lottery = dataFactory.getLottery(lotteryName);

		return transferResult(navigableMap, lottery);
	}

	@Override
	@Transactional(readOnly = true)
	public List<LotteryOpenCodeVO> getLatestFromRedis(final List<String> lotteryNames) {
		List<LotteryOpenCodeVO> openCodeVOs = new ArrayList<>();

		final Map<String,Response<Map<String,String>>> responses = new HashMap<>(lotteryNames.size());
		jedisTemplate.execute(new JedisTemplate.PipelineAction() {
			@Override
			public List<Object> action(Pipeline pipeline) {
				for (String lotteryName : lotteryNames) {

					String key = String.format(WSC.OPEN_CODE_KEY, lotteryName);
					Response<Map<String, String>> mapResponse = pipeline.hgetAll(key);
					responses.put(lotteryName, mapResponse);
				}
				pipeline.sync();
				return null;
			}
		});

		for (String lotteryName : lotteryNames) {
			Lottery lottery = dataFactory.getLottery(lotteryName);
			Response<Map<String, String>> mapResponse = responses.get(lotteryName);
			if (mapResponse != null) {
				Map<String, String> expectsCodes = mapResponse.get();
				if (MapUtils.isNotEmpty(expectsCodes)) {

					// 倒序排序
					TreeMap<String, String> sortMap = new TreeMap<>(expectsCodes);
					NavigableMap<String, String> navigableMap = sortMap.descendingMap();

					List<LotteryOpenCodeVO> lotteryOpenCodeVOs = transferResult(navigableMap, lottery);

					if (CollectionUtils.isNotEmpty(lotteryOpenCodeVOs)) {
						openCodeVOs.addAll(lotteryOpenCodeVOs);
					}
				}
			}
		}

		return openCodeVOs;
	}

	@Override
	public void delJSMMCOpenCodeFromRedis(String expect, int userId) {
		String key = String.format(WSC.OPEN_CODE_KEY, "jsmmc");
		String field = expect + ":" + userId;
		jedisTemplate.hdel(key, field);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasCaptured(String lotteryName, String expect) {
		String key = String.format(WSC.OPEN_CODE_KEY, lotteryName);

		return jedisTemplate.hexists(key, expect);
	}

	private List<LotteryOpenCodeVO> transferResult(NavigableMap<String, String> navigableMap, Lottery lottery) {
		List<LotteryOpenCodeVO> openCodeVOs = new ArrayList<>();

		Iterator<String> iterator = navigableMap.keySet().iterator();
		while (iterator.hasNext()) {

			String key = iterator.next();
			String expect;
			Integer userId = null;
			if (lottery.getId() == 117) {
				String[] split = key.split(":");
				expect = split[0];
				userId = Integer.valueOf(split[1]);
			}
			else {
				expect = key;
			}

			String code = navigableMap.get(key);

			LotteryOpenCodeVO openCodeVO = new LotteryOpenCodeVO();
			openCodeVO.setExpect(expect);
			openCodeVO.setUserId(userId);
			openCodeVO.setCode(code);
			openCodeVO.setLotteryId(lottery.getId());
			openCodeVO.setName(lottery.getShowName());
			openCodeVOs.add(openCodeVO);
		}

		return openCodeVOs;
	}

	@Override
	@Transactional(readOnly = true)
	public LotteryOpenCode getByExcept(String except) {
		return lotteryOpenCodeDao.getByExcept(except);
	}

	@Override
	@Transactional(readOnly = true)
	public LotteryOpenCode getByExcept(String lottery, String except) {
		return lotteryOpenCodeDao.getByExcept(lottery, except);
	}

	@Override
	@Transactional(readOnly = true)
	public List<LotteryOpenCode> getOpenCodeByDate(String lotteryName, String sTime, String eTime) {
		return lotteryOpenCodeDao.getOpenCodeByDate(lotteryName, sTime, eTime);
	}

	@Override
	@Transactional(readOnly = true)
	public List<LotteryOpenCode> getLatest(String lotteryName, int count, Integer userId) {
		return lotteryOpenCodeDao.getLatest(lotteryName, count, userId);
	}
}