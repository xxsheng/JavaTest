package lottery.domains.content.biz.impl;

import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsHitRankingService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.UserBetsHitRankingDao;
import lottery.domains.content.entity.UserBetsHitRanking;
import lottery.domains.content.global.DbServerSyncEnum;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBetsHitRankingServiceImpl implements UserBetsHitRankingService {
	
	@Autowired
	private UserBetsHitRankingDao uBetsHitRankingDao;

	@Autowired
	private DbServerSyncDao dbServerSyncDao;
	
	@Override
	public PageList search(int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("prizeMoney"));
		orders.add(Order.desc("time"));
		return uBetsHitRankingDao.find(criterions, orders, start, limit);
	}

	@Override
	public boolean add(String name, String username, int prizeMoney, String time, String code, String type, int platform) {
		UserBetsHitRanking entity = new UserBetsHitRanking(name, username, prizeMoney, time, code, type, platform);
		boolean added = uBetsHitRankingDao.add(entity);
		if (added) {
			dbServerSyncDao.update(DbServerSyncEnum.HIT_RANKING);
		}
		return added;
	}
	
	@Override
	public boolean edit(int id, String name, String username, int prizeMoney, String time, String code, String type, int platform) {
		UserBetsHitRanking entity = uBetsHitRankingDao.getById(id);
		if(entity != null) {
			entity.setName(name);
			entity.setUsername(username);
			entity.setPrizeMoney(prizeMoney);
			entity.setTime(time);
			entity.setCode(code);
			entity.setType(type);
			entity.setPlatform(platform);
			boolean updated = uBetsHitRankingDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.HIT_RANKING);
			}
			return updated;
		}
		return false;
	}
	
	@Override
	public boolean delete(int id) {
		boolean deleted = uBetsHitRankingDao.delete(id);
		if (deleted) {
			dbServerSyncDao.update(DbServerSyncEnum.HIT_RANKING);
		}
		return deleted;
	}

	@Override
	public UserBetsHitRanking getById(int id) {
		return uBetsHitRankingDao.getById(id);
	}
}