package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.SysPlatformDao;
import lottery.domains.content.entity.SysPlatform;

@Repository
public class SysPlatformDaoImpl implements SysPlatformDao {
	
	private final String tab = SysPlatform.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<SysPlatform> superDao;
	
	@Override
	public List<SysPlatform> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

}
