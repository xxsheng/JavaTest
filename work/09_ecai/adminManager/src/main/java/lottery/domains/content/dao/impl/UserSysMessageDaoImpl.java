package lottery.domains.content.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserSysMessageDao;
import lottery.domains.content.entity.UserSysMessage;

@Repository
public class UserSysMessageDaoImpl implements UserSysMessageDao {
	
	private final String tab = UserSysMessage.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserSysMessage> superDao;
	
	@Override
	public boolean add(UserSysMessage entity) {
		return superDao.save(entity);
	}
	
	@Override
	public List<UserSysMessage> listUnread(int userId) {
		String hql = "from " + tab + " where userId = ?0 and status = 0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}
	
	@Override
	public boolean updateUnread(int userId, int[] ids) {
		if(ids.length > 0) {
			String hql = "update " + tab + " set status = 1 where userId = ?0 and id in (" + ArrayUtils.transInIds(ids) + ")";
			Object[] values = {userId};
			return superDao.update(hql, values);
		}
		return false;
	}

}