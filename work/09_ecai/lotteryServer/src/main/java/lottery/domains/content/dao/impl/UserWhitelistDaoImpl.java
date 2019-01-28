package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserWhitelistDao;
import lottery.domains.content.entity.UserWhitelist;

@Repository
public class UserWhitelistDaoImpl implements UserWhitelistDao {
	
	private final String tab = UserWhitelist.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserWhitelist> superDao;
	
	@Override
	public List<UserWhitelist> getByUsername(String username) {
		String hql = "from " + tab + " where username = ?0";
		Object[] values = {username};
		return superDao.list(hql, values);
	}

}