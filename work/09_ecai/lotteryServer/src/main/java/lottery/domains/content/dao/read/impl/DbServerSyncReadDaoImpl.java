package lottery.domains.content.dao.read.impl;

import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.DbServerSyncReadDao;
import lottery.domains.content.entity.DbServerSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DbServerSyncReadDaoImpl implements DbServerSyncReadDao {
	
	private final String tab = DbServerSync.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<DbServerSync> superDao;
	
	@Override
	public List<DbServerSync> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

}
