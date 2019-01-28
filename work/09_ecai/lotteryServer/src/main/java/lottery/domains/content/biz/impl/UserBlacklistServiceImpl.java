package lottery.domains.content.biz.impl;

import javautils.date.DateUtil;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserBlacklistService;
import lottery.domains.content.dao.UserBlacklistDao;
import lottery.domains.content.entity.UserBlacklist;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBlacklistServiceImpl implements UserBlacklistService {
	
	@Autowired
	private UserBlacklistDao uBlacklistDao;

	@Override
	public boolean add(String username, int bankId, String cardName,
			String cardId, String ip) {
		String operatorUser = "机器人";
		String operatorTime = DateUtil.getCurrentTime();
		String remarks = "尝试绑定资料失败！";
		UserBlacklist entity = new UserBlacklist(username, cardName, cardId, bankId, ip, operatorUser, operatorTime, remarks);
		return uBlacklistDao.add(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBlacklist> getByUsername(String username) {
		return uBlacklistDao.getByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBlacklist> getByCard(String cardName, String cardId) {
		return uBlacklistDao.getByCard(cardName, cardId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBlacklist> getByCardName(String cardName) {
		return uBlacklistDao.getByCardName(cardName);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBlacklist> list(List<Criterion> criterions, List<Order> orders) {
		return uBlacklistDao.list(criterions, orders);
	}

	@Override
	@Transactional(readOnly = true)
	public int getByIp(String ip) {
		return uBlacklistDao.getByIp(ip);
	}
}