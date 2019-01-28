package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsPlanService;
import lottery.domains.content.dao.UserBetsPlanDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBetsPlan;
import lottery.domains.content.vo.user.UserBetsPlanVO;
import lottery.domains.pool.LotteryDataFactory;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBetsPlanServiceImpl implements UserBetsPlanService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserBetsPlanDao uBetsPlanDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public PageList search(String keyword, String username, Integer lotteryId,
			String expect, Integer ruleId, String minTime, String maxTime, Double minMoney,
			Double maxMoney, Integer minMultiple, Integer maxMultiple, Integer status,
			int start, int limit) {
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
		if(StringUtil.isNotNull(keyword)) {
			Disjunction disjunction = Restrictions.or();
			if(StringUtil.isInteger(keyword)) {
				disjunction.add(Restrictions.eq("id", Integer.parseInt(keyword)));
			}
			disjunction.add(Restrictions.like("billno", keyword, MatchMode.ANYWHERE));
			disjunction.add(Restrictions.like("orderId", keyword, MatchMode.ANYWHERE));
			criterions.add(disjunction);
		}
		if(lotteryId != null) {
			criterions.add(Restrictions.eq("lotteryId", lotteryId.intValue()));
		}
		if(StringUtil.isNotNull(expect)) {
			criterions.add(Restrictions.like("expect", expect, MatchMode.END));
		}
		if(ruleId != null) {
			criterions.add(Restrictions.eq("ruleId", ruleId));
		}
		if(StringUtil.isNotNull(minTime)) {
			criterions.add(Restrictions.gt("time", minTime));
		}
		if(StringUtil.isNotNull(maxTime)) {
			criterions.add(Restrictions.lt("time", maxTime));
		}
		if(minMoney != null) {
			criterions.add(Restrictions.ge("money", minMoney.doubleValue()));
		}
		if(maxMoney != null) {
			criterions.add(Restrictions.le("money", maxMoney.doubleValue()));
		}
		if(minMultiple  != null) {
			criterions.add(Restrictions.ge("multiple", minMultiple.intValue()));
		}
		if(maxMultiple  != null) {
			criterions.add(Restrictions.le("multiple", maxMultiple.intValue()));
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<UserBetsPlanVO> list = new ArrayList<>();
			PageList pList = uBetsPlanDao.search(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				UserBetsPlanVO tmpVO = new UserBetsPlanVO((UserBetsPlan) tmpBean, lotteryDataFactory);
				list.add(tmpVO); 
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

}