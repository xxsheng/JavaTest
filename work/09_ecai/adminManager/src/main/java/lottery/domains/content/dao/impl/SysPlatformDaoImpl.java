package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.SysPlatformDao;
import lottery.domains.content.entity.SysPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
}
