package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javautils.StringUtil;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserBaccaratReportService;
import lottery.domains.content.dao.UserBaccaratReportDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBaccaratReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserBaccaratReportVO;

@Service
public class UserBaccaratReportServiceImpl implements UserBaccaratReportService {
	
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserBaccaratReportDao uBaccaratReportDao;
	
	@Override
	public boolean update(int userId, int type, double amount, String time) {
		UserBaccaratReport entity = new UserBaccaratReport();
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
		case Global.BILL_TYPE_WATER_RETURN:
			entity.setWaterReturn(amount);
			break;
		case Global.BILL_TYPE_PROXY_RETURN:
			entity.setProxyReturn(amount);
			break;
		case Global.BILL_TYPE_CANCEL_ORDER:
			entity.setCancelOrder(amount);
			break;
		case Global.BILL_TYPE_ACTIVITY:
			entity.setActivity(amount);
			break;
		default:
			return false;
		}
		UserBaccaratReport bean = uBaccaratReportDao.get(userId, time);
		if(bean != null) {
			entity.setId(bean.getId());
			return uBaccaratReportDao.update(entity);
		} else {
			entity.setUserId(userId);
			entity.setTime(time);
			return uBaccaratReportDao.add(entity);
		}
	}
	
	@Override
	public List<UserBaccaratReportVO> report(String sTime, String eTime) {
		// 首先列出来用户下级以及所有下级
		Map<Integer, User> lowerUsersMap = new HashMap<>();
		List<User> lowerUserList = uDao.listAll();
		List<User> directUserList  = uDao.getUserDirectLower(0);
		// 查询条件
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		for (User tmpUser : lowerUserList) {
			lowerUsersMap.put(tmpUser.getId(), tmpUser);
		}
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		List<UserBaccaratReport> result = uBaccaratReportDao.find(criterions, orders);
		Map<Integer, UserBaccaratReportVO> resultMap = new HashMap<>();
		UserBaccaratReportVO tBean = new UserBaccaratReportVO("总计");
		// 汇总处理
		for (UserBaccaratReport tmpBean : result) {
			User thisUser = lowerUsersMap.get(tmpBean.getUserId());
			if(thisUser.getUpid() == 0) { // 直属下级
				if(!resultMap.containsKey(thisUser.getId())) {
					resultMap.put(thisUser.getId(), new UserBaccaratReportVO(thisUser.getUsername()));
				}
				resultMap.get(thisUser.getId()).addBean(tmpBean);
			} else { // 下级的下级
				for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
					if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
						if(!resultMap.containsKey(tmpUser.getId())) {
							resultMap.put(tmpUser.getId(), new UserBaccaratReportVO(tmpUser.getUsername()));
						}
						resultMap.get(tmpUser.getId()).addBean(tmpBean);
					}
				}
			}
			tBean.addBean(tmpBean);
		}
		List<UserBaccaratReportVO> list = new ArrayList<>();
		list.add(tBean);
		Object[] keys = resultMap.keySet().toArray();
		Arrays.sort(keys);
		for (Object o : keys) {
			list.add(resultMap.get(o));
		}
		return list;
	}
	
	@Override
	public List<UserBaccaratReportVO> report(int userId, String sTime, String eTime) {
		User targetUser = uDao.getById(userId);
		if(targetUser != null) {
			// 首先列出来用户下级以及所有下级
			Map<Integer, User> lowerUsersMap = new HashMap<>();
			List<User> lowerUserList = uDao.getUserLower(targetUser.getId());
			List<User> directUserList  = uDao.getUserDirectLower(targetUser.getId());
			// 查询条件
			List<Criterion> criterions = new ArrayList<>();
			List<Order> orders = new ArrayList<>();
			List<Integer> toUids = new ArrayList<>();
			toUids.add(targetUser.getId());
			for (User tmpUser : lowerUserList) {
				toUids.add(tmpUser.getId());
				lowerUsersMap.put(tmpUser.getId(), tmpUser);
			}
			if(StringUtil.isNotNull(sTime)) {
				criterions.add(Restrictions.ge("time", sTime));
			}
			if(StringUtil.isNotNull(eTime)) {
				criterions.add(Restrictions.lt("time", eTime));
			}
			criterions.add(Restrictions.in("userId", toUids));
			List<UserBaccaratReport> result = uBaccaratReportDao.find(criterions, orders);
			Map<Integer, UserBaccaratReportVO> resultMap = new HashMap<>();
			UserBaccaratReportVO tBean = new UserBaccaratReportVO("总计");
			// 汇总处理
			for (UserBaccaratReport tmpBean : result) {
				if(tmpBean.getUserId() == targetUser.getId()) { // 自己
					if(!resultMap.containsKey(targetUser.getId())) {
						resultMap.put(targetUser.getId(), new UserBaccaratReportVO(targetUser.getUsername()));
					}
					resultMap.get(targetUser.getId()).addBean(tmpBean);
				} else {
					User thisUser = lowerUsersMap.get(tmpBean.getUserId());
					if(thisUser.getUpid() == targetUser.getId()) { // 直属下级
						if(!resultMap.containsKey(thisUser.getId())) {
							resultMap.put(thisUser.getId(), new UserBaccaratReportVO(thisUser.getUsername()));
						}
						resultMap.get(thisUser.getId()).addBean(tmpBean);
					} else { // 下级的下级
						for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
							if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
								if(!resultMap.containsKey(tmpUser.getId())) {
									resultMap.put(tmpUser.getId(), new UserBaccaratReportVO(tmpUser.getUsername()));
								}
								resultMap.get(tmpUser.getId()).addBean(tmpBean);
							}
						}
					}
				}
				tBean.addBean(tmpBean);
			}
			List<UserBaccaratReportVO> list = new ArrayList<>();
			list.add(tBean);
			Object[] keys = resultMap.keySet().toArray();
			Arrays.sort(keys);
			for (Object o : keys) {
				list.add(resultMap.get(o));
			}
			return list;
		}
		return null;
	}

}