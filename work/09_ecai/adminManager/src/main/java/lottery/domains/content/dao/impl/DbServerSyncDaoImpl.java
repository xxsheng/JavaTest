package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.entity.DbServerSync;
import lottery.domains.content.global.DbServerSyncEnum;

import java.util.List;

@Repository
public class DbServerSyncDaoImpl implements DbServerSyncDao {
	
	private final String tab = DbServerSync.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<DbServerSync> superDao;
	
	public static void main(String[] args) {
		System.out.println(DbServerSyncEnum.LOTTERY.name());
	}

	@Override
	public List<DbServerSync> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

	@Override
	public boolean update(DbServerSyncEnum type) {
		String key = type.name();
		String time = new Moment().toSimpleTime();
		String hql = "update " + tab + " set lastModTime = ?1 where key = ?0";
		Object[] values = {key, time};
		return superDao.update(hql, values);
	}

	@Override
	public boolean update(String key, String lastModTime) {
		String hql = "update " + tab + " set lastModTime = ?1 where key = ?0";
		Object[] values = {key, lastModTime};
		return superDao.update(hql, values);
	}

	@Override
	public DbServerSync getByKey(String key) {
		String hql = "from " + tab  +" where key=?0";
		Object[] values = {key};
		return (DbServerSync) superDao.unique(hql, values);
	}

	@Override
	public boolean save(DbServerSync dbServerSync) {
		return superDao.save(dbServerSync);
	}
}
