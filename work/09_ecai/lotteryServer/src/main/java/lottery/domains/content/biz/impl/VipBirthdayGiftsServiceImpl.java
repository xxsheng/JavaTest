package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.VipBirthdayGiftsService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipBirthdayGiftsDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipBirthdayGifts;
import lottery.domains.content.global.Global;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VipBirthdayGiftsServiceImpl implements VipBirthdayGiftsService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipBirthdayGiftsDao vBirthdayGiftsDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;

	@Override
	public boolean received(int userId) {
		VipBirthdayGifts entity = vBirthdayGiftsDao.getByUserId(userId);
		if(entity != null) {
			User uBean = uDao.getById(userId);
			if(uBean != null) {
				boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), entity.getMoney());
				if(uFlag) {
					// 更新领取状态
					vBirthdayGiftsDao.updateReceived(entity.getId(), 1);
					int refType = 2;
					String remarks = "领取VIP生日礼金。";
					uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, entity.getMoney(), refType, remarks);
				}
				return uFlag;
			}
		}
		return false;
	}

}
