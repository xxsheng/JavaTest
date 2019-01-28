package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserCardService;
import lottery.domains.content.dao.UserCardDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.vo.user.UserCardVO;
import lottery.domains.pool.LotteryDataFactory;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCardServiceImpl implements UserCardService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserCardDao uCardDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	public PageList search(String username, String keyword, Integer status, int start, int limit) {
		if(start < 0) start = 0;
		if(limit < 0) limit = 10;
		if(limit > 100) limit = 100;
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

		if(isSearch) {
			if(StringUtil.isNotNull(keyword)) {
				criterions.add(Restrictions.or(Restrictions.eq("cardId", keyword), Restrictions.eq("cardName", keyword)));
			}
			if(status != null) {
				criterions.add(Restrictions.eq("status", status.intValue()));
			}

			orders.add(Order.desc("time"));
			orders.add(Order.desc("id"));

			List<UserCardVO> list = new ArrayList<>();
			PageList pList = uCardDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserCardVO((UserCard) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public UserCardVO getById(int id) {
		UserCard entity = uCardDao.getById(id);
		if(entity != null) {
			UserCardVO bean = new UserCardVO(entity, lotteryDataFactory);
			return bean;
		}
		return null;
	}
	
	@Override
	public List<UserCardVO> getByUserId(int id) {
		List<UserCard> clist = uCardDao.getByUserId(id);
		List<UserCardVO> list = new ArrayList<>();
		for (UserCard tmpBean : clist) {
			list.add(new UserCardVO(tmpBean, lotteryDataFactory));
		}
		return list;
	}

	@Override
	public UserCard getByUserAndCardId(int userId, String cardId) {
		return uCardDao.getByUserAndCardId(userId, cardId);
	}

	@Override
	public boolean add(String username, int bankId, String bankBranch,
			String cardName, String cardId, int status) {
		User uBean = uDao.getByUsername(username);
		if(uBean != null) {
			List<UserCard> list = uCardDao.getByUserId(uBean.getId());
			if(list.size() < 5) {
				int userId = uBean.getId();
				String time = DateUtil.getCurrentTime();
				String lockTime = null;
				int lockStatus = 0;
				UserCard entity = new UserCard(userId, bankId, bankBranch, cardName, cardId, status, time, lockTime, lockStatus);
				return uCardDao.add(entity);
			}
		}
		return false;
	}

	@Override
	public boolean edit(int id, int bankId, String bankBranch, String cardId) {
		UserCard entity = uCardDao.getById(id);
		if(entity != null) {
			entity.setBankId(bankId);
			entity.setBankBranch(bankBranch);
			entity.setCardId(cardId);
			return uCardDao.update(entity);
		}
		return false;
	}
	
	@Override
	public boolean updateStatus(int id, int status) {
		UserCard entity = uCardDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			if(status >= 0) {
				entity.setLockTime(null);
			} else {
				String time = new Moment().toSimpleTime();
				entity.setLockTime(time);
			}
			return uCardDao.update(entity);
		}
		return false;
	}
	
}