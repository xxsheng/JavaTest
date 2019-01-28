package lottery.domains.content.biz.read.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserMessageReadService;
import lottery.domains.content.dao.read.UserMessageReadDao;
import lottery.domains.content.dao.read.UserReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserMessage;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserMessageVO;
import lottery.domains.pool.DataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserMessageReadServiceImpl implements UserMessageReadService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserReadDao uReadDao;
	
	@Autowired
	private UserMessageReadDao uMessageReadDao;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public PageList getInboxMessage(int userId, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		User uBean = uReadDao.getByIdFromRead(userId);
		if(uBean != null) {
			// 查询条件
			List<Criterion> criterions = new ArrayList<>();
			criterions.add(Restrictions.eq("toUid", uBean.getId()));
			criterions.add(Restrictions.ne("toStatus", Global.USER_MESSAGE_STATUS_DELETED));
			criterions.add(Restrictions.ne("fromStatus", Global.USER_MESSAGE_STATUS_DELETED));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			PageList pList = uMessageReadDao.search(criterions, orders, start, limit);
			List<UserMessageVO> list = new ArrayList<>();
			for (Object tmpBean : pList.getList()) {
				list.add(new UserMessageVO((UserMessage) tmpBean, uBean.getUpid(), dataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return new PageList();
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageList getOutboxMessage(int userId, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		User uBean = uReadDao.getByIdFromRead(userId);
		if(uBean != null) {
			// 查询条件
			List<Criterion> criterions = new ArrayList<>();
			criterions.add(Restrictions.eq("fromUid", userId));
			criterions.add(Restrictions.ne("fromStatus", Global.USER_MESSAGE_STATUS_DELETED));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			PageList pList = uMessageReadDao.search(criterions, orders, start, limit);
			List<UserMessageVO> list = new ArrayList<>();
			for (Object tmpBean : pList.getList()) {
				list.add(new UserMessageVO((UserMessage) tmpBean, uBean.getUpid(), dataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return new PageList();
	}

	@Override
	@Transactional(readOnly = true)
	public int getUnreadCount(int userId) {
		return uMessageReadDao.getUnreadCount(userId);
	}
}