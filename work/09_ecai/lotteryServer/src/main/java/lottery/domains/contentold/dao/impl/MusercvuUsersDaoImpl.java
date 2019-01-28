package lottery.domains.contentold.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.contentold.dao.MusercvuUsersDao;
import lottery.domains.contentold.entity.MusercvuUsers;

@Repository
public class MusercvuUsersDaoImpl implements MusercvuUsersDao {
	
	private final String tab = MusercvuUsers.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<MusercvuUsers> superDao;

	@Override
	public List<MusercvuUsers> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

}