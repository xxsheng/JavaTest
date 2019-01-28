package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.VipFreeChipsService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipFreeChipsDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipFreeChips;
import lottery.domains.content.global.Global;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VipFreeChipsServiceImpl implements VipFreeChipsService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipFreeChipsDao vFreeChipsDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;

	@Override
	public boolean received(int userId) {
		VipFreeChips entity = vFreeChipsDao.getByUserId(userId);
		if(entity != null) {
			User uBean = uDao.getById(userId);
			if(uBean != null) {
				boolean uFlag = uDao.updateBaccaratMoney(uBean.getId(), entity.getMoney());
				if(uFlag) {
					// 更新领取状态
					vFreeChipsDao.updateReceived(entity.getId(), 1);
					int refType = 2;
					String remarks = "领取VIP免费筹码。";
					uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_BACCARAT, entity.getMoney(), refType, remarks);
				}
				return uFlag;
			}
		}
		return false;
	}

}
