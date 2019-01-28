package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.SysConfigDao;
import lottery.domains.content.entity.SysConfig;

@Repository
public class SysConfigDaoImpl implements SysConfigDao {

	private final String tab = SysConfig.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<SysConfig> superDao;
	
	@Override
	public List<SysConfig> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

	@Override
	public SysConfig get(String group, String key) {
		String hql = "from " + tab + " where group = ?0 and key = ?1";
		Object[] values = {group, key};
		return (SysConfig) superDao.unique(hql, values);
	}

	@Override
	public boolean update(SysConfig entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean save(SysConfig entity) {
		return superDao.save(entity);
	}
}