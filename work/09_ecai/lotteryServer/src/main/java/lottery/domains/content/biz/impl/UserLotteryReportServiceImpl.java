package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.global.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLotteryReportServiceImpl implements UserLotteryReportService {

	@Autowired
	private UserLotteryReportDao uLotteryReportDao;

	@Override
	public boolean update(int userId, int type, double amount, String time) {
		UserLotteryReport entity = new UserLotteryReport();
		switch (type) {
		case Global.BILL_TYPE_TRANS_IN:
			entity.setTransIn(amount);
			break;
		case Global.BILL_TYPE_TRANS_OUT:
			entity.setTransOut(amount);
			break;
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
		case Global.BILL_TYPE_DIVIDEND:
			entity.setDividend(amount);
			break;
		case Global.BILL_TYPE_DAILY_SETTLE:
		case Global.BILL_TYPE_ACTIVITY:
		case Global.BILL_TYPE_INTEGRAL:
			entity.setActivity(amount);
			break;
		case Global.BILL_TYPE_RED_PACKET:
			entity.setPacket(amount);
			break;
		default:
			return false;
		}
		UserLotteryReport bean = uLotteryReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uLotteryReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uLotteryReportDao.add(entity);
		}
	}

	@Override
	public boolean updateRechargeFee(int userId, double amount, String time) {
		UserLotteryReport entity = new UserLotteryReport();
		entity.setRechargeFee(amount);
		UserLotteryReport bean = uLotteryReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uLotteryReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uLotteryReportDao.add(entity);
		}
	}
}