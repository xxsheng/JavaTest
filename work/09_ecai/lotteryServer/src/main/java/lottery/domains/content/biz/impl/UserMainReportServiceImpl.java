package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserMainReportService;
import lottery.domains.content.dao.UserMainReportDao;
import lottery.domains.content.entity.UserMainReport;
import lottery.domains.content.global.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMainReportServiceImpl implements UserMainReportService {
	@Autowired
	private UserMainReportDao uMainReportDao;

	@Override
	public boolean update(int userId, int type, double amount, String time) {
		UserMainReport entity = new UserMainReport();
		switch (type) {
		case Global.BILL_TYPE_RECHARGE:
			entity.setRecharge(amount);
			break;
		case Global.BILL_TYPE_WITHDRAWALS:
			entity.setWithdrawals(amount);
			break;
		case Global.BILL_TYPE_TRANS_IN:
			entity.setTransIn(amount);
			break;
		case Global.BILL_TYPE_TRANS_OUT:
			entity.setTransOut(amount);
			break;
		case Global.BILL_TYPE_DIVIDEND:
		case Global.BILL_TYPE_ACTIVITY:
		case Global.BILL_TYPE_INTEGRAL:
			entity.setActivity(amount);
			break;
		default:
			return false;
		}
		UserMainReport bean = uMainReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uMainReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uMainReportDao.add(entity);
		}
	}

	@Override
	public boolean updateRecharge(int userId, double amount, String time, double receiveFeeMoney) {
		UserMainReport entity = new UserMainReport();
		entity.setRecharge(amount);
		entity.setReceiveFeeMoney(receiveFeeMoney); // 实际收款手续费，第三方收的

		UserMainReport bean = uMainReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uMainReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uMainReportDao.add(entity);
		}
	}

	@Override
	public boolean updateUserTrans(int userId, int type, double amount, String time, boolean abs) {
		UserMainReport entity = new UserMainReport();
		if(abs) {
			entity.setAccountIn(amount);
		} else {
			entity.setAccountOut(amount);
		}
		UserMainReport bean = uMainReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uMainReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uMainReportDao.add(entity);
		}
	}
}