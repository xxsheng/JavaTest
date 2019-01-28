package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.LotteryOpenTimeService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.LotteryOpenTimeDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.lottery.LotteryOpenTimeVO;
import lottery.domains.pool.LotteryDataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LotteryOpenTimeServiceImpl implements LotteryOpenTimeService {

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private LotteryOpenTimeDao lotteryOpenTimeDao;
	
	@Autowired
	private DbServerSyncDao dbServerSyncDao;
	
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
		orders.add(Order.asc("lottery"));
		orders.add(Order.asc("expect"));
		PageList pList = lotteryOpenTimeDao.find(criterions, orders, start, limit);
		List<LotteryOpenTimeVO> list = new ArrayList<>();
		for (Object tmpBean : pList.getList()) {
			list.add(new LotteryOpenTimeVO((LotteryOpenTime) tmpBean, lotteryDataFactory));
		}
		pList.setList(list);
		return pList;
	}
	
	@Override
	public boolean modify(int id, String startTime, String stopTime) {
		LotteryOpenTime entity = lotteryOpenTimeDao.getById(id);
		if(entity != null) {
			entity.setStartTime(startTime);
			entity.setStopTime(stopTime);
			boolean flag = lotteryOpenTimeDao.update(entity);
			if(flag) {
				// 更新缓存
				lotteryDataFactory.initLotteryOpenTime();
				dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_OPEN_TIME);
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean modifyRefExpect(String lottery, int count) {
		LotteryOpenTime entity = lotteryOpenTimeDao.getByLottery(lottery+"_ref");
		if(entity != null) {
			int expect = Integer.valueOf(entity.getExpect());
			expect += count;
			entity.setExpect(expect+"");
			boolean flag = lotteryOpenTimeDao.update(entity);
			if(flag) {
				// 更新缓存
				lotteryDataFactory.initLotteryOpenTime();
				dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_OPEN_TIME);
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean modify(String lottery, int seconds) {
		boolean allowModify = false;
		Lottery thisLottery = lotteryDataFactory.getLottery(lottery);
		if(thisLottery != null) {
			switch (thisLottery.getType()) {
			case 1:
			case 2:
			case 3:
			case 5:
			case 6:
				allowModify = true;
				break;
			default:
				break;
			}
		}
		// 必须是允许修改的类型，不然不让批量修改
		if(allowModify) {
			List<LotteryOpenTime> list = lotteryOpenTimeDao.list(lottery);
			if(list.size() > 0) {
				for (LotteryOpenTime entity : list) {
					entity.setStartTime(add(entity.getStartTime(), seconds));
					entity.setStopTime(add(entity.getStopTime(), seconds));
					lotteryOpenTimeDao.update(entity);
				}
				// 更新缓存
				lotteryDataFactory.initLotteryOpenTime();
				dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_OPEN_TIME);
				return true;
			}
		}
		return false;
	}

	@Override
	public LotteryOpenTime getByLottery(String lottery) {
		return lotteryOpenTimeDao.getByLottery(lottery);
	}

	@Override
	public boolean update(LotteryOpenTime entity) {
		boolean updated = lotteryOpenTimeDao.update(entity);
		if (updated) {
			lotteryDataFactory.initLotteryOpenTime();
			dbServerSyncDao.update(DbServerSyncEnum.LOTTERY_OPEN_TIME);
		}
		return updated;
	}

	/**
	 * 增减时间
	 * @param time HH:mm:ss
	 * @return
	 */
	public static String add(String time, int seconds) {
		String date = new Moment().toSimpleDate();
		Moment moment = new Moment().fromTime(date + " " + time);
		moment.add(seconds, "seconds");
		return moment.format("HH:mm:ss");
	}
	
}