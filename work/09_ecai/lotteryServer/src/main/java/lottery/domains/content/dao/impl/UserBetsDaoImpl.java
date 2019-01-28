package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.entity.UserBets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBetsDaoImpl implements UserBetsDao {
	private final String tab = UserBets.class.getSimpleName();

	private static final String NO_CODES_CONSTRUCT = "new UserBets(ub.id, ub.billno, ub.userId, ub.type, ub.lotteryId, ub.expect, ub.ruleId, ub.nums, ub.model, ub.multiple, ub.code, ub.point, ub.money, ub.time, ub.stopTime, ub.openTime, ub.status, ub.openCode, ub.prizeMoney, ub.prizeTime, ub.chaseBillno, ub.chaseStop, ub.planBillno, ub.rewardMoney, ub.compressed)";

	@Autowired
	private HibernateSuperDao<UserBets> superDao;

	@Override
	public boolean add(UserBets entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean cancel(int id, int userId) {
		String hql = "update " + tab + " set status = -1 where id = ?0 and status = 0 and userId=?1";
		Object[] values = {id, userId};
		return superDao.update(hql, values);
	}

	@Override
	public List<UserBets> getByChaseBillno(String chaseBillno, int userId) {
		String hql = "select " + NO_CODES_CONSTRUCT + " from " + tab + " ub where ub.chaseBillno = ?0 and ub.userId = ?1 and ub.status = 0 and ub.id > 0";
		Object[] values = {chaseBillno, userId};
		return superDao.list(hql, values);
	}
}