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
import lottery.domains.content.biz.VipFreeChipsService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipFreeChipsDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipFreeChips;
import lottery.domains.content.vo.config.VipConfig;
import lottery.domains.content.vo.vip.VipFreeChipsVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class VipFreeChipsServiceImpl implements VipFreeChipsService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipFreeChipsDao vFreeChipsDao;
	
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
			List<VipFreeChipsVO> list = new ArrayList<>();
			PageList pList = vFreeChipsDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new VipFreeChipsVO((VipFreeChips) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public boolean calculate() {
		List<User> ulist = uDao.listAll();
		// 这里日期写死了，就是每月10号
		String sTime = new Moment().day(10).toSimpleDate();
		String eTime = new Moment().day(10).add(1, "months").toSimpleDate();
		String thisTime = new Moment().toSimpleTime();
		VipConfig vipConfig = lotteryDataFactory.getVipConfig();
		double[] freeChips = vipConfig.getFreeChips();
		for (User tmpBean : ulist) {
			try {
				int vipLevel = tmpBean.getVipLevel();
				double freeMoney = freeChips[vipLevel];
				if(freeMoney > 0) {
					boolean hasRecord = vFreeChipsDao.hasRecord(tmpBean.getId(), sTime, eTime);
					if(!hasRecord) {
						int status = 0;
						int isReceived = 0;
						VipFreeChips entity = new VipFreeChips(tmpBean.getId(), tmpBean.getVipLevel(), freeMoney, sTime, eTime, thisTime, status, isReceived);
						vFreeChipsDao.add(entity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	public boolean agreeAll() {
		List<VipFreeChips> list = vFreeChipsDao.getUntreated();
		for (VipFreeChips tmpBean : list) {
			if(tmpBean.getStatus() == 0) {
				tmpBean.setStatus(1);
				vFreeChipsDao.update(tmpBean);
			}
		}
		return true;
	}

}