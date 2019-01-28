package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserSecurityService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserSecurityDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserSecurity;
import lottery.domains.content.vo.user.UserSecurityVO;
import lottery.domains.pool.LotteryDataFactory;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserSecurityDao uSecurityDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	public PageList search(String username, String key, int start, int limit) {
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
		if(StringUtil.isNotNull(key)) {
			criterions.add(Restrictions.like("key", key, MatchMode.ANYWHERE));
		}
		if(isSearch) {
			List<UserSecurityVO> list = new ArrayList<>();
			PageList pList = uSecurityDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserSecurityVO((UserSecurity) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public List<UserSecurityVO> getByUserId(int id) {
		List<UserSecurity> slist = uSecurityDao.getByUserId(id);
		List<UserSecurityVO> list = new ArrayList<>();
		for (UserSecurity tmpBean : slist) {
			list.add(new UserSecurityVO(tmpBean, lotteryDataFactory));
		}
		return list;
	}
	
	@Override
	public boolean reset(String username) {
		User uBean = uDao.getByUsername(username);
		if(uBean != null) {
			boolean flag = uSecurityDao.delete(uBean.getId());
			if(flag) {
				// 加时间锁3天 （取消充值密保锁定，update by bill）
				//String lockTime = new Moment().add(3, "days").toSimpleTime();
				//uDao.updateLockTime(uBean.getId(), lockTime);
			}
			return flag;
		}
		return false;
	}

}