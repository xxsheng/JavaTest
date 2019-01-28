package lottery.domains.content.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserPlanInfoDao;
import lottery.domains.content.entity.UserPlanInfo;

@Repository
public class UserPlanInfoDaoImpl implements UserPlanInfoDao {
	
	private final String tab = UserPlanInfo.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserPlanInfo> superDao;

	@Override
	public boolean add(UserPlanInfo entity) {
		return superDao.save(entity);
	}

	@Override
	public UserPlanInfo get(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return (UserPlanInfo) superDao.unique(hql, values);
	}

	@Override
	public boolean update(UserPlanInfo entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean update(int userId, int planCount, int prizeCount, double totalMoney, double totalPrize) {
		String hql = "update " + tab + " set planCount = planCount + ?1, prizeCount = prizeCount + ?2, totalMoney = totalMoney + ?3, totalPrize = totalPrize + ?4 where userId = ?0";
		Object[] values = {userId, planCount, prizeCount, totalMoney, totalPrize};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLevel(int userId, int level) {
		String hql = "update " + tab + " set level = ?1 where userId = ?0";
		Object[] values = {userId, level};
		return superDao.update(hql, values);
	}

}