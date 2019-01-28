package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.VipUpgradeGiftsService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipUpgradeGiftsDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipUpgradeGifts;
import lottery.domains.content.global.Global;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VipUpgradeGiftsServiceImpl implements VipUpgradeGiftsService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipUpgradeGiftsDao vUpgradeGiftsDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;
	
	@Override
	public boolean received(int userId) {
		VipUpgradeGifts entity = vUpgradeGiftsDao.getByUserId(userId);
		if(entity != null) {
			User uBean = uDao.getById(userId);
			if(uBean != null) {
				boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), entity.getMoney());
				if(uFlag) {
					// 更新领取状态
					vUpgradeGiftsDao.updateReceived(entity.getId(), 1);
					int refType = 2;
					String remarks = "领取VIP晋级礼金。";
					uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, entity.getMoney(), refType, remarks);
				}
				return uFlag;
			}
		}
		return false;
	}

}
