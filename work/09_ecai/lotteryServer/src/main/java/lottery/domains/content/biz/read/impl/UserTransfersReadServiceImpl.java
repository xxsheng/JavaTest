package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserTransfersReadService;
import lottery.domains.content.dao.read.UserTransfersReadDao;
import lottery.domains.content.entity.UserTransfers;
import lottery.domains.content.vo.user.UserTransfersVO;
import lottery.domains.pool.DataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserTransfersReadServiceImpl implements UserTransfersReadService {
	private static final Logger log = LoggerFactory.getLogger(UserTransfersReadServiceImpl.class);

	@Autowired
	private UserTransfersReadDao uTransfersReadDao;

	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public PageList search(int userId, String billno, Integer type,
			String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		criterions.add(Restrictions.eq("toUid", userId));
		criterions.add(Restrictions.gt("id", 0));
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
		orders.add(Order.desc("id"));
		List<UserTransfersVO> list = new ArrayList<>();
		PageList pList = uTransfersReadDao.search(criterions, orders, start, limit);
		for (Object tmpBean : pList.getList()) {
			list.add(new UserTransfersVO((UserTransfers) tmpBean,dataFactory));
		}
		pList.setList(list);
		return pList;
	}

}
