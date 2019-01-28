package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserWithdrawReadService;
import lottery.domains.content.dao.read.UserWithdrawReadDao;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserWithdrawVO;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserWithdrawReadServiceImpl implements UserWithdrawReadService {
	@Autowired
	private UserWithdrawReadDao uWithdrawReadDao;

	@Override
	@Transactional(readOnly = true)
	public PageList search(int userId, String billno, String sTime,
			String eTime, Integer status, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		criterions.add(Restrictions.eq("userId", userId));
		if(StringUtil.isNotNull(billno)) {
			criterions.add(Restrictions.eq("billno", billno));
		}
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		if (status != null) {
			if (status == -1) {
				// 拒绝支付
				criterions.add(Restrictions.eq("status", -1));
			}
			else if (status == 0) {
				// 待处理
				criterions.add(Restrictions.eq("status", 0));
				criterions.add(Restrictions.eq("checkStatus", 0));
			}
			else if (status == 1) {
				// 已完成
				criterions.add(Restrictions.eq("status", 1));
				criterions.add(Restrictions.eq("checkStatus", 1));
				criterions.add(Restrictions.eq("remitStatus", 2));
			}
			else if (status == 2) {
				// 处理中
				criterions.add(Restrictions.eq("status", 0));
				criterions.add(Restrictions.eq("checkStatus", 1));
				criterions.add(Restrictions.eq("remitStatus", 0));
			}
			else if (status == 3) {
				// 银行处理中
				LogicalExpression and1 = Restrictions.and(Restrictions.eq("status", 0), Restrictions.eq("checkStatus", 1));

				Criterion remitStatuses = Restrictions.in("remitStatus", Arrays.asList(1, 3, -3, -4, -5, -6, -7));
				LogicalExpression and2 = Restrictions.and(and1, remitStatuses);

				criterions.add(and2);
			}
			else if (status == 4) {
				// 提现失败
				Disjunction or1 = Restrictions.or();
				LogicalExpression and1 = Restrictions.and(Restrictions.eq("status", 1), Restrictions.eq("checkStatus", -1));

				Disjunction or2 = Restrictions.or();
				or2.add(Restrictions.eq("remitStatus", -1));
				or2.add(Restrictions.eq("remitStatus", -2));
				LogicalExpression and2 = Restrictions.and(Restrictions.eq("status", 1), Restrictions.eq("checkStatus", 1));
				LogicalExpression and3 = Restrictions.and(and2, or2);

				or1.add(and1);
				or1.add(and3);

				criterions.add(or1);
			}
		}
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		List<UserWithdrawVO> list = new ArrayList<>();
		PageList pList = uWithdrawReadDao.search(criterions, orders, start, limit);
		for (Object tmpBean : pList.getList()) {
			list.add(new UserWithdrawVO((UserWithdraw) tmpBean));
		}
		pList.setList(list);
		return pList;
	}
}