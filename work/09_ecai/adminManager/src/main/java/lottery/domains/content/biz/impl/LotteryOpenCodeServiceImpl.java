package lottery.domains.content.biz.impl;

import admin.web.WebJSONObject;
import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenCode;
import lottery.domains.content.vo.lottery.LotteryOpenCodeVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.domains.utils.lottery.open.LotteryOpenUtil;
import lottery.web.content.validate.CodeValidate;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LotteryOpenCodeServiceImpl implements LotteryOpenCodeService {
	private static final String OPEN_CODE_KEY = "OPEN_CODE:%s";
	private static final String ADMIN_OPEN_CODE_KEY = "ADMIN_OPEN_CODE:%s";
	private static final int OPEN_CODE_MOST_EXPECT = 50; // 保留最近50期开奖号码

	@Autowired
	private CodeValidate codeValidate;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private LotteryOpenCodeDao lotteryOpenCodeDao;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Autowired
	private LotteryOpenUtil lotteryOpenUtil;
	
	@Autowired
	private LotteryDao lotteryDao;
	
	@Override
	public PageList search(String lottery, String expect, int start, int limit) {
		if(start < 0) start = 0;
		if(limit < 0) limit = 10;
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		if(StringUtil.isNotNull(lottery)) {
			criterions.add(Restrictions.eq("lottery", lottery));
		}
		if(StringUtil.isNotNull(expect)) {
			criterions.add(Restrictions.eq("expect", expect));
		}
		orders.add(Order.desc("expect"));
		PageList pList = lotteryOpenCodeDao.find(criterions, orders, start, limit);
		List<LotteryOpenCodeVO> list = new ArrayList<>();
		for (Object tmpBean : pList.getList()) {
			list.add(new LotteryOpenCodeVO((LotteryOpenCode) tmpBean, lotteryDataFactory));
		}
		pList.setList(list);
		return pList;
	}
	
	@Override
	public LotteryOpenCodeVO get(String lottery, String expect) {
		LotteryOpenCode entity = lotteryOpenCodeDao.get(lottery, expect);
		if(entity != null) {
			LotteryOpenCodeVO bean = new LotteryOpenCodeVO(entity, lotteryDataFactory);
			return bean;
		}
		return null;
	}
	
	@Override
	public boolean add(WebJSONObject json, String lottery, String expect, String code, String opername) {
		if (!codeValidate.validateExpect(json, lottery, expect)) {
			Lottery lo = lotteryDao.getByShortName(lottery);
			if(null != lo && lo.getSelf() == 1){
				//存储缓存
				String key = String.format(ADMIN_OPEN_CODE_KEY, lottery);
				jedisTemplate.hset(key, expect, code);
				return true;
			}else{
				return false;
			}
		}
		
		// 如果是腾讯分分彩或腾讯龙虎斗，跟上一期的开奖号码一样，那么自动撤单
		int openStatus = 0;
		if ("txffc".equals(lottery) || "txlhd".equals(lottery)) {
			String lastExpect = lotteryOpenUtil.subtractExpect(lottery, expect);
			LotteryOpenCode lastOpenCode = lotteryOpenCodeDao.get(lottery, lastExpect);
			if (lastOpenCode != null) {
				if (lastOpenCode.getCode().equals(code)) {
					// 0：待开奖；1：已开奖；2：无效待撤单；3：无效已撤单
					openStatus = 2;
				}
			}
		}

		LotteryOpenCode entity = lotteryOpenCodeDao.get(lottery, expect);
		if(entity == null) {
			String time = new Moment().toSimpleTime();
			LotteryOpenCode bean = new LotteryOpenCode();
			bean.setLottery(lottery);
			bean.setExpect(expect);
			bean.setCode(code);
			bean.setTime(time);
			bean.setInterfaceTime(time);
			bean.setOpenStatus(openStatus);
			bean.setRemarks(opername);
			
			boolean result = lotteryOpenCodeDao.add(bean);
			if(result){
				addedToRedis(bean);
			}
			return result;
		}
		else {
			entity.setCode(code);
			entity.setRemarks(opername);
			entity.setOpenStatus(openStatus);
			boolean result = lotteryOpenCodeDao.update(entity);
			if(result){
				addedToRedis(entity);
			}
			return result;
		}
	}



	@Override
	public boolean delete(LotteryOpenCode bean) {
		return lotteryOpenCodeDao.delete(bean);
	}

	@Override
	public LotteryOpenCode getFirstExpectByInterfaceTime(String lottery, String startTime, String endTime) {
		return lotteryOpenCodeDao.getFirstExpectByInterfaceTime(lottery, startTime, endTime);
	}

	@Override
	public int countByInterfaceTime(String lottery, String startTime, String endTime) {
		return lotteryOpenCodeDao.countByInterfaceTime(lottery, startTime, endTime);
	}

	@Override
	public List<LotteryOpenCode> getLatest(String lottery, int count) {
		return lotteryOpenCodeDao.getLatest(lottery, count);
	}

	private void addedToRedis(LotteryOpenCode entity) {
		// 操作redis
		String key = String.format(OPEN_CODE_KEY, entity.getLottery());

		if ("jsmmc".equals(entity.getLottery())) {
			return;
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
}