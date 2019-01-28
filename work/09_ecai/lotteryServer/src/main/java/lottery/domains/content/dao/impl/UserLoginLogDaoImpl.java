package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserLoginLogDao;
import lottery.domains.content.entity.UserLoginLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserLoginLogDaoImpl implements UserLoginLogDao {
	
	private final String tab = UserLoginLog.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserLoginLog> superDao;
	
	@Override
	public boolean add(UserLoginLog entity) {
		return superDao.save(entity);
	}
}