package lottery.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserBetsLimitDao;
import lottery.domains.content.entity.UserBetsLimit;
@Repository
public class UserBetsLimitDaoImpl implements UserBetsLimitDao {
	
	private final String tab = UserBetsLimit.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsLimit> superDao;
	
	@Override
	public List<UserBetsLimit> getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		List<UserBetsLimit> list = superDao.list(hql, values);
		if(list == null){
			list = new ArrayList<UserBetsLimit>();
		}
		return list;
	}


}
