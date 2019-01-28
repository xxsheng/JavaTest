package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserLotteryDetailsReportService;
import lottery.domains.content.dao.UserLotteryDetailsReportDao;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.UserLotteryDetailsReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.HistoryUserLotteryDetailsReportVO;
import lottery.domains.content.vo.bill.UserBetsReportVO;
import lottery.domains.content.vo.bill.UserLotteryDetailsReportVO;
import lottery.domains.pool.LotteryDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserLotteryDetailsReportServiceImpl implements UserLotteryDetailsReportService {
	@Autowired
	private UserLotteryDetailsReportDao uLotteryDetailsReportDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public boolean update(int userId, int lotteryId, int ruleId, int type, double amount,
			String time) {
		UserLotteryDetailsReport entity = new UserLotteryDetailsReport();
		switch (type) {
		case Global.BILL_TYPE_SPEND:
			entity.setSpend(amount);
			break;
		case Global.BILL_TYPE_PRIZE:
			entity.setPrize(amount);
			break;
		case Global.BILL_TYPE_SPEND_RETURN:
			entity.setSpendReturn(amount);
			break;
		case Global.BILL_TYPE_PROXY_RETURN:
			entity.setProxyReturn(amount);
			break;
		case Global.BILL_TYPE_CANCEL_ORDER:
			entity.setCancelOrder(amount);
			break;
		default:
			return false;
		}
		UserLotteryDetailsReport bean = uLotteryDetailsReportDao.get(userId, lotteryId, ruleId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uLotteryDetailsReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setLotteryId(lotteryId);
			entity.setRuleId(ruleId);
			entity.setTime(time);
			return uLotteryDetailsReportDao.add(entity);
		}
	}
	
	@Override
	public List<UserLotteryDetailsReportVO> reportLowersAndSelf(int userId, Integer lotteryId, String sTime, String eTime) {
		if (lotteryId == null) {
			return uLotteryDetailsReportDao.sumLowersAndSelfByLottery(userId, sTime, eTime);
		}
		else {
			return uLotteryDetailsReportDao.sumLowersAndSelfByRule(userId, lotteryId, sTime, eTime);
		}
	}
	@Override
	public List<HistoryUserLotteryDetailsReportVO> historyReportLowersAndSelf(int userId, Integer lotteryId, String sTime, String eTime) {
		if (lotteryId == null) {
			return uLotteryDetailsReportDao.historySumLowersAndSelfByLottery(userId, sTime, eTime);
		}
		else {
			return uLotteryDetailsReportDao.historySumLowersAndSelfByRule(userId, lotteryId, sTime, eTime);
		}
	}
	
	@Override
	public List<UserLotteryDetailsReportVO> reportSelf(int userId, Integer lotteryId, String sTime, String eTime) {
		if (lotteryId == null) {
			return uLotteryDetailsReportDao.sumSelfByLottery(userId, sTime, eTime);
		}
		else {
			return uLotteryDetailsReportDao.sumSelfByRule(userId, lotteryId, sTime, eTime);
		}
	}
	
	@Override
	public List<HistoryUserLotteryDetailsReportVO> historyReportSelf(int userId, Integer lotteryId, String sTime, String eTime) {
		if (lotteryId == null) {
			return uLotteryDetailsReportDao.historySumSelfByLottery(userId, sTime, eTime);
		}
		else {
			return uLotteryDetailsReportDao.historySumSelfByRule(userId, lotteryId, sTime, eTime);
		}
	}
	@Override
	public List<UserBetsReportVO> sumUserBets(Integer type, Integer lottery, Integer ruleId, String sTime, String eTime) {
		List<Integer> lids = new ArrayList<>();
		if(lottery != null) {
			lids.add(lottery);
		} else {
			if(type != null) {
				List<Lottery> llist = lotteryDataFactory.listLottery(type);
				for (Lottery tmpBean : llist) {
					lids.add(tmpBean.getId());
				}
			}
		}

		return uLotteryDetailsReportDao.sumUserBets(lids, ruleId, sTime, eTime);
	}
}