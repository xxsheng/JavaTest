package lottery.domains.content.biz.read.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserDividendReadService;
import lottery.domains.content.dao.read.UserDividendReadDao;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.vo.user.UserDividendVO;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserDividendReadServiceImpl implements UserDividendReadService {
	@Autowired
	private UserDividendReadDao uDividendReadDao;
	@Autowired
	private DataFactory dataFactory;

	@Override
	public PageList searchAll(int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("createTime"));
		PageList pList = uDividendReadDao.search(null, orders, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	public PageList searchByTeam(int[] userIds, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uDividendReadDao.searchByTeam(userIds, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	public PageList searchByTeam(int userId, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uDividendReadDao.searchByTeam(userId, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	public PageList searchByUserId(int userId, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uDividendReadDao.searchByUserId(userId, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	// @Override
	// @Transactional(readOnly = true)
	// public PageList searchByZhuGuans(List<Integer> userIds, int start, int limit) {
	// 	start = start < 0 ? 0 : start;
	// 	limit = limit < 0 ? 0 : limit;
	// 	limit = limit > 10 ? 10 : limit;
    //
	// 	PageList pList = uDividendReadDao.searchByZhuGuans(userIds, start, limit);
	// 	pList = convertPageList(pList);
	// 	return pList;
	// }
    //
	// @Override
	// @Transactional(readOnly = true)
	// public PageList searchByDirectLowers(int upUserId, int start, int limit) {
	// 	start = start < 0 ? 0 : start;
	// 	limit = limit < 0 ? 0 : limit;
	// 	limit = limit > 10 ? 10 : limit;
    //
	// 	PageList pList = uDividendReadDao.searchByDirectLowers(upUserId, start, limit);
	// 	pList = convertPageList(pList);
	// 	return pList;
	// }

	@Override
	@Transactional(readOnly = true)
	public List<UserDividend> findByUserIds(List<Integer> userIds) {
		if (CollectionUtils.isEmpty(userIds)) {
			return new ArrayList<>();
		}
		return uDividendReadDao.findByUserIds(userIds);
	}

	private PageList convertPageList(PageList pList) {
		List<UserDividendVO> convertList = new ArrayList<>();
		if(pList != null && pList.getList() != null){
			for (Object tmpBean : pList.getList()) {
				convertList.add(new UserDividendVO((UserDividend) tmpBean, dataFactory));
			}
		}
		pList.setList(convertList);
		return pList;
	}
	
    @Override
    public Long getCountUser(int userId) {
        return uDividendReadDao.getCountUser(userId);
    }
}