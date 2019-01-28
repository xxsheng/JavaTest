package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.LotteryService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.lottery.LotteryVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class LotteryServiceImpl implements LotteryService {

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private LotteryDao lotteryDao;
	
	@Autowired
	private DbServerSyncDao dbServerSyncDao;
	
	@Override
	public List<LotteryVO> list(String type) {
		List<Lottery> lotteryList;
		if(StringUtil.isNotNull(type) && StringUtil.isInteger(type)) {
			lotteryList = lotteryDataFactory.listLottery(Integer.parseInt(type));
		} else {
			lotteryList = lotteryDataFactory.listLottery();
		}
		List<LotteryVO> list = new ArrayList<>();
		for (Lottery tmpBean : lotteryList) {
			list.add(new LotteryVO(tmpBean, lotteryDataFactory));
		}
		return list;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		boolean result = lotteryDao.updateStatus(id, status);
		if(result) {
			lotteryDataFactory.initLottery();
			dbServerSyncDao.update(DbServerSyncEnum.LOTTERY);
		}
		return result;
	}

	@Override
	public boolean updateTimes(int id, int times) {
		boolean result = lotteryDao.updateTimes(id, times);
		if(result) {
			lotteryDataFactory.initLottery();
			dbServerSyncDao.update(DbServerSyncEnum.LOTTERY);
		}
		return result;
	}

	@Override
	public Lottery getByShortName(String shortName) {
		return lotteryDataFactory.getLottery(shortName);
	}
	
}
