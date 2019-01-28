package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.LotteryCrawlerStatusService;
import lottery.domains.content.dao.LotteryCrawlerStatusDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryCrawlerStatus;
import lottery.domains.content.vo.lottery.LotteryCrawlerStatusVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.domains.utils.lottery.open.LotteryOpenUtil;
import lottery.domains.utils.lottery.open.OpenTime;

@Service
public class LotteryCrawlerStatusServiceImpl implements LotteryCrawlerStatusService {
	
	@Autowired
	private LotteryCrawlerStatusDao lotteryCrawlerStatusDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private LotteryOpenUtil lotteryOpenUtil;
	
	@Override
	public List<LotteryCrawlerStatusVO> listAll() {
		List<LotteryCrawlerStatusVO> list = new ArrayList<>();
		List<LotteryCrawlerStatus> clist = lotteryCrawlerStatusDao.listAll();
		for (LotteryCrawlerStatus lotteryCrawlerStatus : clist) {
			Lottery lottery = lotteryDataFactory.getLottery(lotteryCrawlerStatus.getShortName());
			OpenTime openTime = null;
			if(lottery != null) {
				openTime = lotteryOpenUtil.getCurrOpenTime(lottery.getId());
			}
			LotteryCrawlerStatusVO lotteryCrawlerStatusVO = new LotteryCrawlerStatusVO();
			lotteryCrawlerStatusVO.setBean(lotteryCrawlerStatus);
			lotteryCrawlerStatusVO.setOpenTime(openTime);
			list.add(lotteryCrawlerStatusVO);
		}
		return list;
	}

	@Override
	public LotteryCrawlerStatus getByLottery(String lottery) {
		return lotteryCrawlerStatusDao.get(lottery);
	}

	@Override
	public boolean update(String lottery, String lastExpect, String lastUpdate) {
		LotteryCrawlerStatus entity = getByLottery(lottery);
		if(entity != null) {
			entity.setLastExpect(lastExpect);
			entity.setLastUpdate(lastUpdate);
			return lotteryCrawlerStatusDao.update(entity);
		}
		return false;
	}

}
