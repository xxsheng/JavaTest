package lottery.domains.content.biz.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.read.UserBetsReadService;
import lottery.domains.content.dao.read.UserBetsReadDao;
import lottery.domains.content.entity.UserBets;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bets.UserBetsVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WSC;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserBetsReadServiceImpl implements UserBetsReadService {
	@Autowired
	private UserBetsReadDao uBetsReadDao;

	@Autowired
	private DataFactory dataFactory;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Override
	@Transactional(readOnly = true)
	public UserBetsVO getByIdWithCodes(int id) {
		UserBets userBets = uBetsReadDao.getByIdWithCodes(id);
		if (userBets == null) {
			return null;
		}
		return new UserBetsVO(userBets, dataFactory);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBetsVO getByIdWithoutCodes(int id) {
		UserBets userBets = uBetsReadDao.getByIdWithoutCodes(id);
		if (userBets == null) {
			return null;
		}
		return new UserBetsVO(userBets, dataFactory);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBetsVO getByIdWithCodes(int id, int userId) {
		UserBets userBets = uBetsReadDao.getByIdWithCodes(id, userId);
		if (userBets == null) {
			return null;
		}
		return new UserBetsVO(userBets, dataFactory);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBetsVO getByIdWithoutCodes(int id, int userId) {
		UserBets userBets = uBetsReadDao.getByIdWithoutCodes(id, userId);
		if (userBets == null) {
			return null;
		}
		return new UserBetsVO(userBets, dataFactory);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBetsVO> getRecentUserBets(int userId) {
		String key = String.format(WSC.USER_BETS_RECENT_KEY, userId);
		List<String> keyVal = jedisTemplate.lrange(key, 0, 4);

		if (CollectionUtils.isEmpty(keyVal)) {
			return new ArrayList<>();
		}

		List<Integer> ids = ArrayUtils.transStrToInteger(keyVal);

		List<UserBets> userBetsList = uBetsReadDao.searchByIds(ids);

		List<UserBetsVO> vos = new ArrayList<>();

		if (userBetsList != null && userBetsList.size() > 0) {
			for (int i = 0; i < userBetsList.size(); i++) {
				UserBetsVO vo = new UserBetsVO(userBetsList.get(i), dataFactory);
				vos.add(vo);
			}
		}
		return vos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBetsVO> getRecentChaseUserBets(int userId) {
		String key = String.format(WSC.USER_BETS_RECENT_CHASE_KEY, userId);
		List<String> keyVal = jedisTemplate.lrange(key, 0, 4);

		if (CollectionUtils.isEmpty(keyVal)) {
			return new ArrayList<>();
		}

		List<Integer> ids = ArrayUtils.transStrToInteger(keyVal);

		List<UserBets> userBetsList = uBetsReadDao.searchByIds(ids);

		List<UserBetsVO> vos = new ArrayList<>();

		if (userBetsList != null && userBetsList.size() > 0) {
			for (int i = 0; i < userBetsList.size(); i++) {
				UserBetsVO vo = new UserBetsVO(userBetsList.get(i), dataFactory);
				vos.add(vo);
			}
		}
		return vos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBetsVO> getRecentUserBetsUnOpen(int userId) {
		String key = String.format(WSC.USER_BETS_UNOPEN_RECENT_KEY, userId);
		List<String> keyVal = jedisTemplate.lrange(key, 0, 9);

		if (CollectionUtils.isEmpty(keyVal)) {
			return new ArrayList<>();
		}

		List<Integer> ids = ArrayUtils.transStrToInteger(keyVal);

		List<UserBets> userBetsList = uBetsReadDao.searchByIds(ids);

		List<UserBetsVO> vos = new ArrayList<>();

		if (userBetsList != null && userBetsList.size() > 0) {
			for (int i = 0; i < userBetsList.size(); i++) {
				UserBetsVO vo = new UserBetsVO(userBetsList.get(i), dataFactory);
				vos.add(vo);
			}
		}
		return vos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBetsVO> getRecentUserBetsOpened(int userId) {
		String key = String.format(WSC.USER_BETS_OPENED_RECENT_KEY, userId);
		List<String> keyVal = jedisTemplate.lrange(key, 0, 9);

		if (CollectionUtils.isEmpty(keyVal)) {
			return new ArrayList<>();
		}

		List<Integer> ids = ArrayUtils.transStrToInteger(keyVal);

		List<UserBets> userBetsList = uBetsReadDao.searchByIds(ids);

		List<UserBetsVO> vos = new ArrayList<>();

		if (userBetsList != null && userBetsList.size() > 0) {
			for (int i = 0; i < userBetsList.size(); i++) {
				UserBetsVO vo = new UserBetsVO(userBetsList.get(i), dataFactory);
				vos.add(vo);
			}
		}
		return vos;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchAll(Integer type, Integer lotteryId, String expect, Integer status, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBetsReadDao.searchAll(type, lotteryId, expect, status, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByUserId(int userId, Integer type, Integer lotteryId, String expect, Integer status, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBetsReadDao.searchByUserId(userId, type, lotteryId, expect, status, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByTeam(int userId, Integer type, Integer lotteryId, String expect, Integer status, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList;
		if (userId == Global.USER_TOP_ID) {
			pList = uBetsReadDao.searchAll(type, lotteryId, expect, status, sTime, eTime, start, limit);
		}
		else {
			pList = uBetsReadDao.searchByTeam(userId, type, lotteryId, expect, status, sTime, eTime, start, limit);
		}

		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByDirectTeam(int userId, Integer type, Integer lotteryId, String expect, Integer status, String sTime, String eTime, int start, int limit) {
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBetsReadDao.searchByDirectTeam(userId, type, lotteryId, expect, status, sTime, eTime, start, limit);

		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public PageList searchByTeam(Integer[] userIds, Integer type, Integer lotteryId, String expect, Integer status, String sTime, String eTime, int start, int limit) {
		if (userIds == null || userIds.length <= 0) {
			return new PageList();
		}
		start = start < 0 ? 0 : start;
		limit = limit < 0 ? 0 : limit;
		limit = limit > 10 ? 10 : limit;

		PageList pList = uBetsReadDao.searchByTeam(userIds, type, lotteryId, expect, status, sTime, eTime, start, limit);
		pList = convertPageList(pList);
		return pList;
	}

	@Override
	@Transactional(readOnly = true)
	public double getBillingOrder(int userId, String startTime, String endTime) {
		return uBetsReadDao.getBillingOrder(userId, startTime, endTime);
	}

	@Override
	@Transactional(readOnly = true)
	public double getUnSettleMoney(int userId, String startTime, String endTime) {
		return uBetsReadDao.getUnSettleMoney(userId, startTime, endTime);
	}

	private PageList convertPageList(PageList pList) {
		if (pList == null) {
			return new PageList();
		}
		List<UserBetsVO> list = new ArrayList<>();
		for (Object tmpBean : pList.getList()) {
			list.add(new UserBetsVO((UserBets) tmpBean, dataFactory));
		}
		pList.setList(list);
		return pList;
	}
}