package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.LotteryTypeDao;
import lottery.domains.content.entity.LotteryType;

@Repository
public class LotteryTypeDaoImpl implements LotteryTypeDao {

	private final String tab = LotteryType.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<LotteryType> superDao;
	
	@Override
	public List<LotteryType> listAll() {
		String hql = "from " + tab + " order by sort";
		return superDao.list(hql);
	}

	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
}