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
	public List<LotteryOpenCode> getLatest(String lotteryName, int count, Integer userId) {
		String hql = "from " + tab + " where lottery = ?0";
		Object[] values;
		if (userId != null) {
			hql += " and userId = ?1";
			values = new Object[]{lotteryName, userId};
		}
		else {
			values = new Object[]{lotteryName};
		}
		hql += " order by expect desc";
		return superDao.list(hql, values, 0, count);
	}

	@Override
	public boolean update(int id, String updateTime) {
		String hql = "update " + tab + " set openStatus = 1, openTime = ?0 where  id=?1";
		Object[] values = {updateTime, id};
		return superDao.update(hql, values);
	}

	@Override
	public LotteryOpenCode getByExcept(String expect) {
		String hql = "from " + tab + " where  expect = ?0 ";
		Object[] values = {expect};
		List<LotteryOpenCode> list = superDao.list(hql, values, 0, 1);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
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
	public List<LotteryOpenCode> getOpenCodeByDate(String lotteryName, String sTime, String eTime) {
		String hql = "from " + tab + " where lottery = ?0 and time>=?1 and time<=?2 order by expect desc";
		Object[] values = {lotteryName, sTime,eTime};
		return superDao.list(hql, values);
	}
}