package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.VipUpgradeGiftsService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipUpgradeGiftsDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipUpgradeGifts;
import lottery.domains.content.vo.config.VipConfig;
import lottery.domains.content.vo.vip.VipUpgradeGiftsVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class VipUpgradeGiftsServiceImpl implements VipUpgradeGiftsService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipUpgradeGiftsDao vUpgradeGiftsDao;
	
	/**
	 * LotteryDataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList search(String username, String date, Integer status, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status));
		}
 		orders.add(Order.desc("id"));
		if(isSearch) {
			List<VipUpgradeGiftsVO> list = new ArrayList<>();
			PageList pList = vUpgradeGiftsDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new VipUpgradeGiftsVO((VipUpgradeGifts) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public boolean doIssuingGift(int userId, int beforeLevel, int afterLevel) {
		VipConfig vipConfig = lotteryDataFactory.getVipConfig();
		double[] upgradeGifts = vipConfig.getUpgradeGifts();
		String thisTime = new Moment().toSimpleTime();
		for (int i = beforeLevel; i < afterLevel; i++) {
			try {
				double upgradeMoney = upgradeGifts[i + 1];
				if(upgradeMoney > 0) {
					boolean hasRecord = vUpgradeGiftsDao.hasRecord(userId, beforeLevel, afterLevel);
					if(!hasRecord) {
						int status = 1;
						int isReceived = 0;
						VipUpgradeGifts entity = new VipUpgradeGifts(userId, i, i + 1, upgradeMoney, thisTime, status, isReceived);
						vUpgradeGiftsDao.add(entity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
}