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
import lottery.domains.content.biz.VipBirthdayGiftsService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserInfoDao;
import lottery.domains.content.dao.VipBirthdayGiftsDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserInfo;
import lottery.domains.content.entity.VipBirthdayGifts;
import lottery.domains.content.vo.config.VipConfig;
import lottery.domains.content.vo.vip.VipBirthdayGiftsVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class VipBirthdayGiftsServiceImpl implements VipBirthdayGiftsService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserInfoDao uInfoDao;
	
	@Autowired
	private VipBirthdayGiftsDao vBirthdayGiftsDao;
	
	/**
	 * LotteryDataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public PageList search(String username, Integer level, String date, Integer status, int start, int limit) {
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
		if(level != null) {
			criterions.add(Restrictions.eq("level", level.intValue()));
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.like("time", date, MatchMode.ANYWHERE));
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status));
		}
 		orders.add(Order.desc("id"));
		if(isSearch) {
			List<VipBirthdayGiftsVO> list = new ArrayList<>();
			PageList pList = vBirthdayGiftsDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new VipBirthdayGiftsVO((VipBirthdayGifts) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public boolean calculate(String birthday) {
		String date = new Moment().fromDate(birthday).format("MM-dd");
		List<UserInfo> list = uInfoDao.listByBirthday(date);
		VipConfig vipConfig = lotteryDataFactory.getVipConfig();
		double[] birthdayGifts = vipConfig.getBirthdayGifts();
		String thisTime = new Moment().toSimpleTime();
		int year = new Moment().fromDate(birthday).year();
		for (UserInfo tmpBean : list) {
			try {
				User uBean = uDao.getById(tmpBean.getUserId());
				if(uBean != null) {
					double birthdayMoney = birthdayGifts[uBean.getVipLevel()];
					if(birthdayMoney > 0) {
						boolean hasRecord = vBirthdayGiftsDao.hasRecord(uBean.getId(), year);
						if(!hasRecord) {
							int status = 1;
							int isReceived = 0;
							VipBirthdayGifts entity = new VipBirthdayGifts(uBean.getId(), uBean.getVipLevel(), birthdayMoney, birthday, thisTime, status, isReceived);
							return vBirthdayGiftsDao.add(entity);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
}