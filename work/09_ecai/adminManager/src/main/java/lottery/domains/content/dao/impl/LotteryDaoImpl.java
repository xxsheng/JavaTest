package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.entity.Lottery;

@Repository
public class LotteryDaoImpl implements LotteryDao {

	private final String tab = Lottery.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<Lottery> superDao;
	
	@Override
	public List<Lottery> listAll() {
		String hql = "from " + tab + " order by sort";
		return superDao.list(hql);
	}

	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateTimes(int id, int times) {
		String hql = "update " + tab + " set times = ?1 where id = ?0";
		Object[] values = {id, times};
		return superDao.update(hql, values);
	}

	@Override
	public Lottery getByName(String name) {
		String hql = "from " + tab + "  where showName = ?0";
		Object[] values = {name};
		List<Lottery> list = superDao.list(hql, values);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public Lottery getByShortName(String name) {
		String hql = "from " + tab + "  where shortName = ?0";
		Object[] values = {name};
		List<Lottery> list = superDao.list(hql, values);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	@Override
	public Lottery getById(int id) {
		String hql = "from " + tab + "  where id = ?0";
		Object[] values = {id};
		List<Lottery> list = superDao.list(hql, values);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
}