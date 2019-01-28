package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserActionLogDao;
import lottery.domains.content.entity.UserActionLog;

@Repository
public class UserActionLogDaoImpl implements UserActionLogDao {
	
	@Autowired
	private HibernateSuperDao<UserActionLog> superDao;

	@Override
	public boolean add(UserActionLog entity) {
		return superDao.save(entity);
	}

}