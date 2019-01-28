package lottery.domains.content.dao.read.impl;

import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserBetsHitRankingReadDao;
import lottery.domains.content.entity.UserBetsHitRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBetsHitRankingReadDaoImpl implements UserBetsHitRankingReadDao {

	private final String tab = UserBetsHitRanking.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserBetsHitRanking> superDao;
	
	@Override
	public List<UserBetsHitRanking> listAll(int count) {
		String hql = "from " + tab + " order by prizeMoney desc,time desc";
		return superDao.list(hql, 0, count);
	}
	
}