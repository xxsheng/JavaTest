package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserCodeQuotaDao;
import lottery.domains.content.entity.UserCodeQuota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
	public UserCodeQuota get(int userId, int code) {
		String hql = "from " + tab + " where userId = ?0 and code = ?1";
		Object[] values = {userId, code};
		return (UserCodeQuota) superDao.unique(hql, values);
	}

	@Override
	public List<UserCodeQuota> list(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}

	@Override
	public List<UserCodeQuota> list(int[] userIds) {
		String hql = "from " + tab + " where userId in ("+ ArrayUtils.transInIds(userIds)+")";
		return superDao.list(hql);
	}

	@Override
	public boolean addUpAllocateQuantity(int userId, int code) {
		String hql = "update " + tab + " set upAllocateQuantity = upAllocateQuantity + 1 where userId = ?0 and code = ?1";
		Object[] values = {userId, code};
		return superDao.update(hql, values);
	}

}