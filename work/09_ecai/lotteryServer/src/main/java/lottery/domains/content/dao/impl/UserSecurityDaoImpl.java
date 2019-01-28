package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserSecurityDao;
import lottery.domains.content.entity.UserSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserSecurityDaoImpl implements UserSecurityDao {

	private final String tab = UserSecurity.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserSecurity> superDao;
	
	@Override
	public boolean add(UserSecurity entity) {
		return superDao.save(entity);
	}
	
	@Override
	public List<UserSecurity> listByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}

	@Override
	public UserSecurity getById(int id, int userId) {
		String hql = "from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = {id, userId};
		return (UserSecurity) superDao.unique(hql, values);
	}

}