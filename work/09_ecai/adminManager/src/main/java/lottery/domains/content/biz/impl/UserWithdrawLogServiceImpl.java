package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserWithdrawLogService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserWithdrawLogDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserWithdrawLog;
import lottery.domains.content.vo.user.UserWithdrawLogVO;
import lottery.domains.pool.LotteryDataFactory;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserWithdrawLogServiceImpl implements UserWithdrawLogService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	@Autowired
	private AdminUserDao adminUserDao;
	
	@Autowired
	private UserWithdrawLogDao userWithdrawLogDao;

	/**
	 * DataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public PageList search(String billno,String username, int start, int limit) {
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
			criterions.add(Restrictions.eq("billno", billno));
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<UserWithdrawLogVO> list = new ArrayList<>();
			PageList pList = userWithdrawLogDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				UserWithdrawLog tWithdrawLog=(UserWithdrawLog)tmpBean;
				AdminUser adminUser=adminUserDao.getById(tWithdrawLog.getAdminUserId());
				list.add(new UserWithdrawLogVO(tWithdrawLog, adminUser));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public boolean add(UserWithdrawLog entity) {
		return userWithdrawLogDao.add(entity);
	}
}