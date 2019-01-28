package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.LotteryOpenCodeDao;
import lottery.domains.content.entity.LotteryOpenCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LotteryOpenCodeDaoImpl implements LotteryOpenCodeDao {
	private final String tab =  LotteryOpenCode.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<LotteryOpenCode> superDao;

	@Override
	public boolean updateOpened(int id, String updateTime) {
		String hql = "update " + tab + " set openStatus = 1, openTime = ?0 where  id=?1";
		Object[] values = {updateTime, id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateCancelled(int id, String updateTime) {
		String hql = "update " + tab + " set openStatus = 3, openTime = ?0 where  id=?1";
		Object[] values = {updateTime, id};
		return superDao.update(hql, values);
	}

	@Override
	public List<LotteryOpenCode> getLatest(String lottery, int count) {
		String hql = "from " + tab + " where lottery=?0 order by expect desc";
		Object[] values = {lottery};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, count);
		return list;
	}

	@Override
	public List<LotteryOpenCode> listAfter(String lottery, String expect) {
		String hql = "from " + tab + " where lottery=?0 and expect>=?1";
		Object[] values = {lottery, expect};
		List<LotteryOpenCode> list = superDao.list(hql, values);
		return list;
	}

	@Override
	public List<LotteryOpenCode> getBeforeNotOpen(String lottery, int count) {
		String hql = "from " + tab + " where openStatus = 0 and lottery=?0 order by expect desc";
		Object[] values = {lottery};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, count);
		return list;
	}

	@Override
	public LotteryOpenCode getByExcept(String lottery, String except) {
		String hql = "from " + tab + " where lottery=?0 and  expect = ?1 ";
		Object[] values = {lottery,except};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, 1);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public LotteryOpenCode getByExceptAndUserId(String lottery, int userId, String except) {
		String hql = "from " + tab + " where lottery=?0 and  expect = ?1 and userId = ?2";
		Object[] values = {lottery,except, userId};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, 1);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public boolean add(LotteryOpenCode openCode) {
		return superDao.save(openCode);
	}

	@Override
	public boolean update(LotteryOpenCode openCode) {
		return superDao.update(openCode);
	}
}