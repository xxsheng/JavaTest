package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsRiskDao;
import lottery.domains.content.entity.UserBetsRisk;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBetsRiskDaoImpl implements UserBetsRiskDao {
	private final String tab = UserBetsRisk.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsRisk> superDao;

	@Override
	public List<UserBetsRisk> list(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(UserBetsRisk.class, criterions, orders);
	}

	@Override
	public boolean updateStatus(int id, int fromStatus, int toStatus, String openCode, double prizeMoney, String prizeTime, int winNum) {
		String hql = "update " + tab + " set status = ?0, openCode = ?1, prizeMoney = ?2, prizeTime = ?3, winNum = ?4 where id = ?5 and status = ?6";
		Object[] values = {toStatus, openCode , prizeMoney, prizeTime, winNum, id, fromStatus};
		return superDao.update(hql, values);
	}
}