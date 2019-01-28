package admin.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javautils.StringUtil;
import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.domains.content.biz.AdminUserLogService;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.dao.AdminUserLogDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserLog;
import admin.domains.content.vo.AdminUserLogVO;
import admin.domains.pool.AdminDataFactory;

@Service
public class AdminUserLogServiceImpl implements AdminUserLogService {
	
	@Autowired
	private AdminUserDao adminUserDao;
	
	@Autowired
	private AdminUserLogDao adminUserLogDao;
	
	@Autowired
	private AdminDataFactory adminDataFactory;

	@Override
	public PageList search(String username, String ip, String keyword, String sDate, String eDate, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			AdminUser user = adminUserDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(ip)) {
			criterions.add(Restrictions.eq("ip", ip));
		}
		if(StringUtil.isNotNull(keyword)) {
			StringTokenizer token = new StringTokenizer(keyword);
			Disjunction disjunction = Restrictions.or();
			while (token.hasMoreElements()) {
				String value = (String) token.nextElement();
				disjunction.add(Restrictions.like("action", value, MatchMode.ANYWHERE));
			}
			criterions.add(disjunction);
		}
		if(StringUtil.isNotNull(sDate)) {
			criterions.add(Restrictions.ge("time", sDate));
		}
		if(StringUtil.isNotNull(eDate)) {
			criterions.add(Restrictions.lt("time", eDate));
		}
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<AdminUserLogVO> list = new ArrayList<>();
			PageList pList = adminUserLogDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new AdminUserLogVO((AdminUserLog) tmpBean, adminDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

}