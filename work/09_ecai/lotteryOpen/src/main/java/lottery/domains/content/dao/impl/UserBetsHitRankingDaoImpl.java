package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsHitRankingDao;
import lottery.domains.content.entity.UserBetsHitRanking;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBetsHitRankingDaoImpl implements UserBetsHitRankingDao {

	private final String tab = UserBetsHitRanking.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsHitRanking> superDao;

	@Override
	public long getTotalSize(int platform) {
		String hql = "select count(id) from " + tab + " where platform = ?0";
		Object[] values = {platform};
		return (Long) superDao.unique(hql, values);
	}

	@Override
	public UserBetsHitRanking getMinRanking(int platform, String startTime, String endTime) {
		String hql = "from " + tab + " where platform = ?0 and time >= ?1 and time < ?2 order by prizeMoney asc,time asc";
		Object[] values = {platform, startTime, endTime};
		List<UserBetsHitRanking> list = superDao.list(hql, values, 0, 1);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	@Override
	public boolean add(UserBetsHitRanking ranking) {
		return superDao.save(ranking);
	}

	@Override
	public List<Integer> getIds(int platform, String startTime, String endTime) {
		String hql = "select id from " + tab + " where platform = ?0 and time >= ?1 and time < ?2 order by prizeMoney desc,time desc";
		Object[] values = {platform, startTime, endTime};
		List<Integer> list = (List<Integer>) superDao.listObject(hql, values);
		return list;
	}

	@Override
	public List<Integer> getTotalIds(int count, int platform, String startTime, String endTime) {
		String hql = "select id from " + tab + " where platform = ?0 and time >= ?1 and time < ?2 order by prizeMoney desc,time desc";
		Object[] values = {platform, startTime, endTime};
		List<Integer> list = (List<Integer>) superDao.listObject(hql, values, 0, count);
		return list;
	}

	@Override
	public List<Integer> getIdsByTimeDesc(int count, int platform) {
		String hql = "select id from " + tab + " where platform = ?0 order by time desc";
		Object[] values = {platform};
		List<Integer> list = (List<Integer>) superDao.listObject(hql, values, 0, count);
		return list;
	}

	@Override
	public int delNotInIds(List<Integer> ids, int platform) {
		String hql = "delete from " + tab + " where id not in ("+ ArrayUtils.transInIds(ids)+") and platform="+platform;
		return superDao.updateByHql(hql);
	}

	@Override
	public boolean delNotInIds(List<Integer> ids, int platform, String startTime, String endTime) {
		String hql = "delete from " + tab + " where id not in ("+ ArrayUtils.transInIds(ids)+") and platform=?0 and time >= ?1 and time < ?2";
		Object[] values = {platform, startTime, endTime};
		return superDao.delete(hql, values);
	}

	@Override
	public boolean delByTime(int platform, String endTime) {
		String hql = "delete from " + tab + " where platform=?0 and time < ?1";
		Object[] values = {platform, endTime};
		return superDao.delete(hql, values);
	}
}