package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserMessageService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserMessageDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserMessage;
import lottery.domains.content.vo.user.UserMessageVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class UserMessageServiceImpl implements UserMessageService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserMessageDao uMessageDao;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public PageList search(String toUser, String fromUser, String sTime, String eTime,int type,
			int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(toUser)) {
			User user = uDao.getByUsername(toUser);
			if(user != null) {
				criterions.add(Restrictions.eq("toUid", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(fromUser)) {
			User user = uDao.getByUsername(fromUser);
			if(user != null) {
				criterions.add(Restrictions.eq("fromUid", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		
		if(type ==0){
			criterions.add(Restrictions.eq("toUid", type));
		}else if(type == 1){
			criterions.add(Restrictions.gt("toUid", type));
		}else if(type == -1){
			
		}
		
		orders.add(Order.desc("id"));
		if(isSearch) {
			List<UserMessageVO> list = new ArrayList<>();
			PageList pList = uMessageDao.search(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserMessageVO((UserMessage) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public UserMessageVO getById(int id) {
		UserMessage bean = uMessageDao.getById(id);
		if(bean != null) {
			return new UserMessageVO(bean, lotteryDataFactory);
		}
		return null;
	}
	
	@Override
	public boolean delete(int id) {
		return uMessageDao.delete(id);
	}

	@Override
	public boolean save(int id, String content) {
		UserMessage userMessage = uMessageDao.getById(id);
		userMessage.setToStatus(1);
		uMessageDao.update(userMessage);

		UserMessage replyMessage = new UserMessage(userMessage.getFromUid(), 0, 1,
				"回复>>"+userMessage.getSubject(), content, DateUtil.getCurrentTime(), 0, 0);
		return uMessageDao.save(replyMessage);
	}
	
}