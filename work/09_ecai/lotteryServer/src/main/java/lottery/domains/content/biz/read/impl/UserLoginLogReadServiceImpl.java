package lottery.domains.content.biz.read.impl;

import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserLoginLogReadService;
import lottery.domains.content.dao.read.UserLoginLogReadDao;
import lottery.domains.content.entity.UserLoginLog;
import lottery.domains.content.vo.user.UserLoginLogVO;
import org.apache.commons.collections.CollectionUtils;
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
public class UserLoginLogReadServiceImpl implements UserLoginLogReadService {
	
	@Autowired
	private UserLoginLogReadDao uLoginLogReadDao;

	@Override
	@Transactional(readOnly = true)
	public UserLoginLogVO getLastLogin(int userId, int start) {
		UserLoginLog loginLog = uLoginLogReadDao.getLastLogin(userId, start);

		UserLoginLogVO loginLogVO = null;
		if (loginLog != null) {
			loginLogVO = new UserLoginLogVO(loginLog);
		}

		return loginLogVO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserLoginLogVO> getLoginLog(int userId, int count) {
		List<UserLoginLog> loginLogs = uLoginLogReadDao.getLoginLog(userId, 0, count);

		List<UserLoginLogVO> loginLogVOs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(loginLogs)) {
			for (UserLoginLog loginLog : loginLogs) {
				loginLogVOs.add(new UserLoginLogVO(loginLog));
			}
		}

		return loginLogVOs;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList getLoginLogList(int userId, int start, int limit) {

		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.ge("time", new Moment().subtract(1, "months").toSimpleTime()));
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		List<UserLoginLogVO> list = new ArrayList<>();
		PageList pList =  uLoginLogReadDao.getLoginLogList(criterions, orders, start, limit);

		for (Object tmpBean : pList.getList()) {
			list.add(new UserLoginLogVO((UserLoginLog) tmpBean));
		}
		pList.setList(list);
		return pList;
	}
}