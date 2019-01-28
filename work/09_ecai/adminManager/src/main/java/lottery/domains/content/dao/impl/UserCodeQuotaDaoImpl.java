package lottery.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserCodeQuotaDao;
import lottery.domains.content.entity.UserCodeQuota;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserCodeQuotaDaoImpl implements UserCodeQuotaDao {
	
	private final String tab = UserCodeQuota.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserCodeQuota> superDao;
	
	@Override
	public boolean add(UserCodeQuota entity) {
		return superDao.save(entity);
	}

	@Override
	public UserCodeQuota get(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return (UserCodeQuota) superDao.unique(hql, values);
	}
	
	@Override
	public List<UserCodeQuota> list(int[] userIds) {
		List<UserCodeQuota> result = new ArrayList<>();
		if(userIds.length > 0) {
			String hql = "from " + tab + " where userId in (" + ArrayUtils.transInIds(userIds) + ")";
			result = superDao.list(hql);
		}
		return result;
	}
	
	@Override
	public boolean update(UserCodeQuota entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean delete(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}

}