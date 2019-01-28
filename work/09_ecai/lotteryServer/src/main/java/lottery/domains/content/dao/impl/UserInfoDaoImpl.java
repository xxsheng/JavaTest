package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserInfoDao;
import lottery.domains.content.entity.UserInfo;

@Repository
public class UserInfoDaoImpl implements UserInfoDao {

	private final String tab = UserInfo.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserInfo> superDao;
	
	@Override
	public UserInfo get(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return (UserInfo) superDao.unique(hql, values);
	}

	@Override
	public boolean add(UserInfo entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(UserInfo entity) {
		return superDao.update(entity);
	}

}
