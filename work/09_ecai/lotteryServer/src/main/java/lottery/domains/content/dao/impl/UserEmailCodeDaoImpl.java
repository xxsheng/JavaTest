package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserEmailCodeDao;
import lottery.domains.content.entity.UserEmailCode;

@Repository
public class UserEmailCodeDaoImpl implements UserEmailCodeDao {
	
	private final String tab = UserEmailCode.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserEmailCode> superDao;

	@Override
	public boolean save(UserEmailCode entity) {
		return superDao.save(entity);
	}

	@Override
	public UserEmailCode get(int type, String username, String code) {
		String hql = "from " + tab + " where type =?0 and username = ?1 and code = ?2";
		Object[] values = {type, username, code};
		return (UserEmailCode) superDao.unique(hql, values);
	}

	@Override
	public boolean update(UserEmailCode entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean updateStatus(int id, int status) {
		String hql = "update " + tab + " set status = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}

}