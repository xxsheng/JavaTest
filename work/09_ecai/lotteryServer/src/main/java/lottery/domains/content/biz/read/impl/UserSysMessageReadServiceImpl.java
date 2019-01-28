package lottery.domains.content.biz.read.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserSysMessageReadService;
import lottery.domains.content.dao.read.UserSysMessageReadDao;
import lottery.domains.content.global.Global;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserSysMessageReadServiceImpl implements UserSysMessageReadService {

	@Autowired
	private UserSysMessageReadDao uSysMessageReadDao;
	

	@Override
	@Transactional(readOnly = true)
	public PageList search(int userId, int start, int limit) {
		// 查询条件
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.in("status", Arrays.asList(Global.USER_SYS_MESSAGE_STATUS_UNREAD, Global.USER_SYS_MESSAGE_STATUS_READED)));
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		PageList pList = uSysMessageReadDao.search(criterions, orders, start, limit);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public int getUnreadCount(int userId) {
		return uSysMessageReadDao.getUnreadCount(userId);
	}

}