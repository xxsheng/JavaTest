package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserLotteryDetailsReportService;
import lottery.domains.content.dao.UserLotteryDetailsReportDao;
import lottery.domains.content.entity.UserLotteryDetailsReport;
import lottery.domains.content.global.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLotteryDetailsReportServiceImpl implements UserLotteryDetailsReportService {
	
	@Autowired
	private UserLotteryDetailsReportDao uLotteryDetailsReportDao;

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

}