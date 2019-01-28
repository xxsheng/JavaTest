package lottery.domains.content.biz.read.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.UserBillReadService;
import lottery.domains.content.dao.read.UserBillReadDao;
import lottery.domains.content.entity.UserBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserBillVO;
import lottery.domains.pool.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserBillReadServiceImpl implements UserBillReadService {

	private static final Logger logger = LoggerFactory.getLogger(UserBillReadServiceImpl.class);
	
	@Autowired
	private UserBillReadDao uBillReadDao;

	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public PageList searchAll(Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBillReadDao.searchAll(account, type, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByUserId(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBillReadDao.searchByUserId(userId, account, type, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList;
		if (userId == Global.USER_TOP_ID) {
			pList = uBillReadDao.searchAll(account, type, sTime, eTime, start, limit);
		}
		else {
			pList = uBillReadDao.searchByTeam(userId, account, type, sTime, eTime, start, limit);
		}
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByDirectTeam(int userId, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBillReadDao.searchByDirectTeam(userId, account, type, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByTeam(Integer[] userIds, Integer account, Integer type, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBillReadDao.searchByTeam(userIds, account, type, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	private PageList convertPageList(PageList pList) {
		List<UserBillVO> blist = new ArrayList<>();
		if(pList != null && pList.getList() != null){
			for (Object tmpBean : pList.getList()) {
				blist.add(new UserBillVO((UserBill) tmpBean, dataFactory));
			}
		}
		pList.setList(blist);
		return pList;
	}
}