package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserRechargeReadService;
import lottery.domains.content.dao.read.UserRechargeReadDao;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.vo.user.UserRechargeVO;
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
public class UserRechargeReadServiceImpl implements UserRechargeReadService {
	@Autowired
	private UserRechargeReadDao uRechargeReadDao;

	@Override
	@Transactional(readOnly = true)
	public PageList search(int userId, Integer type, String billno, String sTime,
			String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.gt("id", 0));
		if(type != null) {
			criterions.add(Restrictions.eq("type", type.intValue()));
		}
		if(StringUtil.isNotNull(billno)) {
			criterions.add(Restrictions.eq("billno", billno));
		}
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		criterions.add(Restrictions.eq("status", 1));
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		List<UserRechargeVO> list = new ArrayList<>();
		PageList pList = uRechargeReadDao.search(criterions, orders, start, limit);
		for (Object tmpBean : pList.getList()) {
			list.add(new UserRechargeVO((UserRecharge) tmpBean));
		}
		pList.setList(list);
		return pList;
	}
}